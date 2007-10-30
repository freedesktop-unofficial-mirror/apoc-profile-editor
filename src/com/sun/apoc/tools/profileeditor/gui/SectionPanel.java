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
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import com.sun.apoc.tools.profileeditor.templates.TemplateSection;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author cs202741
 */
public class SectionPanel extends JPanel {
    final Dimension SIZE = new Dimension(300,30);

    public SectionPanel(Template template, TemplateSection section, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager, String aPackageName) {
        
        this.setLayout( new GridBagLayout() );
        
        addContents( this, template, section, aProfileModel, aLocaleManager, aPackageName);
    }
    
    
    private void addContents(JPanel contentPanel, Template template, TemplateSection section, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager, String aPackageName){

        HashMap choosers = new HashMap();
        
        int row = 0;
        
        List propertyList = section.getProperties();
        Iterator it = propertyList.iterator();
        while( it.hasNext() )
        {
            TemplateProperty property = (TemplateProperty)it.next();

            JLabel label = new PropertyJLabel();
            
            ((PropertyJLabel)label).setLocaleManager( aLocaleManager );
            ((PropertyJLabel)label).setResourceId( property.getResourceId() );
            ((PropertyJLabel)label).setPackageName( aPackageName );
            ((PropertyJLabel)label).setDefaultName( property.getDefaultName() );
            ((PropertyJLabel)label).update();
            
            JPanel labelPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
            labelPanel.setOpaque(false);
            labelPanel.setMinimumSize( SIZE );
            labelPanel.setPreferredSize( SIZE );
            labelPanel.add( label );
            
            //contentPanel.add(labelPanel, BorderLayout.LINE_START);
            addWithGridBag( labelPanel, contentPanel,
                            0, row, 1, 1,
                            GridBagConstraints.LINE_START,
                            GridBagConstraints.NONE, 0, 1);    
            
            JComponent comp = ComponentFactory.getComponent( property, aProfileModel, aLocaleManager, aPackageName );
            if( comp instanceof PropertyComponent ){
                ((PropertyComponent)comp).setResourceIdPath( section.getResourceId() + ";" + template.getResourceIdPath() );
                ((PropertyComponent)comp).setPath( section.getDefaultName() + ";" + template.getPath() );
                ((PropertyComponent)comp).setDefaultName( property.getDefaultName() );
                ((PropertyComponent)comp).setResourceId( property.getResourceId() );
//                
//                if( property.getVisualType().equals("chooser") ){
//                    String chooserPath = getChooserPath( property.getPath() ) + property.getDefaultName();
//                    choosers.put( chooserPath, property.getConstraints() );
//                    System.out.println("Adding Chooser: " + chooserPath);
//                    if( !property.getExtendsChooser().equals("") ){
//                        
//                        System.out.println("Property has Extends Chooser: " + property.getExtendsChooser() );
//                        Vector v = (Vector)choosers.get( property.getExtendsChooser() );
//                        if( v != null ){
//                            System.out.println("Found chooser in hash");
//                            String value = ((PropertyComponent)comp).getValue();
//                            System.out.println("Current Value:" + value);
////                            String sep = ((PropertyComponent)comp).getSeperator();
////                            if( sep.equals("") ){
////                                sep = " ";
////                            }
//                        }
//                    }
//                }
            }
            
            JPanel componentPanel = new JPanel( new FlowLayout(FlowLayout.LEFT) );
            componentPanel.setOpaque(false);
            componentPanel.add( comp );
            
            //contentPanel.add(componentPanel, BorderLayout.LINE_END);
            
            addWithGridBag( componentPanel, contentPanel,
                            1, row++, 2, 1,
                            GridBagConstraints.LINE_START,
                            GridBagConstraints.NONE, 0, 1);

        }//end while
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
    
    
    private String getChooserPath(String path){
        
        String[] tokens = path.split(";");
        String newPath = "/";
        for( int i = tokens.length-1; i >= 0; i--){
            newPath += tokens[i] + "/";
        }
        
        newPath = newPath.substring( newPath.indexOf("/", 1) );
        System.out.println("NewPath: " + newPath);
        return newPath;
    }
    
}
