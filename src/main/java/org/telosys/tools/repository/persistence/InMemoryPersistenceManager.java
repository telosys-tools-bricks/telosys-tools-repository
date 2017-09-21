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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.repository.model.RepositoryModel;

/* package */  class InMemoryPersistenceManager extends GenericPersistenceManager
{

	private FileInMemory fileInMemory = null ;
	
	public InMemoryPersistenceManager( FileInMemory fileInMemory, TelosysToolsLogger logger) {
		super(logger);
		this.fileInMemory = fileInMemory ;
	}

	public RepositoryModel load() throws TelosysToolsException {
		//InputStream is = new FileInputStream(this._fileName);
		InputStream is = new ByteArrayInputStream(fileInMemory.getContent());
		
		RepositoryModel repositoryModel =  super.load(is);
		try {
			is.close();
		} catch (IOException e) {
			throw new TelosysToolsException("cannot close ByteArrayInputStream ", e);
		}
		return repositoryModel ;
	}

	public void save(RepositoryModel model) throws TelosysToolsException {
		//OutputStream os = new FileOutputStream(this._fileName, false);
		OutputStream os = new ByteArrayOutputStream();
		
		super.save(os, model);
		try {
			os.close();
		} catch (IOException e) {
			throw new TelosysToolsException("Cannot close ByteArrayOutputStream ", e);
		}
		
		//--- Set new content 
		fileInMemory.setContent(os.toString());
	}

}
