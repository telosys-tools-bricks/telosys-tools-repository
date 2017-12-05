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
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.ObjectUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DbConnectionManager;
import org.telosys.tools.db.model.DatabaseColumn;
import org.telosys.tools.db.model.DatabaseForeignKey;
import org.telosys.tools.db.model.DatabaseModelManager;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.repository.changelog.ChangeLog;
import org.telosys.tools.repository.changelog.ChangeOnColumn;
import org.telosys.tools.repository.changelog.ChangeOnEntity;
import org.telosys.tools.repository.changelog.ChangeOnForeignKey;
import org.telosys.tools.repository.changelog.ChangeType;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.ForeignKeyInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * Service that manages the repository model updates <br>
 * Detects the database changes and updates the repository model <br>
 * including the links since v 2.1.1
 * 
 * @author Laurent GUERIN, Eric LEMELIN
 * 
 */

public class DbModelUpdator extends DbModelManager
{
	private final UpdateLogWriter updateLogger ;

	/**
	 * Constructor
	 * @param dbConnectionManager
	 * @param logger
	 * @param updateLogger
	 */
	public DbModelUpdator(DbConnectionManager dbConnectionManager, TelosysToolsLogger logger, UpdateLogWriter updateLogger) 
	{
		super(dbConnectionManager, logger);
		this.updateLogger = updateLogger;
	}

	/**
	 * Adds a new attribute in the given entity
	 * @param entity
	 * @param dbColumn
	 * @return
	 */
	private AttributeInDbModel addEntityAttribute(EntityInDbModel entity, DatabaseColumn dbColumn ) 	{
		AttributeInDbModel column = buildColumn( entity, dbColumn ) ;
		
		//--- Add the "column" to the "entity"
		entity.storeAttribute(column);
		return column;
	}

	/**
	 * Updates the given column from the database column
	 * @param column
	 * @param dbColumn
	 * @return the number of updates done
	 */
	private int updateEntityAttribute(AttributeInDbModel column, DatabaseColumn dbColumn) {
		int r = 0;

		//--- Update the column 
		r = r + updateDbType(column,   dbColumn.getDbTypeName()); // Database native type		
		r = r + updateTypeCode(column, dbColumn.getJdbcTypeCode()); // JDBC type code 
		r = r + updateNotNull(column,  dbColumn.getNotNullAsString()); // Not null
		r = r + updateSize(column,     dbColumn.getSize()); // Size
		r = r + updateComment(column,  dbColumn.getComment()); // Database comment - v 2.1.1 #LCH 

		//--- If this column is in the Table Primary Key
		r = r + updatePrimaryKey(column, dbColumn.isInPrimaryKey()); // Column in Primary Key
		
		// other updates (in the future ?)
		// . default value
		// . auto incremented
		// . in foreign key

		return r;
	}

	private int updateTypeCode( AttributeInDbModel column, int iDbTypeCode) {
		int r = 0;
		int i = column.getJdbcTypeCode();
		if ( i != iDbTypeCode ) {
			updateLogger.println(" . Column '" + column.getDatabaseName() + "' : JDBC type code changed to " + iDbTypeCode);
			column.setJdbcTypeCode(iDbTypeCode);
			r++;
		}
		return r;
	}

	private int updateDbType( AttributeInDbModel column, String sDbType ) {
		int r = 0;
		String s = column.getDatabaseType(); // v 3.0.0
		if ( ! s.equals(sDbType) ) {
			updateLogger.println(" . Column '" + column.getDatabaseName() + "' : Database type changed to " + sDbType);
			column.setDatabaseTypeName(sDbType);
			r++;
		}
		return r;
	}

	private int updateNotNull( AttributeInDbModel column, String sNotNull) {
		int r = 0;
		String s = column.getDatabaseNotNullAsString();
		if ( ! s.equals(sNotNull) ) {
			updateLogger.println(" . Column '" + column.getDatabaseName() + "' : NotNull changed to " + sNotNull);
			column.setDatabaseNotNull(sNotNull);
			r++;
		}
		return r;
	}

	private int updateSize( AttributeInDbModel column, int iSize) {
		int r = 0;
		if ( column.getDatabaseSize() != iSize ) 
		{
			updateLogger.println(" . Column '" + column.getDatabaseName() + "' : Size changed to " + iSize);
			column.setDatabaseSize(iSize);
			r++;
		}
		return r;
	}

	private int updateComment( AttributeInDbModel column, String sComment) {
		int r = 0;
		if ( ! column.getDatabaseComment().equals(sComment) )
		{
			updateLogger.println(" . Column '" + column.getDatabaseName() + "' : Comment changed to " + sComment);
			column.setDatabaseComment(sComment);
			r++;
		}
		return r;
	}

	private int updatePrimaryKey( AttributeInDbModel column, boolean isPrimaryKey) {
		int r = 0;
		if ( column.isKeyElement() != isPrimaryKey ) // v 3.0.0
		{
			updateLogger.println(" . Column '" + column.getDatabaseName() + "' : Primary Key flag changed to " + isPrimaryKey);
			column.setKeyElement(isPrimaryKey);
			r++;
		}
		return r;
	}

	// -----------------------------------------------------------------------------------------------------
	// UPDATE REPOSITORY
	// -----------------------------------------------------------------------------------------------------
	/**
	 * Updates the given "RepositoryModel" from the database defined in the "DatabaseConfiguration" <br>
	 * 
	 * @param databaseConfiguration the DatabaseConfiguration to be used
	 * @param repositoryModel the RepositoryModel to be updated
	 * @return the ChangeLog holding all the changes
	 * @throws TelosysToolsException
	 */
	public ChangeLog updateRepository( DatabaseConfiguration databaseConfiguration, RepositoryModel repositoryModel ) throws TelosysToolsException 
	{
		Connection connection = getConnection(databaseConfiguration);
		
//		//--- STEP 1 : Updates the repository from the current database meta-data
//		ChangeLog changeLog = updateRepository( databaseConfiguration, repositoryModel, connection );
//
//		//--- STEP 2 : Updates the links between entities ( since v 2.1.1 )
//		LinksManager linksManager = new LinksManager(getRepositoryRules(), getLogger() );
//		linksManager.updateLinks(repositoryModel, changeLog);
		
		//--- STEP 1 : Updates the repository from the current database meta-data
		ChangeLog changeLog = updateRepositoryStep1( databaseConfiguration, repositoryModel, connection );
		closeConnection(connection);
		
		//--- STEP 1.1 : set FK flags on attributes - MUST BE CALLED BEFORE THE LINKS GENERATION
		ForeignKeyTypeManager fkTypeManager = new ForeignKeyTypeManager() ;
		fkTypeManager.setAttributesForeignKeyInformation(repositoryModel);

		//--- STEP 2 : Updates the links between entities ( since v 2.1.1 )
		LinksManager linksManager = new LinksManager(getRepositoryRules() );
		linksManager.updateLinks(repositoryModel, changeLog);
		
		return changeLog ;
	}

	/**
	 * First step of the update<br>
	 * Updates entities, columns and foreign keys from the current database meta-data <br>
	 * The links are not processed in this step
	 * 
	 * @param databaseConfiguration
	 * @param repositoryModel
	 * @param connection
	 * @return
	 * @throws TelosysToolsException
	 */
	private ChangeLog updateRepositoryStep1( DatabaseConfiguration databaseConfiguration, RepositoryModel repositoryModel, 
			Connection connection ) throws TelosysToolsException 
	{
		ChangeLog changeLog = null ;
		
		String catalog           = databaseConfiguration.getMetadataCatalog();
		String schema            = databaseConfiguration.getMetadataSchema(); 
		String tableNamePattern  = databaseConfiguration.getMetadataTableNamePattern();
		String tableTypes[]      = databaseConfiguration.getMetadataTableTypesArray();
		String tableNameInclude  = databaseConfiguration.getMetadataTableNameInclude();
		String tableNameExclude  = databaseConfiguration.getMetadataTableNameExclude();
		
		Date now = new Date();
		try {
			logger.log(" . get meta-data ");
			try {
				logger.log(" . update repository from database tables");
				updateLogger.println("Update date : " + now);
				
				//--- Load the Database Model
				DatabaseModelManager manager = new DatabaseModelManager();
				DatabaseTables dbTables = manager.getDatabaseTables(connection, catalog, schema, 
						tableNamePattern, tableTypes, tableNameInclude, tableNameExclude);

				changeLog = updateRepositoryStep1FromTables(repositoryModel, dbTables);

			} catch (SQLException e) {
				throw new TelosysToolsException("SQLException", e);
			} catch (Exception t) {
				throw new TelosysToolsException("Exception", t);
			}
		}
		finally {
			updateLogger.close();
		}
		return changeLog ;
	}
	
	private ChangeLog updateRepositoryStep1FromTables(RepositoryModel repositoryModel, DatabaseTables dbTables ) {
		ChangeLog changeLog = new ChangeLog() ;
		
		int changesCount = 0 ;
		
		LinkedList<String> databaseTables = new LinkedList<String>();

		//-----------------------------------------------------------------------
		// STEP 1 : Update existing tables and Create new ones
		//-----------------------------------------------------------------------
		//--- For each table in the database ...
		for ( DatabaseTable dbTable : dbTables.getTables() ) {
			
			logger.log("   --------------------------------------------------------------");
			logger.log("   Table '" + dbTable.getTableName() 
					+ "' ( catalog = '" + dbTable.getCatalogName() 
					+ "', schema = '"+ dbTable.getSchemaName() + "' )");

			String sTableName = dbTable.getTableName();

			//--- Store the table name in the list used to delete entities
			databaseTables.add(sTableName);

			updateLogger.println(" ");
			EntityInDbModel entity = repositoryModel.getEntityByTableName(sTableName);
			
			if ( entity != null ) {
				//------------------------------------------------------------------
				//   ENTITY FOUND IN MODEL => UPDATE ENTITY
				//------------------------------------------------------------------
				updateLogger.println(" Table '" + sTableName + "' found in repository");
				ChangeOnEntity changeOnEntity = updateEntity(repositoryModel, dbTable, entity);
				if ( changeOnEntity.getNumberOfChanges() > 0 ) {
					updateLogger.println(" (*) table '" + sTableName + "' updated : " + changeOnEntity.getNumberOfChanges() + " change(s)");
					changeLog.log(changeOnEntity);
				} else {
					updateLogger.println(" (=) table '" + sTableName + "' unchanged");
				}
				changesCount = changesCount + changeOnEntity.getNumberOfChanges() ;
			} else {
				//------------------------------------------------------------------
				//   ENTITY NOT FOUND IN MODEL => CREATE ENTITY  (NEW)
				//------------------------------------------------------------------
				updateLogger.println(" Table '" + sTableName + "' not found in repository");
				EntityInDbModel entityCreated = addEntity(repositoryModel, dbTable) ;
				updateLogger.println(" (+) table '" + sTableName + "' added");
				changeLog.log(new ChangeOnEntity(ChangeType.CREATED, null, entityCreated));
				changesCount++;
			}
		}

		//-----------------------------------------------------------------------
		// STEP 2 : Remove tables that no longer exist in the database
		//-----------------------------------------------------------------------
		//--- For each table in the repository ...
		String[] tableNames = repositoryModel.getEntitiesNames();
		for (int i = 0; i < tableNames.length; i++) {
			String sTableName = tableNames[i];
			if (checkTableExistsInDatabase(sTableName, databaseTables) != true) {
				//--- This table in the repository no longer exists in the database
				updateLogger.println(" ");
				updateLogger.println(" Table '" + sTableName + "' no longer exists in database");
				//--- => Remove it
				EntityInDbModel deletedEntity = repositoryModel.removeEntity(sTableName);
				updateLogger.println(" (-) table '" + sTableName + "' removed");
				changeLog.log(new ChangeOnEntity(ChangeType.DELETED, deletedEntity, null));
				changesCount++;
			}
		}
		return changeLog ;
	}
	
	private ChangeOnEntity updateEntity( RepositoryModel repositoryModel, DatabaseTable dbTable, EntityInDbModel entity) {
		
		EntityInDbModel entityBefore = ObjectUtil.deepCopy(entity);
		ChangeOnEntity changeOnEntity = new ChangeOnEntity(ChangeType.UPDATED, entityBefore, entity);
		//--------------------------------------------------------------------------------
		// 0) added in ver 2.0.7 
		//--------------------------------------------------------------------------------
		//--- Set or update TABLE TYPE ( "TABLE", "VIEW", ... )
		String tableType = dbTable.getTableType() ;
		if ( tableType != null ) {
			if ( StrUtil.nullOrVoid(entity.getDatabaseType()) ) {
				// Not set yet => Set type
				entity.setDatabaseType(tableType);
			}
			else {
				String originalType = entity.getDatabaseType() ;
				if ( tableType.equals(originalType) == false ) {
					// The type has changed => Update type
					entity.setDatabaseType(tableType);
					changeOnEntity.setDatabaseTypeHasChanged(true);
					updateLogger.println(" . Type has changed '" + originalType + "' --> '" + tableType + "'");
				}
			}
		}
		
		//--------------------------------------------------------------------------------
		// 1) remove the columns that doesn't exist in the Database 
		//--------------------------------------------------------------------------------
		//--- For each column in the repository ...
		for ( AttributeInDbModel column : entity.getAttributesArray() ) { 
			String sColumnName = column.getDatabaseName();
			// Does it still exist in the DATABASE ?
			if ( null == dbTable.getColumnByName(sColumnName) ) {
				//--- This column doesn't exist in the DB => remove it from the model
				entity.removeAttribute(column);
				changeOnEntity.addChangeOnColumn( new ChangeOnColumn(ChangeType.DELETED, column, null) );
				updateLogger.println(" . Column '" + sColumnName + "' deleted");
			}
		}

		//--------------------------------------------------------------------------------
		// 2) remove the foreign keys that doesn't exist in the Database 
		//--------------------------------------------------------------------------------
		//--- For each fk in the repository ...
		for ( ForeignKeyInDbModel fk : entity.getForeignKeys() ) {
			String sFkName = fk.getName();
			// Does it still exist in the DATABASE ?
			if ( null == dbTable.getForeignKeyByName(sFkName) ) {
				//--- This FK doesn't exist in the DB => remove it from the model
				entity.removeForeignKey(fk);
				changeOnEntity.addChangeOnForeignKey( new ChangeOnForeignKey(ChangeType.DELETED, fk, null) );
				updateLogger.println(" . Foreign key '" + sFkName + "' deleted");
			}
		}

		//--------------------------------------------------------------------------------
		// 3) UPDATE existing COLUMNS if necessary and ADD new ones
		//--------------------------------------------------------------------------------
		//--- For each column of the table in the DataBase ...
		for ( DatabaseColumn dbColumn : dbTable.getColumns() ) {
			String sColumnName = dbColumn.getColumnName();
			
			//--- Search this column in the REPOSITORY
			AttributeInDbModel column = entity.getAttributeByColumnName(sColumnName);
			if ( column != null ) 
			{
				//--- The column exists => update it
				AttributeInDbModel columnBefore = ObjectUtil.deepCopy(column);
				if ( updateEntityAttribute(column, dbColumn) > 0 ) {
					changeOnEntity.addChangeOnColumn( new ChangeOnColumn(ChangeType.UPDATED, columnBefore, column ) );
					updateLogger.println(" . Column '" + sColumnName + "' updated");
				}
			} else {
				//--- The column doesn't exist => add it
				column = addEntityAttribute(entity, dbColumn);
				changeOnEntity.addChangeOnColumn( new ChangeOnColumn(ChangeType.CREATED, null, column ) );
				updateLogger.println(" . Column '" + sColumnName + "' added");
			}
			//--- If this column is a member of a Foreign Key
			//setFkAttribute(sColumnName, column, listFK);
		}

		//--------------------------------------------------------------------------------
		// 4) UPDATE existing FOREIGN KEYS if necessary and ADD new ones
		//--------------------------------------------------------------------------------
		//--- For each FK of the table in the DataBase ...( v 0.9.0 )
		List<DatabaseForeignKey> dbForeignKeys = dbTable.getForeignKeys();
		for ( DatabaseForeignKey dbForeignKey : dbForeignKeys ) {
			
			String sFkName = dbForeignKey.getForeignKeyName();
			
			//--- Search this foreign key in the REPOSITORY
			ForeignKeyInDbModel newForeignKey = buildForeignKey(dbForeignKey) ;
			ForeignKeyInDbModel foreignKey = entity.getForeignKey(sFkName);
			if ( foreignKey != null ) 
			{
				// The FK exists => update it if it has changed
				if ( ! foreignKey.isIdentical( newForeignKey ) )
				{
					// 
					entity.storeForeignKey(newForeignKey);
					changeOnEntity.addChangeOnForeignKey( new ChangeOnForeignKey(ChangeType.UPDATED, foreignKey, newForeignKey) );
					updateLogger.println(" . Foreign key '" + sFkName + "' updated");
				}
			}
			else
			{
				// The FK doesn't exist => add it to the list
				entity.storeForeignKey(newForeignKey);
				changeOnEntity.addChangeOnForeignKey( new ChangeOnForeignKey(ChangeType.CREATED, null, newForeignKey) );
				updateLogger.println(" . Foreign key '" + sFkName + "' added");
			}
		}
		//--- Return all the changes for this entity
		return changeOnEntity ;
	}
	
	private boolean checkTableExistsInDatabase(String sTableName, LinkedList<String> databaseTables) {
		// --- Search the Table Name in the Database Tables
		int n = databaseTables.size();
		for (int i = 0; i < n; i++) {
			String s = databaseTables.get(i);
			if (s != null) {
				if (s.equals(sTableName))
					return true;
			}
		}
		return false;
	}
}
