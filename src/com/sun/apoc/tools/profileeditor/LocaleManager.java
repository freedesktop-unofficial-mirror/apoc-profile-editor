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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author cs202741
 */
public class LocaleManager implements LocaleNotifier, PreferenceSubscriber {
    private String mLocale = "en";
    private HashMap mPackageLocalePaths = null;
    private HashMap mPackageLocales = null;
    private ProfileEditorPreferences mPreferences = null;
    private ArrayList<Subscriber> mSubscribers = null;
    
    private Properties mAllResources = null;
    
    private boolean displayResourceId = false;
    
    /** Creates a new instance of LocaleManager */
    public LocaleManager() {
        mPackageLocalePaths = new HashMap();
        mPackageLocales = new HashMap();
        mSubscribers = new ArrayList();
        mAllResources = new Properties();
    }
    
    public String getLocalizedName(String resourceId, String defaultName){
        
        if( resourceId == null ){
            return defaultName;
        }
        
        String result = mAllResources.getProperty(resourceId);
        
        if( result == null ){
            if( displayResourceId || defaultName == null ){
                return resourceId;
            }else{
                return defaultName;
            }
        }
        
        return result;
    }
    
    public String getLocalizedName(String packageName, String resourceId, String defaultName){
        
        if( packageName == null || resourceId == null ){
            return null;
        } 
        
        Properties props = (Properties)mPackageLocales.get(packageName);
        
        String name = null;
        if( props != null ){
            name = props.getProperty( resourceId );
        }
        
        if( name == null ){
            if( defaultName == null ){
                return null;
            }
            if( displayResourceId ){
                return resourceId;
            }
            else{
                return defaultName;
            }
        }else
            return name;
    }
    

    
    public void addPackageLocale(String packageName, String localePath){
        
        mPackageLocalePaths.put( packageName, localePath );
        mPackageLocales.put( packageName, getLocaleProperties( localePath, mLocale ) );

    }

    
    public void updateLocales(){
        Set s = mPackageLocalePaths.keySet();
        Iterator it = s.iterator();

        while( it.hasNext() ){
            String packageName = (String)it.next();
            String localePath = (String)mPackageLocalePaths.get( packageName );
            mPackageLocales.put( packageName, getLocaleProperties( localePath, mLocale ) );
        }
        notifySubscribers();
    }
    
    public void setLocale(String aLocale){
        mLocale = aLocale;
        updateLocales();
    }
    
    public String getCurrentLocale(){
        return mLocale;
    }
    
    public void setPreferences(ProfileEditorPreferences prefs){
        mPreferences = prefs;
        mPreferences.attach( this );
        update();
    }
    
    public Properties getLocaleProperties(String path, String locale){
        Properties props = null;
        if( path != null ){
            try {
                ZipFile zf = new ZipFile(path);
                for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry)entries.nextElement();
                    String zipEntryName = entry.getName();

                    if( zipEntryName.endsWith("_" + locale + ".properties") ){
                        //System.out.println("Loading props for: " + locale);
                        props = new Properties();
                        props.load( zf.getInputStream(entry) );
                        mAllResources.load( zf.getInputStream(entry) );
                        break;
                    }
                }
                zf.close();
            } catch (IOException ex) {
                System.err.println("Cannot open: " + path);
                ex.printStackTrace();
            } 
        }
        return props;
    }

    public void attach(Subscriber sub) {
        mSubscribers.add(sub);
    }

    public void detach(Subscriber sub) {
        mSubscribers.remove(sub);
    }

    public void notifySubscribers() {
        Iterator it = mSubscribers.iterator();
        while( it.hasNext() ){
            ((Subscriber)it.next()).update();
        }
    }

    public void update() {
        displayResourceId = Boolean.valueOf( mPreferences.getProperty("displayResourceIds") );
        notifySubscribers();
    }
    
}
