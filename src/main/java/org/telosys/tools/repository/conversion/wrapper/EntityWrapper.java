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
package org.telosys.tools.repository.conversion.wrapper;

import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EntityWrapper {
	
	public EntityWrapper() {
		super();
	}

	public EntityInDbModel getEntity(final Element table) {
		final EntityInDbModel entity = new EntityInDbModel();
//		entity.setBeanJavaClass(table.getAttribute(RepositoryConst.TABLE_JAVA_BEAN));
		entity.setClassName(table.getAttribute(RepositoryConst.TABLE_JAVA_BEAN)); // v 3.0.0
		
//		entity.setName(table.getAttribute(RepositoryConst.TABLE_NAME));
		entity.setDatabaseTable(table.getAttribute(RepositoryConst.TABLE_NAME)); // v 3.0.0
		
//		entity.setCatalog(table.getAttribute(RepositoryConst.TABLE_CATALOG)); 
		entity.setDatabaseCatalog(table.getAttribute(RepositoryConst.TABLE_CATALOG));  // v 3.0.0
		
//		entity.setSchema(table.getAttribute(RepositoryConst.TABLE_SCHEMA));
		entity.setDatabaseSchema(table.getAttribute(RepositoryConst.TABLE_SCHEMA)); // v 3.0.0
		
		entity.setDatabaseType(table.getAttribute(RepositoryConst.TABLE_DATABASE_TYPE)); // added in v 2.0.7
		return entity;
	}

	public Element getXmlDesc(final EntityInDbModel entity, final Document doc) {
		final Element table = doc.createElement(RepositoryConst.TABLE);
		
//		table.setAttribute(RepositoryConst.TABLE_JAVA_BEAN, entity.getBeanJavaClass());
		table.setAttribute(RepositoryConst.TABLE_JAVA_BEAN, entity.getClassName()); // v 3.0.0
		
//		table.setAttribute(RepositoryConst.TABLE_NAME, entity.getName());		
		table.setAttribute(RepositoryConst.TABLE_NAME, entity.getDatabaseTable());	// v 3.0.0	

//		table.setAttribute(RepositoryConst.TABLE_CATALOG, entity.getCatalog());
		table.setAttribute(RepositoryConst.TABLE_CATALOG, entity.getDatabaseCatalog()); // v 3.0.0
		
//		table.setAttribute(RepositoryConst.TABLE_SCHEMA, entity.getSchema());
		table.setAttribute(RepositoryConst.TABLE_SCHEMA, entity.getDatabaseSchema()); // v 3.0.0
		
		table.setAttribute(RepositoryConst.TABLE_DATABASE_TYPE, entity.getDatabaseType()); // added in v 2.0.7
		return table;
	}

}
