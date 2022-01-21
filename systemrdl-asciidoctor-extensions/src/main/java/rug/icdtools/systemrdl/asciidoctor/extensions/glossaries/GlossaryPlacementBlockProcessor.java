/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.glossaries;

import java.util.Map;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;

/**
 *
 * @author hcadavid
 */
public class GlossaryPlacementBlockProcessor extends BlockMacroProcessor{

    public static final String GLOSSARY_PLACEMENT_ID="rug_icdtools_GlossaryPlaceholder";
    
    @Override
    public Object process(StructuralNode parent, String string, Map<String, Object> map) {
        String output = String.format("<table class=\"tableblock frame-all grid-all stretch\" id=\"%s\"></table>", GLOSSARY_PLACEMENT_ID);
        return createBlock(parent,"pass",output);
    }
    
}
