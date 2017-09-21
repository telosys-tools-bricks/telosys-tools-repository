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

public class LinksCriteria {

	private boolean bOwningSide  = true ;
	private boolean bInverseSide = true ;

	private boolean bManyToMany = true ;
	private boolean bManyToOne  = true ;
	private boolean bOneToMany  = true ;
	private boolean bOneToOne   = true ;
	
	
	public void setTypeManyToMany(boolean v) {
		bManyToMany = v;
	}

	public void setTypeManyToOne(boolean v) {
		bManyToOne = v;
	}

	public void setTypeOneToMany(boolean v) {
		bOneToMany = v;
	}

	public void setTypeOneToOne(boolean v) {
		bOneToOne = v;
	}

	public void setOwningSide(boolean v) {
		bOwningSide = v;
	}

	public void setInverseSide(boolean v) {
		bInverseSide = v;
	}

	public boolean isOwningSide() {
		return bOwningSide;
	}
	public boolean isInverseSide() {
		return bInverseSide;
	}
	
	public boolean isTypeManyToMany() {
		return bManyToMany ;
	}
	public boolean isTypeManyToOne()  {
		return bManyToOne;
	}
	public boolean isTypeOneToMany()  {
		return bOneToMany;
	}
	public boolean isTypeOneToOne()   {
		return bOneToOne ;
	}

	public String toString() {
		return "Owning Side = " + bOwningSide 
			+ ", Inverse Side = " + bInverseSide
			+ ", ManyToMany = " + bManyToMany
			+ ", ManyToOne = " + bManyToOne
			+ ", OneToMany = " + bOneToMany
			+ ", OneToOne = " + bOneToOne 
			;
	}
}
