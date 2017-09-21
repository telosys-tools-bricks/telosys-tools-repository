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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.repository.model.RepositoryModel;

/* package */  class StandardFilePersistenceManager extends GenericPersistenceManager
{

	private String _fileName = null ;
	
//	public StandardFilePersistenceManager(String fileName, TelosysToolsLogger logger) {
//		super(logger);
//		this._fileName = fileName ;
//	}

	public StandardFilePersistenceManager(File file, TelosysToolsLogger logger) {
		super(logger);
		this._fileName = file.getAbsolutePath() ;
	}

	public RepositoryModel load() throws TelosysToolsException {
		try {
			InputStream is = new FileInputStream(this._fileName);
			RepositoryModel repositoryModel =  super.load(is);
			try {
				is.close();
			} catch (IOException e) {
				throw new TelosysToolsException("cannot close file " + this._fileName, e);
			}
			return repositoryModel ;
		} catch (FileNotFoundException e) {
			throw new TelosysToolsException("file not found : " + this._fileName, e);
//		} catch (CommandException e) {
//			throw new TelosysToolsException("parsing exception : " + this._fileName, e);
		}
		
	}

	public void save(RepositoryModel model) throws TelosysToolsException {
		try {
			OutputStream os = new FileOutputStream(this._fileName, false);
			super.save(os, model);
			try {
				os.close();
			} catch (IOException e) {
				throw new TelosysToolsException("cannot close file " + this._fileName, e);
			}
		} catch (FileNotFoundException e) {
			throw new TelosysToolsException("file not found : " + this._fileName, e);
		}
	
	}

}
