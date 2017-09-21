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

/**
 * <sequenceGenerator name="MyGenerator" sequenceName="INV_SEQ" allocationSize="5" />
 * @author S.Labbe
 * 
 */
public class SequenceGeneratorInDbModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null ;

	private String sequenceName = null ;

	private int allocationSize = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public int getAllocationSize() {
		return allocationSize;
	}

	public void setAllocationSize(int allocationSize) {
		this.allocationSize = allocationSize;
	}

}
