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
package org.telosys.tools.repository.persistence.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.telosys.tools.commons.TelosysToolsException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Xml 
{	
	
	
	
    private static DocumentBuilder getDocumentBuilder() throws TelosysToolsException
    {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);

		DocumentBuilder builder = null ;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new TelosysToolsException("XML error : Cannot get DocumentBuilder",e);
		}
		return builder ;
    }
    
    public static Document load(String sFileName) throws TelosysToolsException
    {
    	if ( sFileName != null ) {
        	return load(new File(sFileName));
    	}
    	else {
    		throw new TelosysToolsException("XML error : load(null) ");
    	}
    }

    public static Document load(File file) throws TelosysToolsException
    {
    	if ( file != null ) {
    		InputStream is = null ;
    		
    		try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
	    		throw new TelosysToolsException("XML error : load(IFile) : getContents() : " + e.getMessage() , e);
			}
			Document doc = load(is); 
			try {
				is.close();
			} catch (IOException e) {
				// NOTHING TO DO 
			}
			return doc ;
    	}
    	else {
    		throw new TelosysToolsException("XML error : load(null) ");
    	}
    }
    
    public static Document load(InputStream file) throws TelosysToolsException
    {
    	if ( file != null ) {
			DocumentBuilder builder = getDocumentBuilder();		
			Document doc = null;		
			try {
				doc = builder.parse(file);
			} catch (SAXException e) {
				throw new TelosysToolsException("XML error : Cannot parse : SAXException",e);
			} catch (IOException e) {
				throw new TelosysToolsException("XML error : Cannot parse : IOException",e);
			}
			return doc ;
    	}
    	else {
    		throw new TelosysToolsException("XML error : load(null) ");
    	}
    }
    
    public static void save(Document doc, String fileName) throws TelosysToolsException
    {
    	save ( doc, new StreamResult(fileName));
    }
    
    public static void save(Document doc, File file) throws TelosysToolsException
    {
    	save ( doc, new StreamResult(file));
    }
    
    public static void save(Document doc, OutputStream ifile) throws TelosysToolsException
    {
		save ( doc, new StreamResult(ifile));
    }
    
    private static void save(Document doc, Result result) throws TelosysToolsException
    {
        //--- Write the XML document in XML file
        try {
            Source source = new DOMSource(doc);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            //--- Transform the DOM document into XML file 
            transformer.transform(source, result);
        }
        catch (TransformerException e) {
    		throw new TelosysToolsException("XML error : Cannot save : TransformerException", e);
        }
        catch (TransformerFactoryConfigurationError e) {
    		throw new TelosysToolsException("XML error : Cannot save : TransformerFactoryConfigurationError", e);
        }
    }
    
    //---------------------------------------------------------------------------------------------------
    public static String getNodeAttribute( Node node, String sAttributeName )
    {
        String sRetValue = null ;
        NamedNodeMap attributes = node.getAttributes();
        if ( attributes != null )
        {
            //--- Find the attribute 
            Node attrib = attributes.getNamedItem(sAttributeName);
            if ( attrib != null )
            {
                //--- Get the attribute value
                sRetValue = attrib.getNodeValue() ;
            }
        }
        return sRetValue ;
    }

    //---------------------------------------------------------------------------------------------------
    /**
     * Creates an empty DOM document
     * @return
     */
    public static Document createDomDocument() throws TelosysToolsException {
        
        Document doc = null;
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b;
        try {
            b = f.newDocumentBuilder();
            doc = b.newDocument();
        } catch (ParserConfigurationException e) {
        	throw new TelosysToolsException("XML error : Cannot create DocumentBuilder ", e);
        }
        
        return doc;
    }
    
    //---------------------------------------------------------------------------------------------------
//    /**
//     * Creates a DOM document populated with the given XML file
//     * @param file
//     * @return
//     */
//    public static Document createDomDocument(File file) throws TelosysToolsException {
// 
//        
//        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
//        org.w3c.dom.Document doc = createDomDocument();
//        try {
//            doc = f.newDocumentBuilder().parse(file);
//        } catch (SAXException e) {
//            throw new TelosysToolsException("XML error : Cannot parse XML file.", e);
//        } catch (IOException e) {
//            throw new TelosysToolsException("XML error : Cannot parse XML file.", e);
//        } catch (ParserConfigurationException e) {
//            throw new TelosysToolsException("XML error : Cannot parse XML file.", e);
//        }
//        return doc;
//    }
    public static String toString(Document doc) throws TelosysToolsException {
		OutputStream os = new ByteArrayOutputStream();
		Xml.save(doc, os);
		try {
			os.close();
		} catch (IOException e) {
			throw new TelosysToolsException("Cannot close ByteArrayOutputStream", e);
		}
		return os.toString();
    }

}
