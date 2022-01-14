/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.glossaries;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author hcadavid
 */
public class GlossaryPostProcessor extends Postprocessor {

    private static String tabledef = "<table class=\"tableblock frame-all grid-all stretch\"><colgroup> <col style=\"width: 20%;\"> <col style=\"width: 80%;\"></colgroup>";
    private static String tableHeader = "<thead><tr><th class=\"tableblock halign-left valign-top\">%s</th><th class=\"tableblock halign-left valign-top\">%s</th></tr></thead> ";      
    private static String tbodyopen = "<tbody>";       
    private static String trow = "<tr> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td> <td class=\"tableblock halign-left valign-top\"><p class=\"tableblock\">%s</p></td></tr>";
    private static String tbodyclose = "</tbody>" ;
    private static String tableclose = "</table>" ;
    
    
    
    @Override
    public String process(Document dcmnt, String output) {
       org.jsoup.nodes.Document doc = Jsoup.parse(output, "UTF-8"); 
       
       Element glossaryPlaceholder = doc.getElementById(GlossaryPlacementBlockProcessor.GLOSSARY_PLACEMENT_ID);  
       
       StringBuffer sb = new StringBuffer(tabledef);
       sb.append(String.format(tableHeader,"Acronym","Description"));
       sb.append(tbodyopen);

       for (int i=0;i<20;i++){
           sb.append(String.format(trow, "word"+i,"meaning"+i));
       }
       
       sb.append(tbodyclose);
       sb.append(tableclose);
       
       if (glossaryPlaceholder!=null){
           glossaryPlaceholder.text(sb.toString());
       }
       
       return doc.html();
       
    }
    
}


/*

<table class="tableblock frame-all grid-all stretch"> 
      <colgroup> 
       <col style="width: 20%;"> 
       <col style="width: 80%;"> 
      </colgroup> 
      <thead> 
       <tr> 
        <th class="tableblock halign-left valign-top">term</th> 
        <th class="tableblock halign-left valign-top">tjss</th> 
       </tr> 
      </thead> 
      <tbody> 
       <tr> 
        <td class="tableblock halign-left valign-top"><p class="tableblock">word</p></td> 
        <td class="tableblock halign-left valign-top"><p class="tableblock">meaning1</p></td> 
       </tr> 
       <tr> 
        <td class="tableblock halign-left valign-top"><p class="tableblock">word</p></td> 
        <td class="tableblock halign-left valign-top"><p class="tableblock">meaning2</p></td> 
       </tr> 
       <tr> 
        <td class="tableblock halign-left valign-top"><p class="tableblock">word</p></td> 
        <td class="tableblock halign-left valign-top"><p class="tableblock">meaning3</p></td> 
       </tr> 
      </tbody> 
     </table> 

*/