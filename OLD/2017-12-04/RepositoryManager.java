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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.javatypes.JavaTypes;
import org.telosys.tools.commons.javatypes.JavaTypesManager;
import org.telosys.tools.commons.jdbc.ConnectionManager;
import org.telosys.tools.db.model.DatabaseColumn;
import org.telosys.tools.db.model.DatabaseForeignKey;
import org.telosys.tools.db.model.DatabaseForeignKeyColumn;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.generic.model.DateType;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.ForeignKeyColumnInDbModel;
import org.telosys.tools.repository.model.ForeignKeyInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.rules.RepositoryRules;

/**
 * Abstract repository manager ancestor<br>
 * Common functions for repository "generator" and "updator" <br>
 * 
 * @author Sylvain LEROY, Laurent GUERIN, Eric LEMELIN
 * 
 */

public abstract class RepositoryManager //extends StandardTool
{
	
	private final ConnectionManager     connectionManager ;
	private final RepositoryRules       repositoryRules ;
	
	protected final TelosysToolsLogger  logger ;
	protected TelosysToolsLogger getLogger() {
		return logger;
	}	
//	private void log(String s) {
//		if (logger != null ) {
//			logger.log(s);
//		}
//	}

	/**
	 * Constructor
	 * @param connectionManager
	 * @param repositoryRules
	 * @param logger
	 */
	protected RepositoryManager(ConnectionManager connectionManager, RepositoryRules repositoryRules, TelosysToolsLogger logger) 
	{
		//super(logger);
		this.connectionManager = connectionManager ;
		this.repositoryRules = repositoryRules ;
		this.logger = logger;
	}

	/**
	 * Returns a connection using the given DatabaseConfiguration <br>
	 * and the ConnectionManager initialized in the constructor
	 * 
	 * @param databaseConfiguration
	 * @return
	 * @throws TelosysToolsException
	 */
	protected Connection getConnection(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
		if ( this.connectionManager != null ) {
			return this.connectionManager.getConnection(databaseConfiguration) ;
		}
		else {
			throw new TelosysToolsException("Cannot get connection (no connection manager)");
		}
	}

	protected void closeConnection(Connection connection) throws TelosysToolsException {
		if ( connection != null ) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new TelosysToolsException("Cannot close connection (SQLException)", e);
			}
		}
	}

	
	/**
	 * Returns the RepositoryRules initialized in the constructor 
	 * @return
	 */
	protected RepositoryRules getRepositoryRules() {
		return this.repositoryRules ;
	}
	
	protected DatabaseMetaData getMetaData(Connection con) throws TelosysToolsException {
		DatabaseMetaData dbmd = null;
		try {
			dbmd = con.getMetaData();
		} catch (SQLException e) {
			logger.error("Cannot get Meta-Data");
			throw new TelosysToolsException("Cannot get Meta-Data", e);
		}
		return dbmd;
	}

	/**
	 * Creates an new entity from the given database table and adds it in the repository model
	 * @param repositoryModel
	 * @param dbTable
	 * @return the entity created
	 */
	protected EntityInDbModel addEntity(RepositoryModel repositoryModel, DatabaseTable dbTable)
	{
		logger.log("addEntity()...");

		//--- Create Entity from the Database TABLE
		EntityInDbModel entity = new EntityInDbModel();
//		entity.setName( dbTable.getTableName() );
		entity.setDatabaseTable( dbTable.getTableName() );

		//--- Get the VO Bean class name from the Table Name
//		String beanClassName = repositoryRules.getEntityClassName(entity.getName());
		String beanClassName = repositoryRules.getEntityClassName(entity.getDatabaseTable() );

//		entity.setBeanJavaClass(beanClassName);		
		entity.setClassName(beanClassName);		
//		entity.setCatalog ( dbTable.getCatalogName() ); 
		entity.setDatabaseCatalog( dbTable.getCatalogName() ); 
//		entity.setSchema  ( dbTable.getSchemaName() ); 
		entity.setDatabaseSchema( dbTable.getSchemaName() ); 
		entity.setDatabaseType( dbTable.getTableType() ) ; 
		
		//--- Add the columns of this table
		addColumns( entity, dbTable) ;
				
		//--- Add the Foreign Keys of this table
		addForeignKeyParts( entity, dbTable); 
		
		//--- Add the entity in the repository
		repositoryModel.storeEntity(entity);

		logger.log("addEntity() : entity " + dbTable.getTableName() + "stored");
		return entity ;
	}
	
	private void addColumns( EntityInDbModel entity, DatabaseTable dbTable) 
	{
		//--- For each column of the table ...
		for ( DatabaseColumn dbCol : dbTable.getColumns() ) {
			//--- Create a new column from the database model
			AttributeInDbModel column = buildColumn( entity, dbCol );
			
			//--- Add the "column" element in the XML tree
			entity.storeAttribute(column);
		}
	}
	
//    private String getAttributeDateType(String databaseColumnType, int jdbcColumnType )
//    {
//    	switch ( jdbcColumnType )
//    	{
//    		//--- Type of Date :
//    		case Types.DATE : 
//    			return AttributeInDbModel.SPECIAL_DATE_ONLY ; 
//    		case Types.TIME : 
//    			return AttributeInDbModel.SPECIAL_TIME_ONLY ; 
//    		case Types.TIMESTAMP : 
//    			return AttributeInDbModel.SPECIAL_DATE_AND_TIME ;
//    	}
//    	return null ;
//    }
    /**
     * Returns the "Date Type" for the given "JDBC type" 
     * @param jdbcColumnType
     * @return
     */
    private DateType getAttributeDateType(int jdbcColumnType ) // v 3.0.0
    {
    	switch ( jdbcColumnType )
    	{
    		//--- Type of Date :
    		case Types.DATE : 
    			return DateType.DATE_ONLY ;
    		case Types.TIME : 
    			return DateType.TIME_ONLY ;
    		case Types.TIMESTAMP : 
    			return DateType.DATE_AND_TIME ;
    	}
    	return DateType.UNDEFINED ;
    }
	
    private boolean isAttributeLongText (String databaseColumnType, int jdbcColumnType )
    {
		if (   jdbcColumnType == Types.LONGVARCHAR
			|| jdbcColumnType == Types.CLOB
			|| jdbcColumnType == Types.BLOB )
		{
			// Considered as a "Long Text"
			return true ; 
		}
    	return false ;
    }
		
	protected AttributeInDbModel buildColumn(EntityInDbModel entity, DatabaseColumn dbCol ) {	
		String dbColName     = dbCol.getColumnName(); //--- Column Name
		String dbTypeName    = dbCol.getDbTypeName(); //--- Column Type (original database type)
		int    iDbSize       = dbCol.getSize(); //--- Column Size (max nb of characters or decimal precision 
		int    iJdbcTypeCode = dbCol.getJdbcTypeCode(); //--- Column JDBC Type (cf "java.sql.Types" )
		String dbNotNull     = dbCol.getNotNullAsString(); //--- Column NOT NULL ( "true" or "false" )
		
		//--- Java field name and type
		String sAttributeName = "???";
		String sAttributeType = "???";
		boolean bAttributeLongText = false ;
		//String sAttributeDateType = null;
		
		try {
			sAttributeType = repositoryRules.getAttributeType(dbTypeName, iJdbcTypeCode, dbCol.isNotNull() );
			if (sAttributeType == null) {
				sAttributeType = "null";
			}

			sAttributeName = repositoryRules.getAttributeName(dbColName);
			if (sAttributeName == null) {
				sAttributeName = "null";
			}

			//--- Attribute LONG TEXT ? ( BLOB, CLOB, etc )
			bAttributeLongText = isAttributeLongText ( dbTypeName, iJdbcTypeCode );
			
//			//--- Attribute DATE TYPE ( Date Only, Time Only, Date and Type )
//			sAttributeDateType = getAttributeDateType(dbTypeName, iJdbcTypeCode);

		} catch (Throwable t) {
			logger.log("   ERROR : " + t.toString() + " - " + t.getMessage());
		}
		logger.log("   - Column : " + dbColName + " ( " + iJdbcTypeCode + " : " + dbTypeName + " ) ---> "
				+ sAttributeName + " ( " + sAttributeType + " ) ");

		//--- Create a new "column" for this "table/entity"
		AttributeInDbModel column = new AttributeInDbModel(entity);
		column.setDatabaseName(dbColName);
		column.setDatabaseTypeName(dbTypeName);
		column.setJdbcTypeCode(iJdbcTypeCode);
		column.setDatabaseNotNull(dbNotNull);
		column.setDatabaseSize(iDbSize);
		//column.setJavaName(sAttributeName);
		column.setName(sAttributeName); // v 3.0.0
		//column.setJavaType(sAttributeType);
		column.setModelFullType(sAttributeType); // v 3.0.0
		
		//--- Java default value for primitive types
		JavaTypes javaTypes = JavaTypesManager.getJavaTypes();
		String sDefaultValue = javaTypes.getDefaultValueForType(sAttributeType);
		if ( sDefaultValue != null ) {
			// Not null only for primitive types
			column.setDefaultValue(sDefaultValue);
		}
		
		if ( bAttributeLongText == true ) {
			column.setLongText( AttributeInDbModel.SPECIAL_LONG_TEXT_TRUE );
		}
		
//		if (sAttributeDateType != null) {
//			column.setDateType(sAttributeDateType);
//		}
		column.setDateType(getAttributeDateType(iJdbcTypeCode)); // V 3.0.0

		//--- Is this column in the Table Primary Key ?
		//column.setPrimaryKey( dbCol.isInPrimaryKey());
		column.setKeyElement( dbCol.isInPrimaryKey()); // v 3.0.0

		//--- Is this column a member of a Foreign Key ?
		// Removed in v 3.0.0 - now FK Type (Simple/Composite) is set later, after FK building  
		// column.setForeignKey( dbCol.getUsedInForeignKey() > 0 );

		//--- Is this column auto-incremented ?
		column.setAutoIncremented(dbCol.isAutoIncremented());
		column.setDatabasePosition( dbCol.getOrdinalPosition() ); // #LGU 10/08/2011
		column.setDatabaseDefaultValue( dbCol.getDefaultValue() ); // #LGU 10/08/2011
		column.setDatabaseComment( dbCol.getComment() ); // v 2.1.1 - #LCH 20/08/2014

		//--- Further information ( v 2.0.3 )
		column.setLabel(     repositoryRules.getAttributeGuiLabel(dbColName) );
		column.setInputType( repositoryRules.getAttributeGuiType(dbColName, iJdbcTypeCode));
		
		//--- Further information for Java Validator 
		if ( ! column.isJavaPrimitiveType() ) {
			if ( dbCol.isNotNull()  ) {
				//column.setJavaNotNull( true );
				column.setNotNull( true ); // v 3.0.0
				column.setNotEmpty(true);
			}
			if ( column.isJavaTypeString() ) {
				//column.setMaxLength(""+iDbSize);
				column.setMaxLength(iDbSize); // v 3.0.0
			}
		}
		return column ;
	}
	
	protected ForeignKeyInDbModel buildForeignKey( DatabaseForeignKey dbFK ) 
	{
		ForeignKeyInDbModel foreignKey = new ForeignKeyInDbModel();
		foreignKey.setName( dbFK.getForeignKeyName() ); // the name must be set before 'storeForeignKey'
		
		for ( DatabaseForeignKeyColumn dbFkCol : dbFK.getForeignKeyColumns() ) {
			ForeignKeyColumnInDbModel foreignKeyColumn = new ForeignKeyColumnInDbModel();
			
			foreignKeyColumn.setSequence( dbFkCol.getFkSequence() );
			
			foreignKeyColumn.setTableName( dbFkCol.getFkTableName() );
			foreignKeyColumn.setColumnName(dbFkCol.getFkColumnName() );
			
			foreignKeyColumn.setTableRef( dbFkCol.getPkTableName() );
//			foreignKeyColumn.setColumnRef( dbFkCol.getPkColumnName() );
			foreignKeyColumn.setReferencedColumnName( dbFkCol.getPkColumnName() ); // v 3.0.0
			
			foreignKeyColumn.setUpdateRuleCode( dbFkCol.getUpdateRule() );
			foreignKeyColumn.setDeleteRuleCode( dbFkCol.getDeleteRule() );
			foreignKeyColumn.setDeferrableCode( dbFkCol.getDeferrability() );
			
			foreignKey.storeForeignKeyColumn(foreignKeyColumn);
		}
		return foreignKey ;
	}
	
//	private final static int FK_SIMPLE    = 1 ;
//	private final static int FK_COMPOSITE = 2 ;
	
	private void addForeignKeyParts( EntityInDbModel entity, DatabaseTable dbTable) {
		//--- For each foreign key of the table ...
		for ( DatabaseForeignKey dbFK : dbTable.getForeignKeys() ) {
			// Build the FK instance
			ForeignKeyInDbModel fk = buildForeignKey( dbFK ) ;
			// Attach the FK to the entity
			entity.storeForeignKey(fk);
			
			// Set FK type for each attribute involved in a FK  
			// Now done globally in "After Load Processing"
			// setAttributesFKType(entity, fk); // Added in v 3.0.0
		}
	}
	
//	/**
//	 * Set the FK type for each attribute involved in the given FK
//	 * @param entity
//	 * @param fk
//	 * @since v 3.0.0
//	 */
//	private void setAttributesFKType(EntityInDbModel entity, ForeignKeyInDbModel fk) {
//		if ( fk.isComposite() ) {
//			// Composite FK ( many columns )
//			for ( ForeignKeyColumn fkCol : fk.getColumns() ) {
//				setAttributeFKType(entity, fkCol, FK_COMPOSITE) ;
//			}
//		}
//		else {
//			// Simple FK ( only one column )
//			if ( fk.getColumns().size() == 1 ) {
//				ForeignKeyColumn fkCol = fk.getColumns().get(0);
//				setAttributeFKType(entity, fkCol, FK_SIMPLE) ;
//			}
//			else {
//				throw new IllegalStateException("FK has " + fk.getColumns().size() + " part(s) (1 expected)");
//			}
//		}
//	}
//	
//	/**
//	 * Set the FK Type for the attribute associated with the given FK Column
//	 * @param entity
//	 * @param fkCol
//	 * @param fkType
//	 * @since v 3.0.0
//	 */
//	private void setAttributeFKType(EntityInDbModel entity, ForeignKeyColumn fkCol, int fkType ) {
//		String fkColName = fkCol.getColumnName();
//		AttributeInDbModel attribute = entity.getAttributeByColumnName(fkColName);
//		if ( attribute != null ) {
//			if ( fkType == FK_SIMPLE ) {
//				attribute.setFKSimple(true);
//			}
//			else if ( fkType == FK_COMPOSITE ) {
//				attribute.setFKComposite(true);
//			}
//		}
//		else {
//			throw new IllegalStateException("Cannot get attribute by column name'" + fkColName + "'");
//		}
//	}
}
