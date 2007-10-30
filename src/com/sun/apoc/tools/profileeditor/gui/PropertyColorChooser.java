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

import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

/**
 *
 * @author cs202741
 */
public class PropertyColorChooser extends JPanel implements PropertyComponent, ActionListener, KeyListener, FocusListener {
    
    //  PropertyCompnent Interface
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
    
    // Local Variables
    private Border mBorder = null;
    private Border mErrorBorder = null;
    private String mHexChars = "0123456789abcdef";
    
    private JTextField mColorField = null;
    private ColourButton btnPickColor = null;
    
    
    /** Creates a new instance of PropertyColorChooser */
    public PropertyColorChooser() {
        super();
        this.setLayout( new BoxLayout(this, BoxLayout.LINE_AXIS) );
        mColorField = new JTextField(20);
        mColorField.addKeyListener(this);
        mColorField.addFocusListener(this);
        
        mBorder = mColorField.getBorder();
        Color c1 = new Color(255,0,0);
        Color c2 = new Color(139,0,0);
        mErrorBorder = new SoftBevelBorder(1, c1, c2);
        
        btnPickColor = new ColourButton();
        btnPickColor.addActionListener(this);
        btnPickColor.addActionListener(new ComponentListener());
        
        this.add( mColorField );
        this.add( btnPickColor );
    }
    
    private void pickColor(){
        
        Color color = JColorChooser.showDialog(
                             null,
                             "Select a colour.",
                             Color.RED);
        
        if( color != null ){
            String red = Integer.toHexString( color.getRed() );
            String green = Integer.toHexString( color.getGreen() );
            String blue = Integer.toHexString( color.getBlue() );
            
            if( red.length() < 2 ){
                red = "0" + red;
            }
            if( green.length() < 2 ){
                green = "0" + green;
            }
            if( blue.length() < 2 ){
                blue = "0" + blue;
            }
            
            mColorField.setText( "#" + red + green + blue );
            btnPickColor.setColor( color );
            updateJListModel();
        }
    }

    
    public boolean checkInput(){
        mColorField.setBorder(mBorder);
        
        if( !mColorField.getText().equals("") ){
            String color = mColorField.getText();
            if( color.charAt(0) == '#' ){
                color = color.substring(1);
            }
            
            if( color.length() != 6 ){
                mColorField.setBorder(mErrorBorder);
                return false;
            }

            for(int i = 0; i < color.length(); i++){
                String c = color.substring(i,i+1);
                c = c.toLowerCase();
                if( mHexChars.indexOf(c) == -1 ){
                    mColorField.setBorder(mErrorBorder);
                    return false;
                }
            }
        }
        return true;
    }//end checkInput()
    
    
    //
    //  PropertyComponent Interface methods
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
        mColorField.setText( mDefault );
        btnPickColor.setColor( mDefault );
    }
    
    public String getDefaultValue(){
        return mDefault;
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
        return mColorField.getText();
    }
    
    public void setLabel(String s){
    }
    
    public String getLabel(){
        return mColorField.getText();
    }

    
    public boolean hasChanged(){
        
        if( !checkInput() ){
            return false;
        }
        
        String currentValue = mColorField.getText();
        
        if( currentValue.equals( this.getDefaultValue() ) 
            || currentValue.equals("") ) 
            return false;
        else{
            btnPickColor.setColor( currentValue );
            return true;
        }
    }

    
    public void setProfileModel(StandaloneProfileManager aProfileModel){
        mProfileModel = aProfileModel;
        
        checkForLoadedProperty();
    }
    

    public void checkForLoadedProperty() {
        
        PropertyComponent propComp = getListModel().existsByDataPath( mDataPath );
        if( propComp instanceof PropertyComponent ){
            clonePropertyComponent(propComp);
            mColorField.setText( propComp.getValue() );
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
        this.setVisualType( propComp.getVisualType() );
    }
   
    
    public void saveProperty(){
        if( this.hasChanged() && checkInput() ){
            mProfileModel.createProperty( this );
        }
    }
    
    public PropertyJListModel getListModel(){
        return mProfileModel.getListModel();
    }
    
    public void reset(){
        mProfileModel.removeProperty( this );
        mColorField.setText( this.getDefaultValue() );
        //mColorField.fireActionPerformed();
    }
    
    public String toString(){
        String result = "Path: " + this.getDataPath();
        result += "\nValue: " + (String)mColorField.getText();
        return result;
    } 

    
    
    //
    //  Interface methods for Listeners
    //
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == btnPickColor ){
            pickColor();
        }
    }

    public void keyTyped(KeyEvent e) {
        hasChanged();
    }

    public void keyPressed(KeyEvent e) {
        hasChanged();
    }

    public void keyReleased(KeyEvent e) {
        hasChanged();
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        updateJListModel();
    }
    
    private void updateJListModel(){

        PropertyJListModel listModel = getListModel();

        if( this.hasChanged() ){
            if( listModel.exists( this ) ){
                listModel.replaceItem(this, this);
                System.out.println("replacing item");
                //System.out.println("REMOVING FROM LIST BEFORE ADD: " + propComp.getDataPath() );
            }else{
                listModel.addItem( this );
                System.out.println("adding item");
                //System.out.println("ADDING TO LIST: " + propComp.getDataPath() );  
            }

        }else {
            if( listModel.exists( this ) ){
                listModel.removeItem( this );
                //System.out.println("REMOVING FROM LIST: " + propComp.getDataPath() );
            }
        }
    }
    
}
