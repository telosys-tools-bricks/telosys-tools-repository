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

import org.telosys.tools.repository.model.TableGeneratorInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TableGeneratorWrapper {
	
	public TableGeneratorWrapper() {
		super();
	}

	public TableGeneratorInDbModel getTableGenerator(final Element elem) {
		final TableGeneratorInDbModel tg = new TableGeneratorInDbModel();
		tg.setName(elem.getAttribute(RepositoryConst.TABLE_GENERATOR_NAME));
		tg.setTable(elem.getAttribute(RepositoryConst.TABLE_GENERATOR_TABLE));
		tg.setPkColumnName(elem.getAttribute(RepositoryConst.TABLE_GENERATOR_PKCOLUMNNAME));
		tg.setPkColumnValue(elem.getAttribute(RepositoryConst.TABLE_GENERATOR_PKCOLUMNVALUE));
		tg.setValueColumnName(elem.getAttribute(RepositoryConst.TABLE_GENERATOR_VALUECOLUMNNAME));
		return tg;
	}

	public Element getXmlDesc(final TableGeneratorInDbModel tg, final Document doc) {
		final Element element = doc.createElement(RepositoryConst.TABLE_GENERATOR_ELEMENT);
		element.setAttribute(RepositoryConst.TABLE_GENERATOR_NAME, tg.getName());
		element.setAttribute(RepositoryConst.TABLE_GENERATOR_TABLE, tg.getTable());
		element.setAttribute(RepositoryConst.TABLE_GENERATOR_PKCOLUMNNAME, tg.getPkColumnName());
		element.setAttribute(RepositoryConst.TABLE_GENERATOR_PKCOLUMNVALUE, tg.getPkColumnValue());
		element.setAttribute(RepositoryConst.TABLE_GENERATOR_VALUECOLUMNNAME, tg.getValueColumnName());
		return element;
	}

}
