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
package org.telosys.tools.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.telosys.tools.commons.DatabaseUtil;
import org.telosys.tools.commons.JavaTypeUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.jdbctypes.JdbcTypesManager;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.DateType;
import org.telosys.tools.generic.model.types.AttributeTypeInfo;
import org.telosys.tools.generic.model.types.TypeReverser;

/**
 * Column of a table/entity in the Repository Model <br>
 * 
 * A column contains the database informations and the mapped Java attribute informations
 * 
 * @author Laurent Guerin
 *
 */
public class AttributeInDbModel implements Comparable<AttributeInDbModel>, Serializable, Attribute
{
	private static final long serialVersionUID = 1L;
	
	private final EntityInDbModel entity ; // v 3.0.0 - The entity owning the attribute
	
	public final static String SPECIAL_LONG_TEXT_TRUE = "true";
	
	//----- DATABASE -----
	
	private String  _sDatabaseName     = null ;  // dbName=""

	private String  _sDatabaseTypeName = null ;  // dbTypeName="INTEGER" - dbTypeName="VARCHAR"
	
	private String  databaseSize     = null ;     // dbSize=""
	
	private boolean _bDatabaseNotNull  = false ; // dbNotNull="true|false" ( false by default )
	
	private boolean _bKeyElement        = false ; // primaryKey="true|false" ( false by default ) // v 3.0.0
	
	// An attribute can be involved in many FK, it can be both in a SIMPLE FK and in a COMPOSITE FK 
	private boolean _bForeignKeySimple     = false ; // ( false by default )
	private boolean _bForeignKeyComposite  = false ; // ( false by default )
	private String  referencedEntityClassName = null ;
	
	private boolean _bAutoIncremented  = false ; // autoIncremented="true|false" ( false by default )
	
	private int     _iDatabasePosition = 0 ;     // position="" ( database ordinal position ) #LGU 10/08/2011
	
	private String  _sDatabaseDefaultValue = null ;  // dbDefaultValue="" ( database default value ) #LGU 10/08/2011
	
	private String  _sDatabaseComment  = null ;  // comment=""
	
	//----- JDBC -----
	
	private int     _iJdbcTypeCode     = 0 ;     // dbTypeCode="4" - dbTypeCode="12"
	
	//----- JAVA -----

	private String  _sName        = null ;  // javaName=""
	
	private String  _sModelFullType    = null ;  // javaType="int|...." 
	
	private boolean _bNotNull = false ;  // javaNotNull="true|false" 
	
	private String  _sJavaDefaultValue = null ;  // javaDefaultValue="..." 
	
	private boolean _bSelected    = true ;  // selected by default
	
	//----- SPECIAL DATA for ALL -----
	private String  _sLabel     = null ;
	private String  _sInputType = null ;
	
	//----- SPECIAL DATA for STRING -----	
	private boolean _bLongText = false ;  //  
	private boolean _bNotEmpty = false ;  // notEmpty="true|false" 
	private boolean _bNotBlank = false ;  // notBlank="true|false" 
	private Integer  _iMinLength = null ;
	private Integer  _iMaxLength = null ;
	private String  _sPattern   = null ;
	
	//----- SPECIAL DATA for DATE & TIME -----
	private DateType dateType = null ; // enumeration - ver 3.0.0
	private boolean _bDatePast   = false ;
	private boolean _bDateFuture = false ;
	private boolean _bDateBefore = false ;
	private boolean _bDateAfter  = false ;
	private String  _sDateBeforeValue = null ;
	private String  _sDateAfterValue  = null ;
	
	//----- SPECIAL DATA for NUMERIC -----
	private BigDecimal _iMinValue = null ;
	private BigDecimal _iMaxValue = null ;
	
	//----- SPECIAL DATA for BOOLEAN -----
	private String  _sBooleanTrueValue  = null ; // the special value for TRUE 
	private String  _sBooleanFalseValue = null ; // the special value for FALSE 
	
	//----- OTHER SPECIAL DATA -----
	private String  _sFormat = null ;  // Used with NUMERIC, DATE/TIME

	//----- SPECIAL DATA for key generation -----
	
	private GeneratedValueInDbModel generatedValue = null ;
	
	private TableGeneratorInDbModel tableGenerator = null ;
	
	private SequenceGeneratorInDbModel sequenceGenerator = null ;
	
	
	/**
	 * Constructor
	 * @since v 3.0.0
	 */
	public AttributeInDbModel(EntityInDbModel entity) {
		super();
		this.entity = entity ;
	}

	public EntityInDbModel getEntity() {
		return this.entity ;
	}
	//-----------------------------------------------------------------------------
	private AttributeTypeInfo getTypeInfo(String fullType) {
		return TypeReverser.getInstance().getTypeInfo(fullType);
	}
	//-----------------------------------------------------------------------------
	
	public GeneratedValueInDbModel getGeneratedValue() {
		return generatedValue;
	}

	public void setGeneratedValue(GeneratedValueInDbModel generatedValue) {
		this.generatedValue = generatedValue;
	}

	public TableGeneratorInDbModel getTableGenerator() {
		return tableGenerator;
	}

	public void setTableGenerator(TableGeneratorInDbModel tableGenerator) {
		this.tableGenerator = tableGenerator;
	}

	public SequenceGeneratorInDbModel getSequenceGenerator() {
		return sequenceGenerator;
	}

	public void setSequenceGenerator(SequenceGeneratorInDbModel sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	//-----------------------------------------------------------------------------
	@Override
	public String getDatabaseName() {
		return _sDatabaseName ;
	}

	public void setDatabaseName(String name) {
		_sDatabaseName = name ;
	}

	//-----------------------------------------------------------------------------

	public void setKeyElement(boolean b) { // v 3.0.0
		_bKeyElement = b ;
	}
	@Override
	public boolean isKeyElement() { // v 3.0.0
		return _bKeyElement ;
	}

	//-----------------------------------------------------------------------------

	public void setAutoIncremented(boolean b) {
		_bAutoIncremented = b ;
	}

	@Override
	public boolean isAutoIncremented() {
		return _bAutoIncremented ;
	}

	//-----------------------------------------------------------------------------

	public void setDatabaseNotNull(boolean flag) {
		_bDatabaseNotNull = flag ;
	}

	public void setDatabaseNotNull(String flag) {
		_bDatabaseNotNull = "true".equalsIgnoreCase(flag) ;
	}

	@Override
	public boolean isDatabaseNotNull() {
		return _bDatabaseNotNull ;
	}

	public String getDatabaseNotNullAsString() {
		return ( _bDatabaseNotNull ? "true" : "false" ) ;
	}

	//-----------------------------------------------------------------------------
	public void setDatabaseSize(String size) {
		databaseSize = size ;
	}
	@Override
	public String getDatabaseSize() {
		return databaseSize ;
	}

	//-----------------------------------------------------------------------------
	/**
	 * Returns the ordinal position of the column in the database table
	 * @param v
	 */
	public void setDatabasePosition(int v) { // #LGU 10/08/2011
		_iDatabasePosition = v ;
	}
	/**
	 * Set the ordinal position of the column in the database table
	 * @return
	 */
	public int getDatabasePosition() { // #LGU 10/08/2011
		return _iDatabasePosition ;
	}
	
	//-----------------------------------------------------------------------------
	@Override
	public String getDatabaseDefaultValue() { // #LGU 10/08/2011
		return _sDatabaseDefaultValue;
	}

	/**
	 * Set the database default value 
	 * @param v
	 */
	public void setDatabaseDefaultValue(String v) { // #LGU 10/08/2011
		_sDatabaseDefaultValue = v;
	}

	//-----------------------------------------------------------------------------

	@Override
	public String getDatabaseComment() {
		return _sDatabaseComment;
	}

	/**
	 * Set the column comment
	 * @param databaseComment comment
	 */
	public void setDatabaseComment(String databaseComment) {
		_sDatabaseComment = databaseComment;
	}
	
	//-----------------------------------------------------------------------------

	@Override
	public Integer getJdbcTypeCode() {
		return _iJdbcTypeCode ;
	}

	public void setJdbcTypeCode(int typeCode) {
		_iJdbcTypeCode = typeCode ;
	}

	@Override
	public String getJdbcTypeName() {
		String text = JdbcTypesManager.getJdbcTypes().getTextForCode( getJdbcTypeCode() );
		return text != null ? text : "???" ;
	}

	public String getJdbcTypeCodeWithText() {
		int code = getJdbcTypeCode();
		String text = JdbcTypesManager.getJdbcTypes().getTextForCode( code );
		if ( text == null ) text = "???" ;
		return code + " : " + text.toLowerCase() ;
	}

	//-----------------------------------------------------------------------------

	/**
     * Returns the database native type name <br>
     * Examples : INTEGER, VARCHAR, NUMBER, CHAR, etc... 
	 * @return
	 */
	@Override
	public String getDatabaseType() { // ver 3.0.0
		return _sDatabaseTypeName;
	}
	
	/**
     * Returns the database native type name with its size if the size make sense.<br>
     * Examples : INTEGER, VARCHAR(24), NUMBER, CHAR(3), etc... 
	 * @return
	 */
	public String getDatabaseTypeNameWithSize() {
		return DatabaseUtil.getNativeTypeWithSize(_sDatabaseTypeName, databaseSize, _iJdbcTypeCode);
	}

	public void setDatabaseTypeName(String databaseTypeName) {
		_sDatabaseTypeName = databaseTypeName;
	}

	//-----------------------------------------------------------------------------

	@Override
	public String getName() { // v 3.0.0
		return _sName;
	}
	public void setName(String s) { // v 3.0.0
		_sName = s ;
	}
	
	//-----------------------------------------------------------------------------
	@Override
	public String getNeutralType() { // v 3.0.0
		return TypeReverser.getInstance().getNeutralType(_sModelFullType, dateType) ;
	}
	//-----------------------------------------------------------------------------
	
	/**
	 * Returns the DB-Model type ( stored as a Java full type )
	 * @param s
	 */
	public void setModelFullType(String s) { // v 3.0.0
		_sModelFullType = s ;
	}
	
	/**
	 * Returns the DB-Model type ( stored as a Java full type )
	 * @return
	 */
	public String getModelFullType() { // v 3.0.0
		return _sModelFullType ;
	}

	//-----------------------------------------------------------------------------
	@Override
	public String getDefaultValue() { // ver 3.0.0
		return _sJavaDefaultValue ;
	}
	/**
	 * Set the default value for the attribute
	 * @param s the default value ( eg : "0", "false" )
	 */
	public void setDefaultValue(String s) {
		_sJavaDefaultValue = s ;
	}

	//-----------------------------------------------------------------------------
	@Override
	public String getInitialValue() { // v 3.0.0
		return null; // Not yet implemented 
	}

	//-----------------------------------------------------------------------------
	/**
	 * Returns true is the Java type is "boolean" or "java.lang.Boolean"
	 * @return
	 */
	public boolean isJavaTypeBoolean() {
		return JavaTypeUtil.isCategoryBoolean( _sModelFullType ) ;  // v 3.0.0
	}
	
	/**
	 * Returns true is the Java type is "java.lang.String"
	 * @return
	 */
	public boolean isJavaTypeString() {
		return JavaTypeUtil.isCategoryString( _sModelFullType ) ; // v 3.0.0
	}

	/**
	 * Returns true if the Java type is a numeric type : <br>
	 * "byte", "short", "int", "long", "double", "float" <br>
	 * or respective wrappers, or "BigDecimal", or "BigInteger"<br>
	 * @return
	 */
	public boolean isJavaTypeNumber() {
		return JavaTypeUtil.isCategoryNumber( _sModelFullType ) ; // v 3.0.0
	}
	
	/**
	 * Returns true if the Java type is "java.util.Date" or "java.sql.Date" <br>
	 * or "java.sql.Time" or "java.sql.Timestamp" <br>
	 * @return
	 */
	public boolean isJavaTypeDateOrTime() {
		return JavaTypeUtil.isCategoryDateOrTime( _sModelFullType ) ; // v 3.0.0
	}
	
	/**
	 * Returns true if the Java type is a "primitive type" ( "int", "boolean", "short", ... )
	 * @return
	 */
	public boolean isJavaPrimitiveType() {
		return JavaTypeUtil.isPrimitiveType( _sModelFullType ); // v 3.0.0
	}

	//-----------------------------------------------------------------------------
	@Override
	public boolean isNotNull() { // v 3.0.0
		return _bNotNull;
	}
	public void setNotNull(boolean v) {  // v 3.0.0
		_bNotNull = v ;
	}

	//-----------------------------------------------------------------------------
	@Override
	public boolean isNotEmpty() { // v 3.0.0
		return _bNotEmpty;
	}
	public void setNotEmpty(boolean v) {
		_bNotEmpty = v ;
	}

	//-----------------------------------------------------------------------------
	@Override
	public boolean isNotBlank() { // v 3.0.0
		return _bNotBlank;
	}
	public void setNotBlank(boolean v) {
		_bNotBlank = v ;
	}
	//-----------------------------------------------------------------------------
	@Override
	public Integer getMinLength() { // ver 3.0.0
		return _iMinLength;
	}
	public void setMinLength(Integer v) { // ver 3.0.0
		_iMinLength = v ;
	}
	//-----------------------------------------------------------------------------
	@Override
	public Integer getMaxLength() { // ver 3.0.0
		return _iMaxLength;
	}
	public void setMaxLength(Integer v) { // ver 3.0.0
		_iMaxLength = v ;
	}
	//-----------------------------------------------------------------------------
	public String getPattern() {
		return _sPattern;
	}
	public void setPattern(String v) {
		_sPattern = v ;
	}
	//-----------------------------------------------------------------------------
	@Override
	public boolean isSelected() { // v 3.0.0
		return _bSelected ;
	}	
	public void setSelected(boolean b) {
		_bSelected = b ;
	}

	//-----------------------------------------------------------------------------
	// Special infos
	//-----------------------------------------------------------------------------
	@Override
	public String getLabel() {  // V 2.0.3
		return _sLabel ;
	}
	public void setLabel(String s) { // V 2.0.3
		_sLabel = s ;
	}
	
	//-----------------------------------------------------------------------------
	@Override
	public String getInputType() { // V 2.0.3
		return _sInputType ;
	}
	public void setInputType(String s) { // V 2.0.3
		_sInputType = s ;
	}
	
	//-----------------------------------------------------------------------------
	@Override
	public boolean isLongText() { // v 3.0.0
		return _bLongText ;
	}	
	public void setLongText(String flag) {
		setLongText( "true".equalsIgnoreCase(flag) ) ;
	}
	public void setLongText(boolean b) {
		_bLongText = b ;
	}

	//-----------------------------------------------------------------------------
	@Override
	public DateType getDateType() {
		return dateType ; 
	}

	/**
	 * Set the date type : DATE ONLY, TIME ONLY, DATE AND TIME
	 * @param v
	 */
	public void setDateType(DateType v) {
		dateType = v ;
	}
	
	public boolean isDatePast() {
		return _bDatePast;
	}
	public void setDatePast(boolean v) {
		_bDatePast = v;
	}

	public boolean isDateFuture() {
		return _bDateFuture;
	}
	public void setDateFuture(boolean v) {
		_bDateFuture = v;
	}

	public boolean isDateBefore() {
		return _bDateBefore;
	}
	public void setDateBefore(boolean v) {
		_bDateBefore = v;
	}
	public String getDateBeforeValue() {
		return _sDateBeforeValue;
	}
	public void setDateBeforeValue(String v) {
		_sDateBeforeValue = v;
	}

	public boolean isDateAfter() {
		return _bDateAfter;
	}
	public void setDateAfter(boolean v) {
		_bDateAfter = v;
	}
	public String getDateAfterValue() {
		return _sDateAfterValue;
	}
	public void setDateAfterValue(String v) {
		_sDateAfterValue = v;
	}
	//-----------------------------------------------------------------------------

	/**
	 * The value used to store a TRUE in the database ( never null )
	 * @return the value or "" if none (never null)
	 */
	public String getBooleanTrueValue() {
		return ( _sBooleanTrueValue != null ? _sBooleanTrueValue : "" );
	}
	/**
	 * The value used to store a FALSE in the database ( never null )
	 * @return the value or "" if none (never null)
	 */
	public String getBooleanFalseValue() {
		return ( _sBooleanFalseValue != null ? _sBooleanFalseValue : "" );
	}

	public void setBooleanTrueValue(String v) {
		_sBooleanTrueValue = v ;
	}
	public void setBooleanFalseValue(String v) {
		_sBooleanFalseValue = v ;
	}

	//-----------------------------------------------------------------------------

	public String getFormat() {
		return _sFormat ; 
	}
	public void setFormat(String v) {
		_sFormat = v ;
	}
	
	//-----------------------------------------------------------------------------

	@Override
	public BigDecimal getMinValue() { // ver 3.0.0
		return _iMinValue ; 
	}
	public void setMinValue(BigDecimal v) { // ver 3.0.0
		_iMinValue = v ;
	}
	
	@Override
	public BigDecimal getMaxValue() { // ver 3.0.0
		return _iMaxValue ; 
	}
	public void setMaxValue(BigDecimal v) { // ver 3.0.0
		_iMaxValue = v ;
	}
	
	//-----------------------------------------------------------------------------

	/**
	 * Returns the "special type informations" for this column if any ( else "", never null )
	 * @return : Special information, ie "Long Text", "Date only", "Time only", boolean true/false value
	 */
	public String getSpecialTypeInfo()  {
		
		StringBuffer sb = new StringBuffer();
		if ( this.isJavaTypeString() ) {
			if ( isLongText() ) addStr(sb, "Long Text") ; // v 3.0.0
			if ( isNotEmpty() ) addStr(sb, "NE") ; // v 3.0.0
			if ( isNotBlank() ) addStr(sb, "NB") ; // v 3.0.0
			if ( ( getMinLength() != null ) || ( getMaxLength() != null ) )
			{
				addStr( sb, "[" + getMinLength() + ";" + getMaxLength() + "]" );
			}
			if ( ! StrUtil.nullOrVoid( getPattern() ) ) addStr(sb, "P" ) ;
		}
		else if ( this.isJavaTypeBoolean() ) {
			if ( ! StrUtil.nullOrVoid( getBooleanTrueValue() ) ) {
				addStr( sb, getBooleanTrueValue() + ":" + getBooleanFalseValue() );
			}
		}
		else if ( this.isJavaTypeNumber() ) {
			if ( ! StrUtil.nullOrVoid( getDefaultValue() ) )
			{
				addStr( sb, getDefaultValue() );
			}
			if ( ( getMinValue() != null ) || ( getMaxValue() != null ) )
			{
				addStr( sb, "[" + getMinValue() + ";" + getMaxValue() + "]" );
			}
		}
		else if ( this.isJavaTypeDateOrTime() ) {
			DateType dateType = getDateType();
			if ( dateType == DateType.DATE_ONLY )     addStr( sb, "Date only" );
			if ( dateType == DateType.TIME_ONLY )     addStr( sb, "Time only" );
			if ( dateType == DateType.DATE_AND_TIME ) addStr( sb, "Date & Time" );
			
			if ( isDatePast() ) addStr( sb, "P" ); 
			if ( isDateFuture() ) addStr( sb, "F" ); 
			if ( isDateBefore() ) addStr( sb, "B" ); 
			if ( isDateAfter() ) addStr( sb, "A" ); 
		}
		return sb.toString();
	}
	private void addStr(StringBuffer sb, String s)
	{
		if ( sb.length() > 0 ) sb.append(",");
		sb.append(s);
	}
	
	/**
	 * Clear all the "special type informations" for this column
	 */
	public void clearSpecialTypeInfo() 
	{
		setNotNull(false); // v 3.0.0
		//--- Boolean category 
		setBooleanTrueValue(null);
		setBooleanFalseValue(null);
		//--- Date category 
		setDateType(null);
		setDatePast(false);
		setDateFuture(false);
		setDateBefore(false);
		setDateBeforeValue(null);
		setDateAfter(false);
		setDateAfterValue(null);
		//--- Number category 
		setMinValue(null);
		setMaxValue(null);
		//--- String category 
		setLongText(false);
		setNotEmpty(false);
		setNotBlank(false);
		setMinLength(null);
		setMaxLength(null);
		setPattern(null);
	}
	
	public int compareTo(AttributeInDbModel other) {
		if ( other != null )
		{
			return ( this.getDatabasePosition() - other.getDatabasePosition() );
		}
		return 0;
	}

	//-----------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() 
	{
		return 
			getName() + "|" // added in v 3.0.0
			+ "table:" + getDatabaseName() + "|" 
			+ ( isKeyElement() ? "PK" : "" ) + "|" // v 3.0.0
			+ getJdbcTypeCode() + "|" 
			+ getNeutralType() ; // v 3.0.0
	}

	//---------------------------------------------------------------------------------------------------
	// Methods added in ver 3.0.0 in order to be compliant with the "generic model"
	//---------------------------------------------------------------------------------------------------
	@Override
	public boolean isGeneratedValue() {
        if ( this.isAutoIncremented() ) {
        	return true ; 
        } 
        else if (this.getGeneratedValue() != null) {
        	return true ; 
        }
    	return false ; 
	}

	@Override
	public String getGeneratedValueGenerator() {
        if ( this.isAutoIncremented() ) {
        	return null ;
        } 
        else {
			if (this.getGeneratedValue() != null) {
				return this.getGeneratedValue().getGenerator();
			}
			else {
	        	return null ;
			}
        }
	}

	@Override
	public String getGeneratedValueStrategy() {
		// e.g : 'auto', 'identity', 'sequence', 'table' 
        if ( this.isAutoIncremented() ) {
        	return null ; // "AUTO" is the default strategy
        } 
        else {
			if (this.getGeneratedValue() != null) {
				return this.getGeneratedValue().getStrategy();
			}
			else {
	        	return null ;
			}
        }
	}

	//---------------------------------------------------------------------------------------------------
	// Sequence generator information
	//---------------------------------------------------------------------------------------------------
	@Override
	public boolean hasSequenceGenerator() {
		return this.sequenceGenerator != null ;
	}

	@Override
	public Integer getSequenceGeneratorAllocationSize() {
		if (this.sequenceGenerator != null) {
			return this.sequenceGenerator.getAllocationSize() ;
		}
		return null;
	}

	@Override
	public String getSequenceGeneratorName() {
		if (this.sequenceGenerator != null) {
			return this.sequenceGenerator.getName() ;
		}
		return null;
	}

	@Override
	public String getSequenceGeneratorSequenceName() {
		if (this.sequenceGenerator != null) {
			return this.sequenceGenerator.getSequenceName() ;
		}
		return null;
	}

	//---------------------------------------------------------------------------------------------------
	// Table generator information
	//---------------------------------------------------------------------------------------------------
	@Override
	public boolean hasTableGenerator() {
		return this.tableGenerator != null ;
	}

	@Override
	public String getTableGeneratorName() {
		if ( this.tableGenerator != null ) {
			return this.tableGenerator.getName() ;
		}
		return null;
	}

	@Override
	public String getTableGeneratorPkColumnName() {
		if ( this.tableGenerator != null ) {
			return this.tableGenerator.getPkColumnName() ;
		}
		return null;
	}

	@Override
	public String getTableGeneratorPkColumnValue() {
		if ( this.tableGenerator != null ) {
			return this.tableGenerator.getPkColumnValue() ;
		}
		return null;
	}

	@Override
	public String getTableGeneratorTable() {
		if ( this.tableGenerator != null ) {
			return this.tableGenerator.getTable() ;
		}
		return null;
	}

	@Override
	public String getTableGeneratorValueColumnName() {
		if ( this.tableGenerator != null ) {
			return this.tableGenerator.getValueColumnName() ;
		}
		return null;
	}

	@Override
	public boolean isPrimitiveTypeExpected() {
		return getTypeInfo(_sModelFullType).isPrimitiveTypeExpected() ;
	}

	@Override
	public boolean isUnsignedTypeExpected() {
		return false; // Always false for this kind of model (no unsigned type in Java)
	}

	@Override
	public boolean isObjectTypeExpected() {
		return getTypeInfo(_sModelFullType).isObjectTypeExpected();
	}

	@Override
	public boolean isSqlTypeExpected() {
		return getTypeInfo(_sModelFullType).isSqlTypeExpected();
	}

	//-----------------------------------------------------------------------------
	
	@Override
	public boolean isFK() {
		return _bForeignKeySimple || _bForeignKeyComposite ;
	}

	public void setFKSimple(boolean flag) {
		_bForeignKeySimple = flag ;
	}
	@Override
	public boolean isFKSimple() {
		return _bForeignKeySimple;
	}

	public void setFKComposite(boolean flag) {
		_bForeignKeyComposite = flag ;
	}
	@Override
	public boolean isFKComposite() {
		return _bForeignKeyComposite;
	}

	public void setReferencedEntityClassName(String entityClassName) {
		referencedEntityClassName = entityClassName ;
	}
	@Override
	public String getReferencedEntityClassName() {
		if ( isFK() ) {
			return referencedEntityClassName ;
		}
		else {
			return null ;
		}
	}
	
	
	/**
	 * Returns true if the attribute is used in at least one the given links
	 * @param links
	 * @return
	 */
	private boolean isUsedInLink( List<LinkInDbModel> links ) {
		if ( links != null ) {
			for ( LinkInDbModel link : links ) {
				if( link.isOwningSide() ) {
					if ( link.usesAttribute(this) ) {
						return true ;
					}
				}
			}
		}
		return false ;
	}
	
	@Override
	public boolean isUsedInLinks() {
		// Is it used in one of all the links ?
		return isUsedInLink( this.entity.getAllLinks() );
	}
	
	@Override
	public boolean isUsedInSelectedLinks() {
		// Is it used in one of the selected links ?
		return isUsedInLink( this.entity.getSelectedLinks() );
	}

	//-----------------------------------------------------------------------------------------
	// ATTRIBUTE TAGS (added in v 3.3.0) : NO TAGS IN DB-MODEL 
	//-----------------------------------------------------------------------------------------	
	@Override
	public String getTagValue(String tagName) {
		return "";
	}
	@Override
	public boolean hasTag(String tagName) {
		return false;
	}

}
