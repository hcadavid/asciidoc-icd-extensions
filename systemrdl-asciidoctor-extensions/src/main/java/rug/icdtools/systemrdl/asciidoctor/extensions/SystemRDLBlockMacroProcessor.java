/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions;

import java.util.Map;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import rug.icdtools.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class SystemRDLBlockMacroProcessor extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode t, String string, Map<String, Object> map) {
        
        Logger.getInstance().log("$$$$$ - BlockMacro processor with:"+string);
        String yellContent = string.toUpperCase();

        return createBlock(t, "paragraph", yellContent, map);
    }
    
}
