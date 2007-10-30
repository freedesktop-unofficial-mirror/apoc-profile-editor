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

import com.sun.apoc.tools.profileeditor.ComponentFactory;
import com.sun.apoc.tools.profileeditor.LocaleManager;
import com.sun.apoc.tools.profileeditor.packages.Template;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.apoc.tools.profileeditor.templates.TemplatePage;
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import com.sun.apoc.tools.profileeditor.templates.TemplateSection;
import com.sun.apoc.tools.profileeditor.templates.TemplateSet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author cs202741
 */
public class SetSubPanel extends TitledSectionJPanel {
    
    private String mSetName = null;
    private ArrayList mPropertyList = null;
    
    /** Creates a new instance of SetPanel */
    public SetSubPanel() {
        super();
    }
    
    public SetSubPanel(Template template, TemplateSet aSet, String aSetName, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager, String aPackageName){
        super();
        this.setTitleLabel(aSet, aPackageName, aLocaleManager, "  ( " + aSetName + " )");
        mSetName = aSetName;
        
        mPropertyList = new ArrayList();
        
        JPanel contentPanel = new JPanel( new GridBagLayout() );
        contentPanel.setOpaque(false);
        addContents(contentPanel, template, aSet, aSetName, aProfileModel, aLocaleManager, aPackageName);
        this.setContentPanel( contentPanel, true);
    }
    
    
    private void addContents(JPanel contentPanel, Template template, TemplateSet aSet, String aSetName, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager, String aPackageName){
        
        HashMap choosers = new HashMap();
        int row = 0;
        
        TemplatePage page = aSet.getPage();
        
        List sectionList = page.getSections();
        Iterator sectionIt = sectionList.iterator();
        // Loop through Sections in Page
        while( sectionIt.hasNext() ){
            TemplateSection section = (TemplateSection)sectionIt.next();

            // Loop through Properties in Section
            List propertyList = section.getProperties();
            Iterator propertyIt = propertyList.iterator();
            while( propertyIt.hasNext() ){
                TemplateProperty property = (TemplateProperty)propertyIt.next();

                JLabel label = new PropertyJLabel();
                ((PropertyJLabel)label).setLocaleManager( aLocaleManager );
                ((PropertyJLabel)label).setResourceId( property.getResourceId() );
                ((PropertyJLabel)label).setPackageName( aPackageName );
                ((PropertyJLabel)label).update();

                addWithGridBag( label, contentPanel,
                                0, row, 1, 1,
                                GridBagConstraints.EAST,
                                GridBagConstraints.NONE, 0, 1);

                JComponent comp = ComponentFactory.getComponent( property, aProfileModel, aLocaleManager, aPackageName );

                if( comp instanceof PropertyComponent ){
                    ((PropertyComponent)comp).setSectionName( section.getDefaultName() );
                    String dataPath = ((PropertyComponent)comp).getDataPath();
                    dataPath = aSet.getDataPath() + "/" + aSetName + dataPath.substring( dataPath.lastIndexOf("/") );
                    ((PropertyComponent)comp).setPath( aSetName+";"+section.getPath() );
                    ((PropertyComponent)comp).setResourceIdPath( aSetName+";"+section.getResourceIdPath()  );
                    ((PropertyComponent)comp).setDataPath( dataPath );
                    ((PropertyComponent)comp).setDefaultName( property.getDefaultName() );
                    ((PropertyComponent)comp).setResourceId( property.getResourceId() );
                    ((PropertyComponent)comp).setIsSetProperty(true);
                    ((PropertyComponent)comp).checkForLoadedProperty();   
                }

                mPropertyList.add(comp);

                addWithGridBag( comp, contentPanel,
                                1, row++, 2, 1,
                                GridBagConstraints.LINE_START,
                                GridBagConstraints.NONE, 0, 1);
            }//while
        }//while
    }
    
    
    public void deleteProperties(){
        Iterator it = mPropertyList.iterator();
        while(it.hasNext()){
            ((PropertyComponent)it.next()).reset();
        }
    }

    
    private void addWithGridBag(Component comp, Container cont,
                                int x, int y,
                                int width, int height,
                                int anchor, int fill,
                                int weightx, int weighty){
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = new Insets(5,5,5,5);
        cont.add(comp, gbc);
    }
       
    public String toString(){
        return mSetName;
    }

}
