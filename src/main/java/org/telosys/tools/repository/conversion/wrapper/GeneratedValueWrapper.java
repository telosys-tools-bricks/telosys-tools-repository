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

import org.telosys.tools.repository.model.GeneratedValueInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GeneratedValueWrapper {
	
	public GeneratedValueWrapper() {
		super();
	}

	public GeneratedValueInDbModel getGeneratedValue(final Element elem) {
		final GeneratedValueInDbModel gv = new GeneratedValueInDbModel();
		gv.setGenerator(elem.getAttribute(RepositoryConst.GENERATED_VALUE_GENERATOR));
		gv.setStrategy(elem.getAttribute(RepositoryConst.GENERATED_VALUE_STRATEGY));
		return gv;
	}

	public Element getXmlDesc(final GeneratedValueInDbModel column, final Document doc) {
		final Element gv = doc.createElement(RepositoryConst.GENERATED_VALUE_ELEMENT);
		gv.setAttribute(RepositoryConst.GENERATED_VALUE_GENERATOR, column.getGenerator());
		gv.setAttribute(RepositoryConst.GENERATED_VALUE_STRATEGY, column.getStrategy());
		return gv;
	}

}
