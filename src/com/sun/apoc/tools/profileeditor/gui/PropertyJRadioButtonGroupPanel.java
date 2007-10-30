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
import com.sun.apoc.tools.profileeditor.PropertyComponentDummy;
import com.sun.apoc.tools.profileeditor.Subscriber;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author cs202741
 */
public class PropertyJRadioButtonGroupPanel extends JPanel implements PropertyComponent, Subscriber {
    private static final int MAX_LINE_CHARS = 50;
    
    
    //  PropertyComponent Interface variables
    private boolean mIsSet = false;
    private String mDefaultName = null;
    private String mLabel = null;
    private String mSectionName = null;
    private String mPath = null;
    private String mResourceIdPath = null;
    private String mDataType = null;
    private String mVisualType = null;
    private String mDataPath = null;
    private String mDefault = null;
    private String mSeperator = null;
    private String mDescriptionId = null;
    private String mChooserPath = null;
    private String mExtendsChooser = null;
    private StandaloneProfileManager mProfileModel = null;
    
    
    //  Subscriber Interface Variables
    private String mResourceId = null;
    private String mPackageName = null;
    private LocaleManager mLocaleManager;
    
    //  Local variabels
    private JRadioButton[] mRadioButtons = null;
    private ButtonGroup mButtonGroup = null;
    private int index = 0;
    private int defaultIndex = 0;
    
    /** Creates a new instance of PropertyJRadioButton */
    public PropertyJRadioButtonGroupPanel(int size) {
        this.setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        this.setOpaque(false);
        
        mRadioButtons = new JRadioButton[size+1];
        mButtonGroup = new ButtonGroup();
        mRadioButtons[index] = new JRadioButton("-- Not Set --");
        mRadioButtons[index].setSelected( true );
        mRadioButtons[index].addActionListener( new PropertyJRadioButtonListener( this ) );
        mRadioButtons[index].setOpaque(false);
        mButtonGroup.add( mRadioButtons[index] );
        this.add( mRadioButtons[index] );
        index++;
    }
    
    public void addRadioButton(String value){
        
        String name = mLocaleManager.getLocalizedName( mPackageName, mResourceId + "." + value, null );
        if(name == null){
            name = value;
        }
        
        mRadioButtons[index] = new JRadioButton( formatText(name) );
        mRadioButtons[index].setName(value);
        mRadioButtons[index].addActionListener( new PropertyJRadioButtonListener( this ) );
        mRadioButtons[index].setOpaque(false);
        if( value.equals( this.getDefaultValue() ) ){
            mRadioButtons[index].setText( mRadioButtons[index].getText() + " *" );
            defaultIndex = index;
        }
        
        mButtonGroup.add( mRadioButtons[index] );
        this.add( mRadioButtons[index] );
        index++;
    }
    
    private String formatText(String text){
        if( text == null || text.length() < MAX_LINE_CHARS ){
            return text;
        }

        String result = "";
        String current = text;
        while(true){
            if( current.length() < MAX_LINE_CHARS ){
                break;
            }
                
            for( int i = MAX_LINE_CHARS-1; i >= 0; i-- ){
                if( current.charAt(i) == ' ' ){
                    result += "<br>" + current.substring(0, i);
                    current = current.substring(i+1);
                    break;
                }
            }
        }
        result += "<br>" + current;
        if( result.startsWith("<br>") ){
            result = result.substring(4);
        }
        
        return "<html>" + result + "</html>";
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


    public String getResourceId() {
        return mResourceId;
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
        if( mRadioButtons[0].isSelected() )
            return false;
        else
            return true;
    }
    
    public void setValue(String aValue){
        //mValue = aValue;
    }
    
    public String getValue(){
        String result = null;
        
        for(int i = 0; i < mRadioButtons.length; i++){
            if( mRadioButtons[i].isSelected() ){
                result = mRadioButtons[i].getName();
            }
        }
        
        return result;
    }
    
    public void setLabel(String aLabel){
        mLabel = aLabel;
    }
    
    public String getLabel(){
        String result = null;
        
        for(int i = 0; i < mRadioButtons.length; i++){
            if( mRadioButtons[i].isSelected() ){
                result = mRadioButtons[i].getText();
            }
        }
        return result;
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
    
    
    public void setProfileModel(StandaloneProfileManager aProfileModel){
        mProfileModel = aProfileModel;

        checkForLoadedProperty();
    }
    

    public void checkForLoadedProperty() {
        PropertyComponent propComp = getListModel().existsByDataPath( mDataPath );
        if( propComp instanceof PropertyComponent ){
            clonePropertyComponent( propComp );
            
            //
            //  Loop through radioButtons and select the one 
            //  matching the loaded value.
            //
            for( int i = 1; i < mRadioButtons.length; i++){
                if( mRadioButtons[i].getName().equals( propComp.getValue() ) ){
                    getListModel().replaceItem(propComp, this);
                    mRadioButtons[i].doClick();
                    return;
                }
            }
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
        mRadioButtons[0].doClick();
    }
    
    public String toString(){
        String result = "Path: " + this.getDataPath();
        result += "\nValue: ";
        return result;
    }
    
    
    //
    //  Subscriber Interface Methods
    //
    public void setPackageName(String aPackageName){
        mPackageName = aPackageName;
    }


    public void setResourceId(String aResourceId) {
        mResourceId = aResourceId;
    }
    
    public void setDefaultName(String aDefaultName) {
        mDefaultName = aDefaultName;
    }
    

    public void setLocaleManager(LocaleManager aLocaleManager) {
        mLocaleManager = aLocaleManager;
        mLocaleManager.attach( this );
    }
    

    public void update() {
        for(int i = 1; i < mRadioButtons.length; i++){
            String name = mLocaleManager.getLocalizedName( mPackageName, mResourceId+"."+mRadioButtons[i].getName(), null );
            if(name == null){
                name = mRadioButtons[i].getName();
            }
            mRadioButtons[i].setText( formatText( name ) );
        }
        mRadioButtons[defaultIndex].setText( mRadioButtons[defaultIndex].getText() + " *" );
    }
}
