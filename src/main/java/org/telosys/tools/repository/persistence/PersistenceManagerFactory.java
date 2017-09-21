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
package org.telosys.tools.repository.persistence;

import java.io.File;

import org.telosys.tools.commons.TelosysToolsLogger;

public class PersistenceManagerFactory 
{

	public static PersistenceManager createPersistenceManager(File file) {
		return new StandardFilePersistenceManager(file, null);
	}

	public static PersistenceManager createPersistenceManager(File file, TelosysToolsLogger logger) {
		return new StandardFilePersistenceManager(file, logger);
	}
	
	public static PersistenceManager createPersistenceManager(FileInMemory fileInMemory, TelosysToolsLogger logger) {
		return new InMemoryPersistenceManager(fileInMemory, logger);
	}
	
}
