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
package org.telosys.tools.repository.conversion.wrapper;

public abstract class GenericWrapper {

	/**
	 * Returns null if the given value is null or void, else returns the given value 
	 * @param value
	 * @return
	 */
	protected String nullIfVoid(String value) {
		if ( value == null ) return null ;
		if ( value.length() == 0 ) return null ;
		return value ;
	}

	/**
	 * Returns null if the given value is null or void or blank, else returns the given value 
	 * @param value
	 * @return
	 */
	protected String nullIfVoidOrBlank(String value) {
		if ( value == null ) return null ;
		if ( value.trim().length() == 0 ) return null ;
		return value ;
	}

	/**
	 * Throws an exception if the given value is null or void or blank
	 * @param value
	 * @return
	 */
	protected String mandatory(String value) {
		if ( value == null || value.trim().length() == 0 ) {
			throw new RuntimeException("Mandatory value is null or void");
		}
		return value ;
	}
}
