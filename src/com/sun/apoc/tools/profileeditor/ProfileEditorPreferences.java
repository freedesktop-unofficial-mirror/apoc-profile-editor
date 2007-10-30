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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * @author cs202741
 */
public class ProfileEditorPreferences extends Properties implements PreferenceNotifier{
    private final String PREFERENCE_FILE = "preferences.cfg";
    private final String PREFERENCE_DIR = ".profileeditor";
    private final String DEFAULT_LOCALES = "de,en,en_US,es,fr,ja,ko,zh_CN,zh_TW";
    
    private FileInputStream in = null;
    private FileOutputStream out = null;
    
    private ArrayList<PreferenceSubscriber> mSubscribers = null;
    
    /** Creates a new instance of ProfileEditorPreferences */
    public ProfileEditorPreferences() {
        super();
        mSubscribers = new ArrayList();
        verifyDefaults();
    }
    
    public void load() throws IOException {
        String prefFile = System.getProperty("user.home") + File.separator + PREFERENCE_DIR + File.separator + PREFERENCE_FILE;
        
        File file = new File(prefFile);
        if( file.exists() ){
            in = new FileInputStream( prefFile );
            super.load( in );
            in.close();
            verifyDefaults();
            notifySubscribers();
        }

    }
    
    public void store() throws IOException {
        String prefDir = System.getProperty("user.home") + File.separator + PREFERENCE_DIR;
        
        File dir = new File(prefDir);
        if( !dir.exists() ){
            if( !dir.mkdirs() ){
                System.err.println("Could not create directory: " + prefDir + "!");
                prefDir = PREFERENCE_FILE;
            }
        }

        if(dir.canWrite()){
            out = new FileOutputStream( prefDir + File.separator + PREFERENCE_FILE );
            super.store( out, "ProfileEditor Default Preferences");
            out.close();
            notifySubscribers();
        }else{
            System.err.println("Cannot write to: " + dir.toString());
        }
    }
    
    private void verifyDefaults(){
        
        if( this.getProperty( "defaultLocales" ) == null ){
            this.setProperty("defaultLocales", DEFAULT_LOCALES);
        }
    }

    public void attach(PreferenceSubscriber sub) {
        mSubscribers.add( sub );
    }

    public void detach(PreferenceSubscriber sub) {
        mSubscribers.remove( sub );
    }

    public void notifySubscribers() {
        Iterator it = mSubscribers.iterator();
        while( it.hasNext() ){
            ((PreferenceSubscriber)it.next()).update();
        }
    }
    
}
