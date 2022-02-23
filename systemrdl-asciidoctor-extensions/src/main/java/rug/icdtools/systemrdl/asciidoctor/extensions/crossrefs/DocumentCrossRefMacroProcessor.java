/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.crossrefs;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.extension.InlineMacroProcessor;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class DocumentCrossRefMacroProcessor extends InlineMacroProcessor {


    private final static Map<String,String> refToDocNameMap=new HashMap<>();

    public static void resetReferencesMap(){
        refToDocNameMap.clear();
    }
    
    public static Map<String, String> getRefToDocNameMap() {
        return refToDocNameMap;
    }
    
    
    
    @Override
    public Object process(ContentNode contentNode, String docName, Map<String, Object> attributes) {

        
        
        String referencedDocumentURL;
        String inlineText;
        DocProcessLogger.getInstance().log(">>>>Adding/formatting external ref:"+docName, Severity.DEBUG);
        if (docName != null && !docName.isEmpty()) {
            
            //pull data, store in map
            String docRefLabel = "(REF"+(refToDocNameMap.size()+1)+")";
            refToDocNameMap.put(docRefLabel, docName);
            
            referencedDocumentURL = "https://thedoc.gitlab.com/" + docName+"/"+attributes.get("version");                        

            // Define options for an 'anchor' element:
            Map<String, Object> options = new HashMap<>();
            options.put("type", ":link");
            options.put("target", referencedDocumentURL);

            // Create the 'anchor' node:
            PhraseNode inlineRefDocLink = createPhraseNode(contentNode, "anchor", docRefLabel, attributes, options);
            return inlineRefDocLink.convert();
        } else {
            throw new RuntimeException();
        }


    }
    
}
