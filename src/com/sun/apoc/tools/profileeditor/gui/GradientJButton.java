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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import javax.swing.JButton;

/**
 *
 * @author cs202741
 */
public class GradientJButton extends JButton{
    private Color color1 = new Color(0xf2fafa);
    private Color color2 = new Color(0xd2e0eb);
    private Color border = new Color(0x6699cc);
    
    /** Creates a new instance of GradientJButton */
    public GradientJButton(String label) {
        super("My Button");
        setIconTextGap(0);
        setBorderPainted(false);
        //setBorder(null);
        //setContentAreaFilled(false);
        //setMargin(new Insets(5, 5, 5, 5));
    }
    
    public void paintComponent(Graphics g){
        Graphics2D _g = (Graphics2D)g;
        Color origColor = _g.getColor();
        
        Paint gradientPaint = new GradientPaint(0,getHeight(), color1, 0, 0, color2, true);
        
        _g.setPaint( gradientPaint );
        _g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        _g.setColor( border );
        _g.drawRoundRect( 0, 0, getWidth(), getHeight(), 15, 15);
        
        _g.setColor( origColor );
        super.paintComponent(g); 
    }
    
    
}
