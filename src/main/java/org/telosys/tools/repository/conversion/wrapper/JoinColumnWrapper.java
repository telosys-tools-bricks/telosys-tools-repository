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
import org.telosys.tools.repository.model.JoinColumnInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JoinColumnWrapper {

	public JoinColumnWrapper() {
		super();
	}

	public JoinColumnInDbModel getJoinColumn(final Element table) {
		final JoinColumnInDbModel joinColumn = new JoinColumnInDbModel();
		
		joinColumn.setName(table.getAttribute(RepositoryConst.JOIN_COLUMN_NAME));
		joinColumn.setReferencedColumnName(table.getAttribute(RepositoryConst.JOIN_COLUMN_REFERENCEDCOLUMNNAME));
		
		if (StrUtil.nullOrVoid(table.getAttribute(RepositoryConst.JOIN_COLUMN_UNIQUE)) == false) {
			joinColumn.setUnique(StrUtil.getBoolean(table.getAttribute(RepositoryConst.JOIN_COLUMN_UNIQUE)));
		}
		if (StrUtil.nullOrVoid(table.getAttribute(RepositoryConst.JOIN_COLUMN_INSERTABLE)) == false) {
			joinColumn.setInsertable(StrUtil.getBoolean(table.getAttribute(RepositoryConst.JOIN_COLUMN_INSERTABLE)));
		}
		if (StrUtil.nullOrVoid(table.getAttribute(RepositoryConst.JOIN_COLUMN_NULLABLE)) == false) {
			joinColumn.setNullable(StrUtil.getBoolean(table.getAttribute(RepositoryConst.JOIN_COLUMN_NULLABLE)));
		}
		if (StrUtil.nullOrVoid(table.getAttribute(RepositoryConst.JOIN_COLUMN_UPDATABLE)) == false) {
			joinColumn.setUpdatable(StrUtil.getBoolean(table.getAttribute(RepositoryConst.JOIN_COLUMN_UPDATABLE)));
		}
		
		return joinColumn;
	}

	public Element getXmlDesc(final JoinColumnInDbModel joinColumn, final Document doc) {
		final Element table = doc.createElement(RepositoryConst.JOIN_COLUMN_ELEMENT);
		
		table.setAttribute(RepositoryConst.JOIN_COLUMN_NAME, joinColumn.getName());
		table.setAttribute(RepositoryConst.JOIN_COLUMN_REFERENCEDCOLUMNNAME, joinColumn.getReferencedColumnName());
		table.setAttribute(RepositoryConst.JOIN_COLUMN_UNIQUE, Boolean.toString(joinColumn.isUnique()));
		table.setAttribute(RepositoryConst.JOIN_COLUMN_INSERTABLE, Boolean.toString(joinColumn.isInsertable()));
		table.setAttribute(RepositoryConst.JOIN_COLUMN_NULLABLE, Boolean.toString(joinColumn.isNullable()));
		table.setAttribute(RepositoryConst.JOIN_COLUMN_UPDATABLE, Boolean.toString(joinColumn.isUpdatable()));
		return table;
	}

}
