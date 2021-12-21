/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rug.icdtools.customdiag;

import java.util.HashMap;
import java.util.Map;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import rug.icdtools.logging.Logger;

/**
 *
 * @author hcadavid
 * base: https://github.com/sclassen/sessions/blob/1c64202b3f5cdea5fcfd46bf2cefeffad22cca9c/javaland2017/asciidoctor-extensions/asciidoctor-demo/README.adoc
 * dummydiag::sampleparam[]
 */
public class RandomDiagramMacroProcessor extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> map) {
        
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("target", "/tmp/img.png");
        Logger.log("E>>>>>>>RANDOM DIAGRAM MACRO PROCESSOR");        
        
        return this.createBlock(parent, "image", "", attributes);
        
        //createBlock(parent, "image", "", [
        //        target: imagePath
        //], [:])
    }
   
    
    
    
}
