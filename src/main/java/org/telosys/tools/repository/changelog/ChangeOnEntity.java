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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.repository.model.Entity;

/**
 * Changes summary for one Entity
 * 
 * @author L. Guerin
 *
 */
public class ChangeOnEntity {
	
	private final String      entityName ;
	private final ChangeType  changeType ;
	private final Entity      entityBefore ;
	private final Entity      entityAfter ;

	private final List<ChangeOnColumn>     changesOnColumns     = new LinkedList<ChangeOnColumn>();
	private final List<ChangeOnForeignKey> changesOnForeignKeys = new LinkedList<ChangeOnForeignKey>();

	private void check(boolean expression, String errorMessage ) {
		if ( expression != true ) throw new RuntimeException(errorMessage);
	}

	/**
	 * Constructor 
	 * @param changeType
	 * @param entityBefore the 'before state' of the changed entity (for UPDATED and DELETED)
	 * @param entityAfter  the 'after state' of the changed entity (for UPDATED and CREATED)
	 */
	public ChangeOnEntity(ChangeType changeType, Entity entityBefore, Entity entityAfter) {
		super();
		this.changeType   = changeType ;
		this.entityBefore = entityBefore;
		this.entityAfter  = entityAfter;
		if ( changeType == ChangeType.CREATED ) {
			check ( entityBefore == null , "Entity CREATED must not have a 'before state'" ) ;
			check ( entityAfter  != null , "Entity CREATED must have an 'after state'" ) ;
			this.entityName = entityAfter.getName();
		}
		else if ( changeType == ChangeType.DELETED ) {
			check ( entityBefore != null , "Entity DELETED must have a 'before state'" ) ;
			check ( entityAfter  == null , "Entity DELETED must not have an 'after state'" ) ;
			this.entityName = entityBefore.getName();
		}
		else if ( changeType == ChangeType.UPDATED ) {
			check ( entityBefore != null , "Entity UPDATED must have a 'before state'" ) ;
			check ( entityAfter  != null , "Entity UPDATED must have an 'after state'" ) ;
			//check ( entityBefore.getName().equals(entityAfter.getName()), "Entity name is different between 'after' and 'before'");
			check ( StrUtil.identical(entityBefore.getName(), entityAfter.getName() ), "Entity name is different between 'after' and 'before'");
			this.entityName = entityAfter.getName();
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
	public String getEntityName() {
		return this.entityName;
	}

	/**
	 * Returns the 'before state' of the changed entity 
	 * @return
	 */
	public Entity getEntityBefore() {
		return this.entityBefore;
	}

	/**
	 * Returns the 'after state' of the changed entity 
	 * @return
	 */
	public Entity getEntityAfter() {
		return this.entityAfter;
	}
	
	/**
	 * Returns the 'CREATED' entity ( the 'entity after change' )
	 * @return
	 */
	public Entity getEntityCreated() {
		check ( this.changeType == ChangeType.CREATED , "Not a CREATED entity" ) ;
		return this.entityAfter;
	}
	/**
	 * Returns the 'DELETED' entity ( the 'entity before change' )
	 * @return
	 */
	public Entity getEntityDeleted() {
		check ( this.changeType == ChangeType.DELETED , "Not a DELETED entity" ) ;
		return this.entityBefore;
	}
	
	protected void addChangeOnColumn(ChangeOnColumn changeOnColumn) {
		changesOnColumns.add(changeOnColumn);
	}
	public List<ChangeOnColumn> getChangesOnColumn() {
		return changesOnColumns ;
	}

	protected void addChangeOnForeignKey(ChangeOnForeignKey changeOnForeignKey) {
		changesOnForeignKeys.add(changeOnForeignKey);
	}
	public List<ChangeOnForeignKey> getChangesOnForeignKey() {
		return changesOnForeignKeys ;
	}
}