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

import com.sun.apoc.tools.profileeditor.PropertyComponentDummy;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import javax.swing.JPasswordField;

/**
 *
 * @author cs202741
 */
public class PropertyJPasswordField extends JPasswordField implements PropertyComponent {
    
    // PropertyCompnent Interface Variables
    private boolean mIsSet = false;
    private String mResourceId = null;
    private String mDefaultName = null;
    private String mDataType = null;
    private String mSectionName = null;
    private String mPath = null;
    private String mResourceIdPath = null;
    private String mVisualType = null;
    private String mDataPath = null;
    private String mDefault = null;
    private String mSeperator = null;
    private String mDescriptionId = null;
    private String mChooserPath = null;
    private String mExtendsChooser = null;
    private StandaloneProfileManager mProfileModel = null;
    
    /** Creates a new instance of PropertyJTextField */
    public PropertyJPasswordField(int length) {
        super(length);
    }

    //
    //  PropertyComponent Interface Methods
    //
    
    public void setIsSetProperty(boolean isSet) {
        mIsSet = isSet;
    }

    public boolean isSetProperty() {
        return mIsSet;
    }
    
    
    public void setDataType(String aDataType){
        mDataType = aDataType;
    }
    
    public String getDataType(){
        return mDataType;
    }
    
    public void setResourceId(String aResourceId) {
        mResourceId = aResourceId;
    }

    public String getResourceId() {
        return mResourceId;
    }

    public void setDefaultName(String aDefaultName) {
        mDefaultName = aDefaultName;
    }

    public String getDefaultName() {
        return mDefaultName;
    }
    
    public void setSectionName(String aSectionName) {
        mSectionName = aSectionName;
    }

    public String getSectionName() {
        return mSectionName;
    }
    
    public void setPath(String aPath) {
        mPath = aPath;
    }

    public String getPath() {
        return mPath;
    }

    public void setResourceIdPath(String aResourceIdPath) {
        mResourceIdPath = aResourceIdPath;
    }

    public String getResourceIdPath() {
        return mResourceIdPath;
    }
    
    public void setVisualType(String aVisualType){
        mVisualType = aVisualType;
    }
    
    public String getVisualType(){
        return mVisualType;
    }
    
    public void setDataPath(String aDataPath){
        mDataPath = aDataPath;
    }
    
    public String getDataPath(){
        return mDataPath;
    }
    
    public void setDefaultValue(String aDefault){
        mDefault = aDefault;
    }
    
    public String getDefaultValue(){
        return mDefault;
    }
    
    public boolean hasChanged(){
        
        String currentValue = new String( this.getPassword() );
        
        if( currentValue.equals( this.getDefaultValue() ) 
            || currentValue.equals("") ) 
            return false;
        else
            return true;
    }
    
    
    public void setSeperator(String aSeperator) {
        mSeperator = aSeperator;
    }

    public String getSeperator() {
        return mSeperator;
    }

    public void setDescriptionId(String aDescriptionId) {
        mDescriptionId = aDescriptionId;
    }

    public String getDescriptionId() {
        return mDescriptionId;
    }

    public void setChooserPath(String aChooserPath) {
        mChooserPath = aChooserPath;
    }

    public String getChooserPath() {
        return mChooserPath;
    }

    public void setExtendsChooser(String aExtendsChooser) {
        mExtendsChooser = aExtendsChooser;
    }

    public String getExtendsChooser() {
        return mExtendsChooser;
    }
    
    public void setValue(String s){
        
    }
    
    public String getValue(){
        char[] pword = this.getPassword();
        return new String( pword );
    }
    
    public void setLabel(String s){
        
    }
    
    public String getLabel(){
        return "*PASSWORD*";
    }
    
    public void setProfileModel(StandaloneProfileManager aProfileModel){
        mProfileModel = aProfileModel;
        
        checkForLoadedProperty();
    }
    

    public void checkForLoadedProperty() {
        PropertyComponent propComp = getListModel().existsByDataPath( mDataPath );
        if( propComp instanceof PropertyComponent ){
            clonePropertyComponent(propComp);

            this.setText( propComp.getValue() );
            getListModel().replaceItem(propComp, this);
        }
    }
    
    private void clonePropertyComponent(PropertyComponent propComp){
        this.setChooserPath( propComp.getChooserPath() );
        this.setDataPath( propComp.getDataPath() );
        this.setDataType( propComp.getDataType() );
        this.setDefaultName( propComp.getDefaultName() );
        this.setDefaultValue( propComp.getDefaultValue() );
        this.setDescriptionId( propComp.getDescriptionId() );
        this.setExtendsChooser( propComp.getExtendsChooser() );
        this.setPath( propComp.getPath() );
        this.setResourceId( propComp.getResourceId() );
        this.setResourceIdPath( propComp.getResourceIdPath() );
        this.setSeperator( propComp.getSeperator() );
        this.setValue( propComp.getValue() );
        this.setVisualType( propComp.getVisualType() );
    }
    
    public void saveProperty(){
        if( this.hasChanged() ){
            mProfileModel.createProperty( this );
        }
    }
    
    public PropertyJListModel getListModel(){
        return mProfileModel.getListModel();
    }
    
    public void reset(){
        mProfileModel.removeProperty( this );
        this.setText( this.getDefaultValue() );
        this.fireActionPerformed();
    }
    
    public String toString(){
        return "Password: " + new String( this.getPassword() );
    }
}
