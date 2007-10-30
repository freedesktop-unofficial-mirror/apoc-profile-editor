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
import java.awt.Font;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.Dimension;

public class ErrorPanel extends JPanel {
    private JTextArea errorTextArea;
    
    public ErrorPanel(){
        
        this.setLayout( new BorderLayout() );
        this.setPreferredSize( new Dimension(ProfileEditor.windowWidth, ProfileEditor.outputHeight) );
        
        errorTextArea = new JTextArea();
        errorTextArea.setEditable(false);
        Font font = errorTextArea.getFont();
        font = font.deriveFont(Font.PLAIN, font.getSize()-1 );
        errorTextArea.setFont( font );
        
        JScrollPane infoScrollPane = new JScrollPane(errorTextArea);
        
        this.add(infoScrollPane, BorderLayout.CENTER);
        
        System.setErr(new PrintStream( new JTextAreaOutputStream(errorTextArea) ) );
    }
    
    public void addText(String message){
        errorTextArea.setText("\n" + message);
        errorTextArea.setCaretPosition( errorTextArea.getDocument().getLength() );
    }

    
    public class JTextAreaOutputStream extends OutputStream {
        JTextArea textArea;
        
        public JTextAreaOutputStream(JTextArea text){
            super();
            textArea = text;
        }
        
        public void write(int i){
            char[] chars = new char[1];
            chars[0] = (char)i;
            String s = new String(chars);
            textArea.append(s);
        }
        
        public void write(char[] buf, int off, int len){
            String s = new String(buf, off, len);
            textArea.append(s);
        }
    }
    
}//end OutputPane