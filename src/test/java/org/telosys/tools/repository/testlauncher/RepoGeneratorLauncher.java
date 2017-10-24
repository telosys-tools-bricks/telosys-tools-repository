package org.telosys.tools.repository.testlauncher;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DatabasesConfigurations;
import org.telosys.tools.commons.dbcfg.DbConfigManager;
import org.telosys.tools.commons.jdbc.ConnectionManager;
import org.telosys.tools.repository.RepositoryGenerator;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.rules.RepositoryRules;
import org.telosys.tools.repository.rules.RepositoryRulesProvider;

import junit.env.telosys.tools.commons.LoggerProviderForUnitTests;

public class RepoGeneratorLauncher {
	
	private final static String  DBCFG_FILE  = "/dbcfg/databases-test-PostgreSQL.dbcfg" ; ;
	private final static int     DATABASE_ID = 1 ; 
	
	public static void main(String[] args) throws TelosysToolsException {
		
		log("Test started... ");
		
		DatabaseConfiguration databaseConfiguration = getDatabaseConfiguration(DATABASE_ID);
		
		//TelosysToolsLogger logger = new ConsoleLogger() ;
		TelosysToolsLogger logger = LoggerProviderForUnitTests.getLogger();

		ConnectionManager connectionManager = new ConnectionManager(logger);
		RepositoryRules rules = RepositoryRulesProvider.getRepositoryRules() ;
		RepositoryGenerator repositoryGenerator = new RepositoryGenerator(connectionManager, rules, logger);

		RepositoryModel repositoryModel = repositoryGenerator.generate( databaseConfiguration );

		log("End of test. Number of entities = " + repositoryModel.getEntities().size());
		
	}
	
	private static DatabasesConfigurations getDatabasesConfigurations() throws TelosysToolsException {
		log("getDatabaseConfigurations() ");
		DbConfigManager dbConfigManager = new DbConfigManager( FileUtil.getFileByClassPath(DBCFG_FILE) );
		DatabasesConfigurations databasesConfigurations = dbConfigManager.load();
		return databasesConfigurations ;
	}

	private static DatabaseConfiguration getDatabaseConfiguration(int databaseId)  throws TelosysToolsException {
		log("getDatabaseConfiguration(" + databaseId + ") ");
		DatabasesConfigurations databasesConfigurations = getDatabasesConfigurations();
		DatabaseConfiguration databaseConfiguration = databasesConfigurations.getDatabaseConfiguration(databaseId);
		return databaseConfiguration ;
	}
	private static void log(String msg)  {
		System.out.println("LOG : " + msg);
	}
}
