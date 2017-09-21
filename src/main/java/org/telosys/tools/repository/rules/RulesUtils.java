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
package org.telosys.tools.repository.rules;

import java.util.StringTokenizer;

import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.model.LinkInDbModel;

public class RulesUtils {
	
	//------------------------------------------------------------------------------
    /**
     * The first character of the given string is forced to upper case  
     * ie : "aBcDeFg" --> "ABcDeFg"
     * @param inputString 
     * @return 
     */
	public String capitalize(String inputString)
    {
    	if ( inputString == null || inputString.length() == 0 ) return "";
        return inputString.substring(0, 1).toUpperCase() + inputString.substring(1);
    }

	//------------------------------------------------------------------------------
    /**
     * The first character of the given string is forced to upper case and all others are forced to lower case  
     * ie : "aBcDeFg" --> "Abcdefg"
     * @param inputString 
     * @return 
     */
	public String capitalizeFully(String inputString)
    {
    	if ( inputString == null || inputString.length() == 0 ) return "";
        return inputString.substring(0, 1).toUpperCase() + inputString.substring(1).toLowerCase();
    }

	//------------------------------------------------------------------------------
    /**
     * The first character of the given string is forced to lower case  
     * @param inputString
     * @return
     */
    public String uncapitalize(String inputString)
    {
    	if ( inputString == null || inputString.length() == 0 ) return "";
        //return inputString.substring(0,1).toLowerCase() + inputString.substring(1,inputString.length()) ;
        return inputString.substring(0,1).toLowerCase() + inputString.substring(1) ;
    }
	
    //------------------------------------------------------------------------------
    /**
     * Transform the given string to "CamelCase", using the given word separator  
     * ie : "ORDER_ITEM" --> "OrderItem"
     * @param inputString eg "ORDER_ITEM"
     * @param separator   eg "_"
     * @return
     */
    public String camelCase(String inputString, String separator) {
        if (inputString != null)
        {
            StringBuffer sb = new StringBuffer( inputString.length() );
            String sToken = null;
            String s = inputString.trim(); // to be secure
            StringTokenizer st = new StringTokenizer(s, separator);
            while (st.hasMoreTokens())
            {
                sToken = st.nextToken();
                sb.append( capitalizeFully( sToken ) );
            }
            return sb.toString();
        }
        else
        {
            return "";
        }
    }
    
    public String separatedWords(String inputString, String separator) {
        if (inputString != null)
        {
            StringBuffer sb = new StringBuffer( inputString.length() );
            String sToken = null;
            String s = inputString.trim().toLowerCase(); // to be secure
            StringTokenizer st = new StringTokenizer(s, separator);
            int i = 0 ;
            while (st.hasMoreTokens())
            {
            	i++ ;
                sToken = st.nextToken();
                if ( i > 1 ) {
                    sb.append( " " );
                }
                if ( i == 1 ) {
                	sToken = capitalize( sToken ) ;
                }
            	sb.append( sToken );
            }
            return sb.toString();
        }
        else
        {
            return "";
        }
    }

    public boolean attributeNameAlreadyUsed(String attributeName, EntityInDbModel entity ) {
    	//--- Search in all columns/attributes
    	AttributeInDbModel[] columns = entity.getAttributesArray();
    	for ( AttributeInDbModel column : columns ) {
    		//if ( column.getJavaName().equals(attributeName) ) {
        	if ( column.getName().equals(attributeName) ) { // v 3.0.0
    			return true ;
    		}
    	}
    	//--- Search in all links
//    	LinkInDbModel[] links = entity.getLinks();
    	LinkInDbModel[] links = entity.getLinksArray();
    	for ( LinkInDbModel link : links ) {
//    		if ( link.getJavaFieldName().equals(attributeName) ) {
        	if ( link.getFieldName().equals(attributeName) ) {
    			return true ;
    		}
    	}
    	return false ;
    }

}
