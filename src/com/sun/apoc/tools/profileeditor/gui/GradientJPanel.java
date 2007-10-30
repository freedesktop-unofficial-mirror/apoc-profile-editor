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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author cs202741
 */
public class GradientJPanel extends JPanel{
    public static final int ROUNDED_NONE = 0;
    public static final int ROUNDED_TOP = 1;
    public static final int ROUNDED_BOTTOM = 2;
    public static final int ROUNDED_BOTH = 3;
    
    private Color color1 = null;
    private Color color2 = null;
    
    private int CORNER_STYLE = ROUNDED_NONE;
    private int ARC_RADIUS = 5;
    private int OFFSET = 5;
    private int HEIGHT = 0;
    
    public GradientJPanel(Color color1, Color color2, int cornerStyle, int arcRadius, int gradientOffset){
        super();
        this.setOpaque( false );

        this.color1 = color1;
        this.color2 = color2;
        CORNER_STYLE = cornerStyle;
        ARC_RADIUS = arcRadius;
        OFFSET = gradientOffset;
    }

    
    public void setColours(Color color1, Color color2){
        this.color1 = color1;
        this.color2 = color2;
    }
    
    public void setGradientHeight(int height){
        HEIGHT = height;
    }
    
    public void paintComponent(Graphics _g){
        Graphics2D g = (Graphics2D)_g;
        Rectangle bounds = getBounds();
        
        Paint gradientPaint = new GradientPaint(0,bounds.height - OFFSET, color1, 0, 0, color2, true);
        
        g.setPaint(gradientPaint);

        if( CORNER_STYLE == ROUNDED_NONE ){
            g.fillRect(0,0,bounds.width, bounds.height);
        }else if( CORNER_STYLE == ROUNDED_BOTTOM ){
            g.fillRoundRect(0,0,bounds.width, HEIGHT, ARC_RADIUS, ARC_RADIUS);
            g.fillRect(0,0,bounds.width, HEIGHT - ARC_RADIUS + 2);
        }else if( CORNER_STYLE == ROUNDED_TOP ){
            g.fillRoundRect(0, HEIGHT, bounds.width, bounds.height, ARC_RADIUS, ARC_RADIUS);
            g.fillRect(0, HEIGHT + ARC_RADIUS - 2, bounds.width, bounds.height);
        }else if( CORNER_STYLE == ROUNDED_BOTH ){
            g.fillRoundRect(0,0,bounds.width, bounds.height, ARC_RADIUS, ARC_RADIUS);
        }
    }
} 
