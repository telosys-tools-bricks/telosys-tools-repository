package org.telosys.tools.repository.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;

import junit.env.telosys.tools.commons.LoggerProviderForUnitTests;

public class PersistenceManagerInMemoryTest {
	
	private final static String XML1 = 
	  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
	+ "<!-- Telosys Database Repository -->"
	+ "<root>"
	+ "<tableList databaseId=\"1\" databaseName=\"H2 in memory\" databaseProductName=\"H2\" generation=\"2015-07-10 15:53:19\">"
	+ "<table catalog=\"TEST\" databaseType=\"TABLE\" javaBean=\"Student\" name=\"STUDENT\" schema=\"PUBLIC\">"
	+ "<column dbComment=\"\" dbDefaultValue=\"\" dbName=\"ID\" dbNotNull=\"true\" dbPosition=\"1\" dbPrimaryKey=\"true\" dbSize=\"10\" dbTypeName=\"INTEGER\" inputType=\"number\" javaName=\"id\" javaType=\"java.lang.Integer\" jdbcTypeCode=\"4\" label=\"Id\" notNull=\"true\" selected=\"true\"/>"
	+ "<column dbComment=\"\" dbDefaultValue=\"\" dbName=\"FIRST_NAME\" dbNotNull=\"false\" dbPosition=\"2\" dbSize=\"40\" dbTypeName=\"VARCHAR\" inputType=\"text\" javaName=\"firstName\" javaType=\"java.lang.String\" jdbcTypeCode=\"12\" label=\"First name\" maxLength=\"40\" notNull=\"false\" selected=\"true\"/>"
	+ "<column dbComment=\"\" dbDefaultValue=\"\" dbName=\"LAST_NAME\" dbNotNull=\"false\" dbPosition=\"3\" dbSize=\"40\" dbTypeName=\"VARCHAR\" inputType=\"text\" javaName=\"lastName\" javaType=\"java.lang.String\" jdbcTypeCode=\"12\" label=\"Last name\" maxLength=\"40\" notNull=\"false\" selected=\"true\"/>"
	+ "</table>"
	+ "</tableList>"
	+ "</root>" ;

	@Test
	public void test1() throws TelosysToolsException {
		
		System.out.println("test1");
		//TelosysToolsLogger logger = new ConsoleLogger();
		TelosysToolsLogger logger = LoggerProviderForUnitTests.getLogger();
		FileInMemory fileInMemory = new FileInMemory();
		
		assertNotNull(fileInMemory.getContent());
		assertEquals(0, fileInMemory.getContent().length);
		
		fileInMemory.setContent(XML1);
		PersistenceManager pm = PersistenceManagerFactory.createPersistenceManager(fileInMemory, logger);
		RepositoryModel model = pm.load();

		EntityInDbModel student = model.getEntityByTableName("STUDENT");
		assertNotNull(student);
		assertEquals(3,student.getAttributes().size());
		
		student.setDatabaseCatalog("MYCATALOG");
		pm.save(model);
		
		System.out.println(fileInMemory.getContentAsString());
	}
	
}
