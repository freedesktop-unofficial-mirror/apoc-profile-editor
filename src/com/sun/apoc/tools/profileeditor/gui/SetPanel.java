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
import com.sun.apoc.tools.profileeditor.LocalizedDefaultMutableTreeNode;
import com.sun.apoc.tools.profileeditor.packages.Template;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.apoc.tools.profileeditor.templates.TemplateSet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author cs202741
 */
public class SetPanel extends JPanel implements ActionListener, MouseListener {
    
    public static final int MAIN_LIST = 0;
    public static final int SUB_LIST = 1;
    
    private StandaloneProfileManager mProfileModel = null;
    
    //  Subscriber Interface Vars
    private String mPackageName = null;
    private LocaleManager mLocaleManager = null;
    private Template mTemplate;
    
    private JList mMainList = null;
    private JList mSubList = null;
    private TemplateSet mSet = null;
    private DefaultListModel mListModel = null;
    private JTree mTree = null;
    private DefaultMutableTreeNode mSetNode = null;
    
    private Hashtable mSetNodesByName = null;
    
    /** Creates a new instance of SetPanel */
    public SetPanel(Template template, TemplateSet set, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager, String aPackageName, JTree aTree) {
        super(  );
        mSet = set;
        mLocaleManager = aLocaleManager;
        mPackageName = aPackageName;
        mProfileModel = aProfileModel;
        mTemplate = template;
        mTree = aTree;
        mSetNode = getSetNode();
        mSetNodesByName = new Hashtable();
        mListModel = new DefaultListModel();
        this.add( getSetPanel(MAIN_LIST) );
        
        mSetNode.setUserObject( this.getTitledPanel() );
        
        //  So we can scan templates for sets when the user
        //  clicks 'New'.
        mTemplate.addSetPanel(this);
        
        checkForLoadedProperties();
    }
    
    
    private DefaultMutableTreeNode getSetNode(){
        int count = mTemplate.getTreeNode().getChildCount();
        for(int i = 0; i < count; i++){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)mTemplate.getTreeNode().getChildAt(i);
            TitledSectionJPanel panel = (TitledSectionJPanel)node.getUserObject();
            if( panel.getName().equals( mSet.getDataPath() ) ){
                return node;
            }
        }
        LocalizedDefaultMutableTreeNode node = new LocalizedDefaultMutableTreeNode(mSet.getDefaultName(), 
                                                        mPackageName, mSet.getResourceId(),
                                                        mSet.getDefaultName(), mLocaleManager);
        
        mTemplate.getTreeNode().add( node );
        return node;
    }
    
    private void checkForLoadedProperties(){
        
        //  If the SectionPanel is getting recreated we need to 
        //  repopulate the 'mSetNodesByName' with sets currently on tree.
        int count = mSetNode.getChildCount();
        for(int i = 0; i < count; i++){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)mSetNode.getChildAt(i);
            mSetNodesByName.put( node.getUserObject().toString(), node);
            mListModel.addElement( node.getUserObject().toString() );
        }
        
        HashMap setHash = new HashMap();
        Enumeration e = mProfileModel.getListModel().elements();
        while(e.hasMoreElements()){
            PropertyComponent propComp = (PropertyComponent)e.nextElement();
            String dataPath = propComp.getDataPath();
            String loadedPropPath = dataPath.substring(0, dataPath.lastIndexOf("/") );
            int index = loadedPropPath.lastIndexOf("/");
            if( index > 0 ){
                loadedPropPath = loadedPropPath.substring(0, index);
            }
            
            if( loadedPropPath.equals( mSet.getDataPath() ) ){
                String setName = dataPath.substring( loadedPropPath.length() + 1, dataPath.lastIndexOf("/") );
                
                setHash.put(setName, null);
            }
        }
        Set sets = setHash.keySet();
        Iterator it = sets.iterator();
        while(it.hasNext()){
            String setName = it.next().toString();
            if( mSetNodesByName.containsKey(setName) ){
                DefaultTreeModel model = (DefaultTreeModel)mTree.getModel();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)mSetNodesByName.remove(setName);
                SetSubPanel subPanel = (SetSubPanel)node.getUserObject();
                model.removeNodeFromParent( node );
                mListModel.removeElement(setName);
            }
            addItemToList(setName);
        }
                
        mTree.revalidate();
        mTree.repaint();
    }

    
    private void printNodes(){
        System.err.println("-------\nNODES:");
        Enumeration e = mSetNodesByName.elements();
        while(e.hasMoreElements()){
            System.err.println("   " + e.nextElement().toString() );
        }
        System.err.println("-------");
    }
    
    
    private JPanel getSetPanel(int list){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JButton newButton = new JButton("New");
        JButton deleteButton = new JButton("Delete");
        
        newButton.setActionCommand("New");
        
        newButton.addActionListener( this );
        deleteButton.addActionListener( this );
        
        JPanel buttonPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        buttonPanel.setOpaque(false);
        buttonPanel.add( newButton );
        buttonPanel.add( deleteButton );
        buttonPanel.setBorder( BorderFactory.createEmptyBorder(0,30,0,30));
        
        JScrollPane scroll = null;
        if(list == MAIN_LIST){
            mMainList = new JList(mListModel);
            mMainList.addMouseListener( this );
            scroll = new JScrollPane( mMainList );
            deleteButton.setActionCommand("MainDelete");
        }else {
            mSubList = new JList(mListModel);
            mSubList.addMouseListener( this );
            scroll = new JScrollPane( mSubList );
            deleteButton.setActionCommand("SubDelete");
        }
        
        JPanel listPanel = new JPanel( new BorderLayout() );
        listPanel.setOpaque(false);
        listPanel.add( scroll, BorderLayout.CENTER );
        listPanel.setBorder( BorderFactory.createEmptyBorder(15,30,15,30));
        
        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(listPanel, BorderLayout.CENTER );
        
        return panel;
    }
    
    
    //
    //  Returns a copy of 'this' panel wrapped in a titled panel
    //  So that it can be used for the tree node specific to this.
    //
    private JPanel getTitledPanel(){
        
        TitledSectionJPanel sectionPanel = new TitledSectionJPanel();
        sectionPanel.setTitleLabel( mSet, mPackageName, mLocaleManager);
        sectionPanel.setContentPanel( getSetPanel(SUB_LIST), true );
        sectionPanel.setName( mSet.getDataPath() );
        return sectionPanel;
    }
    
    

    private SetSubPanel getSetSubPanel(String setName){

        SetSubPanel setPanel = new SetSubPanel(mTemplate, mSet, setName, mProfileModel, mLocaleManager, mPackageName);
        setPanel.setName(setName);
        
        return setPanel;
    }
    
    private void addItemToList(){
         String inputValue = JOptionPane.showInputDialog("Please input a value");
         addItemToList( inputValue);
    }
    
    
    private void addItemToList(String inputValue){
         if( inputValue == null ){
             return;
         }
         
         if( mSetNodesByName.containsKey(inputValue) ){
            JOptionPane.showMessageDialog(this, "Set already exists, please choose another name!", 
                                        "Invalid Set Name!", JOptionPane.ERROR_MESSAGE);
         }
             
         if( inputValue.length() > 0 ){
             mListModel.addElement( inputValue );
             
             DefaultTreeModel model = (DefaultTreeModel)mTree.getModel();

             DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( getSetSubPanel(inputValue) );
             mSetNodesByName.put( inputValue, newNode );
             model.insertNodeInto( newNode, mSetNode, mSetNode.getChildCount() );
         }
    }
    
    public void removeSet(String setName){

            //  Remove selected element from tree
            DefaultTreeModel model = (DefaultTreeModel)mTree.getModel();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)mSetNodesByName.remove(setName);
            SetSubPanel subPanel = (SetSubPanel)node.getUserObject();
            if( subPanel != null){
                subPanel.deleteProperties();
            }

            model.removeNodeFromParent( node );
            
            //  Remove selected element from list
            mListModel.removeElement(setName);
    }
    
    public void removeAllSets(){

        mListModel.clear();
        Collection c = mSetNodesByName.values();
        Iterator it = c.iterator();
        while( it.hasNext() ){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)it.next();
            SetSubPanel subPanel = (SetSubPanel)node.getUserObject();
            if(subPanel!=null){
                subPanel.deleteProperties();
            }
        }
        mSetNodesByName.clear();
        mSetNode.removeAllChildren();
        ((DefaultTreeModel)mTree.getModel()).nodeStructureChanged(mSetNode);
    }
    

    public void actionPerformed(ActionEvent e) {
        
        if( e.getActionCommand().equals("New") ){
            addItemToList();
        }else if( e.getActionCommand().equals("MainDelete") ){
            if( !mMainList.isSelectionEmpty() ){
                removeSet( mMainList.getSelectedValue().toString() );
            }
        }else if( e.getActionCommand().equals("SubDelete") ){
            if( !mSubList.isSelectionEmpty() ){
                removeSet( mSubList.getSelectedValue().toString() );
            }
        } else if( e.getActionCommand().equals("Done") ){

        }else if( e.getActionCommand().equals("Cancel") ){
            
        }
    }

    public void mouseClicked(MouseEvent e) {
        if( e.getClickCount() == 2){
            if( e.getSource() instanceof JList ){
                JList list = (JList)e.getSource();
                if( !list.isSelectionEmpty() ){
                      DefaultMutableTreeNode node = (DefaultMutableTreeNode)mSetNode.getChildAt( list.getSelectedIndex() );
                      int[] row = mTree.getSelectionRows();
                      mTree.expandRow( row[0] );
                      mTree.setSelectionRow( row[0] + (list.getSelectedIndex()+1) );
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
