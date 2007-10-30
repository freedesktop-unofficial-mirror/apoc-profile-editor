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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author cs202741
 */
public class ComponentListener implements ActionListener, KeyListener, FocusListener {
    
    public void actionPerformed(ActionEvent event){

        if( event.getSource() instanceof PropertyComponent ){
            this.updateJListModel( (PropertyComponent)event.getSource() );
        }
        
    }//end actionPerformed()

    
    public void keyTyped(KeyEvent e) {
	((PropertyComponent)e.getSource()).hasChanged();
    }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
	((PropertyComponent)e.getSource()).hasChanged();
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
	((PropertyComponent)e.getSource()).hasChanged();
    }
    
    
    public void focusGained(FocusEvent event){
        
    }
    
    public void focusLost(FocusEvent event){
        
        if( event.getSource() instanceof PropertyComponent ){
            this.updateJListModel( (PropertyComponent)event.getSource() );
        }
        
    }
    
    private void updateJListModel(PropertyComponent aComponent){

        PropertyJListModel listModel = aComponent.getListModel();

        if( aComponent.hasChanged() ){
            if( listModel.exists( aComponent ) ){
                int index = listModel.indexOf( aComponent );
                listModel.removeItem( aComponent );
                listModel.addItem( index, aComponent );
                //System.out.println("REMOVING FROM LIST BEFORE ADD: " + propComp.getDataPath() );
            }else{
                listModel.addItem( aComponent );
                //System.out.println("ADDING TO LIST: " + propComp.getDataPath() );  
            }

        }else {
            if( listModel.exists( aComponent ) ){
                listModel.removeItem( aComponent );
                //System.out.println("REMOVING FROM LIST: " + propComp.getDataPath() );
            }
        }
    }


}//end ComponentListener
