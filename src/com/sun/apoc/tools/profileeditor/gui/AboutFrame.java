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

import com.sun.apoc.tools.profileeditor.ProfileEditor;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author cs202741
 */
public class AboutFrame extends JFrame implements ActionListener{
    private static final int WIDTH = 300;
    private static final int HEIGHT = 250;
    private JPanel contentPanel = null;
    
    /** Creates a new instance of AboutPanel */
    public AboutFrame() {
        super("About Profile Editor");
        contentPanel = new JPanel(new BorderLayout());
        this.setContentPane( contentPanel );
        this.setAlwaysOnTop(true);
        
        contentPanel.add( getInfoPanel(), BorderLayout.PAGE_START );
        contentPanel.add( getButtonPanel(), BorderLayout.PAGE_END );
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        
        int x = center.x - WIDTH/2, y = center.y - HEIGHT/2;
        this.setBounds(x, y, WIDTH, HEIGHT);
        if (WIDTH == bounds.width && HEIGHT == bounds.height){
            this.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        this.validate();
        this.setVisible(true);
        this.pack();
    }
    
    private JPanel getInfoPanel(){
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 30, 30, 30, 30) );
        panel.add( new JLabel("Profile Editor - version " + ProfileEditor.VERSION));
        return panel;
    }
    
    private JPanel getButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(this);
        panel.add(close);
        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        this.dispose();
    }
}
