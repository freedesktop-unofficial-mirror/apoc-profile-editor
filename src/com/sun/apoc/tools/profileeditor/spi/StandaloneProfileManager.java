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
package com.sun.apoc.tools.profileeditor.spi;

import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.DataType;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.cfgtree.property.ReadWritePropertyImpl;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.apoc.tools.profileeditor.LocaleManager;
import com.sun.apoc.tools.profileeditor.PropertyComponentDummy;
import com.sun.apoc.tools.profileeditor.gui.PropertyJListModel;
import com.sun.apoc.tools.profileeditor.gui.PropertyComponent;
import com.sun.apoc.tools.profileeditor.packages.PackageManager;
import com.sun.apoc.tools.profileeditor.packages.Template;
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JTree;

/**
 *
 * @author cs202741
 */
public class StandaloneProfileManager {
    private StandaloneProfileProviderImpl mProfileProvider = null;
    private StandalonePolicyManagerHelper mManagerHelper = null;
    private PropertyJListModel mListModel = null;
    private StandaloneProfile mProfile = null;
    
    private File mProfileFile = null;
    
    
    /** Creates a new instance of StandaloneProfileModel */
    public StandaloneProfileManager(PropertyJListModel listModel) {
        mListModel = listModel;
    }
    
    public void createProfile(String aName, String aUrl) throws SPIException{

        mProfileProvider = new StandaloneProfileProviderImpl( aUrl );
        mProfileProvider.open();

        ProfileRepository repos = mProfileProvider.getProfileRepository( aName );
        mProfile = (StandaloneProfile)((StandaloneProfileRepositoryImpl)repos).createTheProfile( aName, Applicability.USER, 1);

        StandalonePolicyManager mgr = new StandalonePolicyManager(mProfileProvider);

        if( mProfile != null){
            mManagerHelper = new StandalonePolicyManagerHelper(mgr, mProfile);
            mProfileFile = new File( aUrl + File.separator + aName );
        }
    }
    
    
    public void openProfile(String aName, String aUrl, PackageManager aPackageManager, 
            LocaleManager aLocaleManager, JTree aTree) throws SPIException{

        mProfileProvider = new StandaloneProfileProviderImpl( aUrl );

        ProfileRepository repos = mProfileProvider.getProfileRepository( aName );
        mProfile = (StandaloneProfile)((StandaloneProfileRepositoryImpl)repos).getProfile(aName);

        StandalonePolicyManager mgr = new StandalonePolicyManager(mProfileProvider);

        if( mProfile != null){
            mManagerHelper = new StandalonePolicyManagerHelper(mgr, mProfile);
            
            HashMap aProperties = mManagerHelper.getAllDefinedProperties(mProfile);
            
            loadProperties(aProperties, aPackageManager, aLocaleManager, aTree);
            
            mProfileFile = new File( aUrl + File.separator + aName );
        }
    }
    
    
    private void loadProperties(HashMap aProperties, PackageManager aPackageManager, 
            LocaleManager aLocaleManager, JTree aTree) throws SPIException{

        if( aProperties != null ){
            ArrayList list = new ArrayList();
            HashMap templateHash = new HashMap();
            
            Set set = aProperties.keySet();
            Iterator it = set.iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                TemplateProperty property = aPackageManager.lookupProperty( key );
                
                //  Chop of the last bit of path and check again,
                //  maybe its a 'Set'.
                String setName = "";
                Template template = null;
                if( property == null ){
                    String propName = key.substring( key.lastIndexOf("/") );
                    String path = "./$queriedId" + propName;
                    property = aPackageManager.lookupProperty( path );
                    if(property != null ){
                        setName = key.substring(0, key.length()-propName.length());
                        setName = setName.substring( setName.lastIndexOf("/") + 1 );
                        
                        template = aPackageManager.getTemplateByProperty(path);
                        if( template != null ){
                            templateHash.put( template.getName(), template );
                        }
                    }
                }
                
                if( property != null ){
                    ReadWritePropertyImpl prop = (ReadWritePropertyImpl)aProperties.get(key);
                    PropertyComponentDummy propComp = new PropertyComponentDummy( key, prop);
                    propComp.cloneTemplateProperty( property );
                    propComp.setPath( setName + ";" + property.getPath() );
                    propComp.setResourceIdPath( setName + ";" + property.getResourceIdPath() );
                    propComp.setDataPath( key );
                    propComp.setProfileModel( this );
                    propComp.setLocaleManager( aLocaleManager );
                    propComp.setTemplateProperty( property );
                    list.add( propComp );
                    
                    template = aPackageManager.getTemplateByProperty(key);
                    if( template != null ){
                        templateHash.put( template.getName(), template );
                    }
                    
                    deleteExistingProperty(key);
                    
                    System.out.println("Loading Property: " + key);
                }else{
                    System.out.println("Could not load Property: " + key + ", is its package loaded?");
                }
            }
            if( list.size() > 0 ){
                mListModel.addAll( list );
            }
            //
            //  Force a redraw of 
            Collection c = templateHash.values();
            Iterator tempIt = c.iterator();
            while( tempIt.hasNext() ){
                aPackageManager.getTemplatePanel( (Template)tempIt.next(), "en", aTree);
            }
        }// if
    }
    
    
    private void deleteExistingProperty(String dataPath){
        PropertyComponent propComp = mListModel.existsByDataPath(dataPath);
        if( propComp != null ){
            System.out.println("Deleting Existing Property: " + dataPath);
            mListModel.removeItem( propComp );
        }
    }
    
    
    public void setDisplayName(String name){
        if( mProfile != null ){
            try {
                mProfile.setDisplayName(name);
            } catch (SPIException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public String getDisplayName(){
        if( mProfile != null ){
            return mProfile.getDisplayName();
        }else{
            return "";
        }
    }
    
    
    public void setComment(String comment){
        if( mProfile != null ){
            try {
                mProfile.setComment(comment);
            } catch (SPIException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    public String getComment(){
        String comment = "";
        if( mProfile != null ){
            try {
                comment = mProfile.getComment();
            } catch (SPIException ex) {
                System.err.println("Could not get comment!");
                comment = "";
            }
        }else {
            comment = "";
        }
        
        return comment;
    }
    
    
    public void setAuthor(String author){
        if(mProfile != null ){
            try {
                mProfile.setAuthor(author);
            } catch (SPIException ex) {
                System.err.println("Could not set Author to '" + author + "'!");
            }
        }
    }
    
    
    public String getAuthor(){
        String author = "";
        if(mProfile != null){
            try {
                author = mProfile.getAuthor();
            } catch (SPIException ex) {
                System.err.println("Could not get author!");
            }
        }
        
        return author;
    }
    
    
    public void setApplicability(Applicability applicability){
        if(mProfile != null){
            try {
                mProfile.setApplicability(applicability);
            } catch (SPIException ex) {
                System.err.println("Could not set Applicability!");
            }
        }
    }
    
    
    public Applicability getApplicability(){
        Applicability applicability = Applicability.USER;
        
        if(mProfile != null){
            applicability = mProfile.getApplicability();
        }
        
        return applicability;
    }
    
    
    public void setPriority(int priority){
        if(mProfile != null){
            try {
                mProfile.setPriority(priority);
            } catch (SPIException ex) {
                System.err.println("Could not set Priority!");
            }
        }
    }
    
    
    public int getPriority(){
        int priority = 1;
        if(mProfile != null){
            priority = mProfile.getPriority();
        }
        
        return priority;
    }
    
    
    public void destroyProfile() throws SPIException{
        if( mManagerHelper != null ){
            mManagerHelper.resetAllChanges();
            mManagerHelper = null;
        }
        mProfileProvider = null;
        mProfile = null;
        mProfileFile = null;
    }
    
    
    public boolean exists(){
        return mManagerHelper != null;
    }
    
    public File getProfileFile(){
        return mProfileFile;
    }
    
    
    public void createProperty(PropertyComponent aComponent){
        
        if( mManagerHelper != null ){
            try {
                String path = aComponent.getDataPath();
                
                Property prop = null;
                if(aComponent.isSetProperty()){
                    prop = mManagerHelper.createReplaceProperty(path);
                }else{
                    prop = mManagerHelper.createProperty(path);
                }
                

                DataType dataType = DataType.getDataType( aComponent.getDataType() );
                String oldValue = aComponent.getValue();
                String newValue = oldValue;

                if( dataType == DataType.BOOLEAN && oldValue.startsWith( "Enabled" ) ){
                    newValue = "true";
                }else if ( dataType == DataType.BOOLEAN && oldValue.startsWith( "Disabled" ) ){
                    newValue = "false";
                }else if( dataType == DataType.STRING_LIST ){
                    prop.setSeparator( aComponent.getSeperator() );
                }

                prop.put( newValue, dataType );
                System.out.println("SAVING PROPERTY: " + aComponent.getDataPath() );
            } catch (SPIException ex) {
                ex.printStackTrace();
            }
        }
        
    }//end createProperty()
    
    public void removeProperty(PropertyComponent aComponent){
        if( mManagerHelper != null ){
            try {
                mManagerHelper.removeProperty( aComponent.getDataPath() );
            } catch (SPIException ex) {
                ex.printStackTrace();
            }
        }
    }
    

    
    public void writeChanges(){
        if(mManagerHelper != null ){
            try {
                mManagerHelper.flushAllChanges();
            } catch (SPIException ex) {
                ex.printStackTrace();
            }
        }
    }
    

    
    public PropertyJListModel getListModel(){
        return mListModel;
    }
}
