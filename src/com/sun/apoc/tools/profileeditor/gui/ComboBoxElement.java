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

import com.sun.apoc.tools.profileeditor.LocaleManager;
import com.sun.apoc.tools.profileeditor.Subscriber;

/**
 *
 * @author cs202741
 */
public class ComboBoxElement extends Object implements Subscriber{
    private String mValue = null;
    private String mLabel = null;
    private String mDefaultValue = null;
    
    //  Subscriber Interface Var's
    private LocaleManager mLocaleManager;
    private String mResourceId = null;
    private String mPackageName = null;
    private String mDefaultName = null;
    
    
    /** Creates a new instance of ComboBoxElement */
    public ComboBoxElement() {
    }
    
    public void setValue(String aValue){
        mValue = aValue;
    }
    
    public String getValue(){
        return mValue;
    }
    
    public void setLabel(String aLabel){
        mLabel = aLabel;
    }
    
    public String getLabel(){
        return mLabel;
    }
    
    public void setLocaleManager(LocaleManager aLocaleManager){
        mLocaleManager = aLocaleManager;
        mLocaleManager.attach( this );
    }
    
    public String toString(){
        return mLabel;
    }
    
    public void setPackageName(String name){
        mPackageName = name;
    }
    
    public void setResourceId(String id){
        mResourceId = id;
    }

    public void setDefaultName(String aDefaultName) {
        mDefaultName = aDefaultName;
    }
    
    public void setDefault(String defaultValue){
        mDefaultValue = defaultValue;
    }

    public void update() {
        
        if( mResourceId != null ){
            String label = mLocaleManager.getLocalizedName(mPackageName, mResourceId, mDefaultName );

            if( label == null ){
                if( mValue.equals("true") )
                    if( mValue.equals( mDefaultValue ) )
                        setLabel("Enabled *");
                    else
                        setLabel("Enabled");
                else if( mValue.equals("false") ){
                    if( mValue.equals( mDefaultValue ) ){
                        setLabel("Disabled *");
                    }
                    else{
                        setLabel("Disabled");
                    }
                }
            }else{
                setLabel( label );
            }   
        }else{
            mLabel = mValue;
        }
        
        if( mValue.equals( mDefaultValue ) ){
            setLabel( getLabel() + " *" );
        }
    }
}
