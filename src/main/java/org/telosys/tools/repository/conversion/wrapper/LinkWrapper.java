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

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOption;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.Optional;
import org.telosys.tools.repository.model.LinkInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LinkWrapper extends GenericWrapper {

	public LinkWrapper() {
		super();
	}

	public LinkInDbModel getLink(Element element) 
	{
		LinkInDbModel link = new LinkInDbModel();
		
		link.setId(element.getAttribute(RepositoryConst.LINK_ID));
		
		link.setCascadeOptions(convertStringToCascadeOptions(element.getAttribute(RepositoryConst.LINK_CASCADE)));
		
		link.setFetchType(convertStringToFetchType(element.getAttribute(RepositoryConst.LINK_FETCH))); // v 3.0.0

		link.setInverseSideLinkId(nullIfVoidOrBlank(element.getAttribute(RepositoryConst.LINK_INVERSE_SIDE_OF))); // v 3.0.0
		
		link.setFieldName(mandatory(element.getAttribute(RepositoryConst.LINK_JAVA_NAME))); // v 3.0.0
		
		// REMOVED in v 3.3.0 : link.setFieldType(mandatory(element.getAttribute(RepositoryConst.LINK_JAVA_TYPE))); // v 3.0.0
		// Value is now always "unused" for previous version 
		
		link.setMappedBy( nullIfVoidOrBlank(element.getAttribute(RepositoryConst.LINK_MAPPED_BY)) );
		
		link.setTargetEntityClassName(element.getAttribute(RepositoryConst.LINK_TARGET_ENTITY)); // v 3.0.0
		
		link.setSourceTableName(element.getAttribute(RepositoryConst.LINK_SOURCE_TABLE_NAME));
		
		link.setTargetTableName(element.getAttribute(RepositoryConst.LINK_TARGET_TABLE_NAME));
		
		link.setCardinality(convertStringToCardinality(element.getAttribute(RepositoryConst.LINK_CARDINALITY)));

		if (StrUtil.nullOrVoid(element.getAttribute(RepositoryConst.LINK_FOREIGN_KEY_NAME)) == false) {
			link.setForeignKeyName( element.getAttribute(RepositoryConst.LINK_FOREIGN_KEY_NAME) );
		}
		if (StrUtil.nullOrVoid(element.getAttribute(RepositoryConst.LINK_JOIN_TABLE_NAME)) == false) {
			link.setJoinTableName( element.getAttribute(RepositoryConst.LINK_JOIN_TABLE_NAME) );
		}
		if (StrUtil.nullOrVoid(element.getAttribute(RepositoryConst.LINK_OWNING_SIDE)) == false) {
			link.setOwningSide(StrUtil.getBoolean(element.getAttribute(RepositoryConst.LINK_OWNING_SIDE)));
		}

		link.setOptional(convertStringToOptional(element.getAttribute(RepositoryConst.LINK_OPTIONAL)));

		if (StrUtil.nullOrVoid(element.getAttribute(RepositoryConst.LINK_USED)) == false) {
			link.setSelected(StrUtil.getBoolean(element.getAttribute(RepositoryConst.LINK_USED))); // v 3.0.0
		}
		return link;
	}

	public Element getXmlDesc(LinkInDbModel link, Document doc) 
	{
		Element element = doc.createElement(RepositoryConst.LINK);
		
		element.setAttribute(RepositoryConst.LINK_ID, link.getId());
		
		element.setAttribute(RepositoryConst.LINK_CASCADE, convertCascadeOptionsToString(link.getCascadeOptions())); // v 3.0.0
		
		element.setAttribute(RepositoryConst.LINK_FETCH, convertFetchTypeToString(link.getFetchType()) ); // v 3.0.0
		
		element.setAttribute(RepositoryConst.LINK_INVERSE_SIDE_OF, link.getInverseSideLinkId()); // v 3.0.0
		
		element.setAttribute(RepositoryConst.LINK_JAVA_NAME, link.getFieldName()); // v 3.0.0
		
		// "FieldType" REMOVED in v 3.3.0 BUT KEEP XML ATTRIBUTE in .dbrep file for backward compatibility
		// in previous versions this attribute is "mandatory" : link.setFieldType(mandatory(xx)) => loading error if not exist
		//element.setAttribute(RepositoryConst.LINK_JAVA_TYPE, link.getFieldType()); // v 3.0.0
		element.setAttribute(RepositoryConst.LINK_JAVA_TYPE, "unused"); // v 3.3.0
		
		element.setAttribute(RepositoryConst.LINK_MAPPED_BY, link.getMappedBy());
		
		element.setAttribute(RepositoryConst.LINK_TARGET_ENTITY, link.getTargetEntityClassName()); // v 3.0.0
		
		element.setAttribute(RepositoryConst.LINK_TARGET_TABLE_NAME, link.getTargetTableName());
		
		element.setAttribute(RepositoryConst.LINK_FOREIGN_KEY_NAME, link.getForeignKeyName());
		element.setAttribute(RepositoryConst.LINK_JOIN_TABLE_NAME, link.getJoinTableName());
		
		element.setAttribute(RepositoryConst.LINK_SOURCE_TABLE_NAME, link.getSourceTableName());
		
		element.setAttribute(RepositoryConst.LINK_CARDINALITY, convertCardinalityToString(link.getCardinality()));

		element.setAttribute(RepositoryConst.LINK_OWNING_SIDE, Boolean.toString(link.isOwningSide()));
		
		element.setAttribute(RepositoryConst.LINK_OPTIONAL, convertOptionalToString(link.getOptional()) );

		element.setAttribute(RepositoryConst.LINK_USED, Boolean.toString(link.isSelected())); // v 3.0.0
		
		return element;
	}
	
	//---------------------------------------------------------------------------------------------------
	// CASCADE OPTIONS
	//---------------------------------------------------------------------------------------------------
	/**
	 * Convert the given string to CascadeOptions
	 * @param s ( eg : "MERGE PERSIST", "PERSIST", "ALL" )
	 * @return the CascadeOptions instance
	 */
	private CascadeOptions convertStringToCascadeOptions(String s) {
		CascadeOptions cascadeOptions = new CascadeOptions();
		if ( s != null ) {
			String cascadeUC = s.toUpperCase();
			if (cascadeUC.contains(RepositoryConst.CASCADE_ALL)) {
				cascadeOptions.add(CascadeOption.ALL);
			}
			if (cascadeUC.contains(RepositoryConst.CASCADE_MERGE)) {
				cascadeOptions.add(CascadeOption.MERGE);
			}
			if (cascadeUC.contains(RepositoryConst.CASCADE_PERSIST)) {
				cascadeOptions.add(CascadeOption.PERSIST);
			}
			if (cascadeUC.contains(RepositoryConst.CASCADE_REFRESH)) {
				cascadeOptions.add(CascadeOption.REFRESH);
			}
			if (cascadeUC.contains(RepositoryConst.CASCADE_REMOVE)) {
				cascadeOptions.add(CascadeOption.REMOVE);
			}
		}
		return cascadeOptions ;
	}
	
	private String convertCascadeOptionsToString(CascadeOptions cascadeOptions) {
		if ( cascadeOptions != null ) {
			StringBuilder sb = new StringBuilder();
			for ( CascadeOption co : cascadeOptions.getActiveOptions() ) {
				sb.append(" ");
				switch (co) {
				case MERGE:	
					sb.append(RepositoryConst.CASCADE_MERGE);					
					break;
				case PERSIST:	
					sb.append(RepositoryConst.CASCADE_PERSIST);					
					break;
				case REFRESH:	
					sb.append(RepositoryConst.CASCADE_REFRESH);					
					break;
				case REMOVE:	
					sb.append(RepositoryConst.CASCADE_REMOVE);					
					break;
				case ALL:	
					return RepositoryConst.CASCADE_ALL ; // ALL : return immediately		
				}
			}
			return sb.toString();
		}
		return "" ;
	}
	
	//---------------------------------------------------------------------------------------------------
	// FETCH TYPE
	//---------------------------------------------------------------------------------------------------
	/**
	 * Convert the given string to FetchType
	 * @param s ( eg : "EAGER", "LAZY", etc )
	 * @return the FetchType instance
	 */
	private FetchType convertStringToFetchType(String s) {
		if ( s != null ) {
			if ( RepositoryConst.FETCH_EAGER.equals(s)) return FetchType.EAGER ;
			if ( RepositoryConst.FETCH_LAZY.equals(s)) return FetchType.LAZY ;
			if ( RepositoryConst.FETCH_DEFAULT.equals(s)) return FetchType.DEFAULT ;
		}
		return FetchType.UNDEFINED ;
	}
	
	/**
	 * Convert the given FetchType to a String value
	 * @param fetchType
	 * @return the String value ( eg : "EAGER", "LAZY", etc ) or "" if the FetchType is null or UNDEFINED
	 */
	private String convertFetchTypeToString(FetchType fetchType) {
		if ( fetchType != null ) {
			switch (fetchType) {
				case EAGER : return RepositoryConst.FETCH_EAGER ;
				case LAZY : return RepositoryConst.FETCH_LAZY ;
				case DEFAULT : return RepositoryConst.FETCH_DEFAULT ;
				case UNDEFINED : return "" ;
			}
		}
		return "" ;
	}

	//---------------------------------------------------------------------------------------------------
	// CARDINALITY
	//---------------------------------------------------------------------------------------------------
	/**
	 * Convert the given string to Cardinality
	 * @param s ( eg : "OneToOne", "ManyToOne", etc )
	 * @return the Cardinality instance
	 */
	private Cardinality convertStringToCardinality(String s) {
		if ( s != null ) {
			if ( RepositoryConst.MAPPING_ONE_TO_ONE.equals(s)) return Cardinality.ONE_TO_ONE ;
			if ( RepositoryConst.MAPPING_ONE_TO_MANY.equals(s)) return Cardinality.ONE_TO_MANY ;
			if ( RepositoryConst.MAPPING_MANY_TO_ONE.equals(s)) return Cardinality.MANY_TO_ONE ;
			if ( RepositoryConst.MAPPING_MANY_TO_MANY.equals(s)) return Cardinality.MANY_TO_MANY ;
		}
		return Cardinality.UNDEFINED ;
	}
	
	/**
	 * Convert the given Cardinality to a String value
	 * @param cardinality
	 * @return the String value ( eg : "OneToOne", "ManyToOne", etc  ) or "" if the Cardinality is null or UNDEFINED
	 */
	private String convertCardinalityToString(Cardinality cardinality) {
		if ( cardinality != null ) {
			switch (cardinality) {
				case ONE_TO_ONE : return RepositoryConst.MAPPING_ONE_TO_ONE ;
				case ONE_TO_MANY : return RepositoryConst.MAPPING_ONE_TO_MANY ;
				case MANY_TO_ONE : return RepositoryConst.MAPPING_MANY_TO_ONE ;
				case MANY_TO_MANY : return RepositoryConst.MAPPING_MANY_TO_MANY ;
				case UNDEFINED : return "" ;
			}
		}
		return null ;
	}

	//---------------------------------------------------------------------------------------------------
	// OPTIONAL
	//---------------------------------------------------------------------------------------------------
	/**
	 * Convert the given string to Optional
	 * @param s ( eg : "TRUE", "FALSE" )
	 * @return the Optional instance
	 */
	private Optional convertStringToOptional(String s) {
		if ( s != null ) {
			if ( RepositoryConst.OPTIONAL_TRUE.equals(s)) return Optional.TRUE ;
			if ( RepositoryConst.OPTIONAL_FALSE.equals(s)) return Optional.FALSE ;
		}
		return Optional.UNDEFINED ;
	}
	
	/**
	 * Convert the given Optional to a String value
	 * @param optional
	 * @return the String value ( eg : "TRUE", "FALSE" ) or "" if Optional is null or UNDEFINED
	 */
	private String convertOptionalToString(Optional optional) {
		if ( optional != null ) {
			switch (optional) {
				case TRUE : return RepositoryConst.OPTIONAL_TRUE ;
				case FALSE : return RepositoryConst.OPTIONAL_FALSE ;
				case UNDEFINED : return "" ;
			}
		}
		return "" ;
	}

}
