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
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import com.sun.apoc.tools.profileeditor.templates.TemplatePropertyConstraint;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author cs202741
 */
public class PropertyChooser extends JPanel implements PropertyComponent, ActionListener {
    
    //  PropertyComponent Variables
    private boolean mIsSet = false;
    private String mResourceId = null;
    private String mDefaultName = null;
    private String mDataType = null;
    private String mSectionName = null;
    private String mResourceIdPath = null;
    private String mPath = null;
    private String mVisualType = null;
    private String mDataPath = null;
    private String mDefault = null;
    private String mSeperator = null;
    private String mDescriptionId = null;
    private String mChooserPath = null;
    private String mExtendsChooser = null;
    private StandaloneProfileManager mProfileModel = null;

    
    private TemplateProperty mProperty = null;
    private String[] mDefaultItems;
    private PropertyJListModel mPropertyListModel = null;
    
    // GUI Comps
    private JComboBox itemsComboBox = null;
    private JButton editButton, addButton, deleteButton, okButton, resetDefaultsButton, cancelButton;
    private JTextField mNewItemText = null;
    private JList mList = null;
    private DefaultListModel mDefaultListModel = null;
    private JFrame frame;
    
    private boolean updateLock = false;
    
    /** Creates a new instance of PropertyStringList */
    public PropertyChooser(TemplateProperty aProperty) {
        this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        this.setOpaque(false);
        setChooserPath( aProperty.getChooserPath() );
        setExtendsChooser( aProperty.getExtendsChooser() );
        mProperty = aProperty;
        DefaultComboBoxModel model = new DefaultComboBoxModel( getItems() );
        itemsComboBox = new JComboBox(model);
        itemsComboBox.addActionListener( this );
        
        editButton = new JButton("Edit");
        editButton.addActionListener( this );
        
        this.add( itemsComboBox );
        this.add( editButton );
    }
    
    private String[] getItems(){
        Vector v = mProperty.getConstraints();
        if( v != null){
            Object[] items = v.toArray();
            mDefaultItems = new String[items.length + 1];
            mDefaultItems[0] = "-- Not Set --";
            for(int i = 0; i < items.length; i++){
                mDefaultItems[i + 1] = ((TemplatePropertyConstraint)items[i]).getValue();
            }

            return mDefaultItems;
        }
        String[] str = new String[1];
        str[0] = "-- Not Set --";
        return str;
    }
    
    private void showEditFrame(){
        frame = new JFrame("Edit Options");
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder( BorderFactory.createEmptyBorder( 30, 30, 30, 30) );

        frame.setContentPane( contentPanel );
        
        contentPanel.add( getNewItemPanel(), BorderLayout.PAGE_START );
        contentPanel.add( getListPanel(), BorderLayout.CENTER );
        contentPanel.add( getButtonPanel(), BorderLayout.PAGE_END );
        
        frame.setVisible(true);
        frame.pack();
    }
    
    private JPanel getNewItemPanel(){
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT) );
        mNewItemText = new JTextField(13);
        mNewItemText.addActionListener( this );
        addButton = new JButton("Add Item");
        addButton.addActionListener( this );
        
        panel.add(mNewItemText);
        panel.add(addButton);
        
        return panel;
    }
    
    private JPanel getListPanel(){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.LINE_AXIS ) );

        mDefaultListModel = new DefaultListModel();
        mList = new JList( mDefaultListModel );
        JScrollPane scroll = new JScrollPane( mList );
        loadListFromCombo();
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener( this );
        
        mainPanel.add( scroll );
        mainPanel.add( deleteButton );
        
        return mainPanel;
    }
    
    
    private JPanel getButtonPanel(){
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        okButton = new JButton("OK");
        okButton.addActionListener( this );
        resetDefaultsButton = new JButton("Restore Defaults");
        resetDefaultsButton.addActionListener( this );
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener( this );
        
        panel.add( okButton );
        panel.add( resetDefaultsButton );
        panel.add( cancelButton );
        return panel;
    }
    
    
    private void loadDefaultList(){
        mDefaultListModel.clear();
        for(int i = 1; i < mDefaultItems.length; i++){
            mDefaultListModel.addElement( mDefaultItems[i] );
        }
    }
    
    private void loadListFromCombo(){
        DefaultComboBoxModel model = (DefaultComboBoxModel)itemsComboBox.getModel();
        mDefaultListModel.clear();
        
        for( int i = 1; i < model.getSize(); i++){
            mDefaultListModel.addElement( model.getElementAt(i) );
        }
        
    }
    
    private void updateComboBox(Enumeration e){
        DefaultComboBoxModel model = (DefaultComboBoxModel)itemsComboBox.getModel();
        model.removeAllElements();
        model.addElement("-- Not Set --");
        
        while( e.hasMoreElements() ){
            model.addElement( (String)e.nextElement() );
        }
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
        mDefault = aDefault;
    }

    public String getDefaultValue() {
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

    public void setValue(String aValue) {
    }

    public String getValue() {
        return (String)itemsComboBox.getSelectedItem();
    }

    public void setLabel(String aLabel) {
    }

    public String getLabel() {
        return (String)itemsComboBox.getSelectedItem();
    }

    public boolean hasChanged() {
        if( itemsComboBox.getSelectedIndex() != 0)
            return true;
        else
            return false;
    }

    public void setProfileModel(StandaloneProfileManager aProfileModel) {
        mProfileModel = aProfileModel;
        mPropertyListModel = mProfileModel.getListModel();
        
        checkForLoadedProperty();
    }
    

    public void checkForLoadedProperty() {
        PropertyComponent propComp = getListModel().existsByDataPath( mDataPath );
        if( propComp instanceof PropertyComponent ){
            updateLock = true;
            clonePropertyComponent( propComp );

            //  Check if loaded value is in list, if so we select it
            //  and return, otherwise we have to add the item and select it.
            //
            for( int i = 0; i  < itemsComboBox.getItemCount(); i++){
                String item = (String)itemsComboBox.getItemAt(i);
                if( item.equals( propComp.getValue() )){
                    getListModel().replaceItem(propComp, this);
                    itemsComboBox.setSelectedIndex(i);
                    return;
                }
            }
            getListModel().replaceItem(propComp, this);
            
            itemsComboBox.addItem( propComp.getValue() );
            itemsComboBox.setSelectedItem( propComp.getValue() );
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
        this.setVisualType( propComp.getVisualType() );
    }

    public void saveProperty() {
        if( this.hasChanged() ){
            mProfileModel.createProperty( this );
        }
    }

    public PropertyJListModel getListModel() {
        return mPropertyListModel;
    }

    public void reset() {
        mProfileModel.removeProperty( this );
        itemsComboBox.setSelectedIndex(0);
    }

    
    
    //
    //  ActionListener Interface Methods
    //
    public void actionPerformed(ActionEvent e) {
        
        if( e.getSource() == editButton ){
            showEditFrame();
        }else if( e.getSource() == addButton || e.getSource() == mNewItemText ){
            if( ! mNewItemText.getText().equals("") ){
                mDefaultListModel.addElement( mNewItemText.getText() );
                mNewItemText.setText("");
            }
        }else if( e.getSource() == deleteButton ){
            if( ! mList.isSelectionEmpty() && ! mDefaultListModel.isEmpty() ){
                mDefaultListModel.removeElementAt( mList.getSelectedIndex() );
            }
        }else if( e.getSource() == okButton ){
            updateComboBox( mDefaultListModel.elements() );
            frame.setVisible( false);
            frame = null;
        }else if( e.getSource() == resetDefaultsButton ){
            loadDefaultList();
        }else if( e.getSource() ==  cancelButton ){
            frame.setVisible( false);
            frame = null;
        }else if( e.getSource() == itemsComboBox ){
            updateJListModel();
        }
    }
    
    private void updateJListModel(){

        if( !updateLock ){
            if( this.hasChanged() ) {
                if( mPropertyListModel.exists( this ) ){
                    int index = mPropertyListModel.indexOf( this );
                    mPropertyListModel.removeItem( this );
                    mPropertyListModel.addItem( index, this );
                }else{
                    mPropertyListModel.addItem( this );
                }
            }else {
                if( mPropertyListModel.exists( this ) ){
                    mPropertyListModel.removeItem( this );
                }
            }
        }
    }
}
