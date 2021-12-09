package rug.icdtools.dummy.extension;

import com.sun.tools.sjavac.Log;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.extension.InlineMacroProcessor;

import java.util.HashMap;
import java.util.Map;

import rug.icdtools.logging.Logger;

public class DummyMacro extends InlineMacroProcessor {
    
   
    
    public DummyMacro(String macroName) {
        super(macroName);        
        //Logger.getLogger("asciidoctor").setUseParentHandlers(false);
    }

    @Override
    public Object process(ContentNode contentNode, String macroValue, Map<String, Object> attributes) {

        String twitterLink=null;
        String twitterLinkText=null;                                
        
        if (macroValue == null || macroValue.isEmpty() || macroValue.equals("dummy")) {
            Logger.log("TEST OF A CUSTOM LOGGER WITHIN DUMMY MACRO");
        } else {
            twitterLink = "https://www.twitter.com/" + macroValue;
            // Prepend twitterHandle with @ as text link:
            twitterLinkText = "@:\\---" + macroValue;
        }

        // Define options for an 'anchor' element:
        Map<String, Object> options = new HashMap<>();
        options.put("type", ":link");
        options.put("target", macroValue);

        // Create the 'anchor' node:
        PhraseNode inlineTwitterLink = createPhraseNode(contentNode, "anchor", twitterLinkText, attributes, options);

        // Convert to String value:
        return inlineTwitterLink.convert();
    }
}
