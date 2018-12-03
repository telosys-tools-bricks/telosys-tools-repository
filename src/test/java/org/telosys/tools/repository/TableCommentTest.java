package org.telosys.tools.repository;

import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.repository.changelog.ChangeLog;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TableCommentTest extends AbstractTestCase {
	
	@Test
	public void test999001_1() throws TelosysToolsException {
		printSeparator("test999001 : Table comment");

		RepositoryModel repositoryModel = generateRepositoryModel(999001);
		assertEquals(2, repositoryModel.getNumberOfEntities() );

		EntityInDbModel teacherEntity = repositoryModel.getEntityByTableName("TEACHER") ;
		assertNotNull( teacherEntity );
		assertNotNull( teacherEntity.getAttributeByColumnName("CODE") );
		assertNotNull( teacherEntity.getAttributeByColumnName("NAME") );
		assertEquals("", teacherEntity.getDatabaseComment() );
		

		EntityInDbModel studentEntity = repositoryModel.getEntityByTableName("STUDENT") ;
		assertNotNull( studentEntity );
		assertNotNull( studentEntity.getDatabaseComment() );
		assertEquals("My student comment", studentEntity.getDatabaseComment() );
	}

	@Test
	public void test999001_2() throws TelosysToolsException {
		printSeparator("test999001 : Table comment");

		UpdateResult updateResult = generateAndUpdateRepositoryModel(999001);
		
		RepositoryModel repositoryModel = updateResult.getRepositoryModel();
		
		assertEquals(2, repositoryModel.getNumberOfEntities() );

		EntityInDbModel teacherEntity = repositoryModel.getEntityByTableName("TEACHER") ;
		assertNotNull( teacherEntity );
		assertNotNull( teacherEntity.getAttributeByColumnName("CODE") );
		assertNotNull( teacherEntity.getAttributeByColumnName("NAME") );
		assertEquals("My teacher comment", teacherEntity.getDatabaseComment() );
		

		EntityInDbModel studentEntity = repositoryModel.getEntityByTableName("STUDENT") ;
		assertNotNull( studentEntity );
		assertNotNull( studentEntity.getDatabaseComment() );
		assertEquals("My student comment", studentEntity.getDatabaseComment() );
		
		ChangeLog changeLog = updateResult.getChangeLog();
		assertEquals(1, changeLog.getNumberOfEntitiesUpdated());
	}

}
