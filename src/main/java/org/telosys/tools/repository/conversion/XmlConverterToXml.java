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

import java.util.List;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.ForeignKeyColumnInDbModel;
import org.telosys.tools.repository.model.ForeignKeyInDbModel;
import org.telosys.tools.repository.model.JoinColumnInDbModel;
import org.telosys.tools.repository.model.JoinTableInDbModel;
import org.telosys.tools.repository.model.LinkInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.telosys.tools.repository.persistence.util.Xml;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlConverterToXml {

	private final TelosysToolsLogger _logger;

	/**
	 * Constructor
	 * 
	 * @param logger
	 */
	protected XmlConverterToXml(TelosysToolsLogger logger) {
		_logger = logger;
	}

	private void log(String msg) {
		if ( _logger != null ) {
			_logger.log("[LOG] " + this.getClass().getName() + " : " + msg);
		}
	}

	protected Document modelToXmlDocument(RepositoryModel model) throws TelosysToolsException 
	{
		log("convertToXmlDocument()... ");
		if (model != null) {
			Document doc = Xml.createDomDocument();

			log(" . generate root tags ");

			Comment comment = doc.createComment(" Telosys Database Repository ");
			doc.appendChild(comment);
			
			Element root = doc.createElement(RepositoryConst.ROOT_ELEMENT);

			Element entitiesElement = Wrappers.BASE_WRAPPER.getXmlDesc(model, doc);
			root.appendChild(entitiesElement);
			
			doc.appendChild(root);

			log(" . generate entities tag");
			addAllEntities(doc, model, entitiesElement);
			return doc ;
		} else {
			throw new TelosysToolsException("Cannot create XML/DOM document");
		}
	}

	// ---------------------------------------------------------------------------------------

	private void addAllEntities(Document doc, RepositoryModel model, Element entitiesElement) 
	{
//		EntityInDbModel[] entities = model.getEntities();
		EntityInDbModel[] entities = model.getEntitiesArraySortedByTableName();
		for ( EntityInDbModel entity : entities ) {
//			log("entity : " + entity.getName() );
			log("entity : " + entity.getDatabaseTable() );
			addEntity(doc, entity, entitiesElement);
		}
	}

	/**
	 * Adds an entity in the DOM
	 * 
	 * @param doc
	 * @param entity
	 */
	private void addEntity(Document doc, EntityInDbModel entity, Element parentElement) 
	{
		log("addEntity()");
		// --- Create XML element "table"
		Element entityElement = Wrappers.ENTITY_WRAPPER.getXmlDesc(entity, doc);

		//doc.getElementsByTagName(RepositoryConst.TABLELIST).item(0).appendChild(entityElement);

		addColumns(doc, entity, entityElement);

		addForeignKeys(doc, entity, entityElement);

		addLinks(doc, entity, entityElement);

		parentElement.appendChild(entityElement);
	}

	private void addLinks(Document doc, EntityInDbModel entity, Element entityElement) 
	{
//		LinkInDbModel[] links = entity.getLinks();
		LinkInDbModel[] links = entity.getLinksArray();
		for ( LinkInDbModel link : links )
		{
			Element linkElement = Wrappers.LINK_WRAPPER.getXmlDesc(link, doc);

			//--- "Join Table" 
//			JoinTableInDbModel joinTable = link.getJoinTable();
			JoinTableInDbModel joinTable = (JoinTableInDbModel) link.getJoinTable(); // v 3.0.0
			if ( joinTable != null ) {
				//--- Add the "JoinTable" element
				addJoinTable(doc, joinTable, linkElement);
			}
			else {
//				JoinColumnsInDbModel joinColumns = link.getJoinColumns();
				List<JoinColumn> joinColumns = link.getJoinColumns();
				//--- List of "Join Columns" for this link 
				addJoinColumns(doc, joinColumns, linkElement) ;
			}
			
			entityElement.appendChild(linkElement);
		}
	}
	
	private Element buildJoinColumnsElement(Document doc, String elementName, List<JoinColumn> joinColumns)  // v 3.0.0
	{
		log("addJoinColumns...");
		// Creates the XML element ( "joinColumns" or "inverseJoinColumns" )
		Element joinColumnsElement = doc.createElement(elementName); 
		for ( JoinColumn joinColumn : joinColumns ) {
			log("process joinColumn '" + joinColumn.getName() + "'");
			JoinColumnInDbModel joinColumnInDbModel = (JoinColumnInDbModel) joinColumn ; // v 3.0.0 
			Element joinColumnElement = Wrappers.JOIN_COLUMN_WRAPPER.getXmlDesc(joinColumnInDbModel, doc); // v 3.0.0 
			log( "Element : " + joinColumnElement );
			joinColumnsElement.appendChild(joinColumnElement);
		}
		return joinColumnsElement ;
	}
	
//	private void addJoinColumns(Document doc, JoinColumnsInDbModel joinColumns, Element linkElement) 
	private void addJoinColumns(Document doc, List<JoinColumn> joinColumns, Element linkElement)  // v 3.0.0
	{
		log("addJoinColumns...");
		if ( joinColumns != null ) {
			log("joinColumns is not null");
////			Element joinColumnsElement = RepositoryConst.JOIN_COLUMNS_WRAPPER.getXmlDesc(joinColumns, doc);
////			Element joinColumnsElement = Wrappers.JOIN_COLUMNS_WRAPPER.getXmlDesc(doc); // v 3.0.0
//			Element joinColumnsElement = doc.createElement(RepositoryConst.JOIN_COLUMNS_ELEMENT); // v 3.0.0
////			for ( JoinColumnInDbModel joinColumn : joinColumns ) 
//			for ( JoinColumn joinColumn : joinColumns ) // v 3.0.0
//			{
//				log("process joinColumn '" + joinColumn.getName() + "'");
//				JoinColumnInDbModel joinColumnInDbModel = (JoinColumnInDbModel) joinColumn ; // v 3.0.0 
////				Element joinColumnElement = RepositoryConst.JOIN_COLUMN_WRAPPER.getXmlDesc(joinColumn, doc);
//				Element joinColumnElement = Wrappers.JOIN_COLUMN_WRAPPER.getXmlDesc(joinColumnInDbModel, doc); // v 3.0.0 
//				log( "Element : " + joinColumnElement );
//				joinColumnsElement.appendChild(joinColumnElement);
//			}
			Element joinColumnsElement = buildJoinColumnsElement(doc, RepositoryConst.JOIN_COLUMNS_ELEMENT, joinColumns);
			linkElement.appendChild(joinColumnsElement);
		}
		else {
			log("joinColumns is null");
		}
	}
	
	private void addJoinTable(Document doc, JoinTableInDbModel joinTable, Element linkElement) 
	{
		//--- Add the "JoinTable" element
		Element joinTableElement = Wrappers.JOIN_TABLE_WRAPPER.getXmlDesc(joinTable, doc);
		
		//--- Add the "Join Columns" elements
//		JoinColumnsInDbModel joinColumns = joinTable.getJoinColumns();
		List<JoinColumn> joinColumns = joinTable.getJoinColumns(); // v 3.0.0
		if ( joinColumns != null ) {
////			Element joinColumnsElement = RepositoryConst.JOIN_COLUMNS_WRAPPER.getXmlDesc(joinColumns, doc);
//			Element joinColumnsElement = Wrappers.JOIN_COLUMNS_WRAPPER.getXmlDesc(doc); // v 3.0.0
////			for ( JoinColumnInDbModel joinColumn : joinColumns ) 
//			for ( JoinColumn joinColumn : joinColumns ) // v 3.0.0
//			{
//				JoinColumnInDbModel joinColumnInDbModel = (JoinColumnInDbModel) joinColumn ; // v 3.0.0 
////				Element joinColumnElement = RepositoryConst.JOIN_COLUMN_WRAPPER.getXmlDesc(joinColumn, doc);
//				Element joinColumnElement = Wrappers.JOIN_COLUMN_WRAPPER.getXmlDesc(joinColumnInDbModel, doc); // v 3.0.0 
//				joinColumnsElement.appendChild(joinColumnElement);
//			}
			Element joinColumnsElement = buildJoinColumnsElement(doc, RepositoryConst.JOIN_COLUMNS_ELEMENT, joinColumns);
			joinTableElement.appendChild(joinColumnsElement);
		}

		//--- Add the "Inverse Join Columns" elements
//		InverseJoinColumnsInDbModel inverseJoinColumns = joinTable.getInverseJoinColumns();
		List<JoinColumn> inverseJoinColumns = joinTable.getInverseJoinColumns(); // v 3.0.0
		if ( inverseJoinColumns != null ) {
////			Element inverseJoinColumnsElement = RepositoryConst.INVERSE_JOIN_COLUMNS_WRAPPER.getXmlDesc(inverseJoinColumns, doc);
//			Element inverseJoinColumnsElement = Wrappers.INVERSE_JOIN_COLUMNS_WRAPPER.getXmlDesc(doc); // v 3.0.0
////			for ( JoinColumnInDbModel joinColumn : inverseJoinColumns ) 
//			for ( JoinColumn joinColumn : inverseJoinColumns ) 
//			{
//				JoinColumnInDbModel joinColumnInDbModel = (JoinColumnInDbModel) joinColumn ; // v 3.0.0 
////				Element joinColumnElement = RepositoryConst.JOIN_COLUMN_WRAPPER.getXmlDesc(joinColumn, doc);
//				Element joinColumnElement = Wrappers.JOIN_COLUMN_WRAPPER.getXmlDesc(joinColumnInDbModel, doc); // v 3.0.0
//				inverseJoinColumnsElement.appendChild(joinColumnElement);
//			}
			Element inverseJoinColumnsElement = buildJoinColumnsElement(doc, RepositoryConst.INVERSE_JOIN_COLUMNS_ELEMENT, inverseJoinColumns);
			joinTableElement.appendChild(inverseJoinColumnsElement);
		}

		linkElement.appendChild(joinTableElement);
	}
	
	private void addForeignKeys(Document doc, EntityInDbModel entity, Element table) {
		// --- Foreign Keys
//		Collection<ForeignKeyInDbModel> foreignKeys = entity.getForeignKeysCollection();
		ForeignKeyInDbModel[] foreignKeys = entity.getForeignKeys();
		for ( ForeignKeyInDbModel foreignKey : foreignKeys ) {
			final Element fkElement = Wrappers.FOREIGNKEY_WRAPPER.getXmlDesc(foreignKey, doc);

			addForeignKeyColumns(doc, foreignKey, fkElement);

			table.appendChild(fkElement);
		}
	}

	private void addForeignKeyColumns(Document doc, ForeignKeyInDbModel foreignKey, final Element fkElement) {
		// --- Foreign Key Columns
		ForeignKeyColumnInDbModel[] foreignKeyColumns = foreignKey.getForeignKeyColumns();
		for (int i = 0; i < foreignKeyColumns.length; i++) {
			ForeignKeyColumnInDbModel foreignKeyColumn = foreignKeyColumns[i];
			Element fkColElement = Wrappers.FOREIGNKEY_COLUMN_WRAPPER.getXmlDesc(foreignKeyColumn, doc);
			fkElement.appendChild(fkColElement);
		}
	}

	private void addColumns(Document doc, EntityInDbModel entity, Element parentElement) {
		// --- Columns/attributes
		for ( AttributeInDbModel attributeInDbModel : entity.getAttributesArray() ) {
			final Element attributeElement = Wrappers.ATTRIBUTE_WRAPPER.getXmlElement(attributeInDbModel, doc);

			if (attributeInDbModel.getGeneratedValue() != null) {
				final Element gv = Wrappers.GENERATED_VALUE_WRAPPER.getXmlDesc(attributeInDbModel.getGeneratedValue(), doc);
				attributeElement.appendChild(gv);
			}
			if (attributeInDbModel.getSequenceGenerator() != null) {
				final Element sg = Wrappers.SEQUENCE_GENERATOR_WRAPPER.getXmlDesc(attributeInDbModel.getSequenceGenerator(), doc);
				attributeElement.appendChild(sg);
			}
			if (attributeInDbModel.getTableGenerator() != null) {
				final Element tg = Wrappers.TABLE_GENERATOR_WRAPPER.getXmlDesc(attributeInDbModel.getTableGenerator(), doc);
				attributeElement.appendChild(tg);
			}
			parentElement.appendChild(attributeElement);
		}
	}
}
