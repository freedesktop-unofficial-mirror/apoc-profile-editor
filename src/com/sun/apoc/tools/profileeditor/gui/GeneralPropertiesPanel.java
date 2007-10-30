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

import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.tools.profileeditor.ProfileEditor;
import com.sun.apoc.tools.profileeditor.spi.StandaloneProfileManager;
import com.sun.security.auth.module.UnixSystem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 *
 * @author cs202741
 */
public class GeneralPropertiesPanel extends TitledSectionJPanel implements ActionListener, KeyListener, FocusListener{
    
    private ProfileEditor mProfileEditor = null;
    
    private JTextField mDisplayName = null;
    private JTextField mAuthor = null;
    private JTextArea mComment = null;
    private JComboBox mApplicability = null;
    private JComboBox mPriority = null;
    private JButton applyButton = null;
    private JButton cancelButton = null;
    private StandaloneProfileManager mProfileManager = null;
    
    /** Creates a new instance of GeneralPropertiesPanel */
    public GeneralPropertiesPanel(ProfileEditor profileEditor, StandaloneProfileManager profileManager) {
        super( new JLabel("General Properties") );
        this.setContentPanel( getContentPanel(), true );
        mProfileManager = profileManager;
        mProfileEditor = profileEditor;
    }
    
    
    private JPanel getContentPanel(){
        JPanel mainPanel = new JPanel( new BorderLayout() );
        JPanel contentPanel = new JPanel( new GridBagLayout() );
        JPanel buttonPanel = new JPanel( new FlowLayout(FlowLayout.RIGHT) );
        
        mainPanel.setOpaque(false);
        contentPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        
        JLabel label = new JLabel("Display Name:");
        label.setForeground( new Color(0x405661) );
        Font font = label.getFont();
        font = font.deriveFont( Font.PLAIN, font.getSize() );
        label.setFont( font );
        
        addWithGridBag( label, contentPanel,
                        0, 0, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        mDisplayName = new JTextField(25);
        mDisplayName.setText("NewProfile");
        mDisplayName.addFocusListener(this);
        mDisplayName.addKeyListener(this);
        addWithGridBag( mDisplayName, contentPanel,
                        1, 0, 2, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        label = new JLabel("Comment:");
        label.setForeground( new Color(0x405661) );
        label.setFont( font );
        addWithGridBag( label, contentPanel,
                        0, 1, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);

        mComment = new JTextArea(6, 60);
        mComment.addFocusListener(this);
        mComment.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(mComment);
        addWithGridBag( scroll, contentPanel,
                        1, 1, 2, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        label = new JLabel("Author:");
        label.setForeground( new Color(0x405661) );
        label.setFont( font );
        addWithGridBag( label, contentPanel,
                        0, 2, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        mAuthor = new JTextField(25);
        mAuthor.addFocusListener(this);
        mAuthor.addKeyListener(this);
        mAuthor.setText( System.getProperty("user.name") );
        addWithGridBag( mAuthor, contentPanel,
                        1, 2, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        label = new JLabel("* e.g. Lastname, Firstname");
        addWithGridBag( label, contentPanel,
                        2, 2, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        label = new JLabel("Applicability:");
        label.setForeground( new Color(0x405661) );
        label.setFont( font );
        addWithGridBag( label, contentPanel,
                        0, 3, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
                        
        String[] items = { "USER" };
        mApplicability = new JComboBox(items);
        mApplicability.addActionListener(this);
        addWithGridBag( mApplicability, contentPanel,
                        1, 3, 2, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        label = new JLabel("Priority:");
        label.setForeground( new Color(0x405661) );
        label.setFont( font );
        addWithGridBag( label, contentPanel,
                        0, 4, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
                        
        String[] priorities = { "0", "1", "2" };
        mPriority = new JComboBox(priorities);
        mPriority.addActionListener(this);
        addWithGridBag( mPriority, contentPanel,
                        1, 4, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        label = new JLabel("*Warning, having more than one profile available to the APOC daemon with the same priority can cause errors!");
        addWithGridBag( label, contentPanel,
                        2, 4, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        applyButton = new JButton("Save");
        applyButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        
        return mainPanel;
    }
    
    
    public void reset(){
        mDisplayName.setText( "NewProfile" );
        mAuthor.setText( System.getProperty("user.name") );
        mComment.setText("");
        mApplicability.setSelectedIndex(0);
        mPriority.setSelectedIndex(0);
    }
    
    
    public void update(){
        mDisplayName.setText( mProfileManager.getDisplayName() );
        mComment.setText( mProfileManager.getComment() );
        mAuthor.setText( mProfileManager.getAuthor() );
        mApplicability.setSelectedIndex( 0 );
        mPriority.setSelectedIndex( mProfileManager.getPriority() );
    }
    
    
    public void applyChanges(){
        mProfileManager.setDisplayName( mDisplayName.getText() );
        mProfileManager.setComment( mComment.getText() );
        mProfileManager.setAuthor( mAuthor.getText() );
        mProfileManager.setApplicability( Applicability.getApplicability(mApplicability.getSelectedItem().toString() ));
        mProfileManager.setPriority( mPriority.getSelectedIndex() );
    }
    
    private void saveChanges(){
        mProfileEditor.saveProfile();
    }
    
    public boolean hasChanged(){
        
        if( ! mProfileManager.getDisplayName().equals( mDisplayName.getText() )){
            if( ! mDisplayName.getText().equals("NewProfile") ){
                return true;
            }
        }
        
        if( ! mProfileManager.getComment().equals( mComment.getText() )){
            return true;
        }
        
        if( ! mProfileManager.getAuthor().equals( mAuthor.getText() )){
            if( ! mAuthor.getText().equals(System.getProperty("user.name")) ){
                return true;
            }
        }
        
        String savedApplic = mProfileManager.getApplicability().toString();
        String currentApplic = mApplicability.getSelectedItem().toString();
        
        if( ! savedApplic.equals( currentApplic ) ){
            if( mProfileManager.exists() ){
                return true;
            }else {
                return false;
            }
        }
        
        if( mProfileManager.getPriority() != mPriority.getSelectedIndex() ){
            if( mProfileManager.exists() ){
                return true;
            }else {
                return false;
            }
        }

        return false;
    }
    
    
    public void setDisplayName(String name){
        mDisplayName.setText( name );
    }
    
    
    public String getDisplayName(){
        return mDisplayName.getText();
    }
    
    
    public void setAuthor(String author){
        mAuthor.setText( author );
    }
    
    
    public String getAuthor(){
        return mAuthor.getText();
    }
    
    
    public void setComment(String comment){
        mComment.setText( comment );
    }
    
    
    public String getComment(){
        return mComment.getText();
    }
    
    
    private void addWithGridBag(Component comp, Container cont,
                                int x, int y,
                                int width, int height,
                                int anchor, int fill,
                                int weightx, int weighty){
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,7,6,3);
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

    public void actionPerformed(ActionEvent e) {
        mProfileEditor.updateSavedState();
        
        if(e.getSource() == applyButton){
            saveChanges();
        }else if(e.getSource() == cancelButton){
            update();
        }
    }

    public void keyTyped(KeyEvent e) {
        mProfileEditor.updateSavedState();
    }

    public void keyPressed(KeyEvent e) {
        //mProfileEditor.updateSavedState();
    }

    public void keyReleased(KeyEvent e) {
        //mProfileEditor.updateSavedState();
    }

    public void focusGained(FocusEvent e) {
        //mProfileEditor.updateSavedState();
    }

    public void focusLost(FocusEvent e) {
        mProfileEditor.updateSavedState();
    }
    
}
