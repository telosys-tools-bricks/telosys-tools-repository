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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.repository.model.EntityInDbModel;

/**
 * Changes summary for one Entity
 * 
 * @author L. Guerin
 *
 */
public class ChangeOnEntity {
	
	private final String           entityName ;
	private final ChangeType       changeType ;
	private final EntityInDbModel  entityBefore ;
	private final EntityInDbModel  entityAfter ;

	private final List<ChangeOnColumn>     changesOnColumns     = new LinkedList<>();
	private final List<ChangeOnForeignKey> changesOnForeignKeys = new LinkedList<>();
	private boolean databaseTypeHasChanged    = false ; 
	private boolean databaseCommentHasChanged = false ;  // since DbModel 3.0.3 (dec 2018)

	private void check(boolean expression, String errorMessage ) {
		if ( expression != true ) throw new RuntimeException(errorMessage);
	}

	//----------------------------------------------------------------------------------------------------------
	/**
	 * Constructor 
	 * @param changeType
	 * @param entityBefore the 'before state' of the changed entity (for UPDATED and DELETED)
	 * @param entityAfter  the 'after state' of the changed entity (for UPDATED and CREATED)
	 */
	public ChangeOnEntity(ChangeType changeType, EntityInDbModel entityBefore, EntityInDbModel entityAfter) {
		super();
		this.changeType   = changeType ;
		this.entityBefore = entityBefore;
		this.entityAfter  = entityAfter;
		if ( changeType == ChangeType.CREATED ) {
			check ( entityBefore == null , "Entity CREATED must not have a 'before state'" ) ;
			check ( entityAfter  != null , "Entity CREATED must have an 'after state'" ) ;
			this.entityName = entityAfter.getDatabaseTable();
		}
		else if ( changeType == ChangeType.DELETED ) {
			check ( entityBefore != null , "Entity DELETED must have a 'before state'" ) ;
			check ( entityAfter  == null , "Entity DELETED must not have an 'after state'" ) ;
			this.entityName = entityBefore.getDatabaseTable();
		}
		else if ( changeType == ChangeType.UPDATED ) {
			check ( entityBefore != null , "Entity UPDATED must have a 'before state'" ) ;
			check ( entityAfter  != null , "Entity UPDATED must have an 'after state'" ) ;
			check ( StrUtil.identical(entityBefore.getDatabaseTable(), entityAfter.getDatabaseTable() ), "Entity name is different between 'after' and 'before'");
			this.entityName = entityAfter.getDatabaseTable();
		}
		else {
			throw new RuntimeException("Invalid change type");
		}
	}
	
	//----------------------------------------------------------------------------------------------------------
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
	public EntityInDbModel getEntityBefore() {
		return this.entityBefore;
	}

	/**
	 * Returns the 'after state' of the changed entity 
	 * @return
	 */
	public EntityInDbModel getEntityAfter() {
		return this.entityAfter;
	}
	
	/**
	 * Returns the 'CREATED' entity ( the 'entity after change' )
	 * @return
	 */
	public EntityInDbModel getEntityCreated() {
		check ( this.changeType == ChangeType.CREATED , "Not a CREATED entity" ) ;
		return this.entityAfter;
	}
	/**
	 * Returns the 'DELETED' entity ( the 'entity before change' )
	 * @return
	 */
	public EntityInDbModel getEntityDeleted() {
		check ( this.changeType == ChangeType.DELETED , "Not a DELETED entity" ) ;
		return this.entityBefore;
	}
	
	//----------------------------------------------------------------------------------------------------------
	/**
	 * Add a "column change" for this entity
	 * @param changeOnColumn
	 */
	public void addChangeOnColumn(ChangeOnColumn changeOnColumn) {
		changesOnColumns.add(changeOnColumn);
	}
	
	/**
	 * Returns the list of all the changes on columns for this entity
	 * @return
	 */
	public List<ChangeOnColumn> getChangesOnColumn() {
		return changesOnColumns ;
	}

	//----------------------------------------------------------------------------------------------------------
	/**
	 * Add a "foreign key change" for this entity
	 * @param changeOnForeignKey
	 */
	public void addChangeOnForeignKey(ChangeOnForeignKey changeOnForeignKey) {
		changesOnForeignKeys.add(changeOnForeignKey);
	}
	
	/**
	 * Returns the list of all the changes on FOREIGN KEYS for this entity
	 * @return
	 */
	public List<ChangeOnForeignKey> getChangesOnForeignKey() {
		return changesOnForeignKeys ;
	}

	//----------------------------------------------------------------------------------------------------------
	public void setDatabaseTypeHasChanged(boolean value) {
		databaseTypeHasChanged = value ;
	}
	public boolean isDatabaseTypeHasChanged() {
		return databaseTypeHasChanged ;
	}
	
	//----------------------------------------------------------------------------------------------------------
	/**
	 * Set change flag for 'comment'
	 * @param value
	 * @since DbModel 3.0.3 (dec 2018)
	 */
	public void setDatabaseCommentHasChanged(boolean value) {
		databaseCommentHasChanged = value ;
	}
	/**
	 * Get change flag for 'comment'
	 * @return
	 * @since DbModel 3.0.3 (dec 2018)
	 */
	public boolean isDatabaseCommentHasChanged() {
		return databaseCommentHasChanged ;
	}
	
	//----------------------------------------------------------------------------------------------------------
	/**
	 * Return the number of changes for this entity <br>
	 * Number of changes on column + number of changes on FK + database type change
	 * @return
	 */
	public int getNumberOfChanges() {
		//return changesOnColumns.size() + changesOnForeignKeys.size() + ( databaseTypeHasChanged ? 1 : 0 );
		int count = 0 ;
		count += databaseTypeHasChanged ? 1 : 0 ;
		count += databaseCommentHasChanged ? 1 : 0 ;
		count += changesOnColumns.size() ;
		count += changesOnForeignKeys.size() ;
		return count ;
	}
}
