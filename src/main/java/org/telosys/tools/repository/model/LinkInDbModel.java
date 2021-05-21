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
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generic.model.BooleanValue;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.JoinTable;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.Optional;

/**
 * "Link" model class <br>
 * 
 * @author S.Labbe, L.Guerin
 *
 */
public class LinkInDbModel implements Serializable, Link 
{
	private static final long serialVersionUID = 1L;
	
	private String  id;

	private boolean used = true; // flag : used or not used
	
	private String  sourceTableName; // the source of the link
	private String  targetTableName; // the target of the link
	
	/**
	 * Relationship's cardinality
	 * "OneToOne", "OneToMany", "ManyToOne", "ManyToMany"
	 */
	private Cardinality  cardinality = Cardinality.UNDEFINED ; // v 3.0.0

	private String fieldName; // name of the java field holding the link // v 3.0.0
	
	// private String fieldType; // type of the java field holding the link // REMOVED in v 3.3.0

	private boolean owningSide = true; // porteur de la relation ou non --- pas besoin si "non-owning OneToMany entity side must used the mappedBy element to specify the relationship field"

	private String inverseSideLinkId ; // v 3.0.0
	
	private String mappedBy;  // inverse side, mapped by define property
	
    private BooleanValue isInsertable = BooleanValue.UNDEFINED; // Added in v 3.3.0
    private BooleanValue isUpdatable  = BooleanValue.UNDEFINED; // Added in v 3.3.0
    private boolean isTransient = false ; // Added in v 3.3.0

	/**
	 * The operations that must be cascaded to the target of the association. By default no operations are cascaded : 
	 * ALL,MERGE,PERSIT,REFRESH,REMOVE
	 */
	private CascadeOptions cascadeOptions = null ;
	
	/**
	 * Fetch strategy
	 * DEFAULT|EAGER|LAZY
	 */
	private FetchType fetchType = FetchType.UNDEFINED ;
	
	/**
	 * Whether the association is optional. If set to false then a non-null relationship must always exist. Default to true
	 */
	private Optional optional = Optional.UNDEFINED ; // v 3.0.0
	
	private String   targetEntityClassName ; // v 3.0.0
	
	private String   foreignKeyName = null ;
	private String   joinTableName  = null ;
	
	//--- ManyToOne or OneToOne link based on "Join Columns"
	private List<JoinColumnInDbModel>   joinColumns = null ;

	//--- ManyToMany link based on "Join Table"
	private JoinTableInDbModel    joinTable  = null ;	

	//--------------------------------------------------------------------------
	/**
	 * Constructor  
	 */
	public LinkInDbModel() { 
		super();
		// v 3.0.0 
		this.cascadeOptions = new CascadeOptions() ; // void ( no cascade option )
		this.cardinality = Cardinality.UNDEFINED ; 
		this.fetchType   = FetchType.UNDEFINED ;
		this.optional    = Optional.UNDEFINED ;
	}

	//--------------------------------------------------------------------------
	public final static String buildId(ForeignKeyInDbModel foreignKey, boolean owningSide) 
	{
		return "LINK_FK_" + foreignKey.getName() + "_" + ( owningSide ? "O" : "I" ) ;
	}
	//--------------------------------------------------------------------------
	public final static String buildId(EntityInDbModel joinTable, boolean owningSide) 
	{
		String tableId = "" ;
		if ( StrUtil.nullOrVoid( joinTable.getDatabaseSchema() ) ) {
			tableId = joinTable.getDatabaseTable() ;
		}
		else {
			tableId = joinTable.getDatabaseSchema() + "." + joinTable.getDatabaseTable() ;
		}
		return "LINK_JT_" + tableId + "_" + ( owningSide ? "O" : "I" ) ;
	}
	
	//--------------------------------------------------------------------------
	public void setJoinColumns( List<JoinColumnInDbModel> joinColumns ) {
		this.joinColumns = joinColumns ;
	}
	@Override
	public List<JoinColumn> getJoinColumns() {
		return DbModelUtil.toListOfJoinColumns(this.joinColumns) ;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public JoinTable getJoinTable()	{
		return joinTable ;
	}
	public void setJoinTable( JoinTableInDbModel v ) {
		joinTable = v ;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	//--------------------------------------------------------------------------
	@Override
	public boolean isSelected() {
		return used; 
	}
	public void setSelected(boolean selected) {
		this.used = selected;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String srcTableName) {
		this.sourceTableName = srcTableName;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getTargetTableName() {
		return targetTableName;
	}
	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}

	//--------------------------------------------------------------------------
	// Field Name
	//--------------------------------------------------------------------------
	@Override
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * Set the name of the Java field holding the link   <br>
	 * ie : "book", "customer", "books", "listOfCustomers", ...
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	//--------------------------------------------------------------------------
	// Field Type
	//--------------------------------------------------------------------------
//  REMOVED in v 3.3.0
//	@Override
//	public String getFieldType() { // v 3.0.0
//		if ( ! StrUtil.nullOrVoid(this.fieldType) ) {
//			return this.fieldType; // Specific type : ie "java.util.List"
//		}
//		else {
//			return getTargetEntityClassName(); // "Book", "Customer", ...
//		}
//	}
//
//	/**
//	 * Set the type of the field holding the link   <br>
//	 * ie : "Book", "Customer", or collection type ( "java.util.List", ... )
//	 * @param fieldType
//	 */
//	public void setFieldType(String fieldType) { // v 3.0.0
//		this.fieldType = fieldType;
//	}

	//--------------------------------------------------------------------------
	@Override
	public boolean isOwningSide() {
		return owningSide;
	}

	@Override
	public boolean isInverseSide() {
		return ( owningSide == false ) ;
	}

	public void setOwningSide(boolean owningSide) {
		this.owningSide = owningSide;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getInverseSideLinkId() { // v 3.0.0
		return inverseSideLinkId;
	}
	
	/**
	 * Set the link id of the inverse side
	 * @param inverseSideLinkId
	 */
	public void setInverseSideLinkId(String inverseSideLinkId) { // v 3.0.0
		this.inverseSideLinkId = inverseSideLinkId;
	}

	//--------------------------------------------------------------------------
	@Override
	public String getMappedBy() {
		return mappedBy;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}

	//--------------------------------------------------------------------------
	/**
	 * Set the 'Optional' property ( TRUE, FALSE, UNDEFINED )
	 * @param v
	 */
	public void setOptional(Optional v) { // v 3.0.0
		if ( v != null ) {
			this.optional = v ;
		}
		else {
			this.optional = Optional.UNDEFINED ;
		}
	}
	
	/**
	 * Returns the 'optional' property : "TRUE"/"FALSE"/"UNDEFINED" (never null)
	 * @return
	 */
	@Override
	public Optional getOptional() { // v 3.0.0
		return this.optional ;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Returns the short Class Name of the TARGET ENTITY  <br>
	 * ie "Book", "Customer", ... 
	 * @return
	 */
	@Override
	public String getTargetEntityClassName() { // v 3.0.0
		return this.targetEntityClassName ;
	}

	/**
	 * Set the short Class Name of the TARGET ENTITY  <br>
	 * @param v the short Java type ( ie "Book", "Customer", ... )
	 */
	public void setTargetEntityClassName(String v) { // v 3.0.0
		this.targetEntityClassName = v;
	}

	//--------------------------------------------------------------------------
	// CARDINALITY
	//--------------------------------------------------------------------------
	/**
	 * Returns the link type  : "OneToMany", "ManyToOne", "OneToOne", "ManyToMany" 
	 * @return
	 */
	@Override
	public Cardinality getCardinality() {  // v 3.0.0
		return this.cardinality;
	}

	/**
	 * Set the cardinality
	 * @param v the cardinality to be set, a 'null' value is transformed into 'UNDEFINED'
	 */
	public void setCardinality(Cardinality v) {  // v 3.0.0
		if ( v != null ) {
			this.cardinality = v ;
		}
		else {
			this.cardinality = Cardinality.UNDEFINED ;
		}
	}
		
	public boolean isCardinalityOneToOne() {
		return cardinality != null ? cardinality == Cardinality.ONE_TO_ONE : false ;
	}

	public boolean isCardinalityOneToMany() {
		return cardinality != null ? cardinality == Cardinality.ONE_TO_MANY : false ;
	}
	
	public boolean isCardinalityManyToOne() {
		return cardinality != null ? cardinality == Cardinality.MANY_TO_ONE : false ;
	}
	
	public boolean isCardinalityManyToMany() {
		return cardinality != null ? cardinality == Cardinality.MANY_TO_MANY : false ;
	}

	//--------------------------------------------------------------------------
	// CASCADE
	//--------------------------------------------------------------------------
	@Override
	public CascadeOptions getCascadeOptions() {
		return this.cascadeOptions;
	}

	/**
	 * Set the 'cascade options' ( ALL, MERGE, PERSIST, etc )
	 * @param cascadeOptions the cascade options to be set (a 'null' value is transformed into a void set of options)
	 */
	public void setCascadeOptions(CascadeOptions cascadeOptions) {
		if ( cascadeOptions != null ) {
			this.cascadeOptions = cascadeOptions ;
		}
		else {
			this.cascadeOptions = new CascadeOptions() ; // void (no cascade options)
		}
	}

	/**
	 * Returns "fetch" property (DEFAULT, EAGER, LAZY, UNDEFINED)
	 * @return
	 */
	@Override
	public FetchType getFetchType() { // v 3.0.0
		return this.fetchType;
	}
	
	/**
	 * Set the fetch type (DEFAULT, EAGER, LAZY, UNDEFINED)
	 * @param v the fetch type to be set (a 'null' value is transformed into 'UNDEFINED')
	 */
	public void setFetchType(FetchType v) {  // v 3.0.0
		if ( v != null ) {
			this.fetchType = v ;
		}
		else {
			this.fetchType = FetchType.UNDEFINED ;
		}
		
	}
	
	//--------------------------------------------------------------------------
	// FOREIGN KEY management
	//--------------------------------------------------------------------------
	@Override
	public boolean isBasedOnForeignKey() {
		return StrUtil.nullOrVoid( this.foreignKeyName ) != true ;
	}
	
	/**
	 * Returns the name of the Foreign Key used to generate the link <br>
	 * There's no guarantee that this Foreign Key still exist
	 * @return
	 */
	@Override
	public String getForeignKeyName() {
		return foreignKeyName ;
	}
	
	/**
	 * Set the name of the Foreign Key used to generate the link
	 * @param v
	 */
	public void setForeignKeyName(String v) {
		foreignKeyName = v ;
	}

	//--------------------------------------------------------------------------
	// JOIN TABLE management
	//--------------------------------------------------------------------------
	@Override
	public boolean isBasedOnJoinTable() {
		return StrUtil.nullOrVoid( getJoinTableName() ) != true ;
	}

	/**
	 * Returns the name of the Join Table used to generate the link <br>
	 * There's no guarantee that this Foreign Key still exist
	 * @return
	 */
	@Override
	public String getJoinTableName() {
		if ( joinTable != null ) {
			return joinTable.getName() ; // Only available with Owning Side
		}
		else {
			return joinTableName ; // Available on both Owning Side and Inverse Side
		}
	}
	
	/**
	 * Set the name of the Join Table used to generate the link
	 * @param v
	 */
	public void setJoinTableName(String v) {
		joinTableName = v ;
	}
	
	//--------------------------------------------------------------------------
	@Override
	public boolean isEmbedded() {
		return false; // No embedded notion in this Relational Model
	}

	//--------------------------------------------------------------------------
	/**
	 * Returns a string that can be used to compare 2 fields, and to know if they are different or identical<br>
	 * NB : all the significant fields must be in this string !
	 * @return
	 */
	@Override
	public String getComparableString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "#" );
		sb.append( this.getId() );
		sb.append( ":" );
		sb.append( this.getCascadeOptions() );
		sb.append( "/" );
		sb.append( this.getFetchType() );
		sb.append( "/" );
		sb.append( this.getInverseSideLinkId() );
		sb.append( "/" );
		sb.append( this.getFieldName() );
		sb.append( "/" );
//		sb.append( this.getFieldType() );
//		sb.append( "/" );
		sb.append( this.getMappedBy() );
		sb.append( "/" );
		sb.append( this.getTargetEntityClassName() );
		sb.append( "/" );
		sb.append( this.getTargetTableName() );
		sb.append( "/" );
		sb.append( this.getForeignKeyName() );
		sb.append( "/" );
		sb.append( this.getJoinTableName() );
		sb.append( "/" );
		sb.append( this.getSourceTableName() );
		sb.append( "/" );
		sb.append( this.getCardinality() );
		sb.append( "/" );
		sb.append( this.isOwningSide() );
		sb.append( "/" );
		sb.append( this.getOptional() );
		sb.append( "/" );
		sb.append( this.isSelected() ); // v 3.0.0
		
		return sb.toString();
	}
	
	public boolean usesAttribute(AttributeInDbModel attribute) {
		if ( joinColumns != null ) {
			for ( JoinColumnInDbModel jc : joinColumns ) {
				if ( attribute.getDatabaseName().equals( jc.getName() ) ) {
					return true ;
				}
			}
		}
		return false ;
	}

	//--------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getId() );
		sb.append( " '"  );
		sb.append( this.getSourceTableName() );
		sb.append( "' --> '"  );
		sb.append( this.getTargetTableName() );
		sb.append( "' ("  );
		sb.append( this.getCardinality() );
		sb.append( " - "  );
		sb.append( this.isOwningSide() ? "OWNING-SIDE" : "INVERSE-SIDE" );
		sb.append( ") "  );
//		sb.append( this.getFieldType() );
//		sb.append( " "  );
		sb.append( this.getFieldName() );		
		return sb.toString();
	}
	
    @Override
    public BooleanValue getInsertable() { // v 3.3.0
        return this.isInsertable;
    }
    public void setInsertable(BooleanValue b) {
        this.isInsertable = b;
    }

    @Override
    public BooleanValue getUpdatable() { // v 3.3.0
        return this.isUpdatable;
    }
    public void setUpdatable(BooleanValue b) {
        this.isUpdatable = b;
    }
    
    @Override
    public boolean isTransient() { // v 3.3.0
        return this.isTransient;
    }
    public void setTransient(boolean b) { // v 3.3.0
        this.isTransient = b;
    }
}
