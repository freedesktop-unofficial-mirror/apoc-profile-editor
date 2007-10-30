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

import com.sun.apoc.tools.profileeditor.*;
import com.sun.apoc.tools.profileeditor.packages.Template;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author cs202741
 */
public class PropertyJListPanel extends JPanel {
    private JList mList = null;
    private JButton mClearButton = null;
    private PropertyJListModel mListModel = null;
    //private Template mFilterTemplate = null;
    
    /** Creates a new instance of PropertyListPanel */
    public PropertyJListPanel(PropertyJListModel aListModel) {
        super( new BorderLayout() );
        mListModel = aListModel;
        
        GradientJPanel buttonPanel = new GradientJPanel(new Color(0xCBCFD5), new Color(0xFFFFFF), 
                                                        GradientJPanel.ROUNDED_NONE, 0, 0);
        buttonPanel.setLayout( new FlowLayout(FlowLayout.LEFT) );
        
        mClearButton = new JButton("Clear All");
        mClearButton.addActionListener( new FilterBoxActionListener() );

        buttonPanel.add( mClearButton );
        
        mList = new PropertyJList(aListModel);
        mList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        mList.addMouseListener( new PropertyJListButtonMouseListener() );

        JScrollPane scrollPane = new JScrollPane( mList );

        this.add(buttonPanel, BorderLayout.PAGE_START );
        this.add(scrollPane, BorderLayout.CENTER );
    }
    
    
    class FilterBoxActionListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            
            if( e.getSource() == mClearButton ){
                ArrayList list = new ArrayList();
                Enumeration en = mListModel.elements();
                while(en.hasMoreElements()){
                    list.add( (PropertyComponent)en.nextElement() );
                }
                Iterator it = list.iterator();
                while( it.hasNext() )
                    ((PropertyComponent)it.next()).reset();
            }
        }
    }
    
    class PropertyJListButtonMouseListener implements MouseListener {

        private void forwardEventToButton(MouseEvent event){
            JList list = (PropertyJList)event.getSource();
            
            PropertyJListModel model = (PropertyJListModel)list.getModel();

            JButton button = null;
            MouseEvent buttonEvent = null;
            
            if( list.isSelectionEmpty() || (model.getSize() == 0) ){
                return;
            }
            
            if( ! (list.getComponent( 0 ) instanceof CellRendererPane) ){
                return;
            }

            CellRendererPane cellPane = (CellRendererPane)list.getComponent(0);

            JPanel panel = (JPanel)cellPane.getComponent(0);
            Component[] comps = panel.getComponents();
            
            for(int i = 0;i<comps.length;i++){
                if( comps[i] instanceof ImageButton ){
                    button = (ImageButton)comps[i];
                }
            }
            
            if( button == null ){
                return;
            }

            buttonEvent = (MouseEvent)SwingUtilities.convertMouseEvent(list, event, button);
            button.dispatchEvent(buttonEvent);
            list.repaint();
        }
        
        public void mouseClicked(MouseEvent e) {
            JList list = (PropertyJList)e.getSource();
            if( !list.isSelectionEmpty() ){
                forwardEventToButton(e);
            }
        }

        public void mouseEntered(MouseEvent e) {
            //forwardEventToButton(e);
        }

        public void mouseExited(MouseEvent e) {
            //forwardEventToButton(e);
        }

        public void mousePressed(MouseEvent e) {
            //forwardEventToButton(e);
        }

        public void mouseReleased(MouseEvent e) {
            //forwardEventToButton(e);
        }
    }
}
