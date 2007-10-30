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

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.CellRendererPane;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 * @author csmith
 */
public class ImageButtonMouseListener implements MouseListener {
    private JList mList = null;
    
    public ImageButtonMouseListener(JList aList){
        mList = aList;
    }
    
    public boolean isClickInButton(MouseEvent event){
        Rectangle r = ((ImageButton)event.getSource()).getBounds();
        CellRendererPane cell = (CellRendererPane)mList.getComponent(0);
        JPanel panel = (JPanel)cell.getComponent(0);
        double cellHeight = panel.getPreferredSize().getHeight();
        //System.out.println("Cell Height: " + cellHeight);
        double x = event.getX() - 16;
        double y = event.getY() - 26 - (cellHeight * mList.getSelectedIndex() );
        
        //System.out.println("Rectangle: " + r);
        //System.out.println("Click X: " + x + "  Y: " + y);
        
        if( r.contains( x, y) )
            return true;
        else
            return false;
    }
    
    public void mouseClicked(MouseEvent event) {
        if( isClickInButton( event ) ) {
            //System.out.println("Clicked Button");
            PropertyComponent propComp = (PropertyComponent)mList.getSelectedValue();
            propComp.reset();
        }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }
    
}
