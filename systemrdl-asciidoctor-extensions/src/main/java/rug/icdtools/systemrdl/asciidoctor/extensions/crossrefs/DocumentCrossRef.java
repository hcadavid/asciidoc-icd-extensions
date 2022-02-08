/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.crossrefs;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.InlineMacroProcessor;

/**
 *
 * @author hcadavid
 */
public class DocumentCrossRef extends InlineMacroProcessor {

    private final static Set<String> referencesSet = new LinkedHashSet<>();
    
    @Override
    public Object process(ContentNode t, String string, Map<String, Object> map) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
