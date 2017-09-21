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
package org.telosys.tools.repository.conversion;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.repository.model.RepositoryModel;
import org.w3c.dom.Document;

public class XmlConverter {

	private final TelosysToolsLogger _logger;

	/**
	 * Constructor
	 * 
	 * @param logger
	 */
	public XmlConverter(TelosysToolsLogger logger) {
		_logger = logger;
	}

	private void log(String msg) {
		if ( _logger != null ) {
			_logger.log("[LOG] " + this.getClass().getName() + " : " + msg);
		}
	}

	public RepositoryModel xmlDocumentToModel(Document xmlDocument) throws TelosysToolsException {
		
		log("xmlDocumentToModel()... ");
		XmlConverterFromXml converter = new XmlConverterFromXml(_logger);
		return converter.xmlDocumentToModel(xmlDocument);
	}

	public Document modelToXmlDocument(RepositoryModel model) throws TelosysToolsException {
		
		log("modelToXmlDocument()... ");
		XmlConverterToXml converter = new XmlConverterToXml(_logger);
		return converter.modelToXmlDocument(model);
	}

}
