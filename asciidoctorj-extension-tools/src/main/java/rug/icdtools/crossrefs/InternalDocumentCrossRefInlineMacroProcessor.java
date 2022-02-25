/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.crossrefs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.extension.InlineMacroProcessor;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class InternalDocumentCrossRefInlineMacroProcessor extends InlineMacroProcessor {


    private static final Map<DocumentVersion,String> docNameToRefLabel=new HashMap<>();
    private static final List<DocumentVersion> refDetailsOrderedList = new LinkedList<>();

    
    public static void resetReferencesMap(){
        docNameToRefLabel.clear();
    }
    
    public static Map<DocumentVersion, String> getDocNameToRefLabelMap() {
        return docNameToRefLabel;
    }
    public static List<DocumentVersion> getRefDetailsOrderedList() {
        return refDetailsOrderedList;
    }

    
    @Override
    public Object process(ContentNode contentNode, String docName, Map<String, Object> attributes) {

        
        String versionTag=(String)attributes.get("version");       

        DocProcessLogger.getInstance().log(">>>>Adding/formatting external ref:"+docName, Severity.DEBUG);
        
        if (docName != null && !docName.isEmpty()) {
            
            DocumentVersion docKey =new DocumentVersion(docName, versionTag);                    
            
            String docRefLabel = docNameToRefLabel.get(docKey);                       
                        
            if (docRefLabel==null){
                docRefLabel = "(REF"+(docNameToRefLabel.size()+1)+")";
                docNameToRefLabel.put(docKey, docRefLabel);
                refDetailsOrderedList.add(docKey);
            }                                                                      

            // Define options for an 'anchor' element:
            Map<String, Object> options = new HashMap<>();
            options.put("type", ":link");
            options.put("target", "#"+docKey.toString());            
            
            // Create the 'anchor' node:
            PhraseNode inlineRefDocLink = createPhraseNode(contentNode, "anchor", docRefLabel, attributes, options);
            return inlineRefDocLink.convert();
        } else {
            throw new RuntimeException();
        }


    }
    
}
