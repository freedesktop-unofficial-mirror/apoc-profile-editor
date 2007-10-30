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

package com.sun.apoc.tools.profileeditor.packages;

import com.sun.apoc.tools.profileeditor.LocaleManager;
import com.sun.apoc.tools.profileeditor.LocalizedDefaultMutableTreeNode;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.apoc.tools.profileeditor.PanelFactory;
import com.sun.apoc.tools.profileeditor.templates.TemplateCategory;
import com.sun.apoc.tools.profileeditor.templates.TemplatePage;
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTree;

import javax.swing.tree.*;


/**
 *
 * @author cs202741
 */
public class PackageManager {
    
    private File packageDir;
    private List packageList;
    private Hashtable packageHash;
    private DefaultMutableTreeNode rootNode;
    private JFrame mProgressFrame;
    private HashMap mTemplatePanels;
    private StandaloneProfileManager mProfileModel;
    private boolean mPackageOpen = false;
    
    private ArrayList mPropertyList = null;
    private HashMap mPropertyIndex = null;
    
    private JProgressBar mProgressBar = null;
    
    private LocaleManager mLocaleManager = null;
            
    
    /** Creates a new instance of PackageTreeManager */
    public PackageManager(File aDir, StandaloneProfileManager aProfileModel, LocaleManager aLocaleManager) {
        packageDir = aDir;
        mProfileModel = aProfileModel;
        mLocaleManager = aLocaleManager;
    }
    
    public void initialize() throws FileNotFoundException{

        try{
            mPropertyList = new ArrayList();
            packageList = getPackages( packageDir );
            mTemplatePanels = new HashMap();
            generatePackageHash();
            createPropertyIndex();
        }catch(FileNotFoundException e){
            closeProgressDialog();
            throw new FileNotFoundException();
        }
    }
    
    public List getPackages(File dir) throws FileNotFoundException {
        if( dir.exists() ){
            File[] fileList = dir.listFiles();
            List list = new ArrayList();


            String[] tokens = dir.toString().split("_");
            
            if( tokens[tokens.length-1].equalsIgnoreCase("pkg") ){
                tokens = tokens[0].split("/");
                list.add( new Package( tokens[tokens.length-1], dir, packageDir, mLocaleManager, mPropertyList ) );
            }else if( dir.toString().toLowerCase().lastIndexOf(".xml") > 0){
                list.add( new Package( dir.toString().substring(0,dir.toString().length()-4), dir, packageDir, mLocaleManager, mPropertyList ) );
            }else if( dir.toString().startsWith("SUNW") ){
                list.add( new Package( dir.toString(), dir, packageDir, mLocaleManager, mPropertyList ) );
            }
            
            showProgressDialog(fileList.length-1);

            for( int i = 0; i < fileList.length; i++){
                if( fileList[i].isDirectory() ){
                    tokens = fileList[i].toString().split("_");
                    if ( tokens[tokens.length-1].equalsIgnoreCase("pkg") ){
                        tokens = tokens[0].split("/");
                        list.add( new Package( tokens[tokens.length-1], fileList[i], packageDir, mLocaleManager, mPropertyList ) );
                        mProgressBar.setValue( i + 1);
                    }//if
                }//if
            }//for
            if( list.size() == 0){
                throw new FileNotFoundException( dir.toString() );
            }
            
            closeProgressDialog();
            mPackageOpen = true;
            
            Collections.sort( list );
            return list;
        }else
            throw new FileNotFoundException( dir.toString() );
    }
    
    
    private void generatePackageHash(){
        
        if( packageList != null ){
            Hashtable packages = new Hashtable();
            rootNode = new LocalizedDefaultMutableTreeNode();
            rootNode.setUserObject("Templates");
            
            Iterator pkgIt = packageList.iterator();
            while( pkgIt.hasNext() ){
                Package tmpPkg = (Package)pkgIt.next();

                Iterator templateIt = tmpPkg.getTemplates().iterator();
                while( templateIt.hasNext() ){
                    
                    Template tmpTemplate = (Template)templateIt.next();
                    TemplateCategory category = tmpTemplate.getRootCategory();
                    String name = tmpTemplate.getName();

                    //DefaultMutableTreeNode baseNode = new DefaultMutableTreeNode( name );
                    Hashtable[] subCategories = null;
                    
                    if( ! packages.containsKey(name) ){
                        subCategories = new Hashtable[4];
                        for(int i = 0; i < subCategories.length; i++){
                            subCategories[i] = new Hashtable();
                        }
                        packages.put( name, subCategories );
                    }

                    subCategories = (Hashtable[])packages.get( name );

                    rootNode.add( recursCreateTree( tmpPkg.getName(), subCategories, 0, name, category, tmpTemplate) );
                    
                }//while
            }//while
  
        }//if
        
    }//generatePackageHash()
    
    
    
    private DefaultMutableTreeNode recursCreateTree( String pkgName, Hashtable[] hash, int index, String previousName,
                                                        TemplateCategory category, Template template){

        String name = category.getDefaultName();
        String resourceID = category.getResourceId();
   
        DefaultMutableTreeNode currentNode = getMatchingNode(hash[index], previousName+name );

        if( currentNode == null ){
            currentNode = new LocalizedDefaultMutableTreeNode( name, pkgName, category.getResourceId(), 
                                                                category.getDefaultName(), mLocaleManager );
            hash[index].put( previousName+name, currentNode );
        }
       
        if( category.hasSubCategories() ){
            String[] keys = category.getKeys();

            for(int i = 0; i < keys.length; i++){
                category = category.getSubCategory( keys[i] );
                currentNode.add( recursCreateTree( pkgName, hash, ++index, name, category, template) );
            }
            return currentNode;
        }else{
            template.setName( resourceID, category.getDefaultName() );
            template.setPath( category.getPath() );
            template.setTreeNode(currentNode);
            template.setResourceIdPath( category.getResourceIdPath() );
            currentNode.setUserObject(template);
            return currentNode;
        }
    }//end recursCreateTree()
    
    
    private void createPropertyIndex(){
        mPropertyIndex = new HashMap();
        
        Iterator it = mPropertyList.iterator();
        while(it.hasNext()){
            TemplateProperty property = (TemplateProperty)it.next();
            mPropertyIndex.put( property.getDataPath(), property );
        }   
    }
    
    public Template getTemplateByProperty(String dataPath){
        Iterator it = packageList.iterator();
        while(it.hasNext()){
            Package pkg = (Package)it.next();
            List list = pkg.getTemplates();
            Iterator temIt = list.iterator();
            while(temIt.hasNext()){
                Template template = (Template)temIt.next();
                if( template.containsSet(dataPath) ){
                    return template;
                }
            }
        }
        return null;
    }
    
    public void removeSets(){
        Iterator it = packageList.iterator();
        while(it.hasNext()){
            Package pkg = (Package)it.next();
            List list = pkg.getTemplates();
            Iterator temIt = list.iterator();
            while(temIt.hasNext()){
                ((Template)temIt.next()).removeSets();
            }
        }
    }
    
    public TemplateProperty lookupProperty(String dataPath){
        if( mPropertyIndex != null )
            return (TemplateProperty)mPropertyIndex.get( dataPath );
        else
            return null;
    }
    
    public DefaultMutableTreeNode getMatchingNode(Hashtable hash, String name){
        DefaultMutableTreeNode node = null;
        
        Enumeration e = hash.keys();
        while( e.hasMoreElements() ){
            Object obj = e.nextElement();
            String currName = (String)obj;
            
            if( currName.equals( name ) ) {
                DefaultMutableTreeNode currNode = (DefaultMutableTreeNode)hash.get(obj);
                node = currNode;
                break;
            }
        }
        return node;
    }//end getMatchingNode()
    
    
    public JPanel getTemplatePanel(Template aTemplate, String aLocale, JTree aTree){
        String name = aTemplate.getPath();
        JPanel panel = null;
        
        if( mTemplatePanels.containsKey( name ) ){
            panel = (JPanel)mTemplatePanels.get( name );
        }else{
            panel = PanelFactory.getTemplatePanel( aTemplate, mProfileModel, aTree );
            mTemplatePanels.put( name, panel );
        }
        
        return panel;
    }
    
    public HashMap getTemplatePanels(){
        return mTemplatePanels;
    }
    
    public void clearTemplatePanels(){
        mTemplatePanels.clear();
    }
    
    public DefaultMutableTreeNode getRootTreeNode(){
        return rootNode;
    }
    
    
    public Hashtable getPackageHash(){
        return packageHash;
    }

    public boolean isOpen(){
        return mPackageOpen;
    }

    
    public String toString(){
        String result = "";
                
        Iterator it = packageList.iterator();
        while( it.hasNext() ){
            result += ((Package)it.next()).toString();
        }
        return result;
    }
    
    
    private void showProgressDialog(int length){
        int WIDTH = 300;
        int HEIGHT = 150;
        
        mProgressFrame = new JFrame("Loading Templates...");
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder( BorderFactory.createEmptyBorder( 30, 30, 30, 30) );
        
        mProgressFrame.setContentPane( contentPanel );
        mProgressFrame.setAlwaysOnTop(true);
        mProgressBar = new JProgressBar( 0, length);
        mProgressBar.setValue(0);
        mProgressBar.setStringPainted(true);
        
        contentPanel.add( new JLabel( "Please wait..." ), BorderLayout.PAGE_START );
        contentPanel.add( mProgressBar, BorderLayout.CENTER );

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        
        int x = center.x - WIDTH/2, y = center.y - HEIGHT/2;
        mProgressFrame.setBounds(x, y, WIDTH, HEIGHT);
        if (WIDTH == bounds.width && HEIGHT == bounds.height){
            mProgressFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        mProgressFrame.validate();
        
        mProgressFrame.setVisible(true);
        mProgressFrame.pack();
    }
    
    
    private void closeProgressDialog(){
            
        if( mProgressFrame != null ){
            mProgressFrame.setVisible(false);
            mProgressFrame.dispose();
            mProgressBar = null;
            mProgressFrame = null;   
        }
    }
}