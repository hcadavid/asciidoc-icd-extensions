/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.registry;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import rug.icdtools.systemrdl.asciidoctor.extensions.crossrefs.DocumentCrossRefMacroProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.crossrefs.ReferencesPlacementBlockProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.crossrefs.ReferencesPostProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.sysrdl.SystemRDLBlockMacroProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.sysrdl.SystemRDLBlockProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.glossaries.AcronymInlineMacroProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.glossaries.GlossaryPlacementBlockProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.glossaries.GlossaryPostProcessor;

/**
 *
 * @author hcadavid
 */
public class SystemRDLExtensionRegistry implements ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();
        javaExtensionRegistry.blockMacro("systemrdl", SystemRDLBlockMacroProcessor.class);
        javaExtensionRegistry.block("systemrdl", SystemRDLBlockProcessor.class);        
        
        javaExtensionRegistry.inlineMacro("acr", AcronymInlineMacroProcessor.class);        
        javaExtensionRegistry.blockMacro("glossary", GlossaryPlacementBlockProcessor.class);
        javaExtensionRegistry.postprocessor(GlossaryPostProcessor.class);
        
        javaExtensionRegistry.inlineMacro("docref", DocumentCrossRefMacroProcessor.class);
        javaExtensionRegistry.blockMacro("references",ReferencesPlacementBlockProcessor.class);
        javaExtensionRegistry.postprocessor(ReferencesPostProcessor.class);
        
    }
    
}
