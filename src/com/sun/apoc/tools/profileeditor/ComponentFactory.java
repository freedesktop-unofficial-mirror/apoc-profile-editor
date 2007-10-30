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

import com.sun.apoc.tools.profileeditor.gui.ComboBoxElement;
import com.sun.apoc.tools.profileeditor.gui.PropertyChooser;
import com.sun.apoc.tools.profileeditor.gui.PropertyColorChooser;
import com.sun.apoc.tools.profileeditor.gui.PropertyColorSelector;
import com.sun.apoc.tools.profileeditor.gui.PropertyJPasswordField;
import com.sun.apoc.tools.profileeditor.gui.PropertyStringList;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.apoc.tools.profileeditor.templates.TemplateProperty;
import com.sun.apoc.tools.profileeditor.templates.TemplatePropertyConstraint;
import com.sun.apoc.tools.profileeditor.gui.ComponentListener;
import com.sun.apoc.tools.profileeditor.gui.PropertyComponent;
import com.sun.apoc.tools.profileeditor.gui.PropertyJComboBox;
import com.sun.apoc.tools.profileeditor.gui.PropertyJRadioButtonGroupPanel;
import com.sun.apoc.tools.profileeditor.gui.PropertyJTextField;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author cs202741
 */
public class ComponentFactory {
    private static final String NOT_SET = "-- Not Set --";
    

    public static JComponent getComponent(TemplateProperty aProperty, StandaloneProfileManager aProfileModel,
                                            LocaleManager aLocaleManager, String packageName){
        JComponent component = null;
        String visualType = aProperty.getVisualType();

        if( visualType.equals("comboBox") ){
            component = getComboBox( aProperty, aLocaleManager, packageName );
        }else if( visualType.equals("textField") ){
            component = getTextField( aProperty, packageName );
        }else if( visualType.equals("checkBox") ){
            component = getCheckBox( aProperty, aLocaleManager, packageName );
        }else if( visualType.equals("radioButtons") ){
            component = getRadioButton( aProperty, aLocaleManager, packageName );
        }else if( visualType.equals("password") ){
            component = getPasswordField( aProperty, aLocaleManager, packageName );
        }else if( visualType.equals("colorSelector") ){
            component = getColorSelector( aProperty, aLocaleManager, packageName );
        }else if( visualType.equals("stringList") ){
            component = getStringList( aProperty, aLocaleManager, packageName );
        }else if( visualType.equals("chooser") ){
            component = getChooser( aProperty, aLocaleManager, packageName );
        }else{
            
            return component = new JLabel("IMPLEMENT: " + visualType);
        }

        ((PropertyComponent)component).setDataPath( aProperty.getDataPath() );
        ((PropertyComponent)component).setDataType( aProperty.getDataType() );
        ((PropertyComponent)component).setDefaultValue( aProperty.getDefaultValue() );
        ((PropertyComponent)component).setVisualType( aProperty.getVisualType() );
        ((PropertyComponent)component).setProfileModel(aProfileModel);

        return component;
    }
    
    private static JComponent getComboBox(TemplateProperty aProperty, LocaleManager aLocaleManager, String packageName){
        JComboBox comboBox = new PropertyJComboBox();
        
        Object[] items = aProperty.getConstraints().toArray();
        
        ComboBoxElement localItem = new ComboBoxElement();
        localItem.setLabel(NOT_SET);
        localItem.setValue(NOT_SET);
        comboBox.addItem( localItem );
        for(int i = 0; i < items.length;i++){
            
            localItem = new ComboBoxElement();
            localItem.setValue( ((TemplatePropertyConstraint)items[i]).getValue() );
            localItem.setLocaleManager(aLocaleManager);
            localItem.setPackageName( packageName );
            localItem.setDefault( aProperty.getDefaultValue() );
            localItem.setResourceId( ((TemplatePropertyConstraint)items[i]).getResourceId() );
            localItem.setDefaultName( aProperty.getDefaultName() );
            localItem.update();
            
            comboBox.addItem( localItem );
        }
        
        comboBox.addActionListener( new ComponentListener() );
        
        comboBox.setMaximumSize( getDimension( aProperty.getDataType() ));
        return comboBox;
    }
    
    private static JComponent getTextField(TemplateProperty aProperty, String packageName){
        
        JTextField textField = new PropertyJTextField();
        
        textField.addActionListener( new ComponentListener() );
        textField.setPreferredSize( getDimension( aProperty.getDataType() ) );
        textField.setMaximumSize( getDimension( aProperty.getDataType() ));
        textField.setMinimumSize( getDimension( aProperty.getDataType() ));
        textField.setText( aProperty.getDefaultValue() );      
        
        textField.addKeyListener( new ComponentListener() );
        textField.addFocusListener( new ComponentListener() );
        return textField;
    }
    
    private static JComponent getPasswordField(TemplateProperty aProperty, LocaleManager aLocaleManager, String packageName){
        
        JPasswordField textField = new PropertyJPasswordField(10);
        textField.setEchoChar('*');
        
        textField.addActionListener( new ComponentListener() );
        
        textField.addFocusListener( new ComponentListener() );
        return textField;
    }
    
    private static JComponent getCheckBox(TemplateProperty aProperty, LocaleManager aLocaleManager, String packageName){
        ComboBoxElement notSet = new ComboBoxElement();
        notSet.setLabel( NOT_SET );
        notSet.setValue( NOT_SET );
        
        ComboBoxElement enabled = new ComboBoxElement();
        enabled.setValue("true");
        enabled.setDefault( aProperty.getDefaultValue() );
        enabled.setLocaleManager( aLocaleManager );
        enabled.setPackageName( packageName );
        enabled.setResourceId( aProperty.getResourceId() + ".checked" );
        enabled.setDefaultName( "Enabled" );
        enabled.update();
        
        ComboBoxElement disabled = new ComboBoxElement();
        disabled.setValue("false");
        disabled.setDefault( aProperty.getDefaultValue() );
        disabled.setLocaleManager( aLocaleManager );
        disabled.setPackageName( packageName );
        disabled.setResourceId( aProperty.getResourceId() + ".unchecked" );
        disabled.setDefaultName( "Disabled" );
        disabled.update();
        
        JComboBox comboBox = new PropertyJComboBox();
        comboBox.addItem(notSet);
        comboBox.addItem(enabled);
        comboBox.addItem(disabled);
        comboBox.addActionListener( new ComponentListener() );
        
        return comboBox;
    }
    
       
    private static JComponent getRadioButton(TemplateProperty aProperty, LocaleManager aLocaleManager, String packageName){
        Object[] constraints = aProperty.getConstraints().toArray();

        JPanel panel = new PropertyJRadioButtonGroupPanel( constraints.length );
        ((PropertyJRadioButtonGroupPanel)panel).setLocaleManager( aLocaleManager );
        ((PropertyJRadioButtonGroupPanel)panel).setPackageName( packageName );
        ((PropertyJRadioButtonGroupPanel)panel).setResourceId( aProperty.getResourceId() );
        ((PropertyJRadioButtonGroupPanel)panel).setDefaultName( aProperty.getDefaultName() );
        ((PropertyJRadioButtonGroupPanel)panel).setDefaultValue( aProperty.getDefaultValue() );
        
        for(int i = 0; i < constraints.length; i++){
            String value = ((TemplatePropertyConstraint)constraints[i]).getValue();
            ((PropertyJRadioButtonGroupPanel)panel).addRadioButton( value );
        }
        
        return panel;
    }
    
    private static JComponent getColorSelector(TemplateProperty aProperty, LocaleManager aLocaleManager, String packageName){
        
        JPanel textField = new PropertyColorChooser();
        
        //textField.addActionListener( new ComponentListener() );
        textField.setPreferredSize( getDimension( aProperty.getDataType() ) );
        textField.setMaximumSize( getDimension( aProperty.getDataType() ));
        textField.setMinimumSize( getDimension( aProperty.getDataType() ));
        ((PropertyColorChooser)textField).setDefaultValue( aProperty.getDefaultValue() );
        
        textField.addKeyListener( new ComponentListener() );
        textField.addFocusListener( new ComponentListener() );
        return textField;
    }
    
    private static JComponent getStringList( TemplateProperty aProperty, LocaleManager aLocaleManager, String packageName ){
        
        JPanel panel = new PropertyStringList();
        ((PropertyStringList)panel).setSeperator( aProperty.getSeparator() );
        
        return panel;
    }
    
    private static JComponent getChooser( TemplateProperty aProperty, LocaleManager aLocaleManager, String packageName ){

            JPanel panel = new PropertyChooser(aProperty);
        
        return panel;
    }
    
    private static Dimension getDimension(String aDataType){
        int height;
        int width;
        
        if( aDataType.equals("xs:int") ){
            width = 100;
            height = 20;
        }else if( aDataType.equals("xs:short") ){
            width = 100;
            height = 20;
        }else if( aDataType.equals("xs:double") ){
            width = 100;
            height = 20;
        }else if( aDataType.equals("xs:boolean") ){
            width = 100;
            height = 20;
        }else if( aDataType.equals("xs:string") ){
            width = 200;
            height = 20;
        }else{
            width = 100;
            height = 20;
        }
        
        return new Dimension(width, height);
    }
 
}
