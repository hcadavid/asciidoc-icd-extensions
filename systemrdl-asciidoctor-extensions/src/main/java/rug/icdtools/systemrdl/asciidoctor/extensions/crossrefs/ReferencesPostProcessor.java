/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.crossrefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.Severity;
import rug.icdtools.systemrdl.asciidoctor.extensions.glossaries.sources.GlossaryDataSourceFactory;

/**
 *
 * @author hcadavid
 */
public class ReferencesPostProcessor extends Postprocessor {

    
    private static final String COL_GROUP = "<colgroup> <col style=\"width: 20%;\"> <col style=\"width: 80%;\"></colgroup>";
    private static final String TABLE_HEADER = "<thead><tr><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th></tr></thead> ";
    private static final String TAB_BODY_OPEN = "<tbody>";
    private static final String TAB_ROW = "<tr id=\"%s\"> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td></tr>";
    private static final String TAB_BODY_CLOSE = "</tbody>";

    
    @Override
    public String process(Document dcmnt, String output) {
                       
        org.jsoup.nodes.Document doc = Jsoup.parse(output, "UTF-8");

        Element glossaryPlaceholder = doc.getElementById(ReferencesPlacementBlockProcessor.REFERENCES_PLACEMENT_ID);

        StringBuilder sb = new StringBuilder();
        sb.append(COL_GROUP);
        sb.append(String.format(TABLE_HEADER, "Reference", "Document description"));
        sb.append(TAB_BODY_OPEN);

        
        Map<String,String> refs = DocumentCrossRefMacroProcessor.getRefToDocNameMap();
        
        /*Set<String> docInlineAcronyms = AcronymInlineMacroProcessor.acronymsInstances();
        List<String> sortedAcronyms = new ArrayList<>(docInlineAcronyms);
        Collections.sort(sortedAcronyms);
        */
        
        DocProcessLogger.getInstance().log("Generating references table with "+refs.size()+" terms.", Severity.DEBUG);
        
        for (String refEntry:refs.keySet()){

            String description = String.format("<a href=\"http://reftodoc.com/%s\">Version 1.0 description</a> ",refs.get(refEntry));
            
            sb.append(String.format(TAB_ROW,refEntry,refEntry,description));            
            
        }


        sb.append(TAB_BODY_CLOSE);

        if (glossaryPlaceholder != null) {
            glossaryPlaceholder.text(sb.toString());
        }

        //Reset acronyms instances so existing ones are not included
        //on other documents processed during the build process.
        DocumentCrossRefMacroProcessor.resetReferencesMap();
        
        return doc.html().replace("&lt;", "<").replace("&gt;", ">");

    }

}
