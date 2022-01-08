/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Reader;
import rug.icdtools.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class SystemRDLBlockProcessor extends BlockProcessor {

    
    @SuppressWarnings("serial")
    private static Map<String, Object> configs = new HashMap<String, Object>() {{
        put("contexts", Arrays.asList(":listing"));
        put("content_model", ":compound");
    }};

    public SystemRDLBlockProcessor(String name, Map<String, Object> config) {
        super(name, configs);
    }
    
    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
        List<String> lines = reader.readLines();
        String upperLines = null;
        for (String line : lines) {
            if (upperLines == null) {                
                upperLines = line.toUpperCase();
            } else {
                upperLines = upperLines + "\n" + line.toUpperCase();                
            }
            
        }
        Logger.getInstance().log("$$$$$ - Block processor with:"+upperLines);

        return createBlock(parent, "paragraph", Arrays.asList(upperLines), attributes, new HashMap<>());
    }
    
}
