package rug.icdtools.dummy.extension;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import rug.icdtools.customdiag.RandomDiagramMacroProcessor;

public class DummyMacroExtension implements ExtensionRegistry {

    public void register(Asciidoctor asciidoctor) {
        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();
        javaExtensionRegistry.inlineMacro("dummy", DummyMacro.class);
        javaExtensionRegistry.blockMacro("dummydiag", RandomDiagramMacroProcessor.class);
    }

}
