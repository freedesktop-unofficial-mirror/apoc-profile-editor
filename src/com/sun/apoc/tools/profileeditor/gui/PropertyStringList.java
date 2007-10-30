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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author cs202741
 */
public class PropertyStringList extends JPanel implements PropertyComponent, ActionListener, ListDataListener {
    
    //  PropertyComponent Interface Methods
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
    
    // Used so we can update the listModel without
    // firing events like on 'checkForLoadedProperty()'.
    private boolean updateLock = false;
    
    private JList mList = null;
    private DefaultListModel mDefaultListModel = null;
    

    //private PropertyJListModel mPropertyListModel = null;
    private JButton newButton, clearButton, deleteButton, setDefaultButton;
    
    /** Creates a new instance of PropertyStringList */
    public PropertyStringList() {
        this.setLayout( new BorderLayout() );
        this.setOpaque(false);
        
        mDefaultListModel = new DefaultListModel();
        mList = new JList(mDefaultListModel);
        mDefaultListModel.addListDataListener( this );
        
        JScrollPane scrollPane = new JScrollPane(mList);
        scrollPane.setMinimumSize(new Dimension(160, 50) );
        
        this.add( scrollPane, BorderLayout.CENTER );
        this.add( getButtonPanel(), BorderLayout.LINE_END );
    }
    
    public JPanel getButtonPanel(){
        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        //buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.PAGE_AXIS ) );
        buttonPanel.setOpaque(false);
        
        newButton = new JButton("New");
        newButton.addActionListener(this);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        setDefaultButton = new JButton("Set Defaults");
        setDefaultButton.addActionListener( this );
        
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(setDefaultButton);
        
        return buttonPanel;
    }
    
    private void deleteSelectedItems(){
        int[] index = mList.getSelectedIndices();
        for(int i = index.length-1; i >= 0; i--){
            mDefaultListModel.removeElementAt( index[i] );
        }
        if( mDefaultListModel.getSize() == 0 ){
            reset();
        }
    }

    private void addItemToList(){
         String inputValue = JOptionPane.showInputDialog("Please input a value");
         
         if( inputValue != null ){
             mDefaultListModel.addElement( inputValue );
         }
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
        mDefault = aDefault;
    }

    public String getDefaultValue() {
        return mDefault;
    }
    
    
    public void setSeperator(String aSeperator) {
        mSeperator = aSeperator;
    }

    public String getSeperator() {
        if( mSeperator == null )
            return " ";
        else
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
    

    public void setValue(String aValue) {
    }

    public String getValue() {
        StringBuffer result = new StringBuffer();
        if( mDefaultListModel.getSize() > 0){

            Enumeration e = mDefaultListModel.elements();

            while( e.hasMoreElements() ){
                result.append( (String)e.nextElement() + mSeperator );
            }

            char[] c = mSeperator.toCharArray();

            if( result.charAt( result.length() - 1 ) == c[0] ){
                result = result.deleteCharAt(result.length()-1);
            }
        }

        return result.toString();
    }

    public void setLabel(String aLabel) {
    }

    public String getLabel() {
        return "";
    }


    public boolean hasChanged() {
        
        if( mDefaultListModel.isEmpty() )
            return false;
        else
            return true;
    }

    public void setProfileModel(StandaloneProfileManager aProfileModel) {
        mProfileModel = aProfileModel;
        
        checkForLoadedProperty();
    }
    

    public void checkForLoadedProperty() {
        PropertyComponent propComp = getListModel().existsByDataPath( mDataPath );
        if( propComp instanceof PropertyComponent ){
            updateLock = true;
            clonePropertyComponent(propComp);

            String[] items = propComp.getValue().split( propComp.getSeperator() );
            mDefaultListModel.clear();
            for(int i =0; i < items.length; i++){
                mDefaultListModel.addElement( items[i] );
            }
            getListModel().replaceItem( propComp , this);
            updateLock = false;
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

    public void saveProperty() {
        if( this.hasChanged() ){
            mProfileModel.createProperty( this );
        }
    }

    public PropertyJListModel getListModel() {
        return mProfileModel.getListModel();
    }

    public void reset() {
        mDefaultListModel.clear();
        mProfileModel.removeProperty( this );
    }
    

    //
    //  ActionListener Interface
    //
    public void actionPerformed(ActionEvent e) {
        
        if( e.getSource() == newButton ){
            addItemToList();
        }else if( e.getSource() == deleteButton ){
            deleteSelectedItems();
        }else if( e.getSource() == clearButton ){
            mDefaultListModel.clear();
        }else if( e.getSource() == setDefaultButton ){
            setDefaults();
        }
    }

    //
    //  ListDataListener Interface Methods
    //
    public void intervalAdded(ListDataEvent e) {
        updateProperties();
    }

    public void intervalRemoved(ListDataEvent e) {
        updateProperties();
    }

    public void contentsChanged(ListDataEvent e) {
        updateProperties();
    }
    
    
    private void setDefaults(){
        
        if( !mDefault.equals("") ){
            String[] items = mDefault.split( mSeperator );
            for(int i = 0; i < items.length; i++){
                mDefaultListModel.addElement( items[i] );
            }
        }
    }
    
    
    private void updateProperties(){

        if( !updateLock ){
            PropertyComponent aComponent = this;
            PropertyJListModel listModel = aComponent.getListModel();

            if( aComponent.hasChanged() ){
                if( listModel.exists( aComponent ) ){
                    listModel.replaceItem( aComponent, aComponent);
                }else{
                    listModel.addItem( aComponent );
                }
            }else {
                if( listModel.exists( aComponent ) ){
                    listModel.removeItem( aComponent );
                }
            }   
        }
    }
}
