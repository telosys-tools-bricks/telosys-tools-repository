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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Link;

/**
 * "Entity" model class ( a Database Table mapped to a Java Class ) <br>
 * An entity contains : <br>
 * - 1..N columns <br>
 * - 0..N foreign keys <br>
 * - 0..N links <br>
 * 
 * @author Laurent Guerin
 *
 */
public class EntityInDbModel implements Serializable, Entity // v 3.0.0 ( 2 specific comparators )
{
	private static final long serialVersionUID = 1L;

	private String databaseTable ;
	
	private String databaseCatalog ; 
	
	private String databaseSchema ;  // v 3.0.0

	private String databaseType ; // v 2.0.7 #LGU
	
	private String databaseComment = "";  // v 3.1.0

	private String className ; // v 3.0.0
	
	private Hashtable<String,AttributeInDbModel>  attributes  = new Hashtable<>() ; 

	private Hashtable<String,ForeignKeyInDbModel> foreignKeys = new Hashtable<>() ;

	private Hashtable<String,LinkInDbModel>       links       = new Hashtable<>() ;

	
	/**
	 * Default constructor 
	 */
	public EntityInDbModel() {
		super();
		this.className = "";
		this.databaseTable = "";
	}

	/**
	 * Constructor for tests
	 * @param className
	 * @param databaseTable
	 */
	public EntityInDbModel(String className, String databaseTable) {
		super();
		this.className = className;
		this.databaseTable = databaseTable;
	}

	/**
	 * Returns true if the entity can be considered as a "Join Table" <br>
	 * Conditions : <br>
	 * . the entity has 2 Foreign Keys <br>
	 * . all the columns are in the Primary Key <br>
	 * . all the columns are in a Foreign Key <br>
	 * 
	 * @return
	 */
	public boolean isJoinTable() 
	{
		//--- Check if there are 2 FK
		if ( foreignKeys.size() != 2 ) {
			return false;
		} 
				
		//--- Check if all the columns are in the Primary Key
		for ( AttributeInDbModel column : getAttributesArray() ) {
			if ( ! column.isKeyElement() ) { 
				return false ;
			}
		}
		
		//--- Check if all the columns are in a Foreign Key
		for ( AttributeInDbModel attribute : getAttributesArray() ) {
			if ( ! attribute.isFK() ) { 
				return false ;
			}
		}

		return true ;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseTable() {
		return this.databaseTable;
	}
	public void setDatabaseTable(String s) {
		this.databaseTable = s;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseSchema() {
		return this.databaseSchema ;
	}
	/**
	 * Set the database schema of the entity 
	 * @param s
	 */
	public void setDatabaseSchema(String s) {
		this.databaseSchema = s;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseComment() {
		return databaseComment;
	}
	public void setDatabaseComment(String s) {
		this.databaseComment = s != null ? s : "" ; // never null 
	}	
	
	//--------------------------------------------------------------------------
	
	/**
	 * Returns the database type of the entity ( "TABLE", "VIEW", ... )
	 * @return
	 * @since 2.0.7
	 */
	public String getDatabaseType() {
		return databaseType;
	}

	/**
	 * Set the database type of the entity ( "TABLE", "VIEW", ... )
	 * @param s
	 * @since 2.0.7
	 */
	public void setDatabaseType(String s) {
		this.databaseType = s;
	}
	
	@Override
	public Boolean isTableType() { // v 3.0.0
		if ( databaseType != null ) {
			return "TABLE".equalsIgnoreCase( databaseType.trim() ) ;
		}
		return false;
	}

	@Override
	public Boolean isViewType() { // 3.0.0
		if ( databaseType != null ) {
			return "VIEW".equalsIgnoreCase( databaseType.trim() ) ;
		}
		return false;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getDatabaseCatalog() {
		return this.databaseCatalog;
	}

	/**
	 * Set the database catalog 
	 * @param s
	 */
	public void setDatabaseCatalog(String s) {
		this.databaseCatalog = s;
	}


	//--------------------------------------------------------------------------
	@Override
	public String getClassName() {
		return this.className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String getPackageName() {
		// No package name in this model (defined in the configuration)
		return null;
	}

	@Override
	public String getFullName() {
		// No package name in this model (defined in the configuration)
		return null;
	}

	//--------------------------------------------------------------------------
	// ATTRIBUTES ( ex COLUMNS )  management
	//--------------------------------------------------------------------------
	/**
	 * Returns the number of attributes 
	 * @return
	 */
	public int getAttributesCount() {
		return attributes.size();
	}

	/**
	 * Returns an array containing all the columns of the entity<br>
	 * The columns are sorted by ordinal position (the original database order).
	 * 
	 * @return
	 */
	public AttributeInDbModel[] getAttributesArray() {
		AttributeInDbModel[] cols = (AttributeInDbModel[]) ( attributes.values().toArray( new AttributeInDbModel[attributes.size()] ) );
		Arrays.sort(cols); // sort using the "Comparable" implementation
		return cols ;
	}

	public void storeAttribute(AttributeInDbModel attribute) { // 'storeColumn' renamed in v 3.0.0
		if ( attribute.getEntity() != this ) {
			throw new IllegalStateException("Invalid entity in attribute '" + attribute.getName() + "'");
		}
		attributes.put(attribute.getDatabaseName(), attribute);
	}

	public AttributeInDbModel getAttributeByColumnName(String name) {
		return attributes.get(name);
	}

	public void removeAttribute(AttributeInDbModel attribute) { // 'removeColumn' renamed in v 3.0.0
		attributes.remove(attribute.getDatabaseName());
	}

	//--------------------------------------------------------------------------
	// COLUMNS exposed as "ATTRIBUTES" of the "GENERIC MODEL" ( v 3.0.0 )
	//--------------------------------------------------------------------------
	@Override
	public List<Attribute> getAttributes() {
		Attribute[] attributesArray = getAttributesArray();
		LinkedList<Attribute> attributesList = new LinkedList<>();
		for ( Attribute a : attributesArray ) {
			attributesList.add(a);
		}
		return attributesList ;
	}

	//--------------------------------------------------------------------------
	// FOREIGN KEYS management
	//--------------------------------------------------------------------------
	
	/**
	 * Returns an array of all the foreign keys of the entity (table).<br>
	 * The foreign keys are sorted by name.
	 * @return
	 */
	public ForeignKeyInDbModel[] getForeignKeys()
	{
		ForeignKeyInDbModel[] array = (ForeignKeyInDbModel[]) foreignKeys.values().toArray(new ForeignKeyInDbModel[foreignKeys.size()]);
		Arrays.sort(array);
		return array ;
	}
	
	public void storeForeignKey(ForeignKeyInDbModel foreignKey) {
		foreignKeys.put(foreignKey.getName(), foreignKey);
	}
	
	public ForeignKeyInDbModel getForeignKey(String name) {
		return foreignKeys.get(name);
	}
	
	public void removeForeignKey(ForeignKeyInDbModel foreignKey) {
		foreignKeys.remove(foreignKey.getName() );
	}

	//--------------------------------------------------------------------------
	// FOREIGN KEYS exposed as "GENERIC MODEL FOREIGN KEYS" 
	//--------------------------------------------------------------------------
	@Override
	public List<ForeignKey> getDatabaseForeignKeys() {
		//--- Build a sorted array
		ForeignKey[] foreignKeysArray = (ForeignKey[]) foreignKeys.values().toArray( new ForeignKey[foreignKeys.size()] );
		Arrays.sort(foreignKeysArray); // sort using the "Comparable" implementation		
		//--- Build a List from the array
		LinkedList<ForeignKey> foreignKeysList = new LinkedList<>();
		for ( ForeignKey fk : foreignKeysArray ) {
			foreignKeysList.add(fk);
		}
		return foreignKeysList ;		
	}
	
	//--------------------------------------------------------------------------
	// LINKS management
	//--------------------------------------------------------------------------
	/**
	 * Returns the number of links 
	 * @return
	 */
	public int getLinksCount() {
		return links.size();
	}
	
	/**
	 * Returns all the links of the entity as an array
	 * @return
	 */
	public LinkInDbModel[] getLinksArray()
	{
		return (LinkInDbModel[]) links.values().toArray(new LinkInDbModel[links.size()]);
	}

	/**
	 * Returns a List containing all the links of the entity <br>
	 * The links are in a random order
	 * @return
	 */
	public List<LinkInDbModel> getAllLinks() {
		List<LinkInDbModel> list = new LinkedList<>();
		for ( LinkInDbModel link : links.values() ) {
			list.add(link);
		}
		return list ;
	}

	/**
	 * Returns a List containing all the selected links of the entity <br>
	 * The links are in a random order
	 * @return
	 */
	public List<LinkInDbModel> getSelectedLinks() {
		List<LinkInDbModel> list = new LinkedList<>();
		for ( LinkInDbModel link : links.values() ) {
			if ( link.isSelected() ) {
				list.add(link);
			}
		}
		return list ;
	}

	@Override
	public List<Link> getLinks() {
		Link[] linksArray = links.values().toArray(new Link[links.size()]);
		return Arrays.asList(linksArray);
	}

	/**
	 * Returns all the links referencing the given entity name
	 * @return
	 * @since 2.1.1
	 */
	public List<LinkInDbModel> getLinksTo(String entityName) {
		LinkedList<LinkInDbModel> selectedLinks = new LinkedList<>();
		for ( LinkInDbModel link : links.values() ) {
			if ( link.getTargetTableName().equals(entityName) ) {
				selectedLinks.add(link);
			}
		}
		return selectedLinks;
	}
	
	/**
	 * Store (add or update the given link)
	 * @param link
	 */
	public void storeLink(LinkInDbModel link) {
		links.put(link.getId(), link);
	}
	
	/**
	 * Get a link by its id
	 * @param id
	 * @return
	 */
	public LinkInDbModel getLink(String id) {
		return links.get(id);
	}
	
	/**
	 * Remove the given link from the entity
	 * @param link
	 */
	public int removeLink(LinkInDbModel link) {
		LinkInDbModel linkRemoved = links.remove( link.getId() );
		return linkRemoved != null ? 1 : 0 ;
	}

	/**
	 * Remove all the links from the entity
	 */
	public void removeAllLinks() {
		links.clear();
	}

	//--------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(EntityInDbModel other) {
		if ( other != null ) {
			String sThisName = this.getDatabaseTable() ;
			String sOtherName = other.getDatabaseTable();
			if ( sThisName != null && sOtherName != null ) {
				return this.databaseTable.compareTo(other.getDatabaseTable());
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return  className 
				+ "|" + databaseTable
				+ "|" + databaseCatalog 
				+ "|" + databaseSchema 
				+ "|" + databaseType
				// + "|" + databaseComment
				+ "|columns=" + attributes.size()
				+ "|foreignKeys=" + foreignKeys.size() 
				+ "|links=" + links.size() 
				;
	}
	
	public boolean hasPrimaryKey() {
		for ( AttributeInDbModel attribute : this.attributes.values() ) {
			if ( attribute.isKeyElement() ) {
				return true ;
			}
		}
		return false ; // No key attribute
	}

	@Override
	public List<String> getWarnings() {
		List<String> warnings = new LinkedList<>() ;
		if ( hasPrimaryKey() == false ) {
			warnings.add("No Primary Key");
		}
		return warnings;
	}
}
