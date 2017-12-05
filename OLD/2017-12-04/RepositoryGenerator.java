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
import java.util.Date;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.jdbc.ConnectionManager;
import org.telosys.tools.db.model.DatabaseModelManager;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.rules.RepositoryRules;

/**
 * Repository (model) generator 
 * 
 * @author Laurent GUERIN, Eric LEMELIN
 * 
 */
public class RepositoryGenerator extends RepositoryManager
{
//	/**
//	 * Constructor
//	 * @param entityInformationProvider
//	 * @param uiInfoProvider
//	 * @param logger
//	 */
//	public RepositoryGenerator(EntityInformationProvider entityInformationProvider, UserInterfaceInformationProvider uiInfoProvider, TelosysToolsLogger logger) 
//	{
//		super(entityInformationProvider, uiInfoProvider, logger);
//	}

	/**
	 * Constructor
	 * @param connectionManager
	 * @param repositoryRules
	 * @param logger
	 */
	public RepositoryGenerator(ConnectionManager connectionManager, RepositoryRules repositoryRules, TelosysToolsLogger logger) {
		super(connectionManager, repositoryRules, logger);
	}

//	/**
//	 * Generates the repository model from the given database <br>
//	 * Generates all the entities and all the links between the entities
//	 * 
//	 * @param connection
//	 * @param databaseConfig
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public RepositoryModel generate(Connection connection, DatabaseConfiguration databaseConfig) throws TelosysToolsException 
//	{
//		//--- STEP 1 : Generates the model entities 
//		RepositoryModel repositoryModel = generateRepository(connection, databaseConfig);
//		
//		//--- STEP 2 : Generates the links between entities 
//		LinksGenerator linksGenerator = new LinksGenerator(getLogger());
//		linksGenerator.generateAllLinks(repositoryModel);
//		
//		return repositoryModel ;
//	}
	
	/**
	 * Generates the repository model from the given database configuration<br>
	 * Generates all the entities and all the links between the entities
	 * 
	 * @param databaseConfiguration
	 * @return
	 * @throws TelosysToolsException
	 */
	public RepositoryModel generate(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException 
	{
//		ConnectionManager connectionManager = new ConnectionManager(logger);
//		Connection connection = connectionManager.getConnection(databaseConfiguration);
		Connection connection = getConnection(databaseConfiguration);
		
		//--- STEP 1 : Generates the model entities 
		RepositoryModel repositoryModel;
		try {
			repositoryModel = generateRepository(connection, databaseConfiguration);
		} finally { // v 3.0.0 (finally added for connection closing)
			closeConnection(connection); 
		}
		
		//--- STEP 1.1 : set FK flags on attributes - MUST BE CALLED BEFORE THE LINKS GENERATION
		ForeignKeyTypeManager fkTypeManager = new ForeignKeyTypeManager() ;
		fkTypeManager.setAttributesForeignKeyInformation(repositoryModel);
		
		//--- STEP 2 : Generates the links between entities 
//		LinksGenerator linksGenerator = new LinksGenerator(getRepositoryRules(), getLogger() );
//		linksGenerator.generateAllLinks(repositoryModel);
		// v 2.1.1
		LinksManager linksManager = new LinksManager(getRepositoryRules(), getLogger() );
		linksManager.generateAllLinks(repositoryModel);
		
		return repositoryModel ;
	}

//	/**
//	 * Generates the repository model from the given database configuration using the given connection <br>
//	 * Generates all the entities and all the links between the entities <br>
//	 * This method is supposed to used only in test cases
//	 * 
//	 * @param databaseConfiguration
//	 * @param connection
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	protected RepositoryModel generate(DatabaseConfiguration databaseConfiguration, Connection connection) throws TelosysToolsException 
//	{
//		//--- STEP 1 : Generates the model entities 
//		RepositoryModel repositoryModel = generateRepository(connection, databaseConfiguration);
//		
//		//--- STEP 2 : Generates the links between entities 
////		LinksGenerator linksGenerator = new LinksGenerator(getRepositoryRules(), getLogger() );
////		linksGenerator.generateAllLinks(repositoryModel);
//		// v 2.1.1
//		LinksManager linksManager = new LinksManager(getRepositoryRules(), getLogger() );
//		linksManager.generateAllLinks(repositoryModel);
//		
//		return repositoryModel ;
//	}
	
	/**
	 * Generates the repository model from the given database
	 * @param con
	 * @param databaseConfig
	 * @return
	 * @throws TelosysToolsException
	 */
	private RepositoryModel generateRepository(Connection con, DatabaseConfiguration databaseConfig) throws TelosysToolsException 
	{
		logger.log("--> Repository generation ");

		logger.log(" . get meta-data ");
		DatabaseMetaData dbmd = getMetaData(con);

		RepositoryModel repositoryModel = new RepositoryModel();
					
		try {
			//--- Init new repository	
			repositoryModel.setDatabaseName( databaseConfig.getDatabaseName() );
			repositoryModel.setDatabaseId( databaseConfig.getDatabaseId() );
			repositoryModel.setDatabaseProductName( dbmd.getDatabaseProductName() );
			repositoryModel.setGenerationDate( new Date() );
			//repositoryModel.setVersion( DbModelVersion.VERSION ); // v 3.0.0 (in Model class)

			//--- Add all tables/entities to the new repository	
			generateEntities(repositoryModel, 
					con, 
					databaseConfig.getMetadataCatalog(), 
					databaseConfig.getMetadataSchema(), 
					databaseConfig.getMetadataTableNamePattern(), 
					databaseConfig.getMetadataTableTypesArray(),
					databaseConfig.getMetadataTableNameInclude(),
					databaseConfig.getMetadataTableNameExclude());
			
		} catch (SQLException e) {
			throw new TelosysToolsException("SQLException", e);
		}

		return repositoryModel ;
	}
	
	private void generateEntities(RepositoryModel repositoryModel, Connection con,
			String sCatalog, String sSchema,
			String sTableNamePattern, String[] arrayTableTypes,
			String sTableNameInclude, String sTableNameExclude) throws SQLException 
	{
		// --- Get METADATA parameters
		if (sTableNamePattern == null) {
			sTableNamePattern = "%";
		}

		logger.log("   ... Metadata parameters : ");
		logger.log("   ... * Catalog = " + sCatalog);
		logger.log("   ... * Schema  = " + sSchema);
		logger.log("   ... * Table Name Pattern  = " + sTableNamePattern);

		StringBuffer sb = new StringBuffer(100);
		for (String s : arrayTableTypes ) {
			sb.append("[" + s + "] ");
		}
		logger.log("   ... * Table Types Array  = " + sb.toString());

		//--- Load the Database Model
		DatabaseModelManager manager = new DatabaseModelManager( this.getLogger() );
		DatabaseTables dbTables = manager.getDatabaseTables(con, sCatalog, sSchema, sTableNamePattern, arrayTableTypes, sTableNameInclude, sTableNameExclude);

		//--- For each table add an Entity in the repository
//		Iterator<DatabaseTable> iter = dbTables.iterator();
		int iTablesCount = 0;
//		while ( iter.hasNext() )
		for ( DatabaseTable dbTable : dbTables )
		{
			iTablesCount++;
//			DatabaseTable dbTable = (DatabaseTable) iter.next();
			logger.log("   --------------------------------------------------------------");
			logger.log("   Table '" + dbTable.getTableName() 
					+ "' ( catalog = '" + dbTable.getCatalogName() 
					+ "', schema = '"+ dbTable.getSchemaName() + "' )");
			addEntity(repositoryModel, dbTable) ;
		}
		logger.log("   --------------------------------------------------------------");
		logger.log("   " + iTablesCount + " table(s) generated.");
		logger.log("   --------------------------------------------------------------");
	}

}
