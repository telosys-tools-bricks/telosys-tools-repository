package org.telosys.tools.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.JoinTable;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.repository.conversion.XmlConverter;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.LinkInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.util.Xml;
import org.w3c.dom.Document;

import junit.env.telosys.tools.commons.LoggerProviderForUnitTests;

public class RepositoryGeneratorTest extends AbstractTestCase {

	@Test
	public void test1() throws TelosysToolsException {
		printSeparator("test1");

		RepositoryModel repositoryModel = generateRepositoryModel(1);
		printModel(repositoryModel);
		assertTrue(repositoryModel.getDatabaseId() == DATABASE_ID_1 );
		assertEquals(2, repositoryModel.getNumberOfEntities() );

//		EntityInDbModel customerEntity = repositoryModel.getEntityByName("CUSTOMER") ;
		EntityInDbModel customerEntity = repositoryModel.getEntityByTableName("CUSTOMER") ;
		assertNotNull( customerEntity );
		assertNotNull( customerEntity.getAttributeByColumnName("CODE") );
		assertNotNull( customerEntity.getAttributeByColumnName("FIRST_NAME") );
		assertNotNull( customerEntity.getAttributeByColumnName("LAST_NAME") );
		//assertNotNull( customerEntity.getColumn("xxxx") );

//		EntityInDbModel countryEntity = repositoryModel.getEntityByName("COUNTRY") ;
		EntityInDbModel countryEntity = repositoryModel.getEntityByTableName("COUNTRY") ;
		assertNotNull( countryEntity );
		assertNotNull( countryEntity.getAttributeByColumnName("CODE") );
		assertNotNull( countryEntity.getAttributeByColumnName("NAME") );
	}

	@Test
	public void test2() throws TelosysToolsException {
		printSeparator("test2");
		
		RepositoryModel repositoryModel = generateRepositoryModel(2);
		printModel(repositoryModel);
		assertTrue(repositoryModel.getDatabaseId() == DATABASE_ID_1 );
		assertEquals(2, repositoryModel.getNumberOfEntities() );

//		EntityInDbModel studentEntity = repositoryModel.getEntityByName("STUDENT");
		EntityInDbModel studentEntity = repositoryModel.getEntityByTableName("STUDENT");
		assertNotNull(studentEntity);
		
//		EntityInDbModel teacherEntity = repositoryModel.getEntityByName("TEACHER");
		EntityInDbModel teacherEntity = repositoryModel.getEntityByTableName("TEACHER");
		assertNotNull(teacherEntity);
		
//		LinkInDbModel[] studentLinks = studentEntity.getLinks();
		LinkInDbModel[] studentLinks = studentEntity.getLinksArray();
		System.out.println("STUDENT links : " + studentLinks.length);
		assertTrue(studentLinks.length == 2);

//		LinkInDbModel[] teacherLinks = teacherEntity.getLinks();
		LinkInDbModel[] teacherLinks = teacherEntity.getLinksArray();
		System.out.println("TEACHER links : " + teacherLinks.length);
		assertTrue(teacherLinks.length == 2);
		
//		checkJavaName(studentLinks[0].getJavaFieldName(),studentLinks[1].getJavaFieldName(), "teacher", "teacher2" );
//		checkJavaName(teacherLinks[0].getJavaFieldName(),teacherLinks[1].getJavaFieldName(), "listOfStudent", "listOfStudent2" );		
		checkJavaName(studentLinks[0].getFieldName(),studentLinks[1].getFieldName(), "teacher", "teacher2" );
		checkJavaName(teacherLinks[0].getFieldName(),teacherLinks[1].getFieldName(), "listOfStudent", "listOfStudent2" );	
		
		//--- Check FK information stored in Attribute
		Attribute studentId = studentEntity.getAttributeByColumnName("ID");
		assertNotNull(studentId);
		assertFalse(studentId.isFK());
		assertFalse(studentId.isFKSimple());
		assertFalse(studentId.isFKComposite());
		assertNull (studentId.getReferencedEntityClassName() );

		Attribute studentTeacherCode1 = studentEntity.getAttributeByColumnName("TEACHER_CODE1") ;
		assertNotNull(studentTeacherCode1);
		assertTrue(studentTeacherCode1.isFK());
		assertTrue(studentTeacherCode1.isFKSimple());
		assertFalse(studentTeacherCode1.isFKComposite());
		assertEquals ("Teacher", studentTeacherCode1.getReferencedEntityClassName() );

		Attribute studentTeacherCode2 = studentEntity.getAttributeByColumnName("TEACHER_CODE2") ;
		assertNotNull(studentTeacherCode2);
		assertTrue(studentTeacherCode2.isFK());
		assertTrue(studentTeacherCode2.isFKSimple());
		assertFalse(studentTeacherCode2.isFKComposite());
		assertEquals ("Teacher", studentTeacherCode2.getReferencedEntityClassName() );
	}

	@Test
	public void test3() throws TelosysToolsException {
		printSeparator("test3");
		
		RepositoryModel repositoryModel = generateRepositoryModel(3);
		printModel(repositoryModel);
		assertTrue(repositoryModel.getDatabaseId() == DATABASE_ID_1 );
		assertEquals(2, repositoryModel.getNumberOfEntities() );

//		EntityInDbModel studentEntity = repositoryModel.getEntityByName("STUDENT");
		EntityInDbModel studentEntity = repositoryModel.getEntityByTableName("STUDENT");
		assertNotNull(studentEntity);
		
//		EntityInDbModel teacherEntity = repositoryModel.getEntityByName("TEACHER");
		EntityInDbModel teacherEntity = repositoryModel.getEntityByTableName("TEACHER");
		assertNotNull(teacherEntity);
		
//		LinkInDbModel[] studentLinks = studentEntity.getLinks();
		LinkInDbModel[] studentLinks = studentEntity.getLinksArray();
		System.out.println("STUDENT links : " + studentLinks.length);
		assertTrue(studentLinks.length == 2);

//		LinkInDbModel[] teacherLinks = teacherEntity.getLinks();
		LinkInDbModel[] teacherLinks = teacherEntity.getLinksArray();
		System.out.println("TEACHER links : " + teacherLinks.length);
		assertTrue(teacherLinks.length == 2);
		
//		checkJavaName(studentLinks[0].getJavaFieldName(),studentLinks[1].getJavaFieldName(), "teacher2", "teacher3" );
//		checkJavaName(teacherLinks[0].getJavaFieldName(),teacherLinks[1].getJavaFieldName(), "listOfStudent2", "listOfStudent3" );		
		checkJavaName(studentLinks[0].getFieldName(),studentLinks[1].getFieldName(), "teacher2", "teacher3" );
		checkJavaName(teacherLinks[0].getFieldName(),teacherLinks[1].getFieldName(), "listOfStudent2", "listOfStudent3" );		
	}

	@Test
	public void test4() throws TelosysToolsException {
		printSeparator("test4");
		
		RepositoryModel repositoryModel = generateRepositoryModel(4);
		printModel(repositoryModel);
		assertEquals(3, repositoryModel.getNumberOfEntities() );

//		EntityInDbModel studentEntity = repositoryModel.getEntityByName("STUDENT");
		EntityInDbModel studentEntity = repositoryModel.getEntityByTableName("STUDENT");
		assertNotNull(studentEntity);
		assertFalse( studentEntity.isJoinTable() );
		assertEquals(3, studentEntity.getLinksCount() ); // One LINK "OneToMany"
		
		LinkedList<Link> linksBasedOnFK = new LinkedList<Link>() ;
		LinkedList<Link> linksBasedOnJoinTable = new LinkedList<Link>() ;
		System.out.println("Student nb links : " + studentEntity.getLinks().size() );
		for ( Link link : studentEntity.getLinks() ) {
			System.out.println("Student link : " + link.getId() 
					+ " getForeignKeyName = " + link.getForeignKeyName() 
					+ " getJoinTableName = " + link.getJoinTableName() 
					);
			if ( link.isBasedOnForeignKey() )  {
				System.out.println("Student link : " + link.getId() + " based on FOREIGN KEY");
				linksBasedOnFK.add(link) ;
			}
			if ( link.isBasedOnJoinTable() )  {
				System.out.println("Student link : " + link.getId() + " based on JOIN TABLE");
				linksBasedOnJoinTable.add(link) ;
			}
		}
		assertEquals(2, linksBasedOnFK.size() ); // 2 FK --> Teacher
		assertEquals(1, linksBasedOnJoinTable.size() ); // 1 Join Table "Relation1"
		//--- Link based on a Join Table
		Link linkJT = linksBasedOnJoinTable.getFirst();
		assertTrue(linkJT.isBasedOnJoinTable());
		JoinTable joinTable = linkJT.getJoinTable();
		
		List<JoinColumn> joinColumns = joinTable.getJoinColumns();
		assertEquals(1, joinColumns.size() ); //
		String jcName = joinColumns.get(0).getName() ;
		System.out.println("Join colum name : " + jcName );
		assertTrue (  jcName.equalsIgnoreCase("student_id" ) || jcName.equalsIgnoreCase("teacher_code" ) ) ;
		
		List<JoinColumn> inverseJoinColumns = joinTable.getInverseJoinColumns();
		assertEquals(1, inverseJoinColumns.size() ); //
		String ijcName = inverseJoinColumns.get(0).getName() ;
		System.out.println("Inverse join colum name : " + ijcName );
		
		assertNull(linkJT.getJoinColumns());
		
		if ( jcName.equalsIgnoreCase("student_id" ) ) {
			assertTrue ( ijcName.equalsIgnoreCase("teacher_code" ) ) ;
		}
		else if ( jcName.equalsIgnoreCase("teacher_code" ) ) {
			assertTrue ( ijcName.equalsIgnoreCase("student_id" ) ) ;
		}
//		List<JoinColumn> joinColumns = linkJT.getJoinColumns();
//		assertEquals(1, joinColumns.size() ); // 
		
		
//		EntityInDbModel teacherEntity = repositoryModel.getEntityByName("TEACHER");
		EntityInDbModel teacherEntity = repositoryModel.getEntityByTableName("TEACHER");
		assertNotNull(teacherEntity);
		assertFalse( teacherEntity.isJoinTable() );

//		EntityInDbModel relation1Entity = repositoryModel.getEntityByName("RELATION1");
		EntityInDbModel relation1Entity = repositoryModel.getEntityByTableName("RELATION1");
		assertNotNull(relation1Entity);
		assertTrue( relation1Entity.isJoinTable() );
		
		
//		LinkInDbModel[] studentLinks = studentEntity.getLinks();
		LinkInDbModel[] studentLinks = studentEntity.getLinksArray();
		System.out.println("STUDENT links : " + studentLinks.length);
		assertEquals(3, studentLinks.length);

//		LinkInDbModel[] teacherLinks = teacherEntity.getLinks();
		LinkInDbModel[] teacherLinks = teacherEntity.getLinksArray();
		System.out.println("TEACHER links : " + teacherLinks.length);
		assertEquals(3, teacherLinks.length);
		
//		LinkInDbModel[] relation1Links = relation1Entity.getLinks();
		LinkInDbModel[] relation1Links = relation1Entity.getLinksArray();
		System.out.println("RELATION1 links : " + relation1Links.length);
		assertEquals(0, relation1Links.length);
		
//		checkJavaName(studentLinks[0].getJavaFieldName(),studentLinks[1].getJavaFieldName(), "teacher2", "teacher3" );
//		checkJavaName(teacherLinks[0].getJavaFieldName(),teacherLinks[1].getJavaFieldName(), "listOfStudent2", "listOfStudent3" );	
		Document xmlDocument = convertToXml(repositoryModel);
		
		convertToModel(xmlDocument);

	}

	@Test
	public void test5() throws TelosysToolsException {
		printSeparator("test5");
		
		RepositoryModel repositoryModel = generateRepositoryModel(5);
		printModel(repositoryModel);
		assertEquals(2, repositoryModel.getNumberOfEntities() );

		checkStudentEntity(repositoryModel.getEntityByTableName("STUDENT"));
		checkTeacherEntity(repositoryModel.getEntityByTableName("TEACHER"));		
		
		Document doc = convertToXml(repositoryModel);
		
		RepositoryModel model2 = convertToModel(doc);
		assertEquals(repositoryModel.getNumberOfEntities(), model2.getNumberOfEntities());
		
		checkStudentEntity(model2.getEntityByTableName("STUDENT"));
		checkTeacherEntity(model2.getEntityByTableName("TEACHER"));
	}
	
	private Document convertToXml(RepositoryModel repositoryModel) throws TelosysToolsException {
		//XmlConverter xmlConverter = new XmlConverter(new ConsoleLogger());
		XmlConverter xmlConverter = new XmlConverter(LoggerProviderForUnitTests.getLogger());
	
		Document doc = xmlConverter.modelToXmlDocument(repositoryModel);
		
		System.out.println("XML DOCUMENT : ");
		System.out.println( Xml.toString(doc) );
		
		return doc ;
	}		
	
	private RepositoryModel convertToModel(Document xmlDocument) throws TelosysToolsException {
		//XmlConverter xmlConverter = new XmlConverter(new ConsoleLogger());
		XmlConverter xmlConverter = new XmlConverter(LoggerProviderForUnitTests.getLogger());
		RepositoryModel model = xmlConverter.xmlDocumentToModel(xmlDocument);
		return model ;
	}
	
	private void checkStudentEntity(EntityInDbModel studentEntity) {
		System.out.println("Check STUDENT entity");

		assertNotNull(studentEntity);
		
		assertFalse( studentEntity.isJoinTable() );
		
		//--- Attributes (columns)
		assertEquals(4, studentEntity.getAttributesCount() ) ;

		AttributeInDbModel studentId = studentEntity.getAttributeByColumnName("ID");
		assertNotNull(studentId);
		AttributeInDbModel studentFirstName = studentEntity.getAttributeByColumnName("FIRST_NAME");
		assertNotNull(studentFirstName);
		AttributeInDbModel studentLastName = studentEntity.getAttributeByColumnName("LAST_NAME");
		assertNotNull(studentLastName);
		
		//--- Foreign Keys
		assertEquals(1, studentEntity.getForeignKeys().length ) ;
		assertEquals("STUDENT", studentEntity.getForeignKeys()[0].getTableName() ) ;
		assertEquals("TEACHER", studentEntity.getForeignKeys()[0].getReferencedTableName() ) ;
		
		//--- Links
		LinkInDbModel[] studentLinks = studentEntity.getLinksArray();
		System.out.println("Number of links : " + studentLinks.length);
		assertEquals(1, studentLinks.length);

		System.out.println("Student link : ");
		LinkInDbModel studentLink = studentLinks[0] ;
		
		System.out.println(" . getCardinality : " + studentLink.getCardinality()  );
		assertEquals( Cardinality.MANY_TO_ONE, studentLink.getCardinality() ); 
		
		System.out.println(" . getMappedBy : '" + studentLink.getMappedBy() +"'" );
		assertNull( studentLink.getMappedBy() ); 
		
		System.out.println(" . isOwningSide  : " + studentLink.isOwningSide() );
		assertTrue(studentLink.isOwningSide() );
		System.out.println(" . isInverseSide : " + studentLink.isInverseSide() );
		assertFalse(studentLink.isInverseSide() );

		System.out.println(" . getForeignKeyName : " + studentLink.getForeignKeyName() );
		assertNotNull(studentLink.getForeignKeyName() );

		assertNotNull(studentLink.getJoinColumns() );
		
		List<JoinColumn> joinColumns = studentLink.getJoinColumns();
		System.out.println(" . getJoinColumns / size : " + joinColumns.size() );
		assertEquals(1, joinColumns.size() );
	}
	
	private void checkTeacherEntity(EntityInDbModel teacherEntity) {
		System.out.println("Check TEACHER entity");
		
		assertNotNull(teacherEntity);
		assertFalse( teacherEntity.isJoinTable() );
		assertEquals(0, teacherEntity.getForeignKeys().length ) ; // No FK
		
		LinkInDbModel[] teacherLinks = teacherEntity.getLinksArray();
		System.out.println("Number of  links : " + teacherLinks.length);
		assertEquals(1, teacherLinks.length);
	}
}
