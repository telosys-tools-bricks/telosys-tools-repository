package org.telosys.tools.repository.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.repository.persistence.PersistenceManager;
import org.telosys.tools.repository.persistence.PersistenceManagerFactory;

import junit.env.telosys.tools.commons.LoggerProviderForUnitTests;

public class RepoModelLoadingTest {
	
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
		File modelFile = new File("src/test/resources/repo/BookStore-with-JC.dbrep");
		print(modelFile);
		//TelosysToolsLogger logger = new ConsoleLogger() ;
		TelosysToolsLogger logger = LoggerProviderForUnitTests.getLogger();

		//PersistenceManager pm = new StandardFilePersistenceManager(modelFile, logger);
		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(modelFile, logger);
		RepositoryModel model = pm.load();
		print(model);
		
		checkEntityBook(model);
		
	}

	private void checkEntityBook(RepositoryModel model) {
		System.out.println("----- Entity 'Book' : ");
		EntityInDbModel book = model.getEntityByClassName("Book");
		assertNotNull(book);
		System.out.println("--- Attributes : ");
		for ( Attribute attribute : book.getAttributes() ) {
			System.out.println(" . " + attribute );
		} 
		System.out.println("--- Links : ");
		for ( LinkInDbModel link : book.getAllLinks() ) {
			System.out.println(" . " + link );
			System.out.println(" - Join Columns : ");
			if ( link.getJoinColumns() != null ) {
				for ( JoinColumn jc : link.getJoinColumns() ) {
					System.out.println("   . " + jc );				
				}
			}
			else {
				System.out.println("   join columns = null" );				
			}
		} 
		assertEquals(5, book.getAllLinks().size() );
		
		System.out.println("--- Foreign Keys : ");
		for ( ForeignKeyInDbModel fk : book.getForeignKeys() ) {
			System.out.println(" . " + fk );
		}
		assertEquals(2, book.getForeignKeys().length);
	}
}
