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

import com.sun.apoc.spi.cfgtree.DataType;
import com.sun.apoc.tools.profileeditor.LocaleManager;
import com.sun.apoc.tools.profileeditor.PropertyComponentDummy;
import com.sun.apoc.tools.profileeditor.gui.TitledSectionJPanel;
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import com.sun.apoc.tools.profileeditor.templates.TemplatePropertyConstraint;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author cs202741
 */
public class SummaryJPanel extends JPanel implements ListDataListener{
    private JPanel mainPanel = null;
    private LocaleManager mLocaleManager = null;
    
    /** Creates a new instance of SummaryJPanel */
    public SummaryJPanel(LocaleManager aLocaleManager) {
        super(new BorderLayout());
        this.setBackground(Color.WHITE);
        
        mLocaleManager = aLocaleManager;
        TitledSectionJPanel sectionPanel = new TitledSectionJPanel( new JLabel("Settings Summary") );
        mainPanel = new JPanel(new BorderLayout());
        sectionPanel.setContentPanel(mainPanel, false);
        this.add( sectionPanel, BorderLayout.PAGE_START );
    }
    
    private void buildPanel(Enumeration e){
        
        Map packageHash = new TreeMap();
        
        while(e.hasMoreElements()){
            PropertyComponent propComp = (PropertyComponent)e.nextElement();

            String[] path = propComp.getPath().split(";");
            String[] resource = propComp.getResourceIdPath().split(";");
            
            Map hash = (TreeMap)packageHash.get(path[ path.length-2 ]+":"+resource[ resource.length-2]);
            if( hash == null ){
                hash = new TreeMap();
                packageHash.put(path[ path.length-2 ]+":"+resource[ resource.length-2], hash );
            }
            
            String joinPath = joinString(path, path.length-3);
            String joinResource = joinString(resource, resource.length-3);
            
            ArrayList list = (ArrayList)hash.get( joinPath+":"+joinResource);
            if( list == null ){
                list = new ArrayList();
                hash.put(joinPath+":"+joinResource, list);
            }
            list.add(propComp);
        }
        
        mainPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        mainPanel.add( panel, BorderLayout.PAGE_START );
        addPackagePanel(panel, packageHash);
        
        this.validate();
        this.repaint();
    }
    
    
    private void addPackagePanel(JPanel panel, Map subMap){
        Set keys = subMap.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()){
            String name = (String)it.next();
            String[] path_resource = name.split(":");
            PropertyJLabel label = new PropertyJLabel();
            label.setFont( getFont(label, 1));
            label.setPackageName(path_resource[0]);
            label.setDefaultName(path_resource[0]);
            label.setResourceId(path_resource[1]);
            label.setLocaleManager(mLocaleManager);
            label.update();
            
            GradientJPanel pkgPanel = new GradientJPanel(new Color(0xCBCFD5), new Color(0xFFFFFF), 
                                                        GradientJPanel.ROUNDED_NONE, 0, 0);
            pkgPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            pkgPanel.setBorder( BorderFactory.createLineBorder(new Color(0x71838d),1) );
            pkgPanel.add(label);
            panel.add(pkgPanel);
            
            Map newMap = (Map)subMap.get(name);
            if( newMap != null){
                addCategoryPanel(panel, path_resource[0], newMap);
            }
        }
    }

    
    private void addCategoryPanel(JPanel panel, String pkgName, Map subMap){
        Set keys = subMap.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()){
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            String name = (String)it.next();
            GradientJPanel catPanel = new GradientJPanel(new Color(0xCBCFD5), new Color(0xFFFFFF), 
                                                        GradientJPanel.ROUNDED_NONE, 0, 0);
            catPanel.setBorder( BorderFactory.createLineBorder(new Color(0x71838d),1) );
            
            JPanel spacer = new JPanel();
            spacer.setBackground(new Color(0xbec7cc));
            spacer.add( new JLabel("     "));
            
            mainPanel.add(spacer, BorderLayout.LINE_START);
            mainPanel.add( catPanel, BorderLayout.CENTER);
            
            catPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            String[] both = name.split(":");
            String[] path = both[0].split(";");
            String[] resource = both[1].split(";");
            
            for(int i = 0; i < path.length;i++){
                PropertyJLabel label = new PropertyJLabel();
                label.setFont( getFont(label, 0) );
                label.setPackageName(pkgName);
                label.setDefaultName(path[i]);
                label.setResourceId(resource[i]);
                label.setLocaleManager(mLocaleManager);
                label.update();
                catPanel.add(label);
                if( i+1 < path.length ){
                    JLabel sep = new JLabel(">");
                    sep.setFont( getFont(sep, 0) );
                    catPanel.add( sep );
                }
            }
            panel.add(mainPanel);
        
            ArrayList list = (ArrayList)subMap.get(name);
            if( list != null)
                addPropertyPanel(panel, pkgName, list);
        }
    }

    
    
    private void addPropertyPanel(JPanel panel, String pkgName, ArrayList list){
        Iterator it = list.iterator();
        while(it.hasNext()){
            PropertyComponent propComp = (PropertyComponent)it.next();
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            JPanel spacer = new JPanel();
            spacer.setBackground(new Color(0xbec7cc));
            spacer.add( new JLabel("          "));
            
            mainPanel.add(spacer, BorderLayout.LINE_START);
            mainPanel.add( getPropertyPanel(propComp, pkgName), BorderLayout.CENTER);
            
            panel.add( mainPanel);
        }
    }
    
    
    private JPanel getPropertyPanel(PropertyComponent propComp, String pkgName){
        JPanel mainPanel = new JPanel(new GridLayout(1,0));
        mainPanel.setBorder( BorderFactory.createLineBorder(new Color(0x71838d),1) );
        
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(Color.WHITE);
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.PAGE_AXIS));
        valuePanel.setBackground(Color.WHITE);

        PropertyJLabel name = new PropertyJLabel();
        name.setForeground( new Color(0x405661) );
        name.setFont( getFont( name, -1));
        //name.setPackageName( pkgName );
        name.setDefaultName( propComp.getDefaultName() );
        name.setResourceId( propComp.getResourceId() );
        name.setLocaleManager( mLocaleManager );
        name.update();
        
        namePanel.add( name );
        
        DataType dataType = DataType.getDataType( propComp.getDataType() );
        
        if( dataType == DataType.STRING_LIST ){
            String[] values = propComp.getValue().split( propComp.getSeperator() );
            String items = "<html>";
            for(int i = 0; i < values.length;i++){
                items += values[i] + "<br>";
            }
            JLabel value = new JLabel( items + "</html>" );
            value.setFont( getFont(value, -1) );
            value.setForeground( new Color(0x405661) );
            valuePanel.add( value );
        }else{
            JLabel value = new JLabel( propComp.getLabel() );
            
            value.setFont( getFont(value, -1) );
            valuePanel.add( value );
            value.setForeground( new Color(0x405661) );
            if( propComp.getVisualType().equals("colorSelector") ){
                ColourButton btn = new ColourButton();
                btn.setOpaque(false);
                btn.setColor( propComp.getValue() );
                valuePanel.add( btn );
            }
        }
        mainPanel.add(namePanel);
        mainPanel.add(valuePanel);
        return mainPanel;
    }
    
    
    private Color getColorFromHex(String color){
        if( color.charAt(0) == '#' ){
            color = color.substring(1);
        }
        
        if( color.length() != 6 ){
            return new Color(0x405661);
        }
        
        try {
            int red = Integer.parseInt( color.substring(0,2), 16 );
            int green = Integer.parseInt( color.substring(2,4), 16 );
            int blue = Integer.parseInt( color.substring(4,6), 16 );

            return new Color(red, green, blue);
        } catch (NumberFormatException ex) {return new Color(0x405661);}
    }
    
    private JLabel getLocalizedValue(TemplateProperty property, String value){
        
        Enumeration e = property.getConstraints().elements();
        while(e.hasMoreElements()){
            TemplatePropertyConstraint con = (TemplatePropertyConstraint)e.nextElement();
            if(con.getValue().equals(value)){
                PropertyJLabel label = new PropertyJLabel();
                label.setForeground( new Color(0x405661) );
                label.setFont( getFont(label, -1) );
                label.setResourceId( con.getResourceId() );
                label.setDefaultName( value );
                label.setLocaleManager(mLocaleManager);
                label.update();
                return label;
            }
        }
        
        return null;
    }
    
    //
    //  ResourceIdPaths and Paths stored in profiles are stored in reverse
    //  order, we need to reverse the order. 
    //  i.e. property;section;page;category;root_category
    //
    private String joinString(String[] string, int index){
        StringBuffer buffer = new StringBuffer();
        String result = null;
        for(int i = index; i >= 0; i--){
            buffer.append(string[i] + ";");
        }
        if( buffer.length() > 0 ){
            result = buffer.substring(0, buffer.length()-1);
        }else{
            result = buffer.toString();
        }
        return result;
    }
    
    private Font getFont(JLabel label, int size){
        Font font = label.getFont();
        font = font.deriveFont( Font.BOLD, font.getSize() + size);
        
        return font;
    }
    

    public void intervalAdded(ListDataEvent e) {
    }

    public void intervalRemoved(ListDataEvent e) {
    }

    public void contentsChanged(ListDataEvent e) {
        buildPanel( ((PropertyJListModel)e.getSource()).elements() );
    }
    
}
