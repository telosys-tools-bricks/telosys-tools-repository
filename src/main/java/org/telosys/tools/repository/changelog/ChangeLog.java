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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ChangeLog {

	private final List<ChangeOnEntity> list = new LinkedList<ChangeOnEntity>();
	private final Date date = new Date();
	
	private void check(boolean expression, String errorMessage ) {
		if ( expression != true ) throw new RuntimeException(errorMessage);
	}
	
	/**
	 * Log a change : an entity created or updated or deleted
	 * @param changeOnEntity
	 */
	public void log(ChangeOnEntity changeOnEntity) {
		if ( changeOnEntity.getChangeType() == ChangeType.CREATED ) {
			check( changeOnEntity.getChangesOnColumn().size()     == 0, "Entity CREATED must not have changes on columns" );
			check( changeOnEntity.getChangesOnForeignKey().size() == 0, "Entity CREATED must not have changes on foreign keys" );
		}
		list.add(changeOnEntity);
	}
	
	public Date getDate() {
		return date ;
	}
	
	/**
	 * Returns the list of all the changes (all the entities created, updated or deleted)
	 * @return
	 */
	public List<ChangeOnEntity> getChanges() {
		return list ;
	}

	/**
	 * Returns the list of the changes by type : entities created or updated or deleted
	 * @param changeType
	 * @return
	 */
	public List<ChangeOnEntity> getChangesByType(ChangeType changeType) {
		LinkedList<ChangeOnEntity> selection = new LinkedList<ChangeOnEntity>();
		for ( ChangeOnEntity change : list ) {
			if ( change.getChangeType() == changeType ) {
				selection.add(change);
			}
		}
		return selection ;
	}
	
	public ChangeOnEntity getChangeByEntityName(String entityName) {
		ChangeOnEntity changeFound = null ;
		if ( entityName == null ) throw new IllegalArgumentException("Entity name is null");
		for ( ChangeOnEntity change : list ) {
			if ( entityName.equals(change.getEntityName()) ) {
				if ( changeFound != null ) throw new RuntimeException("Duplicated name '" + entityName +"' in changeLog");
				changeFound = change ;
			}
		}
		return changeFound ;
	}
	
	/**
	 * Returns the number of all entities (created, updated, deleted)
	 * @return
	 */
	public int getNumberOfEntities() {
		return list.size();
	}
	/**
	 * Returns the number of entities created
	 * @return
	 */
	public int getNumberOfEntitiesCreated() {
		return getNumberOfEntities(ChangeType.CREATED) ;
	}
	/**
	 * Returns the number of entities updated
	 * @return
	 */
	public int getNumberOfEntitiesUpdated() {
		return getNumberOfEntities(ChangeType.UPDATED) ;
	}
	/**
	 * Returns the number of entities deleted
	 * @return
	 */
	public int getNumberOfEntitiesDeleted() {
		return getNumberOfEntities(ChangeType.DELETED) ;
	}

	private int getNumberOfEntities(ChangeType changeType) {
		int count = 0 ;
		for ( ChangeOnEntity change : list ) {
			if ( change.getChangeType() == changeType ) {
				count++;
			}
		}
		return count ;
	}
}
