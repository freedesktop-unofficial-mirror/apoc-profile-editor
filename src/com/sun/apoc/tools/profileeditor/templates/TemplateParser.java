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
package com.sun.apoc.tools.profileeditor.templates;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class TemplateParser
{
    public boolean debug = false;
    
    
    public static final String TEMPLATE_PATH_SEPARATOR = "/";
    public static final String SET_PREFIX = "SET_";
    public static final String SET_INDEX_SEPARATOR = "|";            

    public static final String DEFAULT_PACKAGE_DIR = "/packages";
    public static final String DEFAULT_TEMPLATE_DIR =  File.separator + "templates" + File.separator;
    
    public static final String TEMPLATE_EXTENSION   = ".xml";
    public static final String JAR_EXTENSION        = ".jar";
    public static final String PROPERTIES_EXTENSION = ".properties";
    public static final String ROOT_CATEGORY_NAME   = "Policies";
    
    private Writer  out;
    private String m_currentElement = null;
    private File m_DTDLocation;
    private TemplateCategory m_templateRoot = null;
    private Locator m_locator = null;
    
    private ArrayList mPropertyList = null;
    private HashMap mSetList = null;
    
    
    public TemplateParser(File template, File packageRoot, ArrayList aPropertyList) throws SAXParseException 
    {
        m_DTDLocation = new File( packageRoot.getPath()
        //        + File.separator + ".."
                + File.separator + "dtd"
                + File.separator + "policytemplate.dtd" );
        
        mSetList = new HashMap();
        mPropertyList = aPropertyList;
        parseTemplate(template);
    }
    

    private void parseTemplate(File template) throws SAXParseException
    {
        m_templateRoot = new TemplatePage(ROOT_CATEGORY_NAME, null, 
                                     "APOC.policies.root", null, null);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        try {
            out = new OutputStreamWriter(System.out, "UTF8");

            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse( template, new TemplateHandler(m_templateRoot, template.getName()) );
        }catch( ClassCastException e){
            String error = "XML ERROR in file: '" + m_locator.getSystemId() +
                            "', on LINE: " + m_locator.getLineNumber() +
                            "\n   INFO: The element type \"" + m_currentElement + 
                            "\" must be terminated by the matching end-tag \"</" + m_currentElement + ">\".\n";
            throw new SAXParseException(error, m_locator);
        }catch (SAXParseException spe) {
            String error = "XML ERROR in file: '" + spe.getSystemId() +
                            "', on LINE: " + spe.getLineNumber() +
                            "\n   INFO: " + spe.getMessage() + "\n";
            throw new SAXParseException(error, m_locator);
        } catch (SAXException sxe) {
           Exception  x = sxe;
           if (sxe.getException() != null){
               x = sxe.getException();
           }
           x.printStackTrace();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ioe) {
           ioe.printStackTrace();
        } 
    }//end parseTemplate
    
    
    public TemplateCategory getRootCategory(){
        return m_templateRoot;
    }

    public HashMap getSetList(){
        return mSetList;
    }
    
    public ArrayList getPropertyList(){
        return mPropertyList;
    }
    
    
    
    
    class TemplateHandler extends DefaultHandler {
        
        public static final String CATEGORY_TAG     = "category";
        public static final String PAGE_TAG         = "page";
        public static final String SECTION_TAG      = "section";
        public static final String SET_TAG          = "set";
        public static final String PROPERTY_TAG     = "property";
        public static final String RES_IMPORT_TAG   = "resImport";
        public static final String HELP_IMPORT_TAG  = "helpImport";
        public static final String INLINE_HELP_TAG  = "inlineHelp";
        public static final String ENUMERATION_TAG  = "enumeration";
        public static final String VALUE_TAG        = "value";
        public static final String VISUAL_TAG       = "visual";
        public static final String CHECKBOX_TAG     = "checkBox";
        public static final String CHOOSER_TAG      = "chooser";        
        public static final String RES_ID_ATTR      = "apt:resId";
        public static final String LABEL_ATTR       = "apt:label";
        public static final String NAME_ATTR        = "apt:name";
        public static final String PACKAGE_ATTR     = "apt:packagePath";
        public static final String FILE_ATTR        = "apt:filePath";
        public static final String VISUAL_TYPE_ATTR = "apt:visualType";
        public static final String VISUAL_TYPE_ATTR2= "apt:type";
        public static final String DATA_PATH_ATTR   = "apt:dataPath";
        public static final String VALUE_ATTR       = "oor:value";
        public static final String SEPARATOR_ATTR   = "oor:separator";
        public static final String TYPE_ATTR        = "oor:type";
        public static final String SCOPE_ATTR       = "apt:scope";
        public static final String STORE_ATTR       = "apt:storeDefault";
        public static final String INLINE_HELP_ATTR = "apt:inlineHelp";
        public static final String ONLINE_HELP_ATTR = "apt:onlineHelp";
        public static final String LABEL_POST_ATTR  = "apt:labelPost"; 
        public static final String NIL_ATTR         = "xsi:nil";
        public static final String EXTRA_HTML_ATTR  = "apt:extraHtml";
        public static final String CHOOSER_PATH_ATTR= "apt:dataPath";
        public static final String EXTENDS_CHOOSER_ATTR= "apt:extendsChooser";        
        public static final String LABEL_POPUP_ATTR = "apt:labelPopup";                
        public static final String XML_HANDLER_TAG  = "xmlHandler";
        public static final String ACTION_HANDLER_TAG= "actionHandler";
        public static final String EVENT_TAG        = "event";
        public static final String ACTION_TAG       = "action";
        public static final String WHEN_TAG         = "when";
        public static final String OTHERWISE_TAG    = "otherwise";
        public static final String COMMAND_TAG      = "command";
        public static final String XML_HANDLER_ATTR = "apt:xmlHandler";
        public static final String ACTION_HANDLER_ATTR = "apt:actionHandler";
        public static final String HANDLER_TYPE_ATTR= "apt:type";        
        public static final String TEST_ATTR        = "apt:test";
        public static final String CLASS_ATTR       = "apt:class";
        
        public static final String TEMPLATE_SYSTEM_ID = "policytemplate.dtd";

        private String m_packageName        = null;
        private String m_resourceBundle     = null;
        private String m_helpFile           = null;
        private StringBuffer m_buffer       = null;
        private LinkedList m_context        = null;
        
        private String latestCategoryPath           = null;
        private String latestCategoryResourceIdPath = null;
        private String latestSectionPath            = null;
        private String latestSectionResourceIdPath  = null;
        
        private String chooserPath = "/";
        
        private boolean inSet = false;
        
        private int indentLevel = 0;

        
        public TemplateHandler(TemplateCategory templateRoot, 
                    String packageName) {
            m_buffer = new StringBuffer();
            m_context = new LinkedList();
            m_context.add(templateRoot);
            m_packageName = packageName;
        }

        
        protected Object getCurrentContext() {
            return (m_context.getLast());
        }
        
        
        public void startElement(String uri, String localName, 
                String qName, Attributes attr) throws SAXException, ClassCastException{
            
           indentLevel++;
            nl(); emit("ELEMENT: ");
            String eName = localName; // element name
            if ("".equals(eName)){
                eName = qName; // namespaceAware = false
            }
            emit("<"+eName);
            if (attr != null) {
                for (int i = 0; i < attr.getLength(); i++) {
                    String aName = attr.getLocalName(i); // Attr name 
                    if ("".equals(aName)) aName = attr.getQName(i);
                    nl();
                    emit("   ATTR: ");
                    emit(aName);
                    emit("\t\"");
                    emit(attr.getValue(i));
                    emit("\"");
                }
            }
            if (attr.getLength() > 0) nl();
            emit(">");
            
            m_currentElement = qName;
            if (qName.equals(CATEGORY_TAG)) {
                String name = attr.getValue(NAME_ATTR);
                chooserPath += "/" + name;
                TemplateCategory template = (TemplateCategory) getCurrentContext();
                TemplateCategory subCategory = template.getSubCategory(name);
                if (subCategory == null) {
                    String label = attr.getValue(RES_ID_ATTR);
                    if (label == null) {
                        label = attr.getValue(LABEL_ATTR);
                    } 
                    subCategory = new TemplatePage(name, 
                                         attr.getValue(SCOPE_ATTR),
                                         label, 
                                         m_resourceBundle, template);
                    subCategory.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));                     
                    template.addSubCategory(subCategory);
                }
                m_context.add(subCategory);
            } else if (qName.equals(PAGE_TAG)){    
                String name = attr.getValue(NAME_ATTR);
                chooserPath += "/" + name;
                if (getCurrentContext() instanceof TemplateSet) {
                    TemplateSet set = (TemplateSet) getCurrentContext();
                    String label = attr.getValue(RES_ID_ATTR);
                    if (label == null) {
                        label = attr.getValue(LABEL_ATTR);
                    } 
                    if (attr.getValue(ONLINE_HELP_ATTR) != null) {
                        m_helpFile = attr.getValue(ONLINE_HELP_ATTR);
                    }
                    TemplatePage page = new TemplatePage(name,
                                                attr.getValue(SCOPE_ATTR), 
                                                label, 
                                                m_resourceBundle, m_helpFile, 
                                                null, 
                                                m_packageName);
                    page.setDataPath(set.getDataPath());  
                    page.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));                          
                    set.setPage(page);
                    m_context.add(page);
                    
                    //  Added so loaded Policies can get Path
                    //latestCategoryPath = page.getPath();
                    //latestCategoryResourceIdPath = page.getResourceIdPath();
                } else {
                    TemplateCategory template = (TemplateCategory) getCurrentContext();
                    TemplateCategory subCategory = template.getSubCategory(name);
                    if (subCategory == null) {
                        String label = attr.getValue(RES_ID_ATTR);
                        if (label == null) {
                            label = attr.getValue(LABEL_ATTR);
                        }
                        if (attr.getValue(ONLINE_HELP_ATTR) != null) {
                            m_helpFile = attr.getValue(ONLINE_HELP_ATTR);
                        } 
                        subCategory = new TemplatePage(name,
                                             attr.getValue(SCOPE_ATTR), 
                                             label, 
                                             m_resourceBundle, m_helpFile, 
                                             template,
                                             m_packageName);
                        subCategory.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));                     
                        template.addSubCategory(subCategory);
                    }
                    m_context.add(subCategory);
                    
                    // Added so loaded policies can lookup paths
                    latestCategoryPath = subCategory.getPath();
                    latestCategoryResourceIdPath = subCategory.getResourceIdPath();
                }
                
            } else if (qName.equals(SECTION_TAG)) {
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                }     
                TemplateSection section = new TemplateSection(
                                                attr.getValue(NAME_ATTR),

                                                attr.getValue(SCOPE_ATTR), 
                                                label, 
                                                m_resourceBundle);
                TemplatePage page = (TemplatePage) getCurrentContext();
                page.addSection(section);
                m_context.add(section);
                chooserPath += "/" + attr.getValue(NAME_ATTR);
                
                if( inSet ){
                    latestSectionPath = section.getDefaultName() + ";" + latestSectionPath;
                    latestSectionResourceIdPath = section.getResourceId() + ";" + latestSectionResourceIdPath;
                }else{
                    latestSectionPath = section.getDefaultName() + ";" + latestCategoryPath;
                    latestSectionResourceIdPath = section.getResourceId() + ";" + latestCategoryResourceIdPath;                
                }
                section.setPath(latestSectionPath);
                section.setResourceIdPath(latestSectionResourceIdPath);
                
            } else if (qName.equals(XML_HANDLER_TAG)) {
                String label = attr.getValue(NAME_ATTR);
                TemplateXMLHandler handler = new TemplateXMLHandler(label);
                TemplatePage page = (TemplatePage) getCurrentContext();
                page.addXMLHandler(handler);
                m_context.add(handler);        

            } else if (qName.equals(ACTION_HANDLER_TAG)) {
                String name = attr.getValue(NAME_ATTR);
                String classname = attr.getValue(CLASS_ATTR);
                String packageDir = m_packageName;
                TemplateActionHandler handler = new TemplateActionHandler(name, classname, packageDir);
                TemplatePage page = (TemplatePage) getCurrentContext();
                page.addActionHandler(handler);
                m_context.add(handler);  
                
            } else if (qName.equals(SET_TAG)) {
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                } 
                TemplateSet set = new TemplateSet(attr.getValue(NAME_ATTR),
                                        attr.getValue(SCOPE_ATTR), 
                                        label, 
                                        m_resourceBundle,
                                        attr.getValue(DATA_PATH_ATTR),
                                        attr.getValue(LABEL_POPUP_ATTR));
                
                
                inSet = true;
                latestSectionPath = set.getDefaultName() + ";" + latestCategoryPath;
                latestSectionResourceIdPath = set.getResourceId() + ";" + latestCategoryResourceIdPath;
                
                set.setPath(latestSectionPath);
                set.setResourceIdPath(latestSectionResourceIdPath);
                
                TemplatePage page = (TemplatePage) getCurrentContext();                        
                page.addSection(set);
                m_context.add(set);  
            } else if (qName.equals(PROPERTY_TAG)) {
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                }     
                TemplateProperty property = new TemplateProperty(
                                                    attr.getValue(NAME_ATTR),
                                                    attr.getValue(SCOPE_ATTR), 
                                                    label, 
                                                    m_resourceBundle,
                                                    attr.getValue(DATA_PATH_ATTR), 
                                                    attr.getValue(VISUAL_TYPE_ATTR),
                                                    attr.getValue(TYPE_ATTR),
                                                    attr.getValue(STORE_ATTR),
                                                    attr.getValue(XML_HANDLER_ATTR),
                                                    attr.getValue(ACTION_HANDLER_ATTR),
                                                    attr.getValue(EXTRA_HTML_ATTR));
                
                property.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));
                property.setChooserPath(chooserPath);
                
                // Added so loaded policies can lookup paths
                property.setPath( latestSectionPath );
                property.setResourceIdPath( latestSectionResourceIdPath );
                
                TemplateSection section = (TemplateSection) getCurrentContext();                                    
                section.addProperty(property);
                m_context.add(property);
                mPropertyList.add( property );
                
                if( inSet ){
                    mSetList.put(property.getDataPath(), property);
                }
                
            } else if (qName.equals(VISUAL_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                property.setVisualType(attr.getValue(VISUAL_TYPE_ATTR2));    
                
            } else if (qName.equals(CHECKBOX_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                property.setVisualType(TemplateProperty.CHECKBOX);
                //#b5055105# support for localization of label
                String label = attr.getValue(LABEL_POST_ATTR);
                property.setLabelPost(label);
                
            } else if (qName.equals(CHOOSER_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                property.setVisualType(TemplateProperty.CHOOSER);
                property.setExtendsChooser(attr.getValue(EXTENDS_CHOOSER_ATTR));    
                property.setChooserPath(attr.getValue(CHOOSER_PATH_ATTR));    
                property.setLabelPopup(attr.getValue(LABEL_POPUP_ATTR));    
                
            } else if (qName.equals(RES_IMPORT_TAG)) {
                m_resourceBundle = attr.getValue(PACKAGE_ATTR);
                
            } else if (qName.equals(HELP_IMPORT_TAG)) {
                m_helpFile = attr.getValue(FILE_ATTR);
            
            } else if (qName.equals(INLINE_HELP_TAG)) {
                NarratedElement element = (NarratedElement) getCurrentContext();
                element.setDescriptionId(attr.getValue(RES_ID_ATTR)); 
            
            } else if (qName.equals(ENUMERATION_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                }
                property.addConstraint(attr.getValue(VALUE_ATTR), label);

            } else if (qName.equals(VALUE_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                String separator = attr.getValue(SEPARATOR_ATTR);
                property.setSeparator(separator);
                property.setDefaultNilValue(attr.getValue(NIL_ATTR));
                m_buffer.setLength(0);
            
            } else if (qName.equals(ACTION_TAG)) {
                m_context.add(ACTION_TAG) ;
                
            } else if (qName.equals(EVENT_TAG)) {
                TemplateXMLHandler handler = (TemplateXMLHandler) getCurrentContext();
                handler.addType(attr.getValue(HANDLER_TYPE_ATTR));
            
            } else if (qName.equals(WHEN_TAG)) {
                TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 2);
                handler.addTest(attr.getValue(TEST_ATTR));
                handler.addNewCommandList() ;
                m_context.add(WHEN_TAG) ;

            } else if (qName.equals(OTHERWISE_TAG)) {
                m_context.add(OTHERWISE_TAG) ;
                
            } else if (qName.equals(COMMAND_TAG)) {
                m_buffer.setLength(0);
            }
        }
        
        
        public void characters(char[] chars, int start, int len) 
                throws SAXException {
            m_buffer.append(chars, start, len);        
        }
        
        
        public void endElement(String uri, String localName, String qName) 
                throws SAXException {
            
            nl();
            emit("END_ELM: ");
            emit("</"+localName+">");
            indentLevel--;
            
            if ( qName.equals(OTHERWISE_TAG)
                || qName.equals(WHEN_TAG)
                || qName.equals(ACTION_TAG)
                || qName.equals(XML_HANDLER_TAG)                
                || qName.equals(ACTION_HANDLER_TAG)) {    
                
                m_context.removeLast();
            } else if (qName.equals(PROPERTY_TAG)
                || qName.equals(SECTION_TAG)
                || qName.equals(PAGE_TAG)
                || qName.equals(CATEGORY_TAG)){
                
                m_context.removeLast();
               int index = chooserPath.lastIndexOf("/");
               if( index > 0 ){
                    chooserPath = chooserPath.substring(0, index);   
               }
            } else if ( qName.equals(SET_TAG) ) {
                inSet = false;
                m_context.removeLast();
            } else if (qName.equals(VALUE_TAG)) {

                TemplateProperty property = (TemplateProperty) getCurrentContext();
                if (!property.hasDefaultNilValue()) {
                    property.setDefaultValue(m_buffer.toString());
                }
                m_buffer.setLength(0);    

            } else if (qName.equals(COMMAND_TAG)) {
                if(((String)getCurrentContext()).equals(ACTION_TAG)) {
                    TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 2) ;
                    handler.addCommand(m_buffer.toString());
                    m_buffer.setLength(0);    
                } 
                else if(((String)getCurrentContext()).equals(WHEN_TAG)) {
                    TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 3) ;
                    handler.addWhenCommand(m_buffer.toString());
                    m_buffer.setLength(0);   
                }
                else if(((String)getCurrentContext()).equals(OTHERWISE_TAG)) {
                    TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 3) ;
                    handler.addOtherwiseCommand(m_buffer.toString());
                    m_buffer.setLength(0);
                }
            }
        }

        
        public void setDocumentLocator(Locator l)
        {
              m_locator = l;
              System.out.println("Loading Template: " + l.getSystemId() );
        }
        
        
        public InputSource resolveEntity (String publicId, String systemId) throws SAXParseException {
            if ((systemId.endsWith(TEMPLATE_SYSTEM_ID)) && (m_DTDLocation != null)) {
                try {
                    if( m_DTDLocation.exists() ){
                        FileInputStream stream = new FileInputStream(m_DTDLocation);
                        return new InputSource(stream);
                    }
                    
                    File dtd = new File(TEMPLATE_SYSTEM_ID);
                    if( dtd.exists() ){
                        FileInputStream stream = new FileInputStream(dtd);
                        return new InputSource(stream);
                    }else{
                        throw new FileNotFoundException();
                    }
                } catch (FileNotFoundException e) {
                    String error = "Could not load " + TEMPLATE_SYSTEM_ID + "!" +
                                    "\nPlease make sure it is located in the program working directory" +
                                    "\nor in the root directory of the packages.";
                    throw new SAXParseException(error, m_locator);
                }
            } else {
                return null;
            }
        }
        


        //===========================================================
        // SAX ErrorHandler methods
        //===========================================================

        public void fatalError (SAXParseException e)
            throws SAXException
        {
            throw e;
        }

        // treat validation errors as fatal
        public void error(SAXParseException e)
        throws SAXParseException
        {
            String error = "XML ERROR in file: '" + e.getSystemId() +
                            "', on LINE: " + e.getLineNumber() +
                            "\n   INFO: " + e.getMessage() + "\n";
            System.err.println(error);
        }

        // dump warnings too
        public void warning(SAXParseException err)
        throws SAXParseException
        {
    //        System.out.println("** Warning"
    //            + ", line " + err.getLineNumber()
    //            + ", uri " + err.getSystemId());
    //        System.out.println("   " + err.getMessage());
        }

        //===========================================================
        // Utility Methods ...
        //===========================================================

        // Wrap I/O exceptions in SAX exceptions, to
        // suit handler signature requirements
        private void emit(String s)
        throws SAXException
        {
            if(debug){
                try {
                    out.write(s);
                    out.flush();
                } catch (IOException e) {
                    throw new SAXException("I/O error", e);
                }
            }
        }

        // Start a new line
        // and indent the next line appropriately
        private void nl()
        throws SAXException
        {
            if(debug){
                String lineEnd =  System.getProperty("line.separator");
                try {
                    out.write(lineEnd);
                    //for (int i=0; i < indentLevel; i++) out.write(indentString);
                } catch (IOException e) {
                    throw new SAXException("I/O error", e);
                }
            }
        }
        
    }
    
}
