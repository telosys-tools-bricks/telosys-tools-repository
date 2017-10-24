package org.telosys.tools.repository.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.repository.persistence.PersistenceManager;
import org.telosys.tools.repository.persistence.PersistenceManagerFactory;

import junit.env.telosys.tools.commons.LoggerProviderForUnitTests;

public class RepoModelTest {
	
	public void print(File file) {
		System.out.println("Repository model file : " + file.toString());
	}
	
	public void print(RepositoryModel model) {
		System.out.println("MODEL : " );
		System.out.println("Database : " + model.getDatabaseId() + " " + model.getDatabaseName() );
		for ( EntityInDbModel entity : model.getEntitiesArraySortedByTableName() ) {
			System.out.println(" . entity : " + entity );
		}
	}
	
	public void print(EntityInDbModel entity) {
		System.out.println("ENTITY : " );
		for ( AttributeInDbModel column : entity.getAttributesArray() ) {
			System.out.println(" . column : " + column );
		}
		for ( ForeignKeyInDbModel  fk : entity.getForeignKeys() ) {
			System.out.println(" . foreign key : " + fk );
		}
//		for ( LinkInDbModel  link : entity.getLinks()) {
		for ( LinkInDbModel  link : entity.getLinksArray()) {
			System.out.println(" . link : " + link );
		}
	}
	
	@Test
	public void test() throws TelosysToolsException {
		File modelFile = new File("src/test/resources/repo/BookStoreDERBY.dbrep");
		print(modelFile);
		//TelosysToolsLogger logger = new ConsoleLogger() ;
		TelosysToolsLogger logger = LoggerProviderForUnitTests.getLogger();

		//PersistenceManager pm = new StandardFilePersistenceManager(modelFile, logger);
		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(modelFile, logger);
		RepositoryModel model = pm.load();
		print(model);
		String entityName = "BOOK";
		EntityInDbModel entity = model.getEntityByTableName(entityName);
		Assert.assertNotNull(entity);
		print(entity);
		
//		int nbLinks = entity.getLinks().length ;
		int nbLinks = entity.getLinksArray().length ;
//		System.out.println(nbLinks + " link(s) in entity " + entity.getName());
		System.out.println(nbLinks + " link(s) in entity " + entity.getDatabaseTable() );
//		int n = model.removeLinksByEntityName(entity.getName());
		int n = model.removeLinksByEntityName(entity.getDatabaseTable());
		System.out.println(n + " link(s) removed");
		Assert.assertTrue(n == nbLinks * 2 );
	}

}
