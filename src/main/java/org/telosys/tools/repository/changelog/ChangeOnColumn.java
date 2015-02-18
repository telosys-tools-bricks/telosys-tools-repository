/**
 *  Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.repository.changelog;

import org.telosys.tools.repository.model.Column;

public class ChangeOnColumn {
	
	private final ChangeType  changeType ;
	private final Column      columnBefore ;
	private final Column      columnAfter ;
	
	public ChangeOnColumn(ChangeType changeType, Column columnBefore, Column columnAfter) {
		super();
		this.changeType   = changeType ;
		this.columnBefore = columnBefore;
		this.columnAfter  = columnAfter;
	}
	
	public ChangeType getChangeType() {
		return this.changeType;
	}

	public Column getColumnBefore() {
		return this.columnBefore;
	}

	public Column getColumnAfter() {
		return this.columnAfter;
	}
}