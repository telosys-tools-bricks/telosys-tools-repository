package org.telosys.tools.repository.conversion.wrapper;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.repository.conversion.Wrappers;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.telosys.tools.repository.persistence.util.Xml;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EntityWrapperTest {
	
	private static final String CLASS_NAME_VALUE  = "MyClass" ;
	private static final String TABLE_NAME_VALUE  = "MYTABLE" ;
	private static final String SCHEMA_VALUE   = "MYSCHEMA" ;
	private static final String CATALOG_VALUE  = "MYCATALOG" ;
	private static final String TYPE_VALUE     = "TABLE" ;
	private static final String COMMENT_VALUE  = "MYCOMMENT" ;

	//-----------------------------------------------------------------------------------
	@Test
	public void testEntityToXml() throws TelosysToolsException {
		System.out.println("test1");

		EntityInDbModel entity = createEntity();
		System.out.println("EntityInDbModel : " + entity);
		
		Document doc = Xml.createDomDocument();

		Element element = Wrappers.ENTITY_WRAPPER.getXmlDesc(entity, doc);
		
		checkAttribute(element, RepositoryConst.TABLE_NAME, TABLE_NAME_VALUE);
		checkAttribute(element, RepositoryConst.TABLE_CATALOG, CATALOG_VALUE );
		checkAttribute(element, RepositoryConst.TABLE_SCHEMA, SCHEMA_VALUE);
		checkAttribute(element, RepositoryConst.TABLE_DATABASE_TYPE, TYPE_VALUE);
		checkAttribute(element, RepositoryConst.TABLE_JAVA_BEAN, CLASS_NAME_VALUE );
		checkAttribute(element, RepositoryConst.TABLE_DATABASE_COMMENT, COMMENT_VALUE); // ver 3.0.3
	}

	private EntityInDbModel createEntity() {
		EntityInDbModel entity = new EntityInDbModel();
		entity.setDatabaseTable(TABLE_NAME_VALUE);
		entity.setDatabaseCatalog(CATALOG_VALUE);
		entity.setDatabaseSchema(SCHEMA_VALUE);
		entity.setDatabaseType(TYPE_VALUE);
		entity.setClassName(CLASS_NAME_VALUE);
		entity.setDatabaseComment(COMMENT_VALUE);
		
//		entity.storeAttribute(attribute);
//		entity.storeForeignKey(foreignKey);
//		entity.storeLink(link);
		
		return entity ;
	}

	private void checkAttribute(Element element, String attributeName, String expectedValue) {
		Attr attribute = element.getAttributeNode(attributeName);
		assertNotNull( attribute );
		assertEquals(expectedValue, attribute.getNodeValue() );
	}

	//-----------------------------------------------------------------------------------
	@Test
	public void testXmlToEntity() throws TelosysToolsException {
		System.out.println("testXmlToEntity");
		checkEntity1( Wrappers.ENTITY_WRAPPER.getEntity( createXmlElement1() ) );
	}
	private Element createXmlElement1() throws TelosysToolsException {
		Document doc = Xml.createDomDocument();
		Element xmlElement = doc.createElement("table");
		xmlElement.setAttribute(RepositoryConst.TABLE_JAVA_BEAN, CLASS_NAME_VALUE); 
		xmlElement.setAttribute(RepositoryConst.TABLE_NAME, TABLE_NAME_VALUE);
		xmlElement.setAttribute(RepositoryConst.TABLE_CATALOG, CATALOG_VALUE); 
		xmlElement.setAttribute(RepositoryConst.TABLE_SCHEMA, SCHEMA_VALUE); 
		xmlElement.setAttribute(RepositoryConst.TABLE_DATABASE_TYPE, TYPE_VALUE); 
		xmlElement.setAttribute(RepositoryConst.TABLE_DATABASE_COMMENT, COMMENT_VALUE);
		return xmlElement;
	}
	private void checkEntity1(EntityInDbModel entity ) {
		assertEquals(CLASS_NAME_VALUE, entity.getClassName() );
		assertEquals(TABLE_NAME_VALUE, entity.getDatabaseTable() );
		assertEquals(SCHEMA_VALUE, entity.getDatabaseSchema() );
		assertEquals(CATALOG_VALUE, entity.getDatabaseCatalog() );
		assertEquals(TYPE_VALUE, entity.getDatabaseType() );
		assertEquals(COMMENT_VALUE, entity.getDatabaseComment() );
	}
	//-----------------------------------------------------------------------------------
	@Test
	public void testXmlToEntity2() throws TelosysToolsException {
		System.out.println("testXmlToEntity2");
		checkEntity2( Wrappers.ENTITY_WRAPPER.getEntity( createXmlElement2() ) );
	}
	private Element createXmlElement2() throws TelosysToolsException {
		Document doc = Xml.createDomDocument();
		Element xmlElement = doc.createElement("table");
		xmlElement.setAttribute(RepositoryConst.TABLE_JAVA_BEAN, CLASS_NAME_VALUE); 
		xmlElement.setAttribute(RepositoryConst.TABLE_NAME, TABLE_NAME_VALUE);
		xmlElement.setAttribute(RepositoryConst.TABLE_SCHEMA, SCHEMA_VALUE); 
		xmlElement.setAttribute(RepositoryConst.TABLE_CATALOG, CATALOG_VALUE); 
		xmlElement.setAttribute(RepositoryConst.TABLE_DATABASE_TYPE, TYPE_VALUE); 
		// No comment : xmlElement.setAttribute(RepositoryConst.TABLE_DATABASE_COMMENT, COMMENT_VALUE);
		return xmlElement;
	}
	private void checkEntity2(EntityInDbModel entity ) {
		assertEquals(CLASS_NAME_VALUE, entity.getClassName() );
		assertEquals(TABLE_NAME_VALUE, entity.getDatabaseTable() );
		assertEquals(SCHEMA_VALUE, entity.getDatabaseSchema() );
		assertEquals(CATALOG_VALUE, entity.getDatabaseCatalog() );
		assertEquals(TYPE_VALUE, entity.getDatabaseType() );
		assertEquals("", entity.getDatabaseComment() ); // Void comment
	}
}
