/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import rug.icdtools.interfacing.externaltools.ExternalCommandExecutionException;
import rug.icdtools.interfacing.externaltools.ExternalCommandRunner;
import rug.icdtools.logging.Logger;

/**
 * MacroProcessor for systemrdl::rdlfilename[]
 *
 * @author hcadavid
 */
public class SystemRDLBlockMacroProcessor extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> map) {

        Logger.getInstance().log("$$$$$ - BlockMacro processor with:" + target);

        try {

            //target is expectd to be relative to ASCIIDOC document's source
            Path inputPath = Paths.get(System.getProperty("ASCIIDOC_SOURCE_PATH"));
            Path outputPath = Paths.get(System.getProperty("OUTPUT_PATH"));

            //resolve path of the RDL file source code (asciidoc source / target)
            Path rdlSourcePath = inputPath.resolve(target);

            //TODO to be defined: replicate hierarchy of the source? 
            String outputFileName = rdlSourcePath.getFileName() + ".adoc";

            //output's absolute path
            Path outputAbsolutePath = outputPath.resolve(outputFileName);

            //TODO externalize command
            ExternalCommandRunner.runCommand("sysrdl2jinja", rdlSourcePath.toFile().getAbsolutePath(), outputAbsolutePath.toFile().getAbsolutePath());

            //include::table1.adoc[]
            List<String> newOutputAsciidocLines = Arrays.asList("include::" + outputFileName + "[]");

            parseContent(parent, newOutputAsciidocLines);

            return null;

        } catch (ExternalCommandExecutionException ex) {
            Logger.getInstance().log("ERROR >>>>"+ex.getLocalizedMessage());
            throw new RuntimeException(ex);
        }

    }
}