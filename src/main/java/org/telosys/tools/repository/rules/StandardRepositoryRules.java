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

import java.sql.Types;

import org.telosys.tools.commons.jdbctypes.JdbcTypes;
import org.telosys.tools.commons.jdbctypes.JdbcTypesManager;
import org.telosys.tools.repository.model.EntityInDbModel;

/**
 * Repository (model) generator 
 * 
 * @author Laurent GUERIN
 * 
 */
public class StandardRepositoryRules implements RepositoryRules
{
	
	private final static String UNDERSCORE = "_" ;
	
	private final RulesUtils         rulesUtils ;

	/**
	 * Constructor
	 */
	public StandardRepositoryRules() {
		super();
		this.rulesUtils = new RulesUtils();
	}

    //------------------------------------------------------------------------------
    @Override
    public String getEntityClassName(String sTableName)
    {
    	return rulesUtils.camelCase(sTableName, UNDERSCORE);
    }

    //------------------------------------------------------------------------------
    @Override
    public String getAttributeName(String sColumnName )
    {
        //--- Colum name converted in "CamelCase"
        String s = rulesUtils.camelCase(sColumnName, UNDERSCORE);
        
        //--- Java attribute => Force the first char to LowerCase
        //return s.substring(0,1).toLowerCase() + s.substring(1,s.length()) ;
        return rulesUtils.uncapitalize(s) ;
    }

    //------------------------------------------------------------------------------
    @Override
    public String getAttributeType(String databaseColumnType, int iJdbcTypeCode, boolean bColumnNotNull)
    {
    	//--- Special cases for Date/Time/Timestamp
    	if ( iJdbcTypeCode == Types.DATE  || iJdbcTypeCode == Types.TIME || iJdbcTypeCode == Types.TIMESTAMP ) 
    	{
    		// Force "java.util.Date" : see also getAttributeDateType() for DATE_ONLY, TIME_ONLY, etc...
    		return "java.util.Date";
    	}
    	JdbcTypes types = JdbcTypesManager.getJdbcTypes();
    	String sJavaType = types.getJavaTypeForCode(iJdbcTypeCode, bColumnNotNull);
    	return ( sJavaType != null ? sJavaType : "java.lang.String" );    	
    }    

    //------------------------------------------------------------------------------
    @Override
    public String getAttributeGuiLabel( String databaseColumnName ) 
    {
        //--- Colum name converted in "Word1 Word2"
        //return toSeparatedWords(databaseColumnName);
        return rulesUtils.separatedWords(databaseColumnName, UNDERSCORE);
        
    }

    //------------------------------------------------------------------------------
	@Override
	public String getAttributeGuiType(String columnTypeName, int jdbcTypeCode) {
    	//--- Returns the HTML 5 input type
    	switch ( jdbcTypeCode ) {
	    	case Types.CHAR :
	    	case Types.VARCHAR :
	    	case Types.LONGVARCHAR :
	    		return "text" ;
    		
	    	case Types.NUMERIC :
	    	case Types.DECIMAL :
	    	case Types.TINYINT :
	    	case Types.SMALLINT :
	    	case Types.INTEGER :
	    	case Types.BIGINT :    		
	    	case Types.REAL :
	    	case Types.FLOAT :
	    	case Types.DOUBLE :
	    		return "number" ;
    		
    		case Types.DATE :
    			return "date" ;
    			
    		case Types.TIME :
    			return "time" ;
    			
    		case Types.BIT :
    		case Types.BOOLEAN :
    			return "checkbox" ;
    			
    	}
    	return "" ;
	}

    @Override
	public String getAttributeNameForLinkToOne(EntityInDbModel entity, EntityInDbModel referencedEntity ) 
	{
		//--- Determines the attribute name
//		String originalAttributeName = rulesUtils.uncapitalize( referencedEntity.getBeanJavaClass() ) ;
		String originalAttributeName = rulesUtils.uncapitalize( referencedEntity.getClassName() ) ;
		String attributeName = originalAttributeName ; // eg : "book"
		//--- Check the attribute name is not already used
		int n = 1 ;
		while ( rulesUtils.attributeNameAlreadyUsed(attributeName, entity) ) {
			n++;
			attributeName = originalAttributeName + n ; // eg : "book2", "book3", etc
		}
		return attributeName ; // eg : "book", "book2", "book3", etc
	}

    @Override
	public String getAttributeNameForLinkToMany(EntityInDbModel entity, EntityInDbModel referencedEntity ) 
	{
		//--- Determines the attribute name
//		String originalAttributeName = "listOf" + referencedEntity.getBeanJavaClass() ;
		String originalAttributeName = "listOf" + referencedEntity.getClassName() ;
		String attributeName = originalAttributeName ; // eg : "listOfBook"
		//--- Check the attribute name is not already used
		int n = 1 ;
		while ( rulesUtils.attributeNameAlreadyUsed(attributeName, entity) ) {
			n++;
			attributeName = originalAttributeName + n ; // eg : "listOfBook2", "listOfBook3", etc
		}
		return attributeName ; // eg : "listOfBook", "listOfBook2", "listOfBook3", etc
	}
    
}
