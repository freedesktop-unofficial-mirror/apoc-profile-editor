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
import com.sun.apoc.tools.profileeditor.gui.SetPanel;
import com.sun.apoc.tools.profileeditor.templates.TemplateCategory;
import com.sun.apoc.tools.profileeditor.templates.TemplatePage;
import com.sun.apoc.tools.profileeditor.templates.TemplateParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import org.xml.sax.SAXParseException;

/**
 *
 * @author cs202741
 */
public class Template {
    
    private String mTemplateName, mPackageName;
    private File templateFile, packageRoot;
    private TemplateCategory templateRoot;

    private DefaultMutableTreeNode mTreeNode = null;
    private String mDataPath = null;
    private String resourceJar = null;
    private String mResourceIdPath = null;
    private String mPath = null;
    //private String mTemplatePath = null;
    private LocaleManager mLocaleManager = null;
    private HashMap mSetPropertyList = null;
    private ArrayList<SetPanel> mSetPanels = null;
    
    /** Creates a new instance of Template */
    public Template(File templateFile, String aPackageName, File packageRoot, String aResourceJar, 
            LocaleManager aLocaleManager, ArrayList aPropertyList) throws SAXParseException {
        this.templateFile = templateFile;
        this.packageRoot = packageRoot;
        this.resourceJar = aResourceJar;
        mLocaleManager = aLocaleManager;
        mPackageName = aPackageName;
        mSetPanels = new ArrayList();
        
        TemplateParser parser = new TemplateParser( templateFile, packageRoot, aPropertyList );
        templateRoot = parser.getRootCategory();
        mSetPropertyList = parser.getSetList();
        parser = null;

        String[] keys = templateRoot.getKeys();
        
        templateRoot = templateRoot.getSubCategory(keys[0]);
        
        mTemplateName = templateRoot.getDefaultName();
        
        String pkg = packageRoot.getPath();
        String path = templateFile.getPath();
    }

    
    public LocaleManager getLocaleManager(){
        return mLocaleManager;
    }
    
    public TemplateCategory getRootCategory(){
        return (templateRoot != null) ? templateRoot : null;
    }
    
    public String getPackageName(){
        return mPackageName;
    }

    
    public void setName(String aResourceId, String aDefaultName){
        String localeName = mLocaleManager.getLocalizedName( mPackageName, aResourceId, aDefaultName );
        if( localeName == null ){
            localeName = aResourceId;
        }
        
        this.mTemplateName = localeName;
    }
    
    public String getName(){
        return mTemplateName;
    }
    
    
    public void setPath(String path){
        mPath = path;
    }
    
    
    public String getPath(){
        return mPath;
    }
    
    public void setResourceIdPath(String path){
        mResourceIdPath = path;
    }
    
    public String getResourceIdPath(){
        return mResourceIdPath;
    }
    
    public void setDataPath(String aDataPath){
        mDataPath = aDataPath.substring( 0, aDataPath.lastIndexOf("/") );
    }
    
    public String getDataPath(){
        return mDataPath;
    }
    
    public void setTreeNode(DefaultMutableTreeNode node){
        mTreeNode = node;
    }
    
    public DefaultMutableTreeNode getTreeNode(){
        return mTreeNode;
    }
    
    public File getTemplateFile(){
        return templateFile;
    }
    
    public File getPackageRoot(){
        return packageRoot;
    }
    
    public String getResourceJar(){
        return resourceJar;
    }
    
    public boolean containsSet(String dataPath){
        if( mSetPropertyList.get(dataPath) != null ){
            return true;
        }else{
            return false;
        }
    }
    
    public void addSetPanel(SetPanel panel){
        mSetPanels.add( panel );
    }
    
    public void removeSets(){
        Iterator it = mSetPanels.iterator();
        while(it.hasNext()){
            ((SetPanel)it.next()).removeAllSets();
        }
    }
    
    public String toString(){
        return mTemplateName;
    }
    
}
