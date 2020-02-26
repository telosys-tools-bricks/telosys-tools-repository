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
import org.telosys.tools.generic.model.DateType;
import org.telosys.tools.repository.model.AttributeInDbModel;
import org.telosys.tools.repository.model.EntityInDbModel;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AttributeWrapper {
	
	public AttributeWrapper() {
		super();
	}

	public AttributeInDbModel getAttributeObject(EntityInDbModel entity, Element elem) 
	{
		final AttributeInDbModel attributeInDbModel = new AttributeInDbModel(entity);

		attributeInDbModel.setSelected(StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_SELECTED)));

		//--- DATABASE INFOS
		attributeInDbModel.setDatabaseName(elem.getAttribute(RepositoryConst.COLUMN_DB_NAME));
		attributeInDbModel.setKeyElement(StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_DB_PRIMARY_KEY))); // v 3.0.0
		attributeInDbModel.setDatabaseTypeName(elem.getAttribute(RepositoryConst.COLUMN_DB_TYPE_NAME));
//		attributeInDbModel.setDatabaseSize(StrUtil.getInt(elem.getAttribute(RepositoryConst.COLUMN_DB_SIZE)));
		attributeInDbModel.setDatabaseSize( elem.getAttribute(RepositoryConst.COLUMN_DB_SIZE) );
		attributeInDbModel.setDatabaseNotNull(StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_DB_NOTNULL)));
		
		attributeInDbModel.setAutoIncremented(StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_DB_AUTO_INCREMENTED))); // #LGU 04/08/2011
		attributeInDbModel.setDatabaseDefaultValue( elem.getAttribute(RepositoryConst.COLUMN_DB_DEFAULT_VALUE) ); // #LGU 10/08/2011
		attributeInDbModel.setDatabaseComment( elem.getAttribute(RepositoryConst.COLUMN_DB_COMMENT) ); // v 2.1.1 #LCH 20/08/2014
		attributeInDbModel.setDatabasePosition(StrUtil.getInt(elem.getAttribute(RepositoryConst.COLUMN_DB_POSITION))); // #LGU 10/08/2011

		attributeInDbModel.setJdbcTypeCode(StrUtil.getInt(elem.getAttribute(RepositoryConst.COLUMN_JDBC_TYPE_CODE)));

		//--- JAVA OBJECT
		attributeInDbModel.setName(elem.getAttribute(RepositoryConst.COLUMN_JAVA_NAME)); // v 3.0.0
		attributeInDbModel.setModelFullType(elem.getAttribute(RepositoryConst.COLUMN_JAVA_TYPE)); // v 3.0.0
		attributeInDbModel.setNotNull(  StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_NOT_NULL )) ); // v 3.0.0
		attributeInDbModel.setNotEmpty( StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_NOT_EMPTY)) );// #LGU 30/08/2011
		attributeInDbModel.setNotBlank( StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_NOT_BLANK)) );// #LGU 30/08/2011
		
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_JAVA_DEFAULT_VALUE)) == false) {
			attributeInDbModel.setDefaultValue( elem.getAttribute(RepositoryConst.COLUMN_JAVA_DEFAULT_VALUE) );// #LGU 17/10/2011
		}
		
		//--- Retrieve BOOLEAN informations if any
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_BOOL_TRUE)) == false) {
			attributeInDbModel.setBooleanTrueValue(elem.getAttribute(RepositoryConst.COLUMN_BOOL_TRUE));
		}
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_BOOL_FALSE)) == false) {
			attributeInDbModel.setBooleanFalseValue(elem.getAttribute(RepositoryConst.COLUMN_BOOL_FALSE));
		}

		//--- Retrieve STRING informations if any
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_LONG_TEXT)) == false) {
			attributeInDbModel.setLongText(StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_LONG_TEXT)));
		}
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_MIN_LENGTH)) == false) {
			attributeInDbModel.setMinLength( StrUtil.getIntegerObject( elem.getAttribute(RepositoryConst.COLUMN_MIN_LENGTH) ) ); // v 3.0.0
		}
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_MAX_LENGTH)) == false) {
			attributeInDbModel.setMaxLength( StrUtil.getIntegerObject( elem.getAttribute(RepositoryConst.COLUMN_MAX_LENGTH) ) ); // v 3.0.0
		}
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_PATTERN)) == false) {
			attributeInDbModel.setPattern( elem.getAttribute(RepositoryConst.COLUMN_PATTERN) );
		}
		
		//--- Retrieve DATE/TIME informations if any
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_DATE_TYPE)) == false) {
			attributeInDbModel.setDateType(convertStringToDateType(elem.getAttribute(RepositoryConst.COLUMN_DATE_TYPE))); // v 3.0.0
		}
		attributeInDbModel.setDatePast  ( StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_DATE_PAST)) ); // #LGU 30/08/2011
		attributeInDbModel.setDateFuture( StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_DATE_FUTURE)) );// #LGU 30/08/2011
		attributeInDbModel.setDateBefore( StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_DATE_BEFORE)) );// #LGU 30/08/2011
		attributeInDbModel.setDateBeforeValue( elem.getAttribute(RepositoryConst.COLUMN_DATE_BEFORE_VALUE) );// #LGU 30/08/2011
		attributeInDbModel.setDateAfter ( StrUtil.getBoolean(elem.getAttribute(RepositoryConst.COLUMN_DATE_AFTER)) );// #LGU 30/08/2011
		attributeInDbModel.setDateAfterValue ( elem.getAttribute(RepositoryConst.COLUMN_DATE_AFTER_VALUE) );// #LGU 30/08/2011
		
		//--- Retrieve NUMBER informations if any
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_MIN_VALUE)) == false) {
			attributeInDbModel.setMinValue( StrUtil.getBigDecimalObject( elem.getAttribute(RepositoryConst.COLUMN_MIN_VALUE) ) ); // v 3.0.0
		}
		if (StrUtil.nullOrVoid(elem.getAttribute(RepositoryConst.COLUMN_MAX_VALUE)) == false) {
			attributeInDbModel.setMaxValue( StrUtil.getBigDecimalObject( elem.getAttribute(RepositoryConst.COLUMN_MAX_VALUE) ) ); // v 3.0.0
		}
		
		attributeInDbModel.setLabel    ( elem.getAttribute(RepositoryConst.COLUMN_LABEL)     ) ; // #LGU 20/02/2013
		attributeInDbModel.setInputType( elem.getAttribute(RepositoryConst.COLUMN_INPUT_TYPE)) ; // #LGU 20/02/2013
		
		return attributeInDbModel;
	}

	/**
	 * @param attributeInDbModel
	 * @param doc
	 * @return
	 */
	public Element getXmlElement(final AttributeInDbModel attributeInDbModel, final Document doc) 
	{
		final Element element = doc.createElement(RepositoryConst.COLUMN);

		element.setAttribute(RepositoryConst.COLUMN_SELECTED, Boolean.toString(attributeInDbModel.isSelected())); // v 3.0.0

		//--- DATABASE INFOS
		element.setAttribute(RepositoryConst.COLUMN_DB_NAME, attributeInDbModel.getDatabaseName());
		element.setAttribute(RepositoryConst.COLUMN_DB_NOTNULL, Boolean.toString(attributeInDbModel.isDatabaseNotNull()));
//		element.setAttribute(RepositoryConst.COLUMN_DB_SIZE, Integer.toString(attributeInDbModel.getDatabaseSize()));
		element.setAttribute(RepositoryConst.COLUMN_DB_SIZE, attributeInDbModel.getDatabaseSize() );
		element.setAttribute(RepositoryConst.COLUMN_DB_TYPE_NAME, attributeInDbModel.getDatabaseType()); // v 3.0.0

		if ( attributeInDbModel.isKeyElement() ) {
			element.setAttribute(RepositoryConst.COLUMN_DB_PRIMARY_KEY, Boolean.toString(attributeInDbModel.isKeyElement())); // v 3.0.0
		}
		if ( attributeInDbModel.isAutoIncremented() ) { // #LGU 04/08/2011
			element.setAttribute(RepositoryConst.COLUMN_DB_AUTO_INCREMENTED, Boolean.toString(attributeInDbModel.isAutoIncremented()));
		}		
		element.setAttribute(RepositoryConst.COLUMN_DB_POSITION, Integer.toString( attributeInDbModel.getDatabasePosition() ) ); // #LGU 10/08/2011
		element.setAttribute(RepositoryConst.COLUMN_DB_DEFAULT_VALUE, attributeInDbModel.getDatabaseDefaultValue() ); // #LGU 10/08/2011
		element.setAttribute(RepositoryConst.COLUMN_DB_COMMENT, attributeInDbModel.getDatabaseComment() ); // v 2.1.1 #LCH 20/08/2014
		
		element.setAttribute(RepositoryConst.COLUMN_JDBC_TYPE_CODE, Integer.toString(attributeInDbModel.getJdbcTypeCode()));
		
		//--- JAVA OBJECT
		element.setAttribute(RepositoryConst.COLUMN_JAVA_NAME, attributeInDbModel.getName()); // v 3.0.0
		element.setAttribute(RepositoryConst.COLUMN_JAVA_TYPE, attributeInDbModel.getModelFullType()); // v 3.0.0
				
		if (StrUtil.nullOrVoid(attributeInDbModel.getDefaultValue()) == false) {
			element.setAttribute(RepositoryConst.COLUMN_JAVA_DEFAULT_VALUE, attributeInDbModel.getDefaultValue()); // #LGU 17/10/2011
		}
		
		//--- SPECIAL INFO & VALIDATION 

		element.setAttribute(RepositoryConst.COLUMN_NOT_NULL,  Boolean.toString(attributeInDbModel.isNotNull() ) );
		
		
		if ( attributeInDbModel.isJavaTypeBoolean() ) { //--- Keep boolean informations
			if (StrUtil.nullOrVoid(attributeInDbModel.getBooleanFalseValue()) == false) {
				element.setAttribute(RepositoryConst.COLUMN_BOOL_FALSE, attributeInDbModel.getBooleanFalseValue());
			}
			if (StrUtil.nullOrVoid(attributeInDbModel.getBooleanTrueValue()) == false) {
				element.setAttribute(RepositoryConst.COLUMN_BOOL_TRUE, attributeInDbModel.getBooleanTrueValue());
			}
		}

		if ( attributeInDbModel.isJavaTypeString() )  { //--- Keep String informations
			if (attributeInDbModel.isLongText()) {
				element.setAttribute(RepositoryConst.COLUMN_LONG_TEXT, Boolean.toString(attributeInDbModel.isLongText())); // v 3.0.0
			}
			if (attributeInDbModel.isNotEmpty()) {
				element.setAttribute(RepositoryConst.COLUMN_NOT_EMPTY, Boolean.toString(attributeInDbModel.isNotEmpty()) ); // v 3.0.0
			}
			if (attributeInDbModel.isNotBlank()) {
				element.setAttribute(RepositoryConst.COLUMN_NOT_BLANK, Boolean.toString(attributeInDbModel.isNotBlank()) ); // v 3.0.0
			}
			if ( attributeInDbModel.getMinLength() != null ) {
				element.setAttribute(RepositoryConst.COLUMN_MIN_LENGTH, attributeInDbModel.getMinLength().toString() ); // v 3.0.0
			}
			if ( attributeInDbModel.getMaxLength() != null ) {
				element.setAttribute(RepositoryConst.COLUMN_MAX_LENGTH, attributeInDbModel.getMaxLength().toString() ); // v 3.0.0
			}
			if ( ! StrUtil.nullOrVoid(attributeInDbModel.getPattern()) ) {
				element.setAttribute(RepositoryConst.COLUMN_PATTERN, attributeInDbModel.getPattern() );
			}
		}

		if ( attributeInDbModel.isJavaTypeDateOrTime() )  //--- Keep Date/Time informations
		{
			if ( attributeInDbModel.getDateType() != null  &&  attributeInDbModel.getDateType() != DateType.UNDEFINED ) {
				element.setAttribute( RepositoryConst.COLUMN_DATE_TYPE, convertDateTypeToString(attributeInDbModel.getDateType() ) );  // v 3.0.0
			}
			if ( attributeInDbModel.isDatePast() ) {
				element.setAttribute(RepositoryConst.COLUMN_DATE_PAST,  Boolean.toString(true)); // #LGU 30/08/2011
			}
			if ( attributeInDbModel.isDateFuture() ) {
				element.setAttribute(RepositoryConst.COLUMN_DATE_FUTURE,  Boolean.toString(true)); // #LGU 30/08/2011
			}
			if ( attributeInDbModel.isDateBefore() ) {
				element.setAttribute(RepositoryConst.COLUMN_DATE_BEFORE,  Boolean.toString(true)); // #LGU 30/08/2011
				element.setAttribute(RepositoryConst.COLUMN_DATE_BEFORE_VALUE, attributeInDbModel.getDateBeforeValue()); // #LGU 30/08/2011
			}
			if ( attributeInDbModel.isDateAfter() ) {
				element.setAttribute(RepositoryConst.COLUMN_DATE_AFTER,  Boolean.toString(true)); // #LGU 30/08/2011
				element.setAttribute(RepositoryConst.COLUMN_DATE_AFTER_VALUE, attributeInDbModel.getDateAfterValue()); // #LGU 30/08/2011
			}
		}

		if ( attributeInDbModel.isJavaTypeNumber() )  //--- Keep Number informations
		{
			if ( attributeInDbModel.getMinValue() != null ) {
				element.setAttribute(RepositoryConst.COLUMN_MIN_VALUE, attributeInDbModel.getMinValue().toString() ); // v 3.0.0
			}
			if ( attributeInDbModel.getMaxValue() != null ) {
				element.setAttribute(RepositoryConst.COLUMN_MAX_VALUE, attributeInDbModel.getMaxValue().toString() ); // v 3.0.0
			}
		}
		
		if ( attributeInDbModel.isJavaTypeNumber() || attributeInDbModel.isJavaTypeDateOrTime() )  
		{
			if ( ! StrUtil.nullOrVoid(attributeInDbModel.getFormat()) ) {
				element.setAttribute(RepositoryConst.COLUMN_FORMAT, attributeInDbModel.getFormat() );
			}
		}

		element.setAttribute(RepositoryConst.COLUMN_LABEL,      attributeInDbModel.getLabel()     ); // #LGU 20/02/2013
		element.setAttribute(RepositoryConst.COLUMN_INPUT_TYPE, attributeInDbModel.getInputType() ); // #LGU 20/02/2013

		return element;
	}

	//-----------------------------------------------------------------------------------------------------------
	// DateType management ( v 3.0.0 )
	//-----------------------------------------------------------------------------------------------------------
	private static final String STRING_DATE_ONLY      = "D";
	private static final String STRING_TIME_ONLY      = "T";
	private static final String STRING_DATE_AND_TIME  = "DT";

	/**
	 * Convert the given DateType to a String value
	 * @param dateType
	 * @return the String value ( eg : "D", "T", "DT" ) or null if the DateType is null or UNDEFINED
	 */
	private String convertDateTypeToString(DateType dateType) {
		if ( dateType != null ) {
			switch (dateType) {
				case DATE_ONLY : return STRING_DATE_ONLY ;
				case TIME_ONLY : return STRING_TIME_ONLY ;
				case DATE_AND_TIME : return STRING_DATE_AND_TIME ;
				case UNDEFINED : return null ;
			}
		}
		return null ;
	}
	
	/**
	 * Convert the given string to DateType
	 * @param s ( eg : "D", "T", "DT" )
	 * @return the DateType instance, or null if the string has an unexpected value
	 */
	private DateType convertStringToDateType(String s) {
		if ( s != null ) {
			if ( STRING_DATE_ONLY.equals(s)) return DateType.DATE_ONLY ;
			if ( STRING_TIME_ONLY.equals(s)) return DateType.TIME_ONLY ;
			if ( STRING_DATE_AND_TIME.equals(s)) return DateType.DATE_AND_TIME ;
		}
		return null ;
	}
}
