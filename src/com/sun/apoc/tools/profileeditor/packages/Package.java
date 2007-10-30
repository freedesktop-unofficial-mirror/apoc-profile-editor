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
import com.sun.apoc.tools.profileeditor.templates.TemplateCategory;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.xml.sax.SAXParseException;

/**
 *
 * @author cs202741
 */
public class Package implements Comparable{
    
    private String name;
    private File packageDir, packageRoot;
    private List templateList, templateDirs;
    private LocaleManager mLocaleManager = null;
    private String resourceJar = null;
    
    /** Creates a new instance of Package */
    public Package(String name, File packageDir, File packageRoot, LocaleManager aLocaleManager, ArrayList aPropertyList) {
        this.name = name;
        this.packageDir = packageDir;
        this.packageRoot = packageRoot;
        mLocaleManager = aLocaleManager;

        templateList = new ArrayList();
        templateDirs = getTemplateDirs( packageDir );
        resourceJar = getResourceJar();
        mLocaleManager.addPackageLocale( name, resourceJar );
        
        Iterator it = templateDirs.iterator();
        
        while( it.hasNext() ){
            recurseFindTemplates( ((File)it.next()).toString(), aPropertyList );
        }
    }
    
    
    private String recurseFindTemplates(String dirItem, ArrayList aPropertyList) {
        File file;
        String list[], result;

        result = dirItem;
        
        file = new File(dirItem);
        if (file.isDirectory()) {
          list = file.list();
          for (int i = 0; i < list.length; i++)
            result = result + "\n" + recurseFindTemplates(dirItem + File.separatorChar + list[i], aPropertyList);
        }else{
            Template template;
            try {
                String ext = file.getName().toLowerCase();
                int r = ext.lastIndexOf(".xml");
                if( r > 0){
                    template = new Template(file,  name, packageRoot, resourceJar, mLocaleManager, aPropertyList);
                    templateList.add( template  );
                }
            } catch (SAXParseException ex) {
                System.err.println( ex.getMessage() );
            }
        }
        return result;
    }
    
    public List getTemplateDirs(File dir){
        
        File tempDir = new File( dir.getAbsolutePath() + 
                File.separator + 
                "templates" );
        
        File[] tmp = tempDir.listFiles();
        
        List list = new ArrayList();
        
        for( int i = 0; i < tmp.length; i++){
            if( tmp[i].isDirectory() ){
                list.add( tmp[i] );
            }
        }
        
        return list;
    }
    
    private String getResourceJar(){
        File dir = new File( packageDir.toString() + "/lib" );
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.toString().endsWith(".jar");
            }
        };
        File[] files = dir.listFiles(fileFilter);
        
        if( files != null ){
            return files[0].toString();        
        }else
            return null;
    }
    
    public String getName(){
        return name;
    }
    
    public List getTemplates(){
        return templateList;
    }
    
    public String toString(){
        String result = "Package: " + name;
        
        Iterator it = templateList.iterator();
        while( it.hasNext() ){
            result += ((Template)it.next()).toString();
        }
        
        return result;
    }

    public int compareTo(Object o) {
        return name.compareTo(o.toString());
    }
}