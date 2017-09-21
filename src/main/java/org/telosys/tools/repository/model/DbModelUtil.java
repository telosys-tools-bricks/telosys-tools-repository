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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generic.model.JoinColumn;

public class DbModelUtil {
	
	/**
	 * Converts a list of 'JoinColumnInDbModel' to a list of 'JoinColumn' (abstract model) 
	 * @param listOfJoinColumnInDbModel (can be null)
	 * @return list of 'JoinColumn' (or null if the given list is null)
	 */
	protected static List<JoinColumn> toListOfJoinColumns(List<JoinColumnInDbModel> listOfJoinColumnInDbModel) {
		if ( listOfJoinColumnInDbModel != null ) {
			LinkedList<JoinColumn> joinColumns = new LinkedList<JoinColumn>();
			for ( JoinColumn jc : listOfJoinColumnInDbModel ) {
				joinColumns.add(jc);
			}
			return joinColumns ;
		}
		else {
			return null ;
		}
	}

}
