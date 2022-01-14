/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.glossaries;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import rug.icdtools.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class GlossaryPostProcessor extends Postprocessor {

    private static final String COL_GROUP = "<colgroup> <col style=\"width: 20%;\"> <col style=\"width: 80%;\"></colgroup>";
    private static final String TABLE_HEADER = "<thead><tr><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th></tr></thead> ";
    private static final String TAB_BODY_OPEN = "<tbody>";
    private static final String TAB_ROW = "<tr> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td></tr>";
    private static final String TAB_BODY_CLOSE = "</tbody>";

    private static final String WORD_ANCHOR="<a id=\"%s\">%s</a>";
    
    @Override
    public String process(Document dcmnt, String output) {
        org.jsoup.nodes.Document doc = Jsoup.parse(output, "UTF-8");

        Element glossaryPlaceholder = doc.getElementById(GlossaryPlacementBlockProcessor.GLOSSARY_PLACEMENT_ID);

        //StringBuffer sb = new StringBuffer(tabledef);
        StringBuilder sb = new StringBuilder();
        sb.append(COL_GROUP);
        sb.append(String.format(TABLE_HEADER, "Acronym", "Description"));
        sb.append(TAB_BODY_OPEN);

        for (int i = 0; i < 20; i++) {
            String word = "word"+i;
            String meaning = "aaa bbb ccc";
            
            sb.append(String.format(TAB_ROW, String.format(WORD_ANCHOR,word,word), "meaning" + i));
        }

        sb.append(TAB_BODY_CLOSE);
        //sb.append(tableclose);

        if (glossaryPlaceholder != null) {
            glossaryPlaceholder.text(sb.toString());
        }

        return doc.html().replace("&lt;", "<").replace("&gt;", ">");

    }

}
