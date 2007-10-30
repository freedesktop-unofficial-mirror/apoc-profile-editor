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

import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.property.ReadWritePropertyImpl;
import com.sun.apoc.tools.profileeditor.gui.PropertyComponent;
import com.sun.apoc.tools.profileeditor.gui.PropertyJListModel;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;

/**
 *
 * @author cs202741
 */
public class PropertyComponentDummy implements PropertyComponent, Subscriber {
    
    //  PropertyComponent Variables
    private boolean mIsSet = false;
    private String mResourceId = null;
    private String mDefaultName = null;
    private String mDataType = null;
    private String mSectionName = null;
    private String mPath = null;
    private String mResourceIdPath = null;
    private String mVisualType = null;
    private String mDataPath = null;
    private String mDefaultValue = null;
    private String mValue = null;
    private String mLabel = null;
    private String mSeperator = null;
    private String mDescriptionId = null;
    private String mChooserPath = null;
    private String mExtendsChooser = null;
    private StandaloneProfileManager mProfileManager = null;
    
    private TemplateProperty mProperty = null;
    
    //  Subscriber Interface
    private LocaleManager mLocaleManage = null;
    
    
    /** Creates a new instance of PropertyComponentDummy */
    public PropertyComponentDummy(String aDataPath, ReadWritePropertyImpl aProperty) throws SPIException {
        mDataPath = aDataPath;
        mSeperator = aProperty.getSeparator();
        mDataType = aProperty.getDataType().getStringValue();
        mValue = aProperty.getValue();
    }
    
    public void cloneTemplateProperty(TemplateProperty aProperty){
        setChooserPath( aProperty.getChooserPath() );
        setDefaultName( aProperty.getDefaultName() );
        setDefaultValue( aProperty.getDefaultValue() );
        setDescriptionId( aProperty.getDescriptionId() );
        setExtendsChooser( aProperty.getExtendsChooser() );
        setPath( aProperty.getPath() );
        setResourceId( aProperty.getResourceId() );
        setResourceIdPath( aProperty.getResourceIdPath() );
        setVisualType( aProperty.getVisualType() );
    }
    
    public void setIsSetProperty(boolean isSet) {
        mIsSet = isSet;
    }

    public boolean isSetProperty() {
        return mIsSet;
    }
    

    public void setDataType(String aDataType) {
        mDataType = aDataType;
    }

    public String getDataType() {
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

    public void setVisualType(String aVisualType) {
        mVisualType = aVisualType;
    }

    public String getVisualType() {
        return mVisualType;
    }

    public void setDataPath(String aDataPath) {
        mDataPath = aDataPath;
    }

    public String getDataPath() {
        return mDataPath;
    }

    public void setDefaultValue(String aDefault) {
        mDefaultValue = aDefault;
    }

    public String getDefaultValue() {
        return mDefaultValue;
    }

    public void setValue(String aValue) {
        mValue = aValue;
    }

    public String getValue() {
        return mValue;
    }

    public void setLabel(String aLabel) {
        mLabel = aLabel;
    }

    public String getLabel() {
        
        if( mLabel != null ){
            return mLabel;
        }
        
        if( getVisualType().equals("checkBox") ){
            if( getValue().equals("true") ){
                return "Enabled";
            }else{
                return "Disabled";
            }
        }
        
        return getValue();
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
    

    public void checkForLoadedProperty() {
    }

    public boolean hasChanged() {
        return true;
    }

    public void setProfileModel(StandaloneProfileManager aProfileModel) {
        mProfileManager = aProfileModel;
    }

    public void saveProperty() {
        if( hasChanged() ){
            mProfileManager.createProperty( this );
        }
    }

    public PropertyJListModel getListModel() {
        return mProfileManager.getListModel();
    }

    public void reset() {
        mProfileManager.removeProperty( this );
        PropertyJListModel model = getListModel();
        model.removeItem( this );
    }
    
    
    //
    //  Used so we can get the constraints of the property to
    //  localize its value in Summary View.
    //
    public void setTemplateProperty(TemplateProperty prop){
        mProperty = prop;
    }
    
    
    public TemplateProperty getTemplateProperty(){
        return mProperty;
    }

    
    //
    //  Subscriber interface methods
    //
    public void setPackageName(String aPackageName) {
    }

    public void setLocaleManager(LocaleManager aLocaleManager) {
        mLocaleManage = aLocaleManager;
        aLocaleManager.attach( this );
        update();
    }

    public void update() {
    }
}
