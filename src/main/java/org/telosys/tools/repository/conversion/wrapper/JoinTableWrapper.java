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

import org.telosys.tools.repository.model.JoinTableInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JoinTableWrapper {

	public JoinTableWrapper() {
		super();
	}

	public JoinTableInDbModel getObject(final Element table) 
	{
		final JoinTableInDbModel joinTable = new JoinTableInDbModel();
		joinTable.setName(table.getAttribute(RepositoryConst.JOIN_TABLE_NAME));
		return joinTable;
	}

	public Element getXmlDesc(final JoinTableInDbModel joinTable, final Document doc) 
	{
		final Element table = doc.createElement(RepositoryConst.JOIN_TABLE_ELEMENT);
		table.setAttribute(RepositoryConst.JOIN_TABLE_NAME, joinTable.getName());
		return table;
	}

}
