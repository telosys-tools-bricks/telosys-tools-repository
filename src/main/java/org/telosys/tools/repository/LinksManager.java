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
package org.telosys.tools.repository;

import java.util.LinkedList;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.repository.changelog.ChangeLog;
import org.telosys.tools.repository.changelog.ChangeOnEntity;
import org.telosys.tools.repository.changelog.ChangeOnForeignKey;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.ForeignKeyColumnInDbModel;
import org.telosys.tools.repository.model.ForeignKeyInDbModel;
import org.telosys.tools.repository.model.JoinColumnInDbModel;
import org.telosys.tools.repository.model.JoinTableInDbModel;
import org.telosys.tools.repository.model.LinkInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.telosys.tools.repository.rules.RepositoryRules;

/**
 * Links generator <br>
 * Generates inter-entity links from Foreign Keys and Join Tables 
 * 
 * @author Laurent Guerin
 */
public class LinksManager {

	private final RepositoryRules repositoryRules ;
	
	private final TelosysToolsLogger logger;

	/**
	 * Constructor
	 * @param repositoryRules
	 * @param logger
	 */
	public LinksManager(RepositoryRules repositoryRules, TelosysToolsLogger logger) {
		this.logger = logger;
		this.repositoryRules = 	repositoryRules ;
	}
	
	private void log(String msg) {
		if ( logger != null ) {
			logger.log("[LOG] " + this.getClass().getName() + " : " + msg);
		}
	}

	/**
	 * Generates all the links from all the Foreign Keys defined in the repository <br>
	 * Existing links (if any) are removed and re-generate
	 * 
	 * @param model the repository to update with generated links
	 * @return the number of links generated 
	 * @throws TelosysToolsException 
	 */
	public int generateAllLinks(RepositoryModel model) throws TelosysToolsException 
	{
		log("generateAllLinks()...");
		
		int count = 0 ;
		//Entity[] entities = model.getEntities();
		for ( EntityInDbModel entity : model.getEntitiesArraySortedByTableName() ) {
			count = count + createRelations(model, entity);
		}
		return count ;
	}
	
	/**
	 * Removes all the relations regarding the given entity
	 * @param model
	 * @param entity
	 * @return
	 * @throws TelosysToolsException
	 */
	private int removeRelations(RepositoryModel model, EntityInDbModel entity) throws TelosysToolsException 
	{
		log("removeRelations() : entity = " + entity);
		//--- Remove all the links using this entity 
//		return model.removeLinksByEntityName(entity.getName());
		return model.removeLinksByEntityName(entity.getDatabaseTable());
	}
	
	/**
	 * Removes the relation (2 links) based on the given Foreign Key
	 * @param model
	 * @param foreignKey
	 * @return
	 * @throws TelosysToolsException
	 */
	private int removeRelation(RepositoryModel model, ForeignKeyInDbModel foreignKey) throws TelosysToolsException 
	{
		log("removeRelations() : foreignKey = " + foreignKey);
		//--- Remove all the links using this Foreign Key 
		return model.removeLinksByForeignKey(foreignKey);
	}
	
	/**
	 * Generate all the relations ( owning side and inverse side links ) <br>
	 * for the given entity using its foreign keys (if any) <br>
	 * The entity can be a 'standard entity' or a 'join table entity'
	 * @param model
	 * @param entity
	 * @return the number of links generated 
	 * @throws TelosysToolsException
	 */
	private int createRelations(RepositoryModel model, EntityInDbModel entity) throws TelosysToolsException 
	{
		log("createRelations() : entity = " + entity);
		int count = 0 ;
		
		if ( entity.isJoinTable() )
		{
			log("createRelations() : entity is a Join Table ");
			//--- This entity can be considered as a "Join Table" ( all columns are Foreign Keys )
//			ForeignKey[] foreignKeys = entity.getForeignKeys() ;
//			if ( foreignKeys.length == 2 ) 
//			{
//				//--- Generate a bidirectional "ManyToMany" relation for this "Join Table"
//				ForeignKey owningSideForeignKey  = foreignKeys[0]; // Arbitrary choice
//				ForeignKey inverseSideForeignKey = foreignKeys[1]; // Arbitrary choice
//				count = count + createRelationManyToMany( model, entity, owningSideForeignKey, inverseSideForeignKey); 				
//			}
//			// else ( a join table with more than 2 FK ) : do nothing !
			count = count + createRelationManyToMany( model, entity);
		}
		else
		{
			log("createRelations() : entity is standard entity (not a Join Table) ");
			//--- Generate one relation ( 2 links ) for each FK 
			for ( ForeignKeyInDbModel fk : entity.getForeignKeys() ) {
				count = count + createRelationManyToOne(model, entity, fk);
			}
		}
		return count ;
	}

//	/**
//	 * Returns the Java attribute name for the given type <br>
//	 * Returns the same string but with the 1st char in Lower Case 
//	 * ie  "Book" --> "book"
//	 * @param javaType
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	private String getAttributeName(String javaType) throws TelosysToolsException 
//	{
//		String end = javaType.substring(1);
//		char c = javaType.charAt(0);
//		String firstChar = Character.toString(c);
//		return firstChar.toLowerCase() + end ;
//	}
	
//	/**
//	 * Returns the Java attribute name for a collection of the given type <br>
//	 * ie  "Book" --> "listOfBook"
//	 * @param javaType
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	private String getCollectionAttributeName(String javaType) throws TelosysToolsException {
//		return "listOf" + javaType ;
//	}

//	public String getBasicLinkId(RepositoryModel model, Entity entity, ForeignKey fk, boolean owningSide) throws TelosysToolsException {
//		String end = owningSide ? "O" : "I" ;
//		return "LINK_FK_" + fk.getName() + "_" + end ;
//	}
	
	//----------------------------------------------------------------------------------------------------
	// RELATION "* --> 1" ( "ManyToOne" and "OneToMany" links )
	//----------------------------------------------------------------------------------------------------
	/**
	 * Creates a "ManyToOne" relation (2 links) based on the the given Foreign Key
	 * @param model
	 * @param owningSideEntity : the owning side entity
	 * @param owningSideForeignKey : the Foreign Key that defines the relation 
	 * @return the number of links generated (always 2)
	 * @throws TelosysToolsException
	 */
	private int createRelationManyToOne(RepositoryModel model, EntityInDbModel owningSideEntity, ForeignKeyInDbModel owningSideForeignKey) throws TelosysToolsException 
	{
		log("createRelationManyToOne() : Owning Side FK = " + owningSideForeignKey);

//		EntityInDbModel inverseSideEntity = model.getEntityByName( owningSideForeignKey.getTableRef() );
		EntityInDbModel inverseSideEntity = model.getEntityByTableName( owningSideForeignKey.getReferencedTableName() ); // v 3.0.0
		if ( null == inverseSideEntity ) {
			throw new TelosysToolsException("No referenced table for Foreign Key '" + owningSideForeignKey.getName() + "'");
		}
		log("createRelationManyToOne() : Inverse Side Entity = " + inverseSideEntity);
		
		//--- Build the 2 link id
		String owningSideLinkId  = LinkInDbModel.buildId(owningSideForeignKey, true) ;
		String inverseSideLinkId = LinkInDbModel.buildId(owningSideForeignKey, false) ;

//		String originAttributeName = getAttributeName( inverseSideEntity.getBeanJavaClass() ) ;
		
		//--- Remove the links if they are already in the model
		model.removeLinkById(inverseSideLinkId);
		model.removeLinkById(owningSideLinkId);

		//--- Generates the 2 links 
		LinkInDbModel owningSideLink = generateManyToOneLinkOwningSide( owningSideLinkId, owningSideEntity, inverseSideEntity, owningSideForeignKey);
		generateManyToOneLinkInverseSide( inverseSideLinkId, owningSideEntity, inverseSideEntity, owningSideForeignKey, owningSideLink);
		
		return 2 ; // 2 links generated
	}
	
	/**
	 * Generates the owning side link of a "ManyToOne" relation 
	 * @param linkId
	 * @param owningSideEntity
	 * @param inverseSideEntity
	 * @param owningSideForeignKey
	 * @return
	 * @throws TelosysToolsException
	 */
	private LinkInDbModel generateManyToOneLinkOwningSide( String linkId, EntityInDbModel owningSideEntity, EntityInDbModel inverseSideEntity, 
			ForeignKeyInDbModel owningSideForeignKey  ) throws TelosysToolsException 
	{
		log("generateManyToOneLinkOwningSide() : linkId = " + linkId + " "
//				+ owningSideEntity.getName() + " --> " + inverseSideEntity.getName() );
				+ owningSideEntity.getDatabaseTable() + " --> " + inverseSideEntity.getDatabaseTable() );
		
		LinkInDbModel link = new LinkInDbModel();
		link.setId(linkId);
		link.setForeignKeyName( owningSideForeignKey.getName() );
		
		link.setOwningSide(true); // Owning Side
//		link.setInverseSideOf("");
		link.setInverseSideLinkId(""); // v 3.0.0
//		link.setCardinality(RepositoryConst.MAPPING_MANY_TO_ONE);
		link.setCardinality(Cardinality.MANY_TO_ONE); // v 3.0.0
//		link.setFetch(RepositoryConst.FETCH_DEFAULT);
		link.setFetchType(FetchType.DEFAULT); // v 3.0.0
		link.setSourceTableName(owningSideForeignKey.getTableName());
//		link.setTargetTableName(owningSideForeignKey.getTableRef());
		link.setTargetTableName(owningSideForeignKey.getReferencedTableName());
		
		//--- Define the "Join Columns"
		LinkedList<JoinColumnInDbModel> joinColumns = buildJoinColumns(owningSideForeignKey);
//		link.setJoinColumns( new JoinColumnsInDbModel(joinColumns) );
		link.setJoinColumns(joinColumns); // v 3.0.0
		
//		link.setTargetEntityJavaType( inverseSideEntity.getBeanJavaClass() ); // ie "Book" 
		link.setTargetEntityClassName( inverseSideEntity.getClassName() ); // ie "Book" // v 3.0.0
//		link.setJavaFieldType( inverseSideEntity.getBeanJavaClass() ); // ie "Book" 
		link.setFieldType( inverseSideEntity.getClassName() ); // ie "Book" // v 3.0.0
		//--- Updated in ver 2.1.1 (the link manages multiple references to the same inverse-side entity)
		//link.setJavaFieldName( getAttributeName( inverseSideEntity.getBeanJavaClass() ) ); // ie "book"
		link.setFieldName( repositoryRules.getAttributeNameForLinkToOne(owningSideEntity, inverseSideEntity) ) ; // #LGU v 2.1.1


		//--- Store the link in the entity
		owningSideEntity.storeLink(link);
		return link;		
	}
	
	/**
	 * Generates the inverse side link of a "ManyToOne" relation ( generates a "OneToMany" link with a collection )
	 * @param linkId
	 * @param owningSideEntity
	 * @param inverseSideEntity
	 * @param owningSideForeignKey
	 * @param owningSideLink
	 * @return
	 * @throws TelosysToolsException
	 */
	private LinkInDbModel generateManyToOneLinkInverseSide(String linkId, EntityInDbModel owningSideEntity, EntityInDbModel inverseSideEntity, 
			ForeignKeyInDbModel owningSideForeignKey, LinkInDbModel owningSideLink ) throws TelosysToolsException 
	{
		LinkInDbModel link = new LinkInDbModel();
		link.setId(linkId);
		link.setForeignKeyName( owningSideForeignKey.getName() );

		link.setOwningSide(false); // Inverse Side
//		link.setInverseSideOf(owningSideLink.getId());
		link.setInverseSideLinkId(owningSideLink.getId()); // v 3.0.0
		
		//--- Inverse side => No "Join Table", No "Join Columns", No "Inverse Join Columns"

		//--- Inverse side => "Mapped By"
//		link.setMappedBy( owningSideLink.getJavaFieldName() );
		link.setMappedBy( owningSideLink.getFieldName() ); // v 3.0.0

//		link.setCardinality(RepositoryConst.MAPPING_ONE_TO_MANY);
		link.setCardinality(Cardinality.ONE_TO_MANY); // v 3.0.0
//		link.setFetch(RepositoryConst.FETCH_DEFAULT);
		link.setFetchType(FetchType.DEFAULT); // v 3.0.0
//		link.setSourceTableName(owningSideForeignKey.getTableRef());
		link.setSourceTableName(owningSideForeignKey.getReferencedTableName()); // v 3.0.0
		link.setTargetTableName(owningSideForeignKey.getTableName());

//		link.setJavaFieldType(RepositoryConst.COLLECTION_JAVA_TYPE); // ie "java.util.List"
		link.setFieldType(RepositoryConst.COLLECTION_JAVA_TYPE); // ie "java.util.List" // v 3.0.0
		
//		link.setJavaFieldName( repositoryRules.getAttributeNameForLinkToMany(inverseSideEntity, owningSideEntity ) ) ; // #LGU v 2.1.1
		link.setFieldName( repositoryRules.getAttributeNameForLinkToMany(inverseSideEntity, owningSideEntity ) ) ; // v 3.0.0
		
//		link.setTargetEntityJavaType( owningSideEntity.getBeanJavaClass() ); // ie "Book"
		link.setTargetEntityClassName( owningSideEntity.getClassName() ); // ie "Book" // v 3.0.0

		//--- Store the link in the entity
		inverseSideEntity.storeLink(link);
		return link;		
	}

	//----------------------------------------------------------------------------------------------------
	// RELATION "* --> *"  ( 2 "ManyToMany" links )
	//----------------------------------------------------------------------------------------------------
	/**
	 * Creates a "Many To Many" relation (2 links) based on the given "Join Table" entity <br>
	 * The 2 sides links will be generated 
	 * @param model
	 * @param joinTableEntity
	 * @return
	 * @throws TelosysToolsException
	 */
	private int createRelationManyToMany(RepositoryModel model, EntityInDbModel joinTableEntity) throws TelosysToolsException 
	{
		log("createRelationManyToMany()...");
		int count = 0 ;
		//--- This entity can be considered as a "Join Table" ( all columns are Foreign Keys )
		ForeignKeyInDbModel[] foreignKeys = joinTableEntity.getForeignKeys() ;
		if ( foreignKeys.length == 2 ) {
			//--- Generate a bidirectional "ManyToMany" relation for this "Join Table"
			ForeignKeyInDbModel owningSideForeignKey  = foreignKeys[0]; // Arbitrary choice
			ForeignKeyInDbModel inverseSideForeignKey = foreignKeys[1]; // Arbitrary choice
			count = createRelationManyToMany( model, joinTableEntity, owningSideForeignKey, inverseSideForeignKey); 				
		}
		else {
//			throw new TelosysToolsException("Entity '" + joinTableEntity.getName() 
			throw new TelosysToolsException("Entity '" + joinTableEntity.getDatabaseTable()
					+ "' (Join Table) has " + foreignKeys.length + " Foreign Key(s) (2 FK expected)") ;
		}
		return count ;
	}
	
	/**
	 * Creates a "Many To Many" relation (2 links) based on the given "Join Table" entity <br>
	 * The 2 sides links will be generated 
	 * @param model
	 * @param joinTable
	 * @param owningSideForeignKey
	 * @param inverseSideForeignKey
	 * @return the number of links generated (always 2)
	 * @throws TelosysToolsException
	 */
	private int createRelationManyToMany(RepositoryModel model, EntityInDbModel joinTable, 
			ForeignKeyInDbModel owningSideForeignKey, ForeignKeyInDbModel inverseSideForeignKey) throws TelosysToolsException 
	{
		log("createRelationManyToMany()...");

		//--- Build the 2 id
		String owningSideId  = LinkInDbModel.buildId(joinTable, true) ;
		String inverseSideId = LinkInDbModel.buildId(joinTable, false) ;
		
		//--- Remove the links if they are already in the model
		model.removeLinkById(inverseSideId);
		model.removeLinkById(owningSideId);

		//--- One entity is referenced by one of the two foreign keys
//		EntityInDbModel owningSideEntity  = model.getEntityByName( owningSideForeignKey.getTableRef() );		
		EntityInDbModel owningSideEntity  = model.getEntityByTableName( owningSideForeignKey.getReferencedTableName() );	// v 3.0.0	
		//--- The other entity is referenced by the other foreign key
//		EntityInDbModel inverseSideEntity = model.getEntityByName( inverseSideForeignKey.getTableRef() );
		EntityInDbModel inverseSideEntity = model.getEntityByTableName( inverseSideForeignKey.getReferencedTableName() ); // v 3.0.0	
		
		//--- Generates the 2 links 
		LinkInDbModel owningSideLink = generateManyToManyLinkOwningSide( owningSideId, 
				owningSideEntity, inverseSideEntity, 
				joinTable, 
				owningSideForeignKey, inverseSideForeignKey );
		
		generateManyToManyLinkInverseSide( inverseSideId, 
				owningSideEntity, inverseSideEntity, 
				joinTable, 
				owningSideForeignKey, inverseSideForeignKey, 
				owningSideLink );
		
		return 2 ; // 2 links generated
	}
	
	/**
	 * Generates the owning side of the "ManyToMany" relation 
	 * @param linkId
	 * @param owningSideEntity
	 * @param inverseSideEntity
	 * @param joinTableEntity
	 * @param owningSideForeignKey
	 * @param inverseSideForeignKey
	 * @return
	 * @throws TelosysToolsException
	 */
	private LinkInDbModel generateManyToManyLinkOwningSide( String linkId, EntityInDbModel owningSideEntity, EntityInDbModel inverseSideEntity, 
			EntityInDbModel joinTableEntity, ForeignKeyInDbModel owningSideForeignKey, ForeignKeyInDbModel inverseSideForeignKey
			 ) throws TelosysToolsException 
	{		
		LinkInDbModel link = new LinkInDbModel();
		link.setId(linkId);
//		link.setJoinTableName( joinTableEntity.getName() );
		link.setJoinTableName( joinTableEntity.getDatabaseTable() ); // v 3.0.0

		link.setOwningSide(true); // Owning Side
//		link.setInverseSideOf("");
		link.setInverseSideLinkId(""); // v 3.0.0
//		link.setCardinality(RepositoryConst.MAPPING_MANY_TO_MANY);
		link.setCardinality(Cardinality.MANY_TO_MANY); // v 3.0.0
//		link.setFetch(RepositoryConst.FETCH_DEFAULT);
		link.setFetchType(FetchType.DEFAULT); // v 3.0.0
		
		//--- Define the "Join Table"
		JoinTableInDbModel joinTable = new JoinTableInDbModel();
//		joinTable.setName( joinTableEntity.getName() );
		joinTable.setName( joinTableEntity.getDatabaseTable() );
//		joinTable.setSchema( joinTableEntity.getSchema() );
		joinTable.setSchema( joinTableEntity.getDatabaseSchema() );
//		joinTable.setCatalog( joinTableEntity.getCatalog() );
		joinTable.setCatalog( joinTableEntity.getDatabaseCatalog() );
		link.setJoinTable(joinTable);
		
		//--- Define the "Join Columns" of the "Join Table"
		LinkedList<JoinColumnInDbModel> joinColumns = buildJoinColumns(owningSideForeignKey);
//		joinTable.setJoinColumns( new JoinColumnsInDbModel(joinColumns) );
		joinTable.setJoinColumns( joinColumns ); // v 3.0.0
		
		//--- Define the "Inverse Join Columns" of the "Join Table"
		LinkedList<JoinColumnInDbModel> inverseJoinColumns = buildJoinColumns(inverseSideForeignKey);
//		joinTable.setInverseJoinColumns( new InverseJoinColumnsInDbModel(inverseJoinColumns) );
		joinTable.setInverseJoinColumns( inverseJoinColumns ); // v 3.0.0
		
		
//		link.setSourceTableName(owningSideForeignKey.getTableRef());
		link.setSourceTableName(owningSideForeignKey.getReferencedTableName()); // v 3.0.0
//		link.setTargetTableName(inverseSideForeignKey.getTableRef());
		link.setTargetTableName(inverseSideForeignKey.getReferencedTableName()); // v 3.0.0
		
		//--- Java attribute for this link
//		link.setJavaFieldType( RepositoryConst.COLLECTION_JAVA_TYPE ); // ie "java.util.List"
		link.setFieldType( RepositoryConst.COLLECTION_JAVA_TYPE ); // ie "java.util.List" // v 3.0.0
//		link.setJavaFieldName( repositoryRules.getAttributeNameForLinkToMany(owningSideEntity, inverseSideEntity) ) ; // #LGU v 2.1.1
		link.setFieldName( repositoryRules.getAttributeNameForLinkToMany(owningSideEntity, inverseSideEntity) ) ; // v 3.0.0
		
//		link.setTargetEntityJavaType( inverseSideEntity.getBeanJavaClass() ); // ie "Book"
		link.setTargetEntityClassName( inverseSideEntity.getClassName() ); // ie "Book" // v 3.0.0

		//--- Store the link in the entity
		owningSideEntity.storeLink(link);
		return link;		
	}
	
	/**
	 * Generates the inverse side of the "ManyToMany" relation 
	 * @param linkId
	 * @param owningSideEntity
	 * @param inverseSideEntity
	 * @param joinTableEntity
	 * @param owningSideForeignKey
	 * @param inverseSideForeignKey
	 * @param owningSideLink
	 * @return
	 * @throws TelosysToolsException
	 */
	private LinkInDbModel generateManyToManyLinkInverseSide( String linkId, EntityInDbModel owningSideEntity, EntityInDbModel inverseSideEntity, 
			EntityInDbModel joinTableEntity, ForeignKeyInDbModel owningSideForeignKey, ForeignKeyInDbModel inverseSideForeignKey, 
			 LinkInDbModel owningSideLink ) throws TelosysToolsException 
	{		
		LinkInDbModel link = new LinkInDbModel();
		link.setId(linkId);
//		link.setJoinTableName( joinTableEntity.getName() );
		link.setJoinTableName( joinTableEntity.getDatabaseTable() );
		
		link.setOwningSide(false); // Owning Side
//		link.setInverseSideOf( owningSideLink.getId() );
		link.setInverseSideLinkId( owningSideLink.getId() ); // v 3.0.0
//		link.setCardinality(RepositoryConst.MAPPING_MANY_TO_MANY);
		link.setCardinality(Cardinality.MANY_TO_MANY); // v 3.0.0
//		link.setFetch(RepositoryConst.FETCH_DEFAULT);
		link.setFetchType(FetchType.DEFAULT); // v 3.0.0
		
		//--- Inverse side => No "Join Table", No "Join Columns", No "Inverse Join Columns"

		//--- Inverse side => "Mapped By"
//		link.setMappedBy( owningSideLink.getJavaFieldName() );
		link.setMappedBy( owningSideLink.getFieldName() ); // v 3.0.0
		
//		link.setSourceTableName(inverseSideForeignKey.getTableRef());
		link.setSourceTableName(inverseSideForeignKey.getReferencedTableName()); // v 3.0.0
//		link.setTargetTableName(owningSideForeignKey.getTableRef()); 
		link.setTargetTableName(owningSideForeignKey.getReferencedTableName());  // v 3.0.0
		
		//--- Java attribute for this link
//		link.setJavaFieldType( RepositoryConst.COLLECTION_JAVA_TYPE ); // ie "java.util.List"
		link.setFieldType( RepositoryConst.COLLECTION_JAVA_TYPE ); // ie "java.util.List"  // v 3.0.0
//		link.setJavaFieldName( repositoryRules.getAttributeNameForLinkToMany(inverseSideEntity, owningSideEntity ) ) ; // #LGU v 2.1.1
		link.setFieldName( repositoryRules.getAttributeNameForLinkToMany(inverseSideEntity, owningSideEntity ) ) ; // v 3.0.0

//		link.setTargetEntityJavaType( owningSideEntity.getBeanJavaClass() ); // ie "Book"
		link.setTargetEntityClassName( owningSideEntity.getClassName() ); // ie "Book" // v 3.0.0

		//--- Store the link in the entity
		inverseSideEntity.storeLink(link);
		return link;		
	}
	
	private LinkedList<JoinColumnInDbModel>  buildJoinColumns( ForeignKeyInDbModel foreignKey ) throws TelosysToolsException 
	{
		LinkedList<JoinColumnInDbModel> joinColumns = new LinkedList<JoinColumnInDbModel>();
		//JoinColumns joinColumns = new JoinColumns();
		
		ForeignKeyColumnInDbModel[] fkColumns = foreignKey.getForeignKeyColumns();
		
		for ( ForeignKeyColumnInDbModel fkColumn : fkColumns ) {
			JoinColumnInDbModel joinColumn = new JoinColumnInDbModel();
			joinColumn.setName(fkColumn.getColumnName());
//			joinColumn.setReferencedColumnName(fkColumn.getColumnRef());
			joinColumn.setReferencedColumnName(fkColumn.getReferencedColumnName()); // v 3.0.0
			// TODO ???
//			joinColumn.setNullable(xx); 
//			joinColumn.setInsertable(xxx);
//			joinColumn.setUpdatable(xx); 
//			joinColumn.setUnique(xxx); 
			
			joinColumns.add(joinColumn);
		}
		return joinColumns ;
	}
	
	//-----------------------------------------------------------------------------------------
	// LINKS UPDATE
	//-----------------------------------------------------------------------------------------
	/**
	 * Updates the model's links according to the given change log
	 * @param model
	 * @param changeLog
	 * @return
	 * @throws TelosysToolsException
	 */
	public int updateLinks(RepositoryModel model, ChangeLog changeLog ) throws TelosysToolsException 
	{
		int count = 0 ;
		//--- For each entity change...
		for ( ChangeOnEntity change : changeLog.getChanges() ) {
			switch ( change.getChangeType() ) {
			case CREATED :
				//--- An entity as been created
				EntityInDbModel entityCreated = change.getEntityCreated() ;
				log("updateLinks() : entity CREATED = " + entityCreated);
				//--- Create all the links based on this entity (for a standard Table or a  Join Table )
				count = count + this.createRelations(model, entityCreated);
				break;
			case UPDATED :
				//--- An entity as been updated
				EntityInDbModel entityUpdated = change.getEntityAfter() ;
				log("updateLinks() : entity UPDATED = " + entityUpdated);
				count = count + this.updateEntityLinks(model, entityUpdated, change);
				break;
			case DELETED :
				//--- An entity as been deleted
				EntityInDbModel entityDeleted = change.getEntityDeleted() ;
				log("updateLinks() : entity DELETED = " + entityDeleted);
				//--- Remove all the links using this entity 
				count = count + this.removeRelations(model, entityDeleted);
				break;
			}
		}
		return count ;
	}
	
	/**
	 * Updates all the links for the given entity according with the given 'change'
	 * @param model
	 * @param entity
	 * @param change
	 * @return
	 * @throws TelosysToolsException
	 */
	private int updateEntityLinks(RepositoryModel model, EntityInDbModel entity, ChangeOnEntity change ) throws TelosysToolsException 
	{
		int count = 0 ;
		if ( entity.isJoinTable() ) {
			if ( change.getChangesOnForeignKey().size() > 0 ) {
				//--- Something has changed in the Foreign Keys
				//--- 1) Remove existing links
//				model.removeLinksByJoinTableName(entity.getName());
				model.removeLinksByJoinTableName(entity.getDatabaseTable());
				//--- 2) Create new links based on the new Foreign Keys
				count = count + createRelationManyToMany(model, entity);
			}
		}
		else {
			//--- For each Foreign Key change...
			for ( ChangeOnForeignKey fkChange : change.getChangesOnForeignKey() ) {
				switch ( fkChange.getChangeType() ) {
				case CREATED :
					//--- A Foreign Key as been created 
					//--- Generates the relation ( 2 links ) based on the created FK 
					count = count + this.createRelationManyToOne(model, entity, fkChange.getForeignKeyCreated() );
					break;
				case UPDATED :
					//--- A Foreign Key as been updated 
					// 1) remove the OLD links
					//count = count + model.removeLinksByForeignKey(fkChange.getForeignKeyBefore());
					count = count + this.removeRelation(model, fkChange.getForeignKeyBefore());
					// 2) create the NEW links
					count = count + this.createRelationManyToOne(model, entity, fkChange.getForeignKeyAfter() );
					break;
				case DELETED :
					//--- A Foreign Key as been deleted 
					// linksManager.deleteLinks(model,change.getEntityDeleted() );
					//count = count + this.removeManyToOneLinks(model, entity, foreignKeyDeleted );
					//--- Removes the relation ( 2 links ) based on the deleted foreign key
					//count = count + model.removeLinksByForeignKey(foreignKeyDeleted);
					count = count + this.removeRelation(model, fkChange.getForeignKeyDeleted());
					break;
				}
			}
		}
		return 0 ;
	}
	
//	private int removeManyToOneLinks(RepositoryModel model, Entity owningSideEntity, ForeignKey owningSideForeignKey) throws TelosysToolsException 
//	{
//		//log("removeManyToOneLinks()...");
//
//		Entity inverseSideEntity = model.getEntityByName( owningSideForeignKey.getTableRef() );
//		if ( null == inverseSideEntity ) {
//			return 0 ; // It's possible that the referenced table/entity has been removed
//		}
//		
//		//--- Build the 2 link id
//		String owningSideLinkId  = Link.buildId(owningSideForeignKey, true) ;
//		String inverseSideLinkId = Link.buildId(owningSideForeignKey, false) ;
//
//		//--- Remove the links if they are already in the model
//		model.removeLinkById(inverseSideLinkId);
//		model.removeLinkById(owningSideLinkId);
//		
//		return 2 ;
//	}
	
}
