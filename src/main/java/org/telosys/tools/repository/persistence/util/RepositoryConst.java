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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RepositoryConst {

//	//--------------------------------------------------------------------------------------------------
//	// WRAPPERS 
//	//--------------------------------------------------------------------------------------------------
//	public final static BaseWrapper               BASE_WRAPPER = new BaseWrapper();
//	public final static EntityWrapper             ENTITY_WRAPPER = new EntityWrapper();
//	public final static LinkWrapper               LINK_WRAPPER = new LinkWrapper();
//	public final static ColumnWrapper             COLUMN_WRAPPER = new ColumnWrapper();
//	public final static ForeignKeyWrapper         FOREIGNKEY_WRAPPER = new ForeignKeyWrapper();
//	public final static ForeignKeyColumnWrapper   FOREIGNKEY_COLUMN_WRAPPER = new ForeignKeyColumnWrapper();
//
//	public final static JoinTableWrapper          JOIN_TABLE_WRAPPER  = new JoinTableWrapper();
//	//public final static JoinFKWrapper     JOIN_FK_WRAPPER     = new JoinFKWrapper();	
//	public final static JoinColumnWrapper         JOIN_COLUMN_WRAPPER          = new JoinColumnWrapper();	
//	
//	public final static JoinColumnsWrapper        JOIN_COLUMNS_WRAPPER         = new JoinColumnsWrapper();
//	public final static InverseJoinColumnsWrapper INVERSE_JOIN_COLUMNS_WRAPPER = new InverseJoinColumnsWrapper();
//
//	public final static GeneratedValueWrapper     GENERATED_VALUE_WRAPPER = new GeneratedValueWrapper();
//	public final static SequenceGeneratorWrapper  SEQUENCE_GENERATOR_WRAPPER = new SequenceGeneratorWrapper();
//	public final static TableGeneratorWrapper     TABLE_GENERATOR_WRAPPER = new TableGeneratorWrapper();
//	
//	//--------------------------------------------------------------------------------------------------

	// v 2.1.1
	//public final static SimpleDateFormat DATE_TIME_ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final String COLLECTION_JAVA_TYPE = "java.util.List";
	
	public final static String MAPPING_ONE_TO_ONE   = "OneToOne";
	public final static String MAPPING_ONE_TO_MANY  = "OneToMany";
	public final static String MAPPING_MANY_TO_ONE  = "ManyToOne";
	public final static String MAPPING_MANY_TO_MANY = "ManyToMany";
	public final static String MAPPING_UNKNOWN      = "Unknown";
	
	public final static String MAPPING_TO_ONE   = "ToOne";
	public final static String MAPPING_TO_MANY  = "ToMany";

	public final static String CASCADE_ALL     = "ALL";
	public final static String CASCADE_MERGE   = "MERGE";
	public final static String CASCADE_PERSIST = "PERSIST";
	public final static String CASCADE_REFRESH = "REFRESH";
	public final static String CASCADE_REMOVE  = "REMOVE";
		
	public final static String FETCH_DEFAULT = "DEFAULT";
	public final static String FETCH_EAGER   = "EAGER";
	public final static String FETCH_LAZY    = "LAZY";
	
	public final static String OPTIONAL_UNDEFINED = "UNDEFINED";
	public final static String OPTIONAL_TRUE      = "TRUE";
	public final static String OPTIONAL_FALSE     = "FALSE";
	
	public final static String SPECIAL_DATE_ONLY      = "D";
	public final static String SPECIAL_TIME_ONLY      = "T";
	public final static String SPECIAL_DATE_AND_TIME  = "DT";

	//--------------------------------------------------------------------------------------------------
	//--- XML <root> element 
	public final static String ROOT_ELEMENT = "root";
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <tablelist> element and attributes
	public final static String TABLELIST = "tableList";
	public final static String TABLELIST_GENERATION            = "generation";
	public final static String TABLELIST_DATABASE_NAME         = "databaseName";
	public final static String TABLELIST_DATABASE_PRODUCT_NAME = "databaseProductName";
	public final static String TABLELIST_DATABASE_ID           = "databaseId";

	//--------------------------------------------------------------------------------------------------
	//--- XML <table> element and attributes
	public final static String TABLE = "table";
	public final static String TABLE_NAME       = "name";
	public final static String TABLE_CATALOG    = "catalog"; // v 1.0 #LGU
	public final static String TABLE_SCHEMA     = "schema";  // v 1.0 #LGU
	//public final static String TABLE_BOOLEAN_STORAGE = "booleanStorage";
	public final static String TABLE_JAVA_BEAN      = "javaBean";
//	public final static String TABLE_JAVA_BEAN_LIST = "javaBeanList"; // removed in v 2.0.7
//	public final static String TABLE_JAVA_BEAN_DAO  = "javaBeanDAO";// removed in v 2.0.7
//	public final static String TABLE_JAVA_BEAN_CONV = "javaBeanConv";// removed in v 2.0.7
	public final static String TABLE_DATABASE_TYPE  = "databaseType"; // added in v 2.0.7

	//--------------------------------------------------------------------------------------------------
	//--- XML <column> element and attributes
	public final static String COLUMN = "column";
	
	public final static String COLUMN_SELECTED            = "selected";
	
	public final static String COLUMN_DB_NAME             = "dbName";
	public final static String COLUMN_DB_TYPE_NAME        = "dbTypeName";
	public final static String COLUMN_DB_SIZE             = "dbSize";
	public final static String COLUMN_DB_NOTNULL          = "dbNotNull";
	public final static String COLUMN_DB_PRIMARY_KEY      = "dbPrimaryKey";
	public static final String COLUMN_DB_FOREIGN_KEY      = "dbForeignKey"; // v 0.9.0
	public static final String COLUMN_DB_AUTO_INCREMENTED = "dbAutoIncremented"; // v 1.0  #LGU 04/08/2011
	public static final String COLUMN_DB_POSITION         = "dbPosition"; // v 1.0  #LGU 10/08/2011
	public static final String COLUMN_DB_DEFAULT_VALUE    = "dbDefaultValue"; // v 1.0  #LGU 10/08/2011
	public static final String COLUMN_DB_COMMENT          = "dbComment"; // v 2.1.1 #LCH 20/08/2014
	
	public final static String COLUMN_JDBC_TYPE_CODE      = "jdbcTypeCode";

	public final static String COLUMN_JAVA_NAME           = "javaName";
	public final static String COLUMN_JAVA_TYPE           = "javaType";
	public final static String COLUMN_JAVA_DEFAULT_VALUE  = "javaDefaultValue"; // v 1.0  #LGU 17/10/2011
	
	//--- Further informations 
	public final static String COLUMN_NOT_NULL          = "notNull"; // v 1.0  #LGU 30/08/2011
	
	public final static String COLUMN_LONG_TEXT         = "longText";
	public final static String COLUMN_NOT_EMPTY         = "notEmpty";// v 1.0  #LGU 30/08/2011
	public final static String COLUMN_NOT_BLANK         = "notBlank";// v 1.0  #LGU 30/08/2011
	public final static String COLUMN_MIN_LENGTH        = "minLength";// v 1.0  #LGU 01/09/2011
	public final static String COLUMN_MAX_LENGTH        = "maxLength";// v 1.0  #LGU 01/09/2011
	public final static String COLUMN_PATTERN           = "pattern";// v 1.0  #LGU 01/09/2011
	
	public final static String COLUMN_BOOL_TRUE         = "boolTrue";
	public final static String COLUMN_BOOL_FALSE        = "boolFalse";
	
	public final static String COLUMN_DATE_TYPE         = "dateType";
	public final static String COLUMN_DATE_PAST         = "datePast"; // v 1.0  #LGU 30/08/2011
	public final static String COLUMN_DATE_FUTURE       = "dateFuture";// v 1.0  #LGU 30/08/2011
	public final static String COLUMN_DATE_BEFORE       = "dateBefore";// v 1.0  #LGU 30/08/2011
	public final static String COLUMN_DATE_BEFORE_VALUE = "dateBeforeValue";// v 1.0  #LGU 30/08/2011
	public final static String COLUMN_DATE_AFTER        = "dateAfter";// v 1.0  #LGU 30/08/2011
	public final static String COLUMN_DATE_AFTER_VALUE  = "dateAfterValue";// v 1.0  #LGU 30/08/2011
	
	public final static String COLUMN_MIN_VALUE         = "minValue"; // v 1.0  #LGU 31/08/2011
	public final static String COLUMN_MAX_VALUE         = "maxValue"; // v 1.0  #LGU 31/08/2011

	public final static String COLUMN_FORMAT            = "format"; // v 1.0  #LGU 31/08/2011
	
	public final static String COLUMN_LABEL             = "label"; // v 2.0.3  #LGU 20/02/2013
	public final static String COLUMN_INPUT_TYPE        = "inputType"; // v 2.0.3  #LGU 20/02/2013
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <fk> element and attributes
	public static final String FK        = "fk";
	public static final String FK_NAME   = "name";

	//--------------------------------------------------------------------------------------------------
	//--- XML <fkcol> element and attributes
	public static final String FKCOL = "fkcol";
	public static final String FKCOL_TABLENAME   = "tablename";
	public static final String FKCOL_COLNAME     = "colname";
	public static final String FKCOL_SEQUENCE    = "sequence";
	public static final String FKCOL_TABLEREF    = "tableref";
	public static final String FKCOL_COLREF      = "colref";
	public static final String FKCOL_UPDATERULE  = "updaterule";
	public static final String FKCOL_DELETERULE  = "deleterule"; 
	public static final String FKCOL_DEFERRABLE  = "deferrable";

	//--------------------------------------------------------------------------------------------------
	//--- XML <link> element and attributes
	public static final String LINK = "link";
	public static final String LINK_CASCADE = "cascade";
	public static final String LINK_FETCH = "fetch";
	public static final String LINK_ID = "id";
	public static final String LINK_INVERSE_SIDE_OF = "inverseSideOf";
	public static final String LINK_JAVA_NAME = "javaName";
	public static final String LINK_JAVA_TYPE = "javaType";
	public static final String LINK_MAPPED_BY = "mappedBy";
	public static final String LINK_OPTIONAL = "optional";
	public static final String LINK_OWNING_SIDE = "owningSide";
	public static final String LINK_TARGET_ENTITY = "targetEntity";
	public static final String LINK_SOURCE_TABLE_NAME = "sourceTableName";
	public static final String LINK_TARGET_TABLE_NAME = "targetTableName";
	public static final String LINK_FOREIGN_KEY_NAME = "foreignKeyName";
	public static final String LINK_JOIN_TABLE_NAME = "joinTableName";
	
	public static final String LINK_CARDINALITY = "cardinality";
	public static final String LINK_USED = "used";
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <joinTable> element and attributes
	public static final String JOIN_TABLE_ELEMENT = "joinTable";
	public static final String JOIN_TABLE_NAME = "name";
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <joinFK> element and attributes
	public static final String JOIN_FK_ELEMENT = "joinFK";
	public static final String JOIN_FK_NAME = "name";

	//--------------------------------------------------------------------------------------------------
	//--- XML <joinColumns> element and attributes
	public static final String JOIN_COLUMNS_ELEMENT = "joinColumns";

	//--------------------------------------------------------------------------------------------------
	//--- XML <inverseJoinColumns> element and attributes
	public static final String INVERSE_JOIN_COLUMNS_ELEMENT = "inverseJoinColumns";
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <joinColumn> element and attributes
	public static final String JOIN_COLUMN_ELEMENT = "joinColumn";
	public static final String JOIN_COLUMN_NAME = "name";
	public static final String JOIN_FK_TABLE = "table";
	public static final String JOIN_COLUMN_REFERENCEDCOLUMNNAME = "referencedColumnName";
	public static final String JOIN_COLUMN_UNIQUE = "unique";
	public static final String JOIN_COLUMN_NULLABLE = "nullable";
	public static final String JOIN_COLUMN_UPDATABLE = "updatable";
	public static final String JOIN_COLUMN_INSERTABLE = "insertable";
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <generatedValue> element and attributes
	public static final String GENERATED_VALUE_ELEMENT = "generatedValue";
	public static final String GENERATED_VALUE_STRATEGY = "strategy";
	public static final String GENERATED_VALUE_GENERATOR = "generator";
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <sequenceGenerator> element and attributes
	public static final String SEQUENCE_GENERATOR_ELEMENT = "sequenceGenerator";
	public static final String SEQUENCE_GENERATOR_NAME = "name";
	public static final String SEQUENCE_GENERATOR_SEQUENCENAME = "sequenceName";
	public static final String SEQUENCE_GENERATOR_ALLOCATIONSIZE = "allocationSize";
	
	//--------------------------------------------------------------------------------------------------
	//--- XML <tableGenerator> element and attributes
	public static final String TABLE_GENERATOR_ELEMENT = "tableGenerator";
	public static final String TABLE_GENERATOR_NAME = "name";
	public static final String TABLE_GENERATOR_TABLE = "table";
	public static final String TABLE_GENERATOR_PKCOLUMNNAME = "pkColumnName";
	public static final String TABLE_GENERATOR_PKCOLUMNVALUE = "pkColumnValue";
	public static final String TABLE_GENERATOR_VALUECOLUMNNAME = "valueColumnName";
	

	//--------------------------------------------------------------------------------------------------
	private final static String DATE_TIME_ISO_FORMAT = "yyyy-MM-dd HH:mm:ss" ; // v 2.1.1
	
	//public static Date getDate(final String p_date) {
	public static Date parseDate(final String p_date) { // v 2.1.1
		Date date = null;
		try {
			//date = RepositoryConst.DATE_TIME_ISO_FORMAT.parse(p_date);
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_ISO_FORMAT); // v 2.1.1
			date = dateFormat.parse(p_date);
		} catch (ParseException e) {
			throw new RuntimeException("date parsing impossible" + p_date, e);
		}
		return date;
	}
	
	public static String formatDate(final Date date) { // v 2.1.1
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_ISO_FORMAT);
		return dateFormat.format(date);
	}
}
