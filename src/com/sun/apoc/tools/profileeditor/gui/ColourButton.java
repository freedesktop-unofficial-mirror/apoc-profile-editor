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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JButton;

/**
 *
 * @author cs202741
 */
public class ColourButton extends JButton{
    private Color buttonColour = Color.WHITE;
    
    /** Creates a new instance of ColourButton */
    public ColourButton() {
        super();
        setMargin(new Insets(5,5,5,5));
        setIconTextGap(0);
        setText(null);
    }
    
    public void setColor(Color color){
        if(color != null){
            buttonColour = color;
            repaint();
        }
    }
    
    public void setColor(String color){
        if( color == null ){
            return;
        }
        
        if( color.charAt(0) =='#' ){
            color = color.substring(1);
        }
        
        if( color.length() != 6 ){
            return;
        }
        
        try {
            int red = Integer.parseInt( color.substring(0,2), 16 );
            int green = Integer.parseInt( color.substring(2,4), 16 );
            int blue = Integer.parseInt( color.substring(4,6), 16 );

            buttonColour = new Color(red, green, blue);
            repaint();
        } catch (NumberFormatException ex) {}
        
    }
    
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setColor(buttonColour);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, this.getWidth(), this.getHeight());
        g2d.dispose();
    }
    
}
