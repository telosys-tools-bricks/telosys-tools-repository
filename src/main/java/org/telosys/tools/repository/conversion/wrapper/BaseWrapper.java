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

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BaseWrapper {
	
	public BaseWrapper() {
		super();
	}

	public RepositoryModel getBase(final Element base) {
		final RepositoryModel model = new RepositoryModel();
		model.setDatabaseName(base.getAttribute(RepositoryConst.TABLELIST_DATABASE_NAME));
		model.setDatabaseProductName(base.getAttribute(RepositoryConst.TABLELIST_DATABASE_PRODUCT_NAME));
		//model.setGenerationDate(RepositoryConst.getDate(base.getAttribute(RepositoryConst.TABLELIST_GENERATION)));
		model.setGenerationDate(RepositoryConst.parseDate(base.getAttribute(RepositoryConst.TABLELIST_GENERATION))); // v 2.1.1 (parseDate)
		model.setDatabaseId(StrUtil.getInt(base.getAttribute(RepositoryConst.TABLELIST_DATABASE_ID), -1) );	 // v 2.1.0	
		return model;
	}

	public Element getXmlDesc(final RepositoryModel model, final Document doc) {
		final Element table = doc.createElement(RepositoryConst.TABLELIST);
		table.setAttribute(RepositoryConst.TABLELIST_DATABASE_NAME, model.getDatabaseName());
		table.setAttribute(RepositoryConst.TABLELIST_DATABASE_PRODUCT_NAME, model.getDatabaseProductName());
		//table.setAttribute(RepositoryConst.TABLELIST_GENERATION, RepositoryConst.DATE_TIME_ISO_FORMAT.format(model.getGenerationDate()));
		table.setAttribute(RepositoryConst.TABLELIST_GENERATION, RepositoryConst.formatDate(model.getGenerationDate()));
		table.setAttribute(RepositoryConst.TABLELIST_DATABASE_ID, Integer.toString( model.getDatabaseId() ) ); // v 2.1.0
		
		return table;
	}

}
