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
package org.telosys.tools.repository.model.comparators;

import java.util.Comparator;

//import org.telosys.tools.repository.model.Link;
import org.telosys.tools.repository.model.LinkInDbModel;

/**
 * Links comparator ( Comparator implementation )
 * 
 * Based on 'SourceTableName' + '-' + 'TargetTableName'
 * 
 * @author Laurent GUERIN
 *  
 */
//public class LinksComparer implements Comparator<Link>
public class LinkComparator implements Comparator<LinkInDbModel> // v 3.0.0
{
    public final static boolean DESC               = true;

    public final static boolean ASC                = false;

    private boolean             _bDescendingOrder  = false;

    public LinkComparator( boolean bDescendingOrder)
    {
        super();
        _bDescendingOrder = bDescendingOrder;
    }

    /**
     * Compares objects using ASCENDING ORDER
     * @param link1
     * @param link2
     * @return
     */
//    private int compareASC(Link link1, Link link2) 
    private int compareASC(LinkInDbModel link1, LinkInDbModel link2)  // v 3.0.0
    {
        //--- Check parameters
        if (link1 == null && link2 == null) {
            return 0; // obj1 == obj2
        }
        if (link1 == null) {
            return -1; // obj1 < obj2
        }
        if (link2 == null) {
            return +1; // obj1 > obj2
        }

        String s1 = link1.getSourceTableName() + "-" + link1.getTargetTableName() ;
        String s2 = link2.getSourceTableName() + "-" + link2.getTargetTableName() ;
        return s1.compareTo(s2);
    }
    
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
//    public int compare(Link obj1, Link obj2)
    public int compare(LinkInDbModel obj1, LinkInDbModel obj2) // v 3.0.0
    {
        //--- Compare in standard ascending order
        int iRet = compareASC(obj1, obj2);
        if ( _bDescendingOrder ) {
            //--- Reverse result 
            if ( iRet > 0 ) {
                return -1 ;
            }
            if ( iRet < 0 ) {
                return +1 ;
            }
            return iRet ;
        }
        else {
            //--- Keep the standard ascending result 
            return iRet ;            
        }
    }
}