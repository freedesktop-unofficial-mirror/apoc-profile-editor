/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either
 * the GNU General Public License Version 2 only ("GPL") or
 * the Common Development and Distribution License("CDDL")
 * (collectively, the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy
 * of the License at www.sun.com/CDDL or at COPYRIGHT. See the
 * License for the specific language governing permissions and
 * limitations under the License. When distributing the software,
 * include this License Header Notice in each file and include
 * the License file at /legal/license.txt. If applicable, add the
 * following below the License Header, with the fields enclosed
 * by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by
 * only the CDDL or only the GPL Version 2, indicate your
 * decision by adding "[Contributor] elects to include this
 * software in this distribution under the [CDDL or GPL
 * Version 2] license." If you don't indicate a single choice
 * of license, a recipient has the option to distribute your
 * version of this file under either the CDDL, the GPL Version
 * 2 or to extend the choice of license to its licensees as
 * provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the
 * option applies only if the new code is made subject to such
 * option by the copyright holder.
 */

package com.sun.apoc.tools.profileeditor;

import com.sun.apoc.tools.profileeditor.packages.Template;
import java.util.Collections;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author cs202741
 */
public class LocalizedDefaultMutableTreeNode extends DefaultMutableTreeNode implements Subscriber, Comparable {
    
    //  Subscriber Interface Variables
    private String mResourceID = null;
    private String mPackageName = null;
    private String mDefaultName = null;
    private LocaleManager mLocaleManager = null;
    
    
    private boolean mHasSet = false;

    
    /** Creates a new instance of LocalizedDefaultMutableTreeNode */
    public LocalizedDefaultMutableTreeNode(String name, String aPackageName, String aResourceID, 
                                            String aDefaultName, LocaleManager aLocaleManager) {
        super(name);
        mPackageName = aPackageName;
        mResourceID = aResourceID;
        mDefaultName = aDefaultName;
        mLocaleManager = aLocaleManager;
        mLocaleManager.attach( this );
        update();
    }
    
    public LocalizedDefaultMutableTreeNode(){
        super();
    }
    
    
    public void add(final MutableTreeNode newChild) {
        if (newChild != null && newChild.getParent() == this) {
                remove(newChild);
        }
        int index;
        if (children == null) {
                index = 0;
        } else {
                index = Collections.binarySearch(children, newChild);
        }
        if (index < 0) {
                index = -index - 1;
        }
        insert(newChild, index);
    }
    

    public void update() {
         if( this.getUserObject() instanceof Template ){
            ((Template)this.getUserObject()).setName( mResourceID, mDefaultName );
        }else{
            String name = mLocaleManager.getLocalizedName(mPackageName, mResourceID, mDefaultName);
            if( name == null ){
                name = mResourceID;
            }
            super.setUserObject(name);
        }
    }
    
    public void setResourceId(String aResourceID){
        mResourceID = aResourceID;
    }
    
    public String getResourceId(){
        return mResourceID;
    }
    
    public void setContainsSet(boolean hasSet){
        mHasSet = hasSet;
    }
    
    public boolean containsSet(){
        return mHasSet;
    }
    
    public void setPackageName(String aPackageName){
        mPackageName = aPackageName;
    }
    
    public String getPackageName(){
        return mPackageName;
    }
    
    public void setDefaultName(String aDefaultName) {
        mDefaultName = aDefaultName;
    }
    
    public void setLocaleManager(LocaleManager aLocaleManager){
        mLocaleManager = aLocaleManager;
    }

    public int compareTo(Object o) {
        String thisName = getUserObject().toString();
        String otherString = o.toString();
        return thisName.compareTo(otherString);
    }

}
