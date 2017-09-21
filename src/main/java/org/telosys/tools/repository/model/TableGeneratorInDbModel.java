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
package org.telosys.tools.repository.model;

import java.io.Serializable;

/**
 * <tableGenerator name="MyGenerator" table="ID_GEN" pkColumnName="ID_NAME" valueColumnName="ID_VAL" pkColumnValue="INV_GEN" />
 * @author S.Labbe
 * 
 */
public class TableGeneratorInDbModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null ;

	private String table = null ;

	private String pkColumnName = null ;

	private String valueColumnName = null ;

	private String pkColumnValue = null ;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getPkColumnName() {
		return pkColumnName;
	}

	public void setPkColumnName(String pkColumnName) {
		this.pkColumnName = pkColumnName;
	}

	public String getValueColumnName() {
		return valueColumnName;
	}

	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	public String getPkColumnValue() {
		return pkColumnValue;
	}

	public void setPkColumnValue(String pkColumnValue) {
		this.pkColumnValue = pkColumnValue;
	}

}
