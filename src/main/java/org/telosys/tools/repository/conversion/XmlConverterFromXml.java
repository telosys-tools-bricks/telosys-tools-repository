/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.repository.conversion;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.ForeignKeyColumnInDbModel;
import org.telosys.tools.repository.model.ForeignKeyInDbModel;
import org.telosys.tools.repository.model.JoinColumnInDbModel;
import org.telosys.tools.repository.model.JoinTableInDbModel;
import org.telosys.tools.repository.model.LinkInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlConverterFromXml {

	private final TelosysToolsLogger _logger;

	/**
	 * Constructor
	 * 
	 * @param logger
	 */
	protected XmlConverterFromXml(TelosysToolsLogger logger) {
		_logger = logger;
	}

	private void log(String msg) {
		if ( _logger != null ) {
			_logger.log("[LOG] " + this.getClass().getName() + " : " + msg);
		}
	}

	private void assertIsElement(Node node) throws TelosysToolsException {
		if ( node.getNodeType() != Node.ELEMENT_NODE ) {
			throw new TelosysToolsException("Node '" + node.getNodeName() + "' : ELEMENT type expected ");
		}
	}

	private void assertSize(NodeList nodeList, int expectedSize) throws TelosysToolsException {
		if ( nodeList.getLength() != expectedSize ) {
			throw new TelosysToolsException("Unexpected node list size");
		}
	}

	protected RepositoryModel xmlDocumentToModel(Document xmlDocument) throws TelosysToolsException {
		
		log("xmlDocumentToModel");
		
		//--- All tags <tableList> (list of entities/tables)
		NodeList tableListNodes = xmlDocument.getElementsByTagName(RepositoryConst.TABLELIST);
		assertSize(tableListNodes, 1);
		final Element tableListElement = (Element) tableListNodes.item(0);
		final RepositoryModel model = Wrappers.BASE_WRAPPER.getBase(tableListElement);
	
		//--- All tags <table> (entity)
		NodeList tableNodes = xmlDocument.getElementsByTagName(RepositoryConst.TABLE);
		log("Tags <table> : count = " + tableNodes.getLength() );
		if ( tableNodes.getLength() > 0 ) {
			//--- Process each "table/entity"
			for ( int index = 0 ; index < tableNodes.getLength() ; index++) {
				Node tableNode = tableNodes.item(index);
				EntityInDbModel entity = processTableNode( tableNode );
				model.storeEntity(entity);
			}
		}
		
//		// Process each "table/entity"
//		try {
//			ProcessContext context = new ProcessContext(tableNodes);
//			ICommandContext commandContext = commandManager.searchCommand(context);
//			ProcessContext resultat = commandContext.runProcess(context, commandManager);
//			for (Iterator<?> iterator = resultat.getList().iterator(); iterator.hasNext();) {
//				EntityInDbModel entity = (EntityInDbModel) iterator.next();
//				model.storeEntity(entity);
//			}
//		} catch (CommandException e) {
//			throw new TelosysToolsException("Cannot parse model (XML document)", e);
//		}
	
		return model;
		
	}

	private EntityInDbModel processTableNode( Node tableNode ) throws TelosysToolsException  {
		
		log("processTableNode : " + tableNode.getNodeName() );
		assertIsElement(tableNode);
		Element tableElement = (Element) tableNode;
		EntityInDbModel entity = Wrappers.ENTITY_WRAPPER.getEntity(tableElement);
		NodeList childNodes = tableElement.getChildNodes() ;
		//--- Process each "table/entity"
		log("processTableNode childs... "  );
		for ( int index = 0 ; index < childNodes.getLength() ; index++) {
			Node node = childNodes.item(index);
			log(" . child #" + index + " : " +  node.getNodeName() + " ( type = " + node.getNodeType() + " )" );
			//assertIsElement(node);
			if ( node.getNodeType() == Node.ELEMENT_NODE ) {
				if ( RepositoryConst.COLUMN.equals(node.getNodeName())) {
					AttributeInDbModel attribute = processColumnNode(entity, node ) ;
					entity.storeAttribute(attribute);
				}
				else if ( RepositoryConst.FK.equals(node.getNodeName())) {
					ForeignKeyInDbModel foreignKey = processForeignKeyNode( node );
					entity.storeForeignKey(foreignKey);
				}
				else if ( RepositoryConst.LINK.equals(node.getNodeName())) {
					LinkInDbModel link = processLinkNode( node );
					entity.storeLink(link);
				}
				else  {
					throw new TelosysToolsException("Unexpected tag '" + node.getNodeName() + "' at this position");
				}
			}
		}
		
		return entity;
	}
	
	/**
	 * Process the 'column' tag and returns the corresponding 'attribute' for the model 
	 * @param columnNode
	 * @return
	 * @throws TelosysToolsException
	 */
	private AttributeInDbModel processColumnNode( EntityInDbModel entity, Node columnNode ) throws TelosysToolsException  {
		log("processColumnNode : " + columnNode.getNodeName() );
		assertIsElement(columnNode);
		Element columnElement = (Element) columnNode;
		AttributeInDbModel column = Wrappers.ATTRIBUTE_WRAPPER.getAttributeObject(entity, columnElement);
		return column ;
	}

	/**
	 * Process the 'fk' tag and returns the corresponding 'foreign key' for the model 
	 * @param foreignKeyNode
	 * @return
	 * @throws TelosysToolsException
	 */
	private ForeignKeyInDbModel processForeignKeyNode( Node foreignKeyNode ) throws TelosysToolsException  {
		log("processForeignKeyNode : " + foreignKeyNode.getNodeName() );
		assertIsElement(foreignKeyNode);
		Element foreignKeyElement = (Element) foreignKeyNode;
		ForeignKeyInDbModel fk = Wrappers.FOREIGNKEY_WRAPPER.getForeignKey(foreignKeyElement);
		
		//--- Process each "fkcol" child
		NodeList childNodes = foreignKeyElement.getChildNodes() ;
		log("processForeignKeyNode : child nodes... "  );
		for ( int index = 0 ; index < childNodes.getLength() ; index++) {
			Node node = childNodes.item(index);
			log(" . child #" + index + " : " +  node.getNodeName() );
			//assertIsElement(node);
			if ( node.getNodeType() == Node.ELEMENT_NODE ) {
				if ( RepositoryConst.FKCOL.equals(node.getNodeName())) {
					ForeignKeyColumnInDbModel fkCol = processForeignKeyColumnNode( node ) ;
					fk.storeForeignKeyColumn(fkCol);
				}
				else  {
					throw new TelosysToolsException("Unexpected tag '" + node.getNodeName() + "' at this position");
				}
			}
		}
		return fk ;
	}

	private ForeignKeyColumnInDbModel processForeignKeyColumnNode( Node node ) throws TelosysToolsException  {
		log("processForeignKeyColumnNode : " + node.getNodeName() );
		assertIsElement(node);
		Element element = (Element) node;
		ForeignKeyColumnInDbModel foreignKeyColumnInDbModel = Wrappers.FOREIGNKEY_COLUMN_WRAPPER.getForeignKeyColumn(element);
		return foreignKeyColumnInDbModel ;
	}

	private LinkInDbModel processLinkNode( Node linkNode ) throws TelosysToolsException  {
		log("processLinkNode : " + linkNode.getNodeName() );
		assertIsElement(linkNode);
		Element linkElement = (Element) linkNode;
		LinkInDbModel link = Wrappers.LINK_WRAPPER.getLink(linkElement);
		
		//--- Process each child
		NodeList childNodes = linkElement.getChildNodes() ;
		log("processLinkNode : child nodes... "  );
		for ( int index = 0 ; index < childNodes.getLength() ; index++) {
			Node node = childNodes.item(index);
			log(" . child #" + index + " : " +  node.getNodeName() );
			//assertIsElement(node);
			if ( node.getNodeType() == Node.ELEMENT_NODE ) {
				if ( RepositoryConst.JOIN_COLUMNS_ELEMENT.equals(node.getNodeName())) {
					List<JoinColumnInDbModel> joinColumns = processJoinColumnsNode( node ) ;
					link.setJoinColumns(joinColumns);
				}
				else if ( RepositoryConst.JOIN_TABLE_ELEMENT.equals(node.getNodeName())) {
					JoinTableInDbModel joinTable = processJoinTableNode( node ) ;
					link.setJoinTable(joinTable);
				}
				else  {
					throw new TelosysToolsException("Unexpected tag '" + node.getNodeName() + "' at this position");
				}
			}
		}
		return link ;
	}

	private List<JoinColumnInDbModel> processJoinColumnsNode( Node joinColumnsNode ) throws TelosysToolsException  {
		log("processJoinColumnsNode : " + joinColumnsNode.getNodeName() );
		assertIsElement(joinColumnsNode);
		Element joinColumnsElement = (Element) joinColumnsNode;
		List<JoinColumnInDbModel> joinColumns = new LinkedList<JoinColumnInDbModel>();

		//--- Process each child
		NodeList childNodes = joinColumnsElement.getChildNodes() ;
		log("processLinkNode : child nodes... "  );
		for ( int index = 0 ; index < childNodes.getLength() ; index++) {
			Node childNode = childNodes.item(index);
			log(" . child #" + index + " : " +  childNode.getNodeName() );
			if ( childNode.getNodeType() == Node.ELEMENT_NODE ) {
				if ( RepositoryConst.JOIN_COLUMN_ELEMENT.equals(childNode.getNodeName())) {
					JoinColumnInDbModel joinColumn = processJoinColumnNode( childNode ) ;
					joinColumns.add(joinColumn);
				}
				else  {
					throw new TelosysToolsException("Unexpected tag '" + joinColumnsNode.getNodeName() + "' at this position");
				}
			}
		}
		return joinColumns ;
	}

	private JoinColumnInDbModel processJoinColumnNode( Node joinColumnNode ) throws TelosysToolsException  {
		log("processJoinColumnNode : " + joinColumnNode.getNodeName() );
		assertIsElement(joinColumnNode);
		Element joinColumnElement = (Element) joinColumnNode;
		JoinColumnInDbModel joinColumn = Wrappers.JOIN_COLUMN_WRAPPER.getJoinColumn(joinColumnElement);
		
		return joinColumn ;
	}

	private JoinTableInDbModel processJoinTableNode( Node joinTableNode ) throws TelosysToolsException  {
		log("processJoinTableNode : " + joinTableNode.getNodeName() );
		assertIsElement(joinTableNode);
		Element joinTableElement = (Element) joinTableNode;
		JoinTableInDbModel joinTable = Wrappers.JOIN_TABLE_WRAPPER.getObject(joinTableElement);
		
		//--- Process each child
		NodeList childNodes = joinTableElement.getChildNodes() ;
		log("processJoinTableNode : child nodes... "  );
		for ( int index = 0 ; index < childNodes.getLength() ; index++) {
			Node childNode = childNodes.item(index);
			log(" . child #" + index + " : " +  childNode.getNodeName() );
			if ( childNode.getNodeType() == Node.ELEMENT_NODE ) {
				if ( RepositoryConst.JOIN_COLUMNS_ELEMENT.equals(childNode.getNodeName())) {
					List<JoinColumnInDbModel> joinColumns = processJoinColumnsNode( childNode ) ;
					joinTable.setJoinColumns(joinColumns);
				}
				else if ( RepositoryConst.INVERSE_JOIN_COLUMNS_ELEMENT.equals(childNode.getNodeName())) {
					List<JoinColumnInDbModel> joinColumns = processJoinColumnsNode( childNode ) ;
					joinTable.setInverseJoinColumns(joinColumns);
				}
				else  {
					throw new TelosysToolsException("Unexpected tag '" + childNode.getNodeName() + "' at this position");
				}
			}
		}
		return joinTable ;
	}
}
