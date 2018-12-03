package org.telosys.tools.repository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.dbcfg.DbConnectionManager;
import org.telosys.tools.repository.changelog.ChangeLog;
import org.telosys.tools.repository.changelog.ChangeOnColumn;
import org.telosys.tools.repository.changelog.ChangeOnEntity;
import org.telosys.tools.repository.changelog.ChangeOnForeignKey;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.LinkInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import junit.env.telosys.tools.commons.LoggerProviderForUnitTests;
import junit.env.telosys.tools.commons.TestsEnv;

public abstract class AbstractTestCase {
	
	private   static final String PROJECT_FOLDER = "project2" ;
	
	protected static final int    DEFAULT_DATABASE_ID  = 1 ;
	
	protected void printSeparator(String s ) {
		System.out.println("==================================================================================================");
		System.out.println("=== " + s );
		System.out.println("==================================================================================================");
	}
	
	protected void printEntity(EntityInDbModel entity) {
		System.out.println("ENTITY : table = '" + entity.getDatabaseTable() 
				+ "',  class = '" + entity.getClassName() 
				+ "',  nb columns = " + entity.getAttributesArray().length
				+ ",  nb FK = " + entity.getForeignKeys().length
				+ ",  nb LINKS = " + entity.getLinks().size() );
	}

	protected void printModel (RepositoryModel model)  {
		System.out.println("---------------------------------------------------" );
		System.out.println("MODEL : " );
		System.out.println("Database ID = " + model.getDatabaseId() );
		
		System.out.println("Number of entities = " + model.getNumberOfEntities() );
		String[] entitiesNames = model.getEntitiesNames();
		for ( String name : entitiesNames ) {
			System.out.println("Entity name = '" + name + "'");
//			EntityInDbModel entity = model.getEntityByName(name);
			EntityInDbModel entity = model.getEntityByTableName(name);
			AttributeInDbModel[] columns = entity.getAttributesArray();
			for ( AttributeInDbModel c : columns ) {
				System.out.println(" . Column : " + c );
			}
//			LinkInDbModel[] links = entity.getLinks();
			LinkInDbModel[]  links = entity.getLinksArray();
			for ( LinkInDbModel link : links ) {
				System.out.println(" . Link : "  + link);
//				System.out.println(" . " + link.getJavaFieldName() );
				System.out.println("     Field name = " + link.getFieldName() );
				System.out.println("     Foreign Key name = " + link.getForeignKeyName() );
				System.out.println("     Join Table name = " + link.getJoinTableName() );
			}
		}
		System.out.println("---------------------------------------------------" );

	}
	
	/**
	 * Initialize a database in memory using the given SQL Script ID, <br>
	 * then generate the RepositoryModel from this Database
	 * 
	 * @param sqlScriptId
	 * @return
	 * @throws TelosysToolsException
	 */
	protected RepositoryModel generateRepositoryModel(int sqlScriptId) throws TelosysToolsException {
		
		System.out.println("Database initialization... ");
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(DEFAULT_DATABASE_ID);
		databaseInMemory.executeSqlInit(sqlScriptId);
		
		System.out.println("Repository generation... ");
		//RepositoryGenerator repositoryGenerator = getRepositoryGenerator() ;
		DbModelGenerator repositoryGenerator = getDbModelGenerator(PROJECT_FOLDER);

		RepositoryModel repositoryModel = repositoryGenerator.generate( databaseInMemory.getDatabaseConfiguration() );
		
		databaseInMemory.close();
		
		return repositoryModel ;
	}
	
	/**
	 * Initialize an database in memory using the given SQL Script ID, <br>
	 * then generate the RepositoryModel from this Database, <br>
	 * then change the database schema using "alter" SQL Script for the given script ID, <br>
	 * then update the RepositoryModel
	 * 
	 * @param sqlScriptId
	 * @return
	 * @throws TelosysToolsException
	 */
	protected UpdateResult generateAndUpdateRepositoryModel(int sqlScriptId) throws TelosysToolsException {
		
		System.out.println("Database initialization... ");
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(DEFAULT_DATABASE_ID);
		databaseInMemory.executeSqlInit(sqlScriptId);
		

		
		System.out.println("Repository model generation... ");
		//RepositoryGenerator repositoryGenerator = getRepositoryGenerator() ;
		DbModelGenerator repositoryGenerator = getDbModelGenerator(PROJECT_FOLDER);
		RepositoryModel repositoryModel = repositoryGenerator.generate( databaseInMemory.getDatabaseConfiguration() );
		
		System.out.println("Database changes... ");
		databaseInMemory.executeSqlAlter(sqlScriptId);
		
		System.out.println("Repository model update... ");
		ByteArrayOutputStream baosUpdateLog = new ByteArrayOutputStream();
		//RepositoryUpdator repositoryUpdator = getRepositoryUpdator(baosUpdateLog);
		DbModelUpdator repositoryUpdator = getDbModelUpdator(PROJECT_FOLDER, baosUpdateLog);

		ChangeLog changeLog = repositoryUpdator.updateRepository(databaseInMemory.getDatabaseConfiguration(), repositoryModel);
		
		System.out.println(baosUpdateLog.toString());
		System.out.println("Repo entities count = " + repositoryModel.getNumberOfEntities() );
		System.out.println("ChangeLog number of entities = " + changeLog.getNumberOfEntities() );
		System.out.println("ChangeLog number of entities created = " + changeLog.getNumberOfEntitiesCreated() );
		System.out.println("ChangeLog number of entities updated = " + changeLog.getNumberOfEntitiesUpdated() );
		System.out.println("ChangeLog number of entities deleted = " + changeLog.getNumberOfEntitiesDeleted() );
		
		databaseInMemory.close();
		
		return new UpdateResult(repositoryModel, changeLog);
	}
	
	private TelosysToolsCfg getTelosysToolsCfg(String projectName) throws TelosysToolsException {
		File projectFolder = TestsEnv.getTestFolder(projectName);
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(projectFolder.getAbsolutePath());
		TelosysToolsCfg telosysToolsCfg = cfgManager.loadTelosysToolsCfg();
		return telosysToolsCfg ;
	}
	
	private DbConnectionManager getDbConnectionManager(String projectName) throws TelosysToolsException {
		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg(projectName);
		return new DbConnectionManager(telosysToolsCfg);
	}
	
	protected DbModelGenerator getDbModelGenerator(String projectFolderName) throws TelosysToolsException {
		DbConnectionManager dbConnectionManager = getDbConnectionManager(projectFolderName);
		TelosysToolsLogger logger = LoggerProviderForUnitTests.getLogger();
		return new DbModelGenerator(dbConnectionManager, logger);
	}
	
	protected DbModelUpdator getDbModelUpdator(String projectFolderName, ByteArrayOutputStream baosUpdateLog) throws TelosysToolsException {
		TelosysToolsLogger logger = LoggerProviderForUnitTests.getLogger();
		DbConnectionManager dbConnectionManager = getDbConnectionManager(projectFolderName);
		UpdateLogWriter updateLogger = new UpdateLogWriter( baosUpdateLog );
		return new DbModelUpdator(dbConnectionManager, logger, updateLogger);
	}
	
	/**
	 * Checks each given java name is one of the given expected names 
	 * @param javaName1
	 * @param javaName2
	 * @param expectedName1
	 * @param expectedName2
	 */
	protected void checkJavaName(String javaName1, String javaName2, String expectedName1, String expectedName2 ) {
		System.out.println("Check Java names : '" + javaName1 + "', '" + javaName2 + "' expected : '" + expectedName1 + "', '" + expectedName2 +"'");
		assertTrue( expectedName1.equals( javaName1 ) || expectedName2.equals( javaName1 ) );
		assertTrue( expectedName1.equals( javaName2 ) || expectedName2.equals( javaName2 ) );
		if ( expectedName1.equals( javaName1 ) ) {
			assertEquals(expectedName2, javaName2);
		}
		else if ( expectedName2.equals( javaName1 ) ) {
			assertEquals(expectedName1, javaName2);
		}
		else {
			fail("Unexpected jave name for link '" + javaName1 + "' or '" + javaName2 + "'");
		}
	}

	protected void printChangeLog(ChangeLog changeLog ) {
		System.out.println("----------------------------------------" );
		System.out.println("CHANGE LOG : " );
		System.out.println("Date : " + changeLog.getDate() );
		System.out.println("Number of entities : " + changeLog.getNumberOfEntities() );
		System.out.println("Number of entities created : " + changeLog.getNumberOfEntitiesCreated() );
		System.out.println("Number of entities updated : " + changeLog.getNumberOfEntitiesUpdated() );
		System.out.println("Number of entities deleted : " + changeLog.getNumberOfEntitiesDeleted() );
		
		for ( ChangeOnEntity change : changeLog.getChanges() ) {
			System.out.println("- Entity : " + change.getEntityName() + " change type '" + change.getChangeType() + "'");
			System.out.println("  . before = " + change.getEntityBefore());
			System.out.println("  . after  = " + change.getEntityAfter());
			
			System.out.println(" Changes on Columns : " );
			for ( ChangeOnColumn changeOnColumn : change.getChangesOnColumn() ) {
				System.out.println("- Column : " + changeOnColumn.getChangeType()  );
				System.out.println("  . column before = " + changeOnColumn.getColumnBefore() );
				System.out.println("  . column after  = " + changeOnColumn.getColumnAfter() );
			}
			
			System.out.println(" Changes on Foreign Keys : " );
			for ( ChangeOnForeignKey changeOnFK : change.getChangesOnForeignKey() ) {
				System.out.println("- FK : " + changeOnFK.getChangeType() );
				System.out.println("  . column before = " + changeOnFK.getForeignKeyBefore() );
				System.out.println("  . column after  = " + changeOnFK.getForeignKeyAfter() );
			}
		}
		System.out.println("----------------------------------------" );
	}
	
	protected void printEntitiesChanged(List<ChangeOnEntity> entitiesChanged) {
		System.out.println("--- LIST OF ENTITIES CHANGED : " );
		for ( ChangeOnEntity entityChanged : entitiesChanged ) {
			printEntityChanged(entityChanged);
		}
	}
	
	protected void printEntityChanged(ChangeOnEntity entityChanged) {
		System.out.println("ENTITY CHANGED : " );
		System.out.println(" . name         : " + entityChanged.getEntityName() ) ;
		System.out.println(" . change type  : " + entityChanged.getChangeType() ) ;
		switch ( entityChanged.getChangeType() ) {
		case CREATED :
			System.out.println("ENTITY CREATED : " );
			printEntity( entityChanged.getEntityCreated() );
			break;
		case DELETED :
			System.out.println("ENTITY DELETED : " );
			printEntity( entityChanged.getEntityDeleted() );
			break;
		case UPDATED :
			System.out.println("ENTITY UPDATED / BEFORE : " );
			printEntity( entityChanged.getEntityBefore() );
			System.out.println("ENTITY UPDATED / AFTER : " );
			printEntity( entityChanged.getEntityAfter() );
			break;
		}
	}


}
