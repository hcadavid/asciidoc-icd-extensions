/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.registry;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import rug.icdtools.systemrdl.asciidoctor.extensions.SystemRDLBlockMacroProcessor;
import rug.icdtools.systemrdl.asciidoctor.extensions.SystemRDLBlockProcessor;

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
    }
    
}
