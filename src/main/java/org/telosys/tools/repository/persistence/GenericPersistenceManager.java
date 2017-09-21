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

import java.io.InputStream;
import java.io.OutputStream;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.repository.ForeignKeyTypeManager;
import org.telosys.tools.repository.conversion.XmlConverter;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.util.Xml;
import org.w3c.dom.Document;

/**
 * Abstract PersistenceManager <br>
 * Contains all load/save processing for the Model <br>
 * 
 * 
 * @author Laurent Guerin
 *
 */
public abstract class GenericPersistenceManager implements PersistenceManager {

	private final TelosysToolsLogger _logger;

	private void log(String msg) {
		if ( _logger != null ) {
			_logger.log("[LOG] " + this.getClass().getName() + " : " + msg);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param logger
	 */
	public GenericPersistenceManager(TelosysToolsLogger logger) {
		_logger = logger;
	}

	/**
	 * Loads the repository model from the given XML InputStream
	 * @param is
	 * @return
	 * @throws TelosysToolsException
	 */
	public RepositoryModel load(InputStream is) throws TelosysToolsException {
		
		log(" . load XML from input stream");
		Document xmlDocument = Xml.load(is);

		//--- Model loading 
		log(" . convert XML to model");
		XmlConverter  xmlConverter = new XmlConverter(_logger);
		RepositoryModel model = xmlConverter.xmlDocumentToModel(xmlDocument);
		
		//--- Model finalization 
		ForeignKeyTypeManager fkTypeManager = new ForeignKeyTypeManager() ;
		fkTypeManager.setAttributesForeignKeyInformation(model);
		
		return model;
	}

	/**
	 * Saves the repository model in the given XML OutputStream
	 * 
	 * @param os
	 * @param model
	 * @throws TelosysToolsException
	 */
	public void save(OutputStream os, RepositoryModel model) throws TelosysToolsException 
	{
		log("save(OutputStream, RepositoryModel)... ");
		if (model != null) {

			log(" . convert model to XML ");
			XmlConverter  xmlConverter = new XmlConverter(_logger);
			Document doc = xmlConverter.modelToXmlDocument(model);
			
			log(" . save XML in output stream");
			Xml.save(doc, os);
		} else {
			throw new TelosysToolsException("Cannot save model : model is null");
		}
	}

}
