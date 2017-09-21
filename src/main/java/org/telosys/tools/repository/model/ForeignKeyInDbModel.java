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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyColumn;

public class ForeignKeyInDbModel implements Comparable<ForeignKeyInDbModel>, Serializable, ForeignKey
{
	private static final long serialVersionUID = 1L;

	private String name ;
	
	// Column name --> Column
	private Hashtable<String, ForeignKeyColumnInDbModel> foreignKeyColumns = new Hashtable<String,ForeignKeyColumnInDbModel>() ;
	
	//-------------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String v) {
		this.name = v;
	}
	
	//-------------------------------------------------------------------------------
	public String getTableName() { 
		// The table name is supposed to be the same for all foreignKeyColumns
		String tableName = "";
		// keep the last one 
		for (ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values()) {
			if ( ! StrUtil.nullOrVoid(fkc.getTableName()) ) {
				tableName = fkc.getTableName();
			}
		}
		return tableName;
	}
	
	//-------------------------------------------------------------------------------
//	public String getTableRef() {
	@Override
	public String getReferencedTableName() { // v 3.0.0	
		String referencedTableName = "";
		// keep the last one 
		for (ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values()) {
			if ( ! StrUtil.nullOrVoid(fkc.getTableRef()) ) {
				referencedTableName = fkc.getTableRef();
			}
		}
		return referencedTableName ;
	}
	
	//--------------------------------------------------------------------------
	
	/**
	 * Returns an array containing all the columns of the foreign key<br>
	 * The columns are sorted by sequence (the original database order).
	 * @return
	 */
	public ForeignKeyColumnInDbModel[] getForeignKeyColumns()
	{
		//return (ForeignKeyColumn[]) foreignKeyColumns.values().toArray(new ForeignKeyColumn[foreignKeyColumns.size()]);
		ForeignKeyColumnInDbModel[] array = (ForeignKeyColumnInDbModel[]) foreignKeyColumns.values().toArray(new ForeignKeyColumnInDbModel[foreignKeyColumns.size()]);
		Arrays.sort(array);
		return array ;
		
	}
	
//	/**
//	 * Returns a collection of all the columns of the foreign key.<br>
//	 * The columns are sorted by sequence (the original database order).
//	 * @return
//	 */
//	public Collection<ForeignKeyColumnInDbModel> getForeignKeyColumnsCollection()
//	{
//		//return foreignKeyColumns.values() ;
//		ForeignKeyColumnInDbModel[] array = getForeignKeyColumns();
//		return Arrays.asList(array);
//	}
//	
	
	/**
	 * Returns TRUE if the FK is "composite", that is to say based on more than 1 column
	 * @return
	 * @since v 3.0.0
	 */
	public boolean isComposite() {
		return foreignKeyColumns.size() > 1 ;
	}
	
	@Override
	public List<ForeignKeyColumn> getColumns() {
		//--- Returns the columns of this FK sorted by "sequence"
		ForeignKeyColumnInDbModel[] array = getForeignKeyColumns();
		List<ForeignKeyColumn> list = new LinkedList<ForeignKeyColumn>();
		for ( ForeignKeyColumn fkc : array ) {
			list.add(fkc);
		}
		return list;
	}

	
	public void storeForeignKeyColumn(ForeignKeyColumnInDbModel fkColumn)
	{
		foreignKeyColumns.put(fkColumn.getColumnName(), fkColumn);
	}

	public ForeignKeyColumnInDbModel getForeignKeyColumn(String columnName)
	{
		return (ForeignKeyColumnInDbModel) foreignKeyColumns.get(columnName);
	}
	
	public void removeForeignKeyColumn(ForeignKeyColumnInDbModel fkColumn)
	{
		foreignKeyColumns.remove(fkColumn.getColumnName() );
	}
	
	//-------------------------------------------------------------------------------
	//public boolean equals(Object o) // unreliable
	public boolean isIdentical(ForeignKeyInDbModel o)  // v 2.1.1	
	{
		if ( null == o ) return false ;
		if ( this == o ) return true ;
		if ( o.getClass() != this.getClass() ) return false ;
		
		ForeignKeyInDbModel fk = (ForeignKeyInDbModel) o ;
		
		//--- Not the same name
		if ( ! StrUtil.identical(this.name, fk.getName() ) ) return false ;
		
		ForeignKeyColumnInDbModel[] thisColumns = this.getForeignKeyColumns();
		ForeignKeyColumnInDbModel[] otherColumns = fk.getForeignKeyColumns();
		
		//--- Not the same number of fk colums
		if ( thisColumns.length != otherColumns.length ) return false ;
		
		for ( int i = 0 ; i < thisColumns.length ; i++ )
		{
			ForeignKeyColumnInDbModel c1 = thisColumns[i];
			ForeignKeyColumnInDbModel c2 = otherColumns[i];
			//--- Not the same fk colum 
			//if ( ! c1.equals(c2) ) return false ;
			if ( ! c1.isIdentical(c2) ) return false ;  // v 2.1.1	
		}
		
		//--- No difference
		return true ;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(ForeignKeyInDbModel other) {
		if ( other != null )
		{
			String sThisName  = this.getName() ;
			String sOtherName = other.getName();
			if ( sThisName != null && sOtherName != null )
			{
				return sThisName.compareTo(sOtherName);
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return  name + "|" + foreignKeyColumns.size() ;
	}

	//-------------------------------------------------------------------------------
	// DEFERRABLE (code & text) stored in each Foreign Key Column
	//-------------------------------------------------------------------------------
	@Override
	public String getDeferrable() { // v 3.0.0
		String deferrableText = "";
		// keep the last one 
		for (ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values()) {
			if ( ! StrUtil.nullOrVoid(fkc.getDeferrableText()) ) {
				deferrableText = fkc.getDeferrableText();
			}
		}
		return deferrableText ;
	}

	@Override
	public int getDeferrableCode() { // v 3.0.0
		int deferrableCode = 0;
		// keep the last one 
		for ( ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values() ) {
			deferrableCode = fkc.getDeferrableCode();
		}
		return deferrableCode ;
	}

	//-------------------------------------------------------------------------------
	// DELETE RULE (code & text) stored in each Foreign Key Column
	//-------------------------------------------------------------------------------
	@Override
	public String getDeleteRule() { // v 3.0.0
		String deleteRuleText = "";
		// keep the last one 
		for (ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values()) {
			if ( ! StrUtil.nullOrVoid(fkc.getDeleteRuleText()) ) {
				deleteRuleText = fkc.getDeleteRuleText();
			}
		}
		return deleteRuleText ;
	}

	@Override
	public int getDeleteRuleCode() { // v 3.0.0
		int deleteRuleCode = 0;
		// keep the last one 
		for ( ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values() ) {
			deleteRuleCode = fkc.getDeleteRuleCode();
		}
		return deleteRuleCode ;
	}

	//-------------------------------------------------------------------------------
	// UPDATE RULE (code & text) stored in each Foreign Key Column
	//-------------------------------------------------------------------------------
	@Override 
	public String getUpdateRule() { // v 3.0.0
		String updateRuleText = "";
		// keep the last one 
		for ( ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values() ) {
			if ( ! StrUtil.nullOrVoid( fkc.getUpdateRuleText() ) ) {
				updateRuleText = fkc.getUpdateRuleText();
			}
		}
		return updateRuleText ;
	}

	@Override
	public int getUpdateRuleCode() { // v 3.0.0
		int updateRuleCode = 0;
		// keep the last one 
		for ( ForeignKeyColumnInDbModel fkc : foreignKeyColumns.values() ) {
			updateRuleCode = fkc.getUpdateRuleCode();
		}
		return updateRuleCode ;
	}

}
