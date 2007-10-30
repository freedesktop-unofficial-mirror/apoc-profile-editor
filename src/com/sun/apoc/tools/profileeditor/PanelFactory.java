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

import com.sun.apoc.tools.profileeditor.gui.OnlineHelpFrame;
import com.sun.apoc.tools.profileeditor.gui.PropertyJLabel;
import com.sun.apoc.tools.profileeditor.gui.SectionPanel;
import com.sun.apoc.tools.profileeditor.gui.SetPanel;
import com.sun.apoc.tools.profileeditor.gui.TitledSectionJPanel;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.apoc.tools.profileeditor.packages.Template;
import com.sun.apoc.tools.profileeditor.templates.TemplateCategory;
import com.sun.apoc.tools.profileeditor.templates.TemplatePage;
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import com.sun.apoc.tools.profileeditor.templates.TemplateSection;
import com.sun.apoc.tools.profileeditor.templates.TemplateSet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;

public class PanelFactory {
    private static final boolean DEBUG = false;
    private final static Dimension SIZE = new Dimension(300,35);
    
    
    public static JPanel getTemplatePanel(Template template, StandaloneProfileManager aProfileModel, JTree aTree){
        LocaleManager aLocaleManager = template.getLocaleManager();
        String aPackageName = template.getPackageName();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS) );
        
        //  Lock the listModel so it doesnt keep calling
        //  repaints of the summary view
        aProfileModel.getListModel().setLocked(true);
        addSections( template, template.getRootCategory(), panel, aProfileModel, aLocaleManager, aPackageName, aTree );
        aProfileModel.getListModel().setLocked(false);
        aProfileModel.getListModel().notifyListeners();
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        JPanel spacerPanel = new JPanel();
        spacerPanel.setBackground( Color.WHITE );
        mainPanel.add( panel, BorderLayout.PAGE_START );
        mainPanel.add(spacerPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    
    private static void addSections(Template template, TemplateCategory category, JPanel mainPanel, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager,String aPackageName, JTree aTree){
        
        if( category.hasSubCategories() ){
            String[] keys = category.getKeys();

            for(int i = 0; i < keys.length; i++){
                addSections( template, category.getSubCategory( keys[i] ), mainPanel, aProfileModel, aLocaleManager, aPackageName, aTree );
            }
        }else{
            List sectionList = ((TemplatePage)category).getSections();
            mainPanel.add( getInlineHelp(template, (TemplatePage)category, aLocaleManager) );
            Iterator it = sectionList.iterator();
            while( it.hasNext() )
            {
                Object section = it.next();
                if( section instanceof TemplateSet ){
                    mainPanel.add( getSetPanel(template, section, aProfileModel, aLocaleManager, aPackageName, aTree ) );
                }else if( section instanceof TemplateSection ){
                    mainPanel.add( getSectionPanel(template, section, aProfileModel, aLocaleManager, aPackageName) );
                }
            }
        }
    }
    
    private static JPanel getSectionPanel(Template template, Object section, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager,String aPackageName){
        
        TitledSectionJPanel sectionPanel = new TitledSectionJPanel();
        sectionPanel.setTitleLabel( (TemplateSection)section, aPackageName, aLocaleManager);
        
        JPanel panel = new SectionPanel( template, (TemplateSection)section, aProfileModel, aLocaleManager, aPackageName );
        sectionPanel.setContentPanel( panel, true );
        return sectionPanel;
    }
    
    private static JPanel getSetPanel(Template template, Object section, StandaloneProfileManager aProfileModel, 
            LocaleManager aLocaleManager,String aPackageName, JTree aTree){
        
        TitledSectionJPanel sectionPanel = new TitledSectionJPanel();
        sectionPanel.setTitleLabel((TemplateSet)section, aPackageName, aLocaleManager);
        
        JPanel panel = new SetPanel(template, (TemplateSet)section, aProfileModel, aLocaleManager, aPackageName, aTree );
        sectionPanel.setContentPanel( panel, true );
        return sectionPanel;
    }
    
    private static JPanel getInlineHelp(Template template, TemplatePage page, LocaleManager aLocaleManager){
        File file = template.getTemplateFile();
        JButton button = null;
        String helpPath = file.toString();
        helpPath = helpPath.substring(0, helpPath.indexOf("templates/"));
        helpPath = helpPath + "web" + page.getHelpFile();

        final String path = helpPath;
        final LocaleManager locale = aLocaleManager;
        
        button = new JButton("Online Help");
        button.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OnlineHelpFrame(locale, path);
            }
        });

        
        TitledSectionJPanel helpPanel = new TitledSectionJPanel();
        helpPanel.setTitleLabel(page.getResourceId(), page.getDefaultName(), aLocaleManager);
        PropertyJLabel inlineHelp = new PropertyJLabel();

        inlineHelp.setResourceId( page.getDescriptionId());
        inlineHelp.setDefaultName( page.getDescriptionId());
        inlineHelp.setLocaleManager(aLocaleManager);
        inlineHelp.update();
        
        JLabel label = new JLabel("InLine Help: ");
        label.setMinimumSize(SIZE);
        label.setPreferredSize(SIZE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setOpaque(false);
        
        contentPanel.add(label);
        contentPanel.add(button);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.add(contentPanel, BorderLayout.LINE_START);
        panel.add(inlineHelp, BorderLayout.CENTER);
        helpPanel.setContentPanel(panel, true);
        
        return helpPanel;
    }

    private static void printProperties(TemplateSection section)
    {
        List list = section.getProperties();
        Iterator it = list.iterator();
        while( it.hasNext() ){
            TemplateProperty property = (TemplateProperty)it.next();
            
            debug( "\t\tPROPERTY: " + property.getDefaultName() );
            debug( "\t\t\tChoserPath: " + property.getChooserPath() );
            debug( "\t\t\tConstraints: " + property.getConstraints() );
            debug( "\t\t\tDataPath: " + property.getDataPath() );
            debug( "\t\t\tDataType: " + property.getDataType() );
            debug( "\t\t\tDefaultValue: " + property.getDefaultValue() );
            debug( "\t\t\tDescriptionId: " + property.getDescriptionId() );
            debug( "\t\t\tExtendsChooser: " + property.getExtendsChooser() );
            debug( "\t\t\tExtraData: " + property.getExtraData() );
            debug( "\t\t\tExtraHtml: " + property.getExtraHtml() );
            debug( "\t\t\tLabelPopup: " + property.getLabelPopup() );
            debug( "\t\t\tLabelPost: " + property.getLabelPost() );
            debug( "\t\t\tResourceBundle: " + property.getResourceBundle() );
            debug( "\t\t\tResourceId: " + property.getResourceId() );        
            debug( "\t\t\tSeperator: " + property.getSeparator() );
            debug( "\t\t\tVisualType: " + property.getVisualType() );
            debug( "\t\t\tXmlHandler: " + property.getXmlHandler() );
        }
    }
    
    private static void debug(String msg){
        if(DEBUG){
            System.out.println(msg);
        }
    }

}//end Class
