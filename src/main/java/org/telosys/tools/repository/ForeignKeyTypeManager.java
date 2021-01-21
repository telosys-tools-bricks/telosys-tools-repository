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
package org.telosys.tools.repository;

import java.util.List;

import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyColumn;
import org.telosys.tools.generic.model.ForeignKeyPart;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.ForeignKeyPartInDbModel;
import org.telosys.tools.repository.model.RepositoryModel;


/**
 * Utility class providing the unique method to set the Foreign Key type <br>
 * for each attribute involved in a Foreign Key ( Simple or Composite FK ) <br>
 *  
 * @author Laurent GUERIN
 *
 */
public class ForeignKeyTypeManager 
{
	private final static int FK_SIMPLE    = 1 ;
	private final static int FK_COMPOSITE = 2 ;
	
	/**
	 * Set the Foreign Key type for each attribute involved in a Foreign Key <br>
	 * NB : This method must be called before the links generation<br>
	 * ( it can be called many times on the same model ) 
	 * 
	 * @param repositoryModel
	 * @since v 3.0.0
	 */
	public void setAttributesForeignKeyInformation(RepositoryModel repositoryModel) {
		
		for ( Entity entity : repositoryModel.getEntities() ) {
			List<ForeignKey> foreignKeys = entity.getDatabaseForeignKeys();
			for ( ForeignKey fk : foreignKeys ) {
				EntityInDbModel referencedEntity = repositoryModel.getEntityByTableName( fk.getReferencedTableName() );
				// Check if found
				if ( referencedEntity == null ) {
					String msg = "Table '" + fk.getReferencedTableName() + "' not found in model." 
							+ " Referenced by Foreign Key '" + fk.getName() + "' : "
							+ " table '" + fk.getTableName() + "' --> '" + fk.getReferencedTableName() +"'" ;
					throw new RuntimeException(msg);
				}
				// Set FK type for each attribute involved in a FK  
				setAttributesFKInfo((EntityInDbModel)entity, fk, referencedEntity); 
				// Set FK parts for each attribute involved in one or more FK  ( ver 3.3.0 )
				setAttributesFKParts((EntityInDbModel)entity, fk, referencedEntity); 
			}
		}		
	}
	
	/**
	 * Set the FK information for all the attributes associated with the given FK
	 * @param entity
	 * @param fk
	 * @param referencedEntity
	 * @since v 3.0.0
	 */
	private void setAttributesFKInfo(EntityInDbModel entity, ForeignKey fk, EntityInDbModel referencedEntity ) {
		List<ForeignKeyColumn> fkColumns = fk.getColumns() ;
		if ( fkColumns != null ) {
			if ( fkColumns.size() > 1 ) {
				//--- Composite FK ( many columns )
				for ( ForeignKeyColumn fkCol : fkColumns ) {
					setAttributeFKInfo(entity, fkCol, FK_COMPOSITE, referencedEntity) ;
				}
			}
			else if ( fk.getColumns().size() == 1 ) {
				//--- Simple FK ( only one column )
				ForeignKeyColumn fkCol = fkColumns.get(0);
				setAttributeFKInfo(entity, fkCol, FK_SIMPLE, referencedEntity) ;
			}
		}
	}
	
	/**
	 * Setting 'Foreign Key Parts' for attributes involved in Foreign Key(s)
	 * Added in ver 3.3.0
	 * @param entity
	 * @param fk
	 * @param referencedEntity
	 */
	private void setAttributesFKParts(EntityInDbModel entity, ForeignKey fk, EntityInDbModel referencedEntity ) {
		List<ForeignKeyColumn> fkColumns = fk.getColumns() ;
		if ( fkColumns != null ) {
			for ( ForeignKeyColumn fkCol : fkColumns ) {
				AttributeInDbModel attribute = entity.getAttributeByColumnName(fkCol.getColumnName());
				if ( attribute != null ) {
					// Build FK part
					AttributeInDbModel referencedAttribute = referencedEntity.getAttributeByColumnName(
							fkCol.getReferencedColumnName());
					ForeignKeyPart fkPart = new ForeignKeyPartInDbModel(
							fk.getName(),
							fk.getReferencedTableName(), 
							fkCol.getReferencedColumnName(),
							referencedEntity.getClassName(), 
							referencedAttribute.getName());
					// Add FK part
					attribute.addFKPart(fkPart);
				}
			}
		}
	}
	
	/**
	 * Set the FK information for the attribute associated with the given FK Column
	 * @param entity
	 * @param fkCol
	 * @param fkType
	 * @param referencedEntity
	 * @since v 3.0.0
	 */
	private void setAttributeFKInfo(EntityInDbModel entity, ForeignKeyColumn fkCol, int fkType, EntityInDbModel referencedEntity ) {
		String fkColName = fkCol.getColumnName();
		AttributeInDbModel attribute = entity.getAttributeByColumnName(fkColName);
		if ( attribute != null ) {
			if ( fkType == FK_SIMPLE ) {
				attribute.setFKSimple(true);
				// Always set the "FK Simple" reference as the main referenced entity (priority = 1)
				attribute.setReferencedEntityClassName(referencedEntity.getClassName());
			}
			else if ( fkType == FK_COMPOSITE ) {
				attribute.setFKComposite(true);
				// Set the reference only if there's no "FK Simple" reference (priority = 2)
				if ( ! attribute.isFKSimple() ) {
					attribute.setReferencedEntityClassName(referencedEntity.getClassName());
				}
			}
		}
		else {
			throw new IllegalStateException("Cannot get attribute by column name'" + fkColName + "'");
		}
	}

}
