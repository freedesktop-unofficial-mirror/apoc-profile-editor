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

import com.sun.apoc.tools.profileeditor.ProfileEditorPreferences;
import com.sun.apoc.tools.profileeditor.gui.GradientJButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author cs202741
 */
public class PreferencesJPanel extends JPanel implements ActionListener{
    
    private ProfileEditorPreferences mPreferences = null;
    private JPanel mainPanel  = null;
    private ArrayList<Component> components = null;
    private JTabbedPane mTabbedPane = null;
    
    /** Creates a new instance of PreferencesPanel */
    public PreferencesJPanel(ProfileEditorPreferences aPreferences, JTabbedPane tabbedPane) {
        mPreferences = aPreferences;
        mTabbedPane = tabbedPane;
        components = new ArrayList();

        this.setBackground( Color.WHITE );
        this.setLayout( new BorderLayout() );

        mainPanel = new JPanel();
        mainPanel.setLayout( new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS) );

        mainPanel.add( getGeneral() );
        mainPanel.add( getButtons() );
        
        this.add( mainPanel, BorderLayout.PAGE_START);
    }
    
    private JPanel getGeneral(){

        TitledSectionJPanel titledPanel = new TitledSectionJPanel(new JLabel("General Preferences"));
        
        JPanel panel = new JPanel(new GridBagLayout());
        titledPanel.setContentPanel( panel, true );

        // displayResourceIds
        String value = mPreferences.getProperty( "displayResourceIds");
        addWithGridBag( new JLabel("<html>Display Resource ID's instead of default name<br>" +
                        "when no locale information is available?</html>"), panel, 
                        0, 0, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                        0, 0);
        
        JCheckBox box = new JCheckBox();
        box.setOpaque(false);
        box.setSelected( Boolean.valueOf( value ) );
        box.setName( "displayResourceIds");
        components.add( box );
        addWithGridBag( box, panel, 
                        1, 0, 1, 1,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        0, 0);        
        
        
        // templatePackagePath
        value = mPreferences.getProperty( "templatePackagePath");
        addWithGridBag( new JLabel("Path to Template Package Root: "), panel, 
                        0, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                        0, 0);
        
        JTextField text = new JTextField( value );
        text.setName("templatePackagePath");
        components.add(text);
        addWithGridBag( text, panel, 
                        1, 1, 1, 1,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        0, 0);
        
        // userLocales
        value = mPreferences.getProperty( "userLocales");
        addWithGridBag( new JLabel("User Locales: "), panel, 
                        0, 2, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                        0, 0);
        text = new JTextField( value );
        text.setName("userLocales");
        components.add(text);
        addWithGridBag( text, panel, 
                        1, 2, 1, 1,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        0, 0);

        addWithGridBag( new JLabel("Defaults: " + mPreferences.getProperty("defaultLocales")), panel, 
                        1, 3, 1, 1,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        0, 0);
        
        return titledPanel;
    }
    
    
    private JPanel getButtons(){
        
        Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 12, 0));
        panel.setBackground( Color.WHITE );
        panel.setBorder( border );

        JButton ok = new JButton("Ok");
        ok.setActionCommand("Ok");
        ok.addActionListener(this);
        
        JButton reset = new JButton("Reset Defaults");
        reset.setActionCommand("ResetDefaults");
        reset.addActionListener(this);
        
        JButton cancel = new JButton("Cancel");
        cancel.setActionCommand("Cancel");
        cancel.addActionListener(this);
        
        panel.add(ok);
        panel.add(reset);
        panel.add(cancel);
        
        return panel;
    }
    
    
    private void addWithGridBag(Component comp, Container cont,
                                int x, int y,
                                int width, int height,
                                int anchor, int fill,
                                int weightx, int weighty){
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets( 10, 5, 10, 5);
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        cont.add(comp, gbc);
    }
    
    
    private void savePreferences(){
        Iterator it = components.iterator();
        
        while(it.hasNext()){
            Object comp = it.next();
            
            if( comp instanceof JTextField ){
                JTextField text = (JTextField)comp;
                if( !(text.getText().equals("")) ){
                    mPreferences.setProperty( text.getName(), text.getText() );
                }else
                    mPreferences.remove( text.getName() );   
            }else if( comp instanceof JCheckBox ){
                JCheckBox box = (JCheckBox)comp;
                mPreferences.setProperty( box.getName(), Boolean.toString( box.isSelected() ) );
            }
        }
        try {
            mPreferences.store();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void resetDefaults(){
        
    }

    public void actionPerformed(ActionEvent event) {
        
        if( event.getActionCommand().equals("Ok") ){
            savePreferences();
            mTabbedPane.remove( mTabbedPane.getSelectedIndex() );
            mTabbedPane.setSelectedIndex(0);
        }else if( event.getActionCommand().equals("Cancel") ){
            mTabbedPane.remove( mTabbedPane.getSelectedIndex() );
            mTabbedPane.setSelectedIndex(0);
        }else if( event.getActionCommand().equals("ResetDefaults") ){
            resetDefaults();
        }
    }
}
