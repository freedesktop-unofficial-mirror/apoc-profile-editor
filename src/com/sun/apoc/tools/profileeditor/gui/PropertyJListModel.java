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

package com.sun.apoc.tools.profileeditor.gui;

import com.sun.apoc.tools.profileeditor.*;
import com.sun.apoc.tools.profileeditor.gui.PropertyComponent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.AbstractListModel;

/**
 *
 * @author cs202741
 */
public class PropertyJListModel extends AbstractListModel {
    private Vector mProperties = null;
    private ProfileEditor mProfileEditor = null;
    
    //  if 'locked' then model will not fire 
    //  contents changed event.  This is to
    //  stop a redraw of the summary view after 
    //  each component is added
    private boolean locked = false;

    
    /** Creates a new instance of PropertyListModel */
    public PropertyJListModel(ProfileEditor profileEditor) {
        mProperties = new Vector();
        mProfileEditor = profileEditor;
    }
    
    public int getSize(){
        return mProperties.size();
    }

    public Object getElementAt( int index ){
        return mProperties.get( index );
    }
    
    public void addItem(PropertyComponent aComponent){
        mProperties.add(aComponent);
        if(!locked){
            this.fireContentsChanged(this, mProperties.size()-1, mProperties.size() );
            mProfileEditor.updateSavedState();
        }
    }
    
    public void addItem(int index, PropertyComponent aComponent){
        mProperties.add(index, aComponent);
        if(!locked){
            this.fireContentsChanged(this, mProperties.size()-1, mProperties.size() );
            mProfileEditor.updateSavedState();
        }
    }
    
    public void addAll(Collection c){
        mProperties.addAll(c);
        if(!locked){
            this.fireContentsChanged(this, 0, mProperties.size() );
            mProfileEditor.updateSavedState();
        }
    }
    
    public boolean exists(PropertyComponent aComponent){
        return mProperties.contains(aComponent);
    }
    
    public PropertyComponent existsByDataPath(String aDataPath){
        
        for(int i = 0; i < mProperties.size(); i++){
            PropertyComponent propComp = (PropertyComponent)mProperties.get(i);
            if( propComp.getDataPath().equals( aDataPath ) ){
                return propComp;
            }
        }
        
        return null;
    }
    
    
    public int indexOf(PropertyComponent aComponent){
        
        for(int i = 0; i < mProperties.size(); i++){
            PropertyComponent propComp = (PropertyComponent)mProperties.get(i);
            if( propComp.getDataPath().equals( aComponent.getDataPath() ) ){
                return i;
            }
        }
        
        return -1;
    }

    
    
    public void replaceItem(PropertyComponent aComponent, PropertyComponent aReplacement){
        mProperties.remove( aComponent );
        mProperties.add(aReplacement);
        if(!locked){
            this.fireContentsChanged(this, 0, mProperties.size());
            mProfileEditor.updateSavedState();
        }
    }
    
    
    public void removeItem(PropertyComponent aComponent){
        mProperties.remove( aComponent );
        if(!locked){
            this.fireContentsChanged(this, 0, mProperties.size() );
            mProfileEditor.updateSavedState();
        }
    }
    
    public void removeItem(int index){
        
        if( index < mProperties.size() ){
            mProperties.removeElementAt( index );
            if(!locked){
                this.fireContentsChanged(this, 0, mProperties.size() );
                mProfileEditor.updateSavedState();
            }
        }
    }
    
    public void removeAll(){
        mProperties.clear();
        this.fireContentsChanged(this, 0, mProperties.size() );
        mProfileEditor.updateSavedState();
    }
    
    public Enumeration elements(){
        return mProperties.elements();
    }
    
    public void setLocked(boolean lock){
        locked = lock;
    }
    
    public Object getPropertyVector(){
        return mProperties.clone();
    }
    
    public void printItems(){
        int count = 0;
        Iterator it = mProperties.iterator();
        while(it.hasNext()){
            System.err.println("PROPERTY" + count++ + ": " + ((PropertyComponent)it.next()).getDataPath() );
        }
    }
    
    //
    //  Used to force a redraw of the summary view
    //  after properties have been added or removed
    //
    public void notifyListeners(){
        this.fireContentsChanged(this, 0, mProperties.size() );
        mProfileEditor.updateSavedState();
    }
    
}//end class
