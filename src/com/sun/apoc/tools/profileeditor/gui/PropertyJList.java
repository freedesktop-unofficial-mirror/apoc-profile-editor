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

import com.sun.apoc.tools.profileeditor.gui.ImageButton;
import com.sun.apoc.tools.profileeditor.gui.PropertyJListModel;
import com.sun.apoc.tools.profileeditor.gui.ImageButtonMouseListener;
import com.sun.apoc.tools.profileeditor.gui.PropertyComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 *
 * @author cs202741
 */
public class PropertyJList extends JList {
    static Color listForeground, listBackground,
            listSelectionForeground, listSelectionBackground,
            
            listEven1, listEven2,
            listOdd1, listOdd2,
            listSelection1, listSelection2;
    
    static {
        UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
        listForeground = uid.getColor("List.forground");
        listBackground = uid.getColor("List.background");
        
        listEven1 = new Color(0xFFFFFF);
        listEven2 = new Color(0xE5EBE9);
        listOdd1 = new Color(0xE5EBE9);
        listOdd2 = new Color(0xCBCFD5);

        listSelection1 = new Color(0x81939B);
        listSelection2 = new Color(0xBEC7CC);
    }
    
    JComponent propertyCell;
    JLabel propertyNameLabel, dataTypeLabel, valueLabel, pathLabel;
    ImageIcon closeIcon;
    JButton imageButton;
    /** Creates a new instance of PropertyJList */
    public PropertyJList(PropertyJListModel aListModel) {
        super(aListModel);
        buildCells();
        this.setCellRenderer(new PropertyRenderer() );
    }
    
    
    protected void buildCells(){
        
        propertyCell = new GradientJPanel(new Color(0xCBCFD5), new Color(0xFFFFFF), 
                                                        GradientJPanel.ROUNDED_NONE, 0, 0);
        propertyCell.setLayout( new GridBagLayout() );
        
        propertyNameLabel = new JLabel();
        Font defaultFont = propertyNameLabel.getFont();
        Font defaultLabelFont = defaultFont.deriveFont( Font.PLAIN, defaultFont.getSize() - 2);
        Font nameFont = defaultFont.deriveFont( Font.BOLD, defaultFont.getSize() - 1);
        propertyNameLabel.setFont( nameFont );
        addWithGridBag( propertyNameLabel, propertyCell,
                        1, 0, 1, 1,
                        GridBagConstraints.LINE_START,
                        GridBagConstraints.NONE, 1, 0);
        
        dataTypeLabel = new JLabel();
        dataTypeLabel.setFont( defaultLabelFont );
        addWithGridBag( dataTypeLabel, propertyCell,
                        1, 1, 1, 1,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE, 1, 0);
        
        closeIcon = new ImageIcon( "images/close.png" );
        imageButton = new ImageButton(closeIcon);
        imageButton.addMouseListener( new ImageButtonMouseListener(this) );

        addWithGridBag(imageButton, propertyCell,
                        2, 0, 1, 2,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE, 1, 1);
        
        opacify(propertyCell);
    }
    
    private void addWithGridBag(Component comp, Container cont,
                                int x, int y,
                                int width, int height,
                                int anchor, int fill,
                                int weightx, int weighty){
        
        GridBagConstraints gbc = new GridBagConstraints();
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
    
    private void opacify(Container cont){
        Component[] comps = cont.getComponents();
        
        for(int i = 0; i < comps.length; i++){
            if(comps[i] instanceof JComponent){
                ((JComponent)comps[i]).setOpaque(false);
            }
        }
    }
    
    
    public class PropertyRenderer extends Object implements ListCellRenderer{
        
        public Component getListCellRendererComponent(JList list, Object value,
                                                        int index,
                                                        boolean isSelected,
                                                        boolean cellHasFocus){

            if( value instanceof PropertyComponent ){
                int numRows = list.getModel().getSize();
                String dataType = ((PropertyComponent)value).getDataType();
                dataType = dataType.substring( dataType.indexOf(":") + 1 );
                String valueString = ((PropertyComponent)value).getValue();
                String labelString = ((PropertyComponent)value).getLabel();
                
                String name = ((PropertyComponent)value).getDataPath();
                
                propertyNameLabel.setText(name);
                dataTypeLabel.setText( dataType + ":  " + valueString + "  ( " + labelString + " )");
                setColorsForSelectionState( propertyCell, isSelected, index, numRows );
            }
            
            return propertyCell;
        }


        private void setColorsForSelectionState(Container cont, boolean isSelected, 
                                                int index, int rows){
            
            Component[] comps = cont.getComponents();

            if(isSelected){
                ((GradientJPanel)cont).setColours(listSelection1, listSelection2);
            }else{
                if(index % 2 == 0){
                    ((GradientJPanel)cont).setColours(listEven2, listEven1);
                }else
                    ((GradientJPanel)cont).setColours(listOdd1, listOdd2);
            }
        }//end setColorsForSelectionState()
    }
}
