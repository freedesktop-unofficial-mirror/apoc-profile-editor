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

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author cs202741
 */
public class ProfileEditorJMenuBar extends JMenuBar {
    ActionListener mListener = null;
    
    /** Creates a new instance of ProfileEditorJMenuBar */
    public ProfileEditorJMenuBar( ActionListener aListener ) {
        mListener = aListener;
        
        this.add( getFileMenu() );
        this.add( getToolsMenu() );
        this.add( getHelpMenu() );
        
    }
    
    private JMenu getFileMenu(){
        JMenu fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( KeyEvent.VK_F );
        
        JMenuItem menuItem = new JMenuItem( "New Profile", KeyEvent.VK_N );
        menuItem.setActionCommand("New");
        menuItem.addActionListener(mListener);
        fileMenu.add(menuItem);
        
        menuItem = new JMenuItem( "Open Profile", KeyEvent.VK_O );
        menuItem.setActionCommand("OpenProfile");
        menuItem.addActionListener(mListener);
        fileMenu.add(menuItem);
        
        fileMenu.addSeparator();
        
        menuItem = new JMenuItem( "Open Package", KeyEvent.VK_O );
        menuItem.setActionCommand("OpenPackage");
        menuItem.addActionListener(mListener);
        fileMenu.add(menuItem);
        
        fileMenu.addSeparator();
        
        menuItem = new JMenuItem( "Save", KeyEvent.VK_S );
        menuItem.setActionCommand("Save");
        menuItem.addActionListener(mListener);
        fileMenu.add(menuItem);
        
        menuItem = new JMenuItem( "Save As", KeyEvent.VK_A );
        menuItem.setActionCommand("SaveAs");
        menuItem.addActionListener(mListener);
        fileMenu.add(menuItem);
        
        fileMenu.addSeparator();
        
        menuItem = new JMenuItem( "Exit", KeyEvent.VK_X );
        menuItem.setActionCommand("Exit");
        menuItem.addActionListener(mListener);
        fileMenu.add(menuItem);
        
        return fileMenu;
    }
    
    public JMenu getToolsMenu(){
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem menuItem = null;
        toolsMenu.setMnemonic( KeyEvent.VK_T );
        
        menuItem = new JMenuItem( "Preferences", KeyEvent.VK_N );
        menuItem.addActionListener(mListener);
        menuItem.setActionCommand("Preferences");
        toolsMenu.add( menuItem );
        
        return toolsMenu;
    }
    
    public JMenu getHelpMenu(){
        JMenu helpMenu = new JMenu("Help");
        JMenuItem menuItem = null;
        helpMenu.setMnemonic( KeyEvent.VK_H );
        
        menuItem = new JMenuItem( "About", KeyEvent.VK_A );
        menuItem.addActionListener(mListener);
        menuItem.setActionCommand("About");
        helpMenu.add( menuItem );
        
        return helpMenu;
    }
}
