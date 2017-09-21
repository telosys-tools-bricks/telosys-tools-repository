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
import java.util.List;

import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.JoinTable;


/**
 * "JoinTable" model class <br>
 *
 */
public class JoinTableInDbModel implements Serializable, JoinTable 
{
	private static final long serialVersionUID = 1L;

	private String name ;

	private String schema ;

	private String catalog ;
	
//	private JoinColumnsInDbModel        joinColums = null ;
	private List<JoinColumnInDbModel>   joinColumns = null ;

//	private InverseJoinColumnsInDbModel inverseJoinColums = null ;
	private List<JoinColumnInDbModel>   inverseJoinColumns = null ;
	
	
	//--------------------------------------------------------------------------
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//--------------------------------------------------------------------------
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	//--------------------------------------------------------------------------
	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	//--------------------------------------------------------------------------
//	private List<JoinColumn> toListOfJoinColumns(List<JoinColumnInDbModel> listOfJoinColumnInDbModel) {
//		LinkedList<JoinColumn> joinColumns = new LinkedList<JoinColumn>();
//		for ( JoinColumn jc : listOfJoinColumnInDbModel ) {
//			joinColumns.add(jc);
//		}
//		return joinColumns ;
//	}

//	public void setJoinColumns( JoinColumnsInDbModel joinColumns ) {
//		joinColums = joinColumns ;
//	}
	public void setJoinColumns( List<JoinColumnInDbModel> joinColumns ) 	{
		this.joinColumns = joinColumns ;
	}
//	public JoinColumnsInDbModel getJoinColumns() {
//		return joinColums ;
//	}
	@Override
	public List<JoinColumn> getJoinColumns() {
//		LinkedList<JoinColumn> joinColumns = new LinkedList<JoinColumn>();
//		for ( JoinColumn jc : joinColumsInDbModel ) {
//			joinColumns.add(jc);
//		}
//		return joinColumns ;
//		return toListOfJoinColumns(this.joinColumns) ;
		return DbModelUtil.toListOfJoinColumns(this.joinColumns);
	}
	
	//--------------------------------------------------------------------------
//	public void setInverseJoinColumns( InverseJoinColumnsInDbModel joinColumns ) {
//		inverseJoinColums = joinColumns ;
//	}
	public void setInverseJoinColumns( List<JoinColumnInDbModel> inverseJoinColumns ) {
		this.inverseJoinColumns = inverseJoinColumns ;
	}
//	public InverseJoinColumnsInDbModel getInverseJoinColumns() {
//		return inverseJoinColums ;
//	}
	@Override
	public List<JoinColumn> getInverseJoinColumns() {
		//return toListOfJoinColumns(this.inverseJoinColumns) ;
		return DbModelUtil.toListOfJoinColumns(this.inverseJoinColumns);		
	}
	
	//--------------------------------------------------------------------------
	public String getCheckSum() {
		return name;
	}

// Unreliable & Unused (removed in v 2.1.1 )
//	/**
//	 * Control functionnal equality of link
//	 * @param otherLink
//	 * @return if param link is equal to current link
//	 */
//	public boolean equals(final JoinTable joinTable) {
//		// considers that the comparison of the name of the join table is sufficient to establish the equity of JoinTable
//		if (joinTable != null) {
//			if (joinTable.getCheckSum().equals(this.getCheckSum())) {
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}
	
}
