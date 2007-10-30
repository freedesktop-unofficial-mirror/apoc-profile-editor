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
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 *
 * @author csmith
 */
public class GradientTabPaneUI extends BasicTabbedPaneUI{
    
    private static final int TAB_HEIGHT = 10;
    private static final int TAB_WIDTH = 10;
    
    protected Color lightHighlight = new Color(0x71838d);
    protected Color shadow = new Color(0x71838d);
    protected Color darkShadow = new Color(0x495b67);
    
    /** Creates a new instance of GradientTabPaneUI */
    public GradientTabPaneUI() {
        super();
    }
    
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        int tabCount = tabPane.getTabCount();

        Rectangle iconRect = new Rectangle(),
                  textRect = new Rectangle();
        Rectangle clipRect = g.getClipBounds();  

        // Paint tabRuns of tabs from back to front
        for (int i = runCount - 1; i >= 0; i--) {
            int start = tabRuns[i];
            int next = tabRuns[(i == runCount - 1)? 0 : i + 1];
            int end = (next != 0? next - 1: tabCount - 1);
            for (int j = start; j <= end; j++) {
                if (j != selectedIndex && rects[j].intersects(clipRect)) {
                    paintTab(g, tabPlacement, rects, j, iconRect, textRect);
                }
            }
        }

        // Paint selected tab if its in the front run
        // since it may overlap other tabs
        if (selectedIndex >= 0 && rects[selectedIndex].intersects(clipRect)) {
            paintTab(g, tabPlacement, rects, selectedIndex, iconRect, textRect);
        }
    }

    protected void paintTab(Graphics g, int tabPlacement,
                            Rectangle[] rects, int tabIndex, 
                            Rectangle iconRect, Rectangle textRect) {
        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;
        Graphics2D g2 = null;
        Polygon cropShape = null;
        Shape save = null;
        int cropx = 0;
        int cropy = 0;


        //if (tabPane.isOpaque()) {
            paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
                    tabRect.width, tabRect.height, isSelected);
        //}

        paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, 
                       tabRect.width, tabRect.height, isSelected);
        
        String title = tabPane.getTitleAt(tabIndex);
        Font font = tabPane.getFont();
        //FontMetrics metrics = SwingUtilities2.getFontMetrics(tabPane, g, font);
        FontMetrics metrics = g.getFontMetrics( font );
        Icon icon = getIconForTab(tabIndex);

        layoutLabel(tabPlacement, metrics, tabIndex, title, icon, 
                    tabRect, iconRect, textRect, isSelected);

        paintText(g, tabPlacement, font, metrics, 
                  tabIndex, title, textRect, isSelected);

        paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);

        paintFocusIndicator(g, tabPlacement, rects, tabIndex, 
                  iconRect, textRect, isSelected);

    }
    
    
    protected void paintTabBorder(Graphics g, int tabPlacement,
                                  int tabIndex,
                                  int x, int y, int w, int h, 
                                  boolean isSelected ) {
        
        g.setColor(lightHighlight); 
        
        g.drawLine(x, y+2, x, y+h-1); // left highlight
        g.drawLine(x+1, y+1, x+1, y+1); // top-left highlight
        g.drawLine(x+2, y, x+w-3, y); // top highlight              

        g.setColor(shadow);  
        g.drawLine(x+w-2, y+2, x+w-2, y+h-1); // right shadow

        g.setColor(darkShadow); 
        g.drawLine(x+w-1, y+2, x+w-1, y+h-1); // right dark-shadow
        g.drawLine(x+w-2, y+1, x+w-2, y+1); // top-right shadow

    }
    
    
    protected void paintTabBackground(Graphics g, int tabPlacement,
                                      int tabIndex,
                                      int x, int y, int w, int h, 
                                      boolean isSelected ) {
        
        Graphics2D _g = (Graphics2D)g;
        Paint gradientPaint;
        
        if( isSelected ){
            gradientPaint = new GradientPaint(0,h, new Color(0xdee7e7), 0, 0, new Color(0xFFFFFF), true);
        }else{
            gradientPaint = new GradientPaint(0,h, new Color(0xa5aeb5), 0, 0, new Color(0xced3de), true);
        }
        _g.setPaint( gradientPaint );
        
        switch(tabPlacement) {
          case LEFT:
              _g.fillRect(x+1, y+1, w-1, h-3);
              break;
          case RIGHT:
              _g.fillRect(x, y+1, w-2, h-3);
              break;
          case BOTTOM:
              _g.fillRect(x+1, y, w-3, h-1);
              break;
          case TOP:
          default:
              _g.fillRect(x+1, y+1, w-3, h-1);
        }
    }
    
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight){
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + TAB_HEIGHT;
    }
    
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + TAB_WIDTH;
    }
}
