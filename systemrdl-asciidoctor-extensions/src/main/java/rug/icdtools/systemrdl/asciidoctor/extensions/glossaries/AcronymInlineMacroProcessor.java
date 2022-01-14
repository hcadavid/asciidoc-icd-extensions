/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.glossaries;

import java.util.HashMap;
import java.util.Map;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.extension.InlineMacroProcessor;
import rug.icdtools.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class AcronymInlineMacroProcessor extends InlineMacroProcessor {

   @Override
    public Object process(ContentNode contentNode, String term, Map<String, Object> attributes) {


        // Define options for an 'anchor' element:
        Map<String, Object> options = new HashMap<>();
        options.put("type", ":link");
        options.put("target", "www.google.com");

        // Create the 'anchor' node:
        PhraseNode inlineTwitterLink = createPhraseNode(contentNode, "anchor", term, attributes, options);

        // Convert to String value:
        return inlineTwitterLink.convert();
    }

}
