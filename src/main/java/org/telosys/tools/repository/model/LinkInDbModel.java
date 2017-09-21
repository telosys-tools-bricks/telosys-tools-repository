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
//	private String  cardinality;
	private Cardinality  cardinality = Cardinality.UNDEFINED ; // v 3.0.0

//	private String javaFieldName; // name of the java field holding the link 
	private String fieldName; // name of the java field holding the link // v 3.0.0
	
//	private String javaFieldType; // type of the java field holding the link
	private String fieldType; // type of the java field holding the link // v 3.0.0

	private boolean owningSide = true; // porteur de la relation ou non --- pas besoin si "non-owning OneToMany entity side must used the mappedBy element to specify the relationship field"

//	private String inverseSideOf; // identifie le link id porteur de la relation
	private String inverseSideLinkId ; // v 3.0.0
	
	private String mappedBy;  // inverse side, mapped by define property

	/**
	 * The operations that must be cascaded to the target of the association. By default no operations are cascaded : 
	 * ALL,MERGE,PERSIT,REFRESH,REMOVE
	 */
	// The operations that must be cascaded to the target of the association. 
	// By default no operations are cascaded.
//	private boolean cascadeALL     = false;
//	private boolean cascadeMERGE   = false;
//	private boolean cascadePERSIST = false;
//	private boolean cascadeREFRESH = false;
//	private boolean cascadeREMOVE  = false;
	private CascadeOptions cascadeOptions = null ;
	
	/**
	 * Fetch strategy
	 * DEFAULT|EAGER|LAZY
	 */
//	private String fetch = RepositoryConst.FETCH_DEFAULT ;
	private FetchType fetchType = FetchType.UNDEFINED ;
	
	/**
	 * Whether the association is optional. If set to false then a non-null relationship must always exist. Default to true
	 */
//	private String  optional = RepositoryConst.OPTIONAL_UNDEFINED ;
	private Optional optional = Optional.UNDEFINED ; // v 3.0.0
	
//	private String   targetEntityJavaType ;
	private String   targetEntityClassName ; // v 3.0.0
	
	private String   foreignKeyName = null ;
	private String   joinTableName  = null ;
	
	//--- ManyToOne or OneToOne link based on "Join Columns"
//	private JoinColumnsInDbModel  joinColums = null ;
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
//		if ( StrUtil.nullOrVoid( joinTable.getSchema() ) ) {
		if ( StrUtil.nullOrVoid( joinTable.getDatabaseSchema() ) ) {
//			tableId = joinTable.getName() ;
			tableId = joinTable.getDatabaseTable() ;
		}
		else {
//			tableId = joinTable.getSchema() + "." + joinTable.getName() ;
			tableId = joinTable.getDatabaseSchema() + "." + joinTable.getDatabaseTable() ;
		}
		return "LINK_JT_" + tableId + "_" + ( owningSide ? "O" : "I" ) ;
	}
	

//	//--------------------------------------------------------------------------
//	public String getCheckSum() {
//		return this.getSourceTableName() + "#" +  this.getTargetTableName();
//	}
//
	
	//--------------------------------------------------------------------------
//	public void setJoinColumns( JoinColumnsInDbModel joinColumns ) {
//		joinColums = joinColumns ;
//	}
	public void setJoinColumns( List<JoinColumnInDbModel> joinColumns ) {
		this.joinColumns = joinColumns ;
	}
//	public JoinColumnsInDbModel getJoinColumns() {
//		return joinColums ;
//	}
	@Override
	public List<JoinColumn> getJoinColumns() {
		return DbModelUtil.toListOfJoinColumns(this.joinColumns) ;
	}
	
	//--------------------------------------------------------------------------
//	public JoinTableInDbModel getJoinTable()
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
//	public boolean isUsed() {
//		return used;
//	}
//
//	public void setUsed(boolean used) {
//		this.used = used;
//	}
	@Override
	public boolean isSelected() { // replaces isUsed()
		return used; 
	}
	public void setSelected(boolean selected) { // replaces setUsed(boolean used)
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
//	/**
//	 * Returns the name of the Java field holding the link  <br>
//	 * ie : "book", "customer", "books", "customers", ...
//	 * @return
//	 */
//	public String getJavaFieldName() {
//		return javaFieldName;
//	}
	@Override
	public String getFieldName() {
		return this.fieldName;
	}

//	/**
//	 * Set the name of the Java field holding the link   <br>
//	 * ie : "book", "customer", "books", "listOfCustomers", ...
//	 * @param javaName
//	 */
//	public void setJavaFieldName(String javaName) {
//		this.javaFieldName = javaName;
//	}
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
//	/**
//	 * Returns the Java type of the link reference <br>
//	 * ie : "Book", "Customer", or collection type ( "java.util.List", ... )
//	 * @return
//	 */
//	public String getJavaFieldType() {
//		if ( ! StrUtil.nullOrVoid(javaFieldType) ) {
//			return javaFieldType; // Specific type : ie "java.util.List"
//		}
//		else {
//			return getTargetEntityJavaType() ; // "Book", "Customer", ...
//		}
//	}

	@Override
	public String getFieldType() { // v 3.0.0
		if ( ! StrUtil.nullOrVoid(this.fieldType) ) {
			return this.fieldType; // Specific type : ie "java.util.List"
		}
		else {
			return getTargetEntityClassName(); // "Book", "Customer", ...
		}
	}

//	/**
//	 * Set the type of the Java field holding the link   <br>
//	 * ie : "Book", "Customer", or collection type ( "java.util.List", ... )
//	 * @param javaType
//	 */
//	public void setJavaFieldType(String javaType) {
//		this.javaFieldType = javaType;
//	}
	/**
	 * Set the type of the field holding the link   <br>
	 * ie : "Book", "Customer", or collection type ( "java.util.List", ... )
	 * @param fieldType
	 */
	public void setFieldType(String fieldType) { // v 3.0.0
		this.fieldType = fieldType;
	}

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
//	/**
//	 * Returns the link id of the inverse side 
//	 */
//	public String getInverseSideOf() {
//		return inverseSideOf;
//	}
	@Override
	public String getInverseSideLinkId() { // v 3.0.0
		return inverseSideLinkId;
	}
	
//	/**
//	 * Set the link id of the inverse side
//	 */
//	public void setInverseSideOf(String inverseSideOf) {
//		this.inverseSideOf = inverseSideOf;
//	}
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
//	public void setOptional(String v) {
//		this.optional = RepositoryConst.OPTIONAL_UNDEFINED;
//		if (v != null) {
//			if (RepositoryConst.OPTIONAL_TRUE.equalsIgnoreCase(v)) 
//			{
//				this.optional = RepositoryConst.OPTIONAL_TRUE;
//			} 
//			else if (RepositoryConst.OPTIONAL_FALSE.equalsIgnoreCase(v)) 
//			{
//				this.optional = RepositoryConst.OPTIONAL_FALSE;
//			}
//		}
//	}
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
//	public String getOptional() {
//		return this.optional ;
//	}
	@Override
	public Optional getOptional() { // v 3.0.0
		return this.optional ;
	}
	
//	public boolean isOptionalTrue() {
//		return ( RepositoryConst.OPTIONAL_TRUE.equals( this.optional ) ) ;
//	}
//	public boolean isOptionalFalse() {
//		return ( RepositoryConst.OPTIONAL_FALSE.equals( this.optional ) ) ;
//	}
//	public boolean isOptionalUndefined() {
//		if ( isOptionalTrue() || isOptionalFalse() ) return false ;
//		return true ;
//	}
	

	//--------------------------------------------------------------------------
	/**
	 * Returns the short Class Name of the TARGET ENTITY  <br>
	 * ie "Book", "Customer", ... 
	 * @return
	 */
//	public String getTargetEntityJavaType() {
//		return targetEntityJavaType;
//	}
	@Override
	public String getTargetEntityClassName() { // v 3.0.0
		return this.targetEntityClassName ;
	}

	/**
	 * Set the short Class Name of the TARGET ENTITY  <br>
	 * @param v the short Java type ( ie "Book", "Customer", ... )
	 */
//	public void setTargetEntityJavaType(String v) {
//		this.targetEntityJavaType = v;
//	}
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
//	public String getCardinality() {
//		return cardinality;
//	}
	@Override
	public Cardinality getCardinality() {  // v 3.0.0
		return this.cardinality;
	}

//	public void setCardinality(String p_type) {
//		if (p_type != null) {
//			if (RepositoryConst.MAPPING_ONE_TO_ONE.equalsIgnoreCase(p_type)) {
//				this.cardinality = RepositoryConst.MAPPING_ONE_TO_ONE;
//			} else if (RepositoryConst.MAPPING_ONE_TO_MANY.equalsIgnoreCase(p_type)) {
//				this.cardinality = RepositoryConst.MAPPING_ONE_TO_MANY;
//			} else if (RepositoryConst.MAPPING_MANY_TO_ONE.equalsIgnoreCase(p_type)) {
//				this.cardinality = RepositoryConst.MAPPING_MANY_TO_ONE;
//			} else if (RepositoryConst.MAPPING_MANY_TO_MANY.equalsIgnoreCase(p_type)) {
//				this.cardinality = RepositoryConst.MAPPING_MANY_TO_MANY;
//			} else {
//				this.cardinality = RepositoryConst.MAPPING_UNKNOWN;
//			}
//		} else {
//			this.cardinality = RepositoryConst.MAPPING_UNKNOWN;
//		}
//	}
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
		
//	public boolean isTypeOneToOne() {
//		return RepositoryConst.MAPPING_ONE_TO_ONE.equals(cardinality);
//	}
//
//	public boolean isTypeOneToMany() {
//		return RepositoryConst.MAPPING_ONE_TO_MANY.equals(cardinality);
//	}
//
//	public boolean isTypeManyToOne() {
//		return RepositoryConst.MAPPING_MANY_TO_ONE.equals(cardinality);
//	}
//
//	public boolean isTypeManyToMany() {
//		return RepositoryConst.MAPPING_MANY_TO_MANY.equals(cardinality);
//	}

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
//	public String getCascade() {
//		if ( this.cascadeALL ) {
//			return RepositoryConst.CASCADE_ALL ;
//		}
//		else {
//			StringBuffer sb = new StringBuffer();
//			if ( this.cascadeMERGE ) {
//				sb.append(" ");
//				sb.append(RepositoryConst.CASCADE_MERGE);
//			}
//			if ( this.cascadePERSIST ) {
//				sb.append(" ");
//				sb.append(RepositoryConst.CASCADE_PERSIST);
//			}
//			if ( this.cascadeREFRESH ) {
//				sb.append(" ");
//				sb.append(RepositoryConst.CASCADE_REFRESH);
//			}
//			if ( this.cascadeREMOVE ) {
//				sb.append(" ");
//				sb.append(RepositoryConst.CASCADE_REMOVE);
//			}
//			return sb.toString();
//		}
//	}
	
	@Override
	public CascadeOptions getCascadeOptions() {
		return this.cascadeOptions;
	}

//	public void setCascade(String cascade) {
//		// Traitement normatif, NON exclusif (cumul possible) et NON obligatoire de la strategie de cascade
//		this.cascadeALL     = false;
//		this.cascadeMERGE   = false;
//		this.cascadePERSIST = false;
//		this.cascadeREFRESH = false;
//		this.cascadeREMOVE  = false;
//		
//		if ( cascade != null ) {
//			String cascadeUC = cascade.toUpperCase();
//			if (cascadeUC.contains(RepositoryConst.CASCADE_ALL)) {
//				this.cascadeALL = true;
//			}
//			if (cascadeUC.contains(RepositoryConst.CASCADE_MERGE)) {
//				this.cascadeMERGE = true;
//			}
//			if (cascadeUC.contains(RepositoryConst.CASCADE_PERSIST)) {
//				this.cascadePERSIST = true;
//			}
//			if (cascadeUC.contains(RepositoryConst.CASCADE_REFRESH)) {
//				this.cascadeREFRESH = true;
//			}
//			if (cascadeUC.contains(RepositoryConst.CASCADE_REMOVE)) {
//				this.cascadeREMOVE = true;
//			}
//		}
//	}

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

//	public boolean isCascadeALL() {
//		return cascadeALL;
//	}
//	public void setCascadeALL(boolean v) {
//		cascadeALL = v ;
//	}
//
//	public boolean isCascadeMERGE() {
//		return cascadeMERGE;
//	}
//	public void setCascadeMERGE(boolean v) {
//		cascadeMERGE = v ;
//	}
//
//	public boolean isCascadePERSIST() {
//		return cascadePERSIST;
//	}
//	public void setCascadePERSIST(boolean v) {
//		cascadePERSIST = v ;
//	}
//
//	public boolean isCascadeREFRESH() {
//		return cascadeREFRESH;
//	}
//	public void setCascadeREFRESH(boolean v) {
//		cascadeREFRESH = v;
//	}
//
//	public boolean isCascadeREMOVE() {
//		return cascadeREMOVE;
//	}
//	public void setCascadeREMOVE(boolean v) {
//		cascadeREMOVE = v;
//	}

	/**
	 * Returns "fetch" property (DEFAULT, EAGER, LAZY, UNDEFINED)
	 * @return
	 */
//	public String getFetch() {
//		return fetch;
//	}
	@Override
	public FetchType getFetchType() { // v 3.0.0
		return this.fetchType;
	}
	

//	/**
//	 * @param p_fetch
//	 */
//	public void setFetch(String p_fetch) {
//		// Traitement normatif, exclusif (cumul possible) et NON obligatoire de la strategie du Fetch
////		this.fetchDEFAULT = false;
////		this.fetchEAGER = false;
////		this.fetchLAZY = false;
//		if (p_fetch != null) {
//			if (RepositoryConst.FETCH_EAGER.equalsIgnoreCase(p_fetch)) {
//				this.fetch = RepositoryConst.FETCH_EAGER;
//				//this.fetchEAGER = true;
//			} else if (RepositoryConst.FETCH_LAZY.equalsIgnoreCase(p_fetch)) {
//				this.fetch = RepositoryConst.FETCH_LAZY;
//				//this.fetchLAZY = true;
//			} else {
//				this.fetch = RepositoryConst.FETCH_DEFAULT;
//				//this.fetchDEFAULT = true;
//			}
//		} else {
//			this.fetch = RepositoryConst.FETCH_DEFAULT;
//			//this.fetchDEFAULT = true;
//		}
//	}
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
	
//	public boolean isFetchDEFAULT() {
//		//return fetchDEFAULT;
//		return ( RepositoryConst.FETCH_DEFAULT.equals( this.fetch ) ) ;
//	}
//
//	public boolean isFetchEAGER() {
//		//return fetchEAGER;
//		return ( RepositoryConst.FETCH_EAGER.equals( this.fetch ) ) ;
//	}
//
//	public boolean isFetchLAZY() {
//		//return fetchLAZY;
//		return ( RepositoryConst.FETCH_LAZY.equals( this.fetch ) ) ;
//	}
	
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
		StringBuffer sb = new StringBuffer();
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
		sb.append( this.getFieldType() );
		sb.append( "/" );
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
		//sb.append( this.isUsed() );
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
		StringBuffer sb = new StringBuffer();
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
		sb.append( this.getFieldType() );
		sb.append( " "  );
		sb.append( this.getFieldName() );		
		return sb.toString();
	}
}
