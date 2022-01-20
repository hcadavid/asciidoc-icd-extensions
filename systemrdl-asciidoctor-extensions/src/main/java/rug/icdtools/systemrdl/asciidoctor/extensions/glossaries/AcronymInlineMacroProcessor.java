/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.glossaries;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.extension.InlineMacroProcessor;
import rug.icdtools.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class AcronymInlineMacroProcessor extends InlineMacroProcessor {

    private final static Set<String> acronymsInstancesSet = new LinkedHashSet<>();
    
    public static Set<String> acronymsInstances(){
        return acronymsInstancesSet;
    }
    
   @Override
    public Object process(ContentNode contentNode, String term, Map<String, Object> attributes) {


        // Define options for an 'anchor' element:
        Map<String, Object> options = new HashMap<>();
        options.put("type", ":link");
        options.put("target", "#"+term);
        acronymsInstancesSet.add(term);

        // Create the 'anchor' node:
        PhraseNode glossaryAnchorLink = createPhraseNode(contentNode, "anchor", term, attributes, options);

        // Convert to String value:
        return glossaryAnchorLink.convert();
    }

}
