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

import org.telosys.tools.repository.model.EntityInDbModel;

/**
 * @author L.GUERIN
 * 
 */
public interface RepositoryRules
{
    /**
     * Returns the ENTITY class name for the given database table name
     * @param databaseTableName
     * @return
     */
    public String getEntityClassName(String databaseTableName);
    

//    /**
//     * Returns the ENTITY attribute name for the given database column 
//     * @param databaseColumnName the name of the column in the database
//     * @param databaseColumnType the native type of the column in the database
//     * @param jdbcTypeCode the JDBC type code
//     * @return the attribute name
//     */
//    public String getAttributeName(String databaseColumnName, String databaseColumnType, int jdbcTypeCode);

    
    /**
     * Returns the attribute name for the given database column 
     * @param databaseColumnName the name of the column in the database
     * @return the attribute name
     */
    public String getAttributeName(String databaseColumnName );

    
    /**
     * Returns the attribute type for the given column 
     * @param databaseColumnType the native database type
     * @param jdbcTypeCode the JDBC type code
     * @param columnNotNull flag "NOT NULL" for the column
     * @return the attribute type
     */
    public String getAttributeType(String databaseColumnType, int jdbcTypeCode, boolean columnNotNull );

	/**
     * Returns the attribute name for a link with a "Many To One" or "One To One" cardinality
	 * @param entity  the entity needing an attribute name ( for a single object )
	 * @param referencedEntity the entity referenced by the link
	 * @return the attribute name ( eg "book", "employee", "employee2", etc )
	 */
	public String getAttributeNameForLinkToOne(EntityInDbModel entity, EntityInDbModel referencedEntity ) ;

	/**
     * Returns the attribute name for a link with a "One To Many" or "Many To Many" cardinality
	 * @param entity  the entity needing an attribute name ( for a collection )
	 * @param referencedEntity the entity referenced by the link
	 * @return the attribute name ( eg "listOfBook", "listOfEmployee", "listOfEmployee2", etc )
	 */
	public String getAttributeNameForLinkToMany(EntityInDbModel entity, EntityInDbModel referencedEntity ) ;

    /**
     * Returns the attribute GUI type for the given column 
     * @param columnTypeName
     * @param jdbcTypeCode
     * @return
     */
    public String getAttributeGuiType(String columnTypeName, int jdbcTypeCode );

    /**
     * Returns the attribute GUI label for the given column name
     * @param databaseColumnName
     * @return
     */
    public String getAttributeGuiLabel( String databaseColumnName );


}