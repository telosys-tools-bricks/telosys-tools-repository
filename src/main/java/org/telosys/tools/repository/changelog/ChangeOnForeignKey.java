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
package org.telosys.tools.repository.changelog;

import org.telosys.tools.repository.model.ForeignKeyInDbModel;

public class ChangeOnForeignKey {
	
	private final String     fkName ;
	private final ChangeType changeType ;
	private final ForeignKeyInDbModel foreignKeyBefore ;
	private final ForeignKeyInDbModel foreignKeyAfter ;
	
	private void check(boolean expression, String errorMessage ) {
		if ( expression != true ) throw new RuntimeException(errorMessage);
	}

	/**
	 * Constructor
	 * @param changeType
	 * @param foreignKeyBefore the 'before state' of the changed FK (for UPDATED and DELETED)
	 * @param foreignKeyAfter  the 'after state' of the changed FK (for UPDATED and CREATED)
	 */
	public ChangeOnForeignKey(ChangeType changeType, ForeignKeyInDbModel foreignKeyBefore, ForeignKeyInDbModel foreignKeyAfter) {
		super();
		this.changeType   = changeType ;
		this.foreignKeyBefore = foreignKeyBefore;
		this.foreignKeyAfter  = foreignKeyAfter;
		
		if ( changeType == ChangeType.CREATED ) {
			check ( foreignKeyBefore == null , "ForeignKey CREATED must not have a 'before state'" ) ;
			check ( foreignKeyAfter  != null , "ForeignKey CREATED must have an 'after state'" ) ;
			this.fkName = foreignKeyAfter.getName();
		}
		else if ( changeType == ChangeType.DELETED ) {
			check ( foreignKeyBefore != null , "ForeignKey DELETED must have a 'before state'" ) ;
			check ( foreignKeyAfter  == null , "ForeignKey DELETED must not have an 'after state'" ) ;
			this.fkName = foreignKeyBefore.getName();
		}
		else if ( changeType == ChangeType.UPDATED ) {
			check ( foreignKeyBefore != null , "ForeignKey UPDATED must have a 'before state'" ) ;
			check ( foreignKeyAfter  != null , "ForeignKey UPDATED must have an 'after state'" ) ;
			check ( foreignKeyBefore.getName().equals(foreignKeyAfter.getName()), "FK name is different between 'after' and 'before'");
			this.fkName = foreignKeyAfter.getName();
		}
		else {
			throw new RuntimeException("Invalid change type");
		}
	}
	
	/**
	 * Returns the change type ( CREATED / UPDATED / DELETED )
	 * @return
	 */
	public ChangeType getChangeType() {
		return this.changeType;
	}
	
	/**
	 * Returns the entity name
	 * @return
	 */
	public String getForeignKeyName() {
		return this.fkName;
	}


	/**
	 * Returns the 'before state' of the changed FK 
	 * @return
	 */
	public ForeignKeyInDbModel getForeignKeyBefore() {
		return this.foreignKeyBefore;
	}

	/**
	 * Returns the 'after state' of the changed FK 
	 * @return
	 */
	public ForeignKeyInDbModel getForeignKeyAfter() {
		return this.foreignKeyAfter;
	}
	
	/**
	 * Returns the 'CREATED' FK ( the 'FK after change' )
	 * @return
	 */
	public ForeignKeyInDbModel getForeignKeyCreated() {
		check ( this.changeType == ChangeType.CREATED , "Not a CREATED Foreign Key" ) ;
		return this.foreignKeyAfter;
	}
	
	/**
	 * Returns the 'DELETED' FK ( the 'FK before change' )
	 * @return
	 */
	public ForeignKeyInDbModel getForeignKeyDeleted() {
		check ( this.changeType == ChangeType.DELETED , "Not a DELETED Foreign Key" ) ;
		return this.foreignKeyBefore;
	}
	
}
