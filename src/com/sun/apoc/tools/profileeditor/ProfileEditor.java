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
import com.sun.apoc.tools.profileeditor.gui.AboutFrame;
import com.sun.apoc.tools.profileeditor.gui.ErrorPanel;
import com.sun.apoc.tools.profileeditor.gui.GeneralPropertiesPanel;
import com.sun.apoc.tools.profileeditor.gui.GradientJPanel;
import com.sun.apoc.tools.profileeditor.gui.GradientTabPaneUI;
import com.sun.apoc.tools.profileeditor.gui.SetPanel;
import com.sun.apoc.tools.profileeditor.gui.SetSubPanel;
import com.sun.apoc.tools.profileeditor.gui.SummaryJPanel;
import com.sun.apoc.tools.profileeditor.gui.TitledSectionJPanel;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.apoc.tools.profileeditor.gui.PreferencesJPanel;
import com.sun.apoc.tools.profileeditor.gui.PropertyComponent;
import com.sun.apoc.tools.profileeditor.gui.ImageButton;
import com.sun.apoc.tools.profileeditor.gui.InfoPanel;
import com.sun.apoc.tools.profileeditor.gui.ProfileEditorJMenuBar;
import com.sun.apoc.tools.profileeditor.gui.PropertyJListModel;
import com.sun.apoc.tools.profileeditor.gui.PropertyJListPanel;
import com.sun.apoc.tools.profileeditor.packages.*;
import com.sun.apoc.tools.profileeditor.templates.TemplateCategory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.*;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;


public class ProfileEditor extends JFrame implements WindowListener{
    
    public static final String VERSION = "0.12";
    
    public static final String PREFERENCES = "preferences.cfg";
    public static final int buttonHeight = 42;
    public static final int infoHeight = 20;
    public static final int centerHeight = 600;
    public static final int outputHeight = 240;
    public static final int windowHeight = buttonHeight + infoHeight + centerHeight + outputHeight;
    
    public static final int leftWidth = 220;
    public static final int centerWidth = 640;
    public static final int rightWidth = 240;
    public static final int windowWidth = leftWidth + centerWidth + rightWidth;

    private JPanel contentPanel, outputPanel, mPropertyListPanel;
    private GeneralPropertiesPanel mGeneralProperties = null;
    private SummaryJPanel summaryPanel;
    private JSplitPane mainSplitPane;
    private JScrollPane contentScrollPane, treeScrollPane;
    private JComboBox localeComboBox;
    private JLabel profileLabel;
    private JTabbedPane mTabbedPane = null;
    private JTree mTemplateTree = null;
    private JButton btnSave = null;
    
    private LocaleManager mLocaleManager = null;
    private ProfileEditorPreferences mPreferences = null;
    private StandaloneProfileManager mProfileManager = null;
    private PropertyJListModel mListModel = null;
    private PackageManager mPackageManager = null;
    
    private String mLocale = Locale.getDefault().toString();
    private String mProfilePath = "";
    private File mPackagePath = null;
    
    private Vector mLastSave = null;

    
    public ProfileEditor(){
        super("Profile Editor");
        makeFrame();
        loadPreferences();
        
        mLocaleManager = new LocaleManager();
        mLocaleManager.setPreferences( mPreferences );
        mLocaleManager.setLocale( Locale.getDefault().toString() );
        mListModel = new PropertyJListModel(this);
        mProfileManager = new StandaloneProfileManager(mListModel);
        summaryPanel = new SummaryJPanel(mLocaleManager);
        mListModel.addListDataListener( summaryPanel );

        this.getContentPane().add( getTopPanel() ,BorderLayout.NORTH);
        //this.getContentPane().add( mainSplitPane, BorderLayout.CENTER );
        this.getContentPane().add( getContentTabbedPane(), BorderLayout.CENTER);
        
        this.pack();
        
        openPackage( mPreferences.getProperty( "templatePackagePath" ) );
    }
    
    
    private JPanel getContentTabbedPane(){
        JPanel panel = new JPanel( new BorderLayout() );
        mTabbedPane = new JTabbedPane();
        mTabbedPane.setUI( new GradientTabPaneUI() );
        
        contentPanel = new JPanel( new BorderLayout() );
        contentPanel.setBackground( Color.WHITE );
        contentScrollPane = new JScrollPane(contentPanel);
        
        mainSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, getTreeScrollPane(), contentScrollPane );
        mainSplitPane.setDividerSize(4);
        mainSplitPane.setDividerLocation(200);
        
        JScrollPane summaryScrollPane = new JScrollPane( summaryPanel );
        mGeneralProperties = new GeneralPropertiesPanel(this, mProfileManager);
        mTabbedPane.add( "General Properties", mGeneralProperties );
        mTabbedPane.add( "Configuration Settings", mainSplitPane );
        mTabbedPane.add( "Settings Summary", summaryScrollPane );
        mTabbedPane.add( "Info", new InfoPanel() );
        mTabbedPane.add( "Errors", new ErrorPanel() );
        
        panel.add( mTabbedPane, BorderLayout.CENTER );
        return panel;
    }
    
    
    private JPanel getTopPanel(){
        JPanel topPanel = new JPanel(new BorderLayout());
        //topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.PAGE_AXIS ) );
        topPanel.add( getImageButtonPanel(), BorderLayout.PAGE_START );
        topPanel.add( getInfoPanel(), BorderLayout.PAGE_END );
        
        return topPanel;
    }
    
    
    public JScrollPane getTreeScrollPane(){
        
        treeScrollPane = new JScrollPane();
        treeScrollPane.setPreferredSize( new Dimension( leftWidth, centerHeight));
        
        return treeScrollPane;
    }
    
    
    public void createJTree(DefaultMutableTreeNode node){
        
        Icon leafIcon = new ImageIcon( "images/tree_document.gif");
        Icon parentIcon = new ImageIcon( "images/tree_folder.gif");
        
        UIManager.put("Tree.collapsedIcon",new ImageIcon("images/tree_closed.gif"));
        UIManager.put("Tree.expandedIcon",new ImageIcon("images/tree_open.gif"));
        
        mTemplateTree = new JTree( node );
        mTemplateTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)mTemplateTree.getCellRenderer();
        
        mTemplateTree.putClientProperty("JTree.lineStyle", "None");
                
        renderer.setTextNonSelectionColor(new Color( 0x405661 ));
        renderer.setTextSelectionColor(new Color( 0x405661 ));
        renderer.setLeafIcon(leafIcon); 
        renderer.setOpenIcon( parentIcon );
        renderer.setClosedIcon( parentIcon );
        
        mTemplateTree.addTreeSelectionListener( new TreeListener() );
        mTemplateTree.addMouseListener( new TreeMouseListener() );
        treeScrollPane.setViewportView( mTemplateTree );
    }
    
    
    private JPanel getListPanel(){
        mPropertyListPanel = new PropertyJListPanel(mListModel);
        return mPropertyListPanel;
    }//end getListPanel()
    
    
    private JPanel getOutputPanel(){
        outputPanel = new InfoPanel();
        return outputPanel;
    }//end getOutputPanel()
    
    
    private JPanel getInfoPanel(){
        //JPanel panel = new JPanel();
        GradientJPanel panel = new GradientJPanel(new Color(0xCBCFD5), new Color(0xFFFFFF), 
                                                        GradientJPanel.ROUNDED_NONE, 0, 0);
        
        panel.setLayout( new FlowLayout(FlowLayout.LEFT) );
        profileLabel = new JLabel("Current Profile: ");
        panel.add( profileLabel );

        return panel;
    }//end getInfoPanel()
    
    
    private JPanel getImageButtonPanel(){
        
        ActionListener listener = new ButtonListener();
        
        GradientJPanel mainPanel = new GradientJPanel(new Color(0xCBCFD5), new Color(0xFFFFFF), 
                                                        GradientJPanel.ROUNDED_NONE, 20, 5);
        mainPanel.setLayout( new BorderLayout() );
        
        JPanel buttonPanel = new JPanel( new FlowLayout(FlowLayout.LEFT ) );
        buttonPanel.setOpaque(false);
        JPanel localePanel = new JPanel( new FlowLayout( FlowLayout.RIGHT) );
        localePanel.setOpaque(false);
        
        JButton btnNew = new ImageButton( new ImageIcon("images/new.png") );
        JButton btnOpen = new ImageButton( new ImageIcon("images/open.png") );
        btnSave = new ImageButton( new ImageIcon("images/save.png") );
        JButton btnSaveAs = new ImageButton( new ImageIcon("images/save_as.png") );
        JButton btnPreferences = new ImageButton( new ImageIcon("images/preferences.png") );
        
        btnNew.setActionCommand("New");
        btnOpen.setActionCommand("OpenProfile");
        btnSave.setActionCommand("Save");
        btnSaveAs.setActionCommand("SaveAs");
        btnPreferences.setActionCommand("Preferences");
        
        btnNew.addActionListener( listener );
        btnOpen.addActionListener( listener );
        btnSave.addActionListener( listener );
        btnSaveAs.addActionListener( listener );
        btnPreferences.addActionListener( listener );
        
        btnNew.setToolTipText("Create New Profile");
        btnOpen.setToolTipText("Open Profile");
        btnSave.setToolTipText("Save Profile");
        btnSaveAs.setToolTipText("Save Profile As");
        btnPreferences.setToolTipText("Preferences");
        
        
        String locales = mPreferences.getProperty("defaultLocales");
        String userLocales = mPreferences.getProperty("userLocales");
        if( userLocales != null ){
            locales +=  "," + userLocales;
        }
        String[] splitLocales = locales.split(",");
        localeComboBox = new JComboBox(splitLocales);
        localeComboBox.setSelectedItem( Locale.getDefault().toString() );
        localeComboBox.setActionCommand("localeChange");
        localeComboBox.addActionListener( new ButtonListener() );
        
        //panel.setPreferredSize( new Dimension(windowWidth, buttonHeight) );
        buttonPanel.add(btnNew);
        buttonPanel.add(btnOpen);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnSaveAs);
        buttonPanel.add(btnPreferences);
        
        localePanel.add( new JLabel("      Locale:") );
        localePanel.add(localeComboBox);
        
        mainPanel.add( buttonPanel, BorderLayout.LINE_START );
        mainPanel.add( localePanel, BorderLayout.LINE_END );
        
        return mainPanel;
    }//end getButtonPanel()
    


    public void makeFrame(){
        this.setJMenuBar( new ProfileEditorJMenuBar( new ButtonListener() ) );
        this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        this.addWindowListener( this );
        this.setLayout( new BorderLayout() );
        this.setExtendedState(this.MAXIMIZED_BOTH );
        this.setVisible(true);
    } // makeFrame
    
    
    private void openPackage(String packagePath){
        
        if( packagePath == null ){
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
            chooser.setDialogTitle("Select a Package directory.");
            int returnVal = chooser.showOpenDialog(this);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                mPackagePath = file;
                mPreferences.setProperty( "templatePackagePath", file.toString() );
                try{
                    mPreferences.store();
                }catch(IOException e){}
            }else
                return;
        }else{
            mPackagePath = new File(packagePath);
        }
        
        SwingWorker worker = new SwingWorker(){
                @Override 
                public Object construct() {
                    openPackage();
               return "done";
                }
         };
        worker.start();
        
    }//end openPackage()
    
    
    public void openPackage(){

        mListModel.removeAll();
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
        mPackageManager = new PackageManager( mPackagePath, mProfileManager, mLocaleManager );
        
        try {
            mPackageManager.initialize();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error: Invalid package directory:\n",
                    "Invalid Package Directory!",
                    JOptionPane.ERROR_MESSAGE);
            openPackage(null);
        }
        
        createJTree( mPackageManager.getRootTreeNode() );
        this.setTitle( "Profile Editor - " );

    }//end openPackage(File)
       
    
    public void updateTemplate(JPanel aPanel){
        contentPanel.removeAll();
        contentPanel.add(aPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    
    public void updateSavedState(){
        
        if( changedSinceSave() ){
            this.setTitle( "Profile Editor - " + mProfilePath + " *CHANGED");
        }else {
            this.setTitle( "Profile Editor - " + mProfilePath );
        }
    }
    
    
    public void saveProfile(){
        
        if( changedSinceSave() ){
            if( !mProfileManager.exists() ){
                createNewProfileRepository();
            }
            Enumeration e = mListModel.elements();
            while( e.hasMoreElements() ){
                ((PropertyComponent)e.nextElement()).saveProperty();
            }
            mGeneralProperties.applyChanges();
            mProfileManager.writeChanges();
            mLastSave = (Vector)mListModel.getPropertyVector();
            updateSavedState();
        }else{
            //JOptionPane.showMessageDialog(this, "Nothing to save!", "Warning!", JOptionPane.WARNING_MESSAGE);
        }
            
    }//end saveProfile()


    private void saveProfileAs(){

        if( changedSinceSave() ){
            if( createNewProfileRepository() ){
                saveProfile();
            }
        }else{
            JOptionPane.showMessageDialog(this, "Nothing to save!", "Warning!", JOptionPane.WARNING_MESSAGE);
        }

    }//end saveProfileAs()

    
    private boolean changedSinceSave(){
        boolean changed = true;

        if( mLastSave == null && mListModel.getSize() > 0 ){
            return changed;
        }

        if( mLastSave == null && mListModel.getSize() == 0 ){
            if( mGeneralProperties.hasChanged() ){
                return changed;
            }else {
                return false;
            }
        }
        
        if( mLastSave.size() != mListModel.getSize() ){
            return changed;
        }
        
        if( mGeneralProperties.hasChanged() ){
            return changed;
        }
        
        HashSet savedSet = new HashSet();
        HashSet currentSet = new HashSet();
        
        Enumeration e = mListModel.elements();
        while(e.hasMoreElements()){
            PropertyComponent propComp = (PropertyComponent)e.nextElement();
            currentSet.add( propComp.getDataPath() + propComp.getValue() );
        }
        
        e = mLastSave.elements();
        while(e.hasMoreElements()){
            PropertyComponent oldProp = (PropertyComponent)e.nextElement();
            savedSet.add( oldProp.getDataPath() + oldProp.getValue() );
        }

        
        if( savedSet.equals(currentSet) ){
            changed = false;
        }

        return changed;
    }        
    
    
    private boolean createNewProfileRepository(){
        File file;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType( JFileChooser.SAVE_DIALOG );
        chooser.setDialogTitle("Save Profile");
        chooser.setSelectedFile( new File( mProfileManager.getDisplayName() ));
        
        int returnVal = chooser.showSaveDialog(this);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            String name = file.getName();
            
            String path = file.getPath();
            String url = "file://" + path.substring( 0, path.length() - name.length() );
            try {
                mProfileManager.destroyProfile();
                mProfileManager.createProfile( file.getName(), url);
                mGeneralProperties.applyChanges();
                mProfilePath = url + name;
                profileLabel.setText( "Current Profile - " + mProfilePath);
                this.setTitle( "Profile Editor - " + mProfilePath);
                return true;
            } catch (SPIException ex) {
                ex.printStackTrace();
                System.err.println("Could not create profile!");
            }
        }
        
        return false;

    }//end createNewProfile()
    
    private void openProfile(){

        if( mPackageManager.isOpen() ){
            File file;

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open Profile");

            int returnVal = chooser.showOpenDialog(this);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                String name = file.getName();
                String path = file.getPath();
                String url = "file://" + path.substring( 0, path.length() - name.length() );

                try {
                    mProfileManager.openProfile(name, url, mPackageManager, mLocaleManager, mTemplateTree);
                    mGeneralProperties.update();
                    mPackageManager.clearTemplatePanels();
                    treeClicked();
                    mLastSave = (Vector)mListModel.getPropertyVector();
                    profileLabel.setText( "Current Profile - " + url + name);
                    this.setTitle( "Profile Editor - " + url + name);
                } catch (SPIException ex) {
                     JOptionPane.showMessageDialog(this, "Could not open profile!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }   
        }else{
            JOptionPane.showMessageDialog(this, "You must open a package location first!", "No Packages Open!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void newProfile(){
        
        if( mListModel.getSize() > 0 && changedSinceSave() ){
            int val = JOptionPane.showConfirmDialog(this, "The existing Profile has unsaved changes.\n" + 
                                            "Do you want to save the changes before continuing?",
                                            "Warning!",
                                            JOptionPane.YES_NO_CANCEL_OPTION);
            
            if( val == JOptionPane.OK_OPTION ){
                saveProfile();
                closeProfile();
            }else if( val == JOptionPane.NO_OPTION ){
                closeProfile();
            }else
                return;   
        }else
            closeProfile();
    }
    
    
    private void closeProfile(){
        try {
            mPackageManager.removeSets();

            mListModel.removeAll();
            mProfileManager.destroyProfile();
            mGeneralProperties.reset();
            mPackageManager.clearTemplatePanels();
            mProfilePath = "";
            treeClicked();
            profileLabel.setText( "Current Profile - ");
            this.setTitle( "Profile Editor");
            mTemplateTree.revalidate();
            mTemplateTree.repaint();
        } catch (SPIException ex) {
            ex.printStackTrace();
        }
    }

    
    private void loadPreferences(){
        mPreferences = new ProfileEditorPreferences();
        try{
            mPreferences.load();
        }catch(IOException e){System.out.println("Could not Load Default Preferences");}
    }
    
    
    public String getCurrentLocale(){
        return mLocale;
    }
    
    public void openTemplate(Template template){
        
    }
    
    private void shutDown(){
        
        if( mListModel.getSize() != 0 && changedSinceSave() ){
            int result = JOptionPane.showConfirmDialog(
                this,
                "Would you like to save your profile before exiting?",
                "Save Profile?",
                JOptionPane.YES_NO_OPTION);
            if( result == JOptionPane.YES_OPTION ){
                saveProfile();
            }
        }
        System.exit(0);
    }
    
    
    private void treeClicked(){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           mTemplateTree.getLastSelectedPathComponent();

        if (node == null) return;

        Object obj = node.getUserObject();
        if( obj instanceof Template ){
            Template template = (Template)node.getUserObject();
            updateTemplate( mPackageManager.getTemplatePanel( template, mLocale, mTemplateTree ) );
        }else if( obj instanceof SetSubPanel ){
            updateTemplate( (JPanel)obj );
        }else if( obj instanceof TitledSectionJPanel ){
            updateTemplate( (JPanel)obj );
        }
    }
   
    
    public static void main(String [] argv){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ProfileEditor();
            }
        });
  }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        shutDown();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }


    public class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent event){
            
            if( event.getActionCommand().equals("New") ){
                newProfile();
            }else if( event.getActionCommand().equals("OpenProfile") ){
                openProfile();
            }else if( event.getActionCommand().equals("OpenPackage") ){
                openPackage( null );
            }else if( event.getActionCommand().equals("Save") ){
                saveProfile();
            }else if( event.getActionCommand().equals("SaveAs") ){
                saveProfileAs();
            }else if( event.getActionCommand().equals("Preferences") ){
                mTabbedPane.add( "Preferences", new PreferencesJPanel( mPreferences, mTabbedPane ) );
                mTabbedPane.setSelectedIndex( mTabbedPane.getComponentCount() - 1 );
            }else if( event.getActionCommand().equals("localeChange") ){
                mLocale = ((JComboBox)event.getSource()).getSelectedItem().toString();
                mLocaleManager.setLocale( mLocale );
                mTemplateTree.repaint();
            }else if( event.getActionCommand().equals("Exit") ){
                shutDown();
            }else if( event.getActionCommand().equals("About") ){
                new AboutFrame();
            }
        }
        
    }//end ButtonListener
    
    
    public class TreeListener implements TreeSelectionListener {
        
        public void valueChanged(TreeSelectionEvent e) {
            
            SwingWorker worker = new SwingWorker(){
                    @Override 
                    public Object construct() {
                        treeClicked();
                    return "done";
                    }
             };
            worker.start();
        }
    }//
    
    public class TreeMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            if( e.getClickCount() == 2 ){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                   mTemplateTree.getLastSelectedPathComponent();

                if (node == null) return;

                if (node.isLeaf()) {
                    try{
                        Template template = (Template)node.getUserObject();
                        openTemplate( template );
                    }catch(ClassCastException cce){
                        System.out.println(cce);
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

}
