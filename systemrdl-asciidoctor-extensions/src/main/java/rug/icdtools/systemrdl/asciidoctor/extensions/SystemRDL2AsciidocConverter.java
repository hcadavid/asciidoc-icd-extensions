/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BaseProcessor;
import rug.icdtools.interfacing.externaltools.ExternalCommandExecutionException;
import rug.icdtools.interfacing.externaltools.ExternalCommandRunner;

/**
 *
 * @author hcadavid
 */
public class SystemRDL2AsciidocConverter {

    public static void convertAndAddToOutput(String registryMapName, File input, StructuralNode parent, BaseProcessor asccidocProcessor) throws ExternalCommandExecutionException, FileNotFoundException, IOException {

        File tmpFile = File.createTempFile("sysardl2adoc-", ".converter_output", null);

        ExternalCommandRunner.runCommand("sh", "sysrdl2jinja/convert_to_adoc.sh", input.getAbsolutePath(), tmpFile.getAbsolutePath());

        List<String> newOutputAsciidocLines;
        try ( Scanner s = new Scanner(tmpFile)) {
            newOutputAsciidocLines = new LinkedList<>();
            while (s.hasNext()) {
                newOutputAsciidocLines.add(s.next());
            }
        }

        //generate a C header and a link to it
        Path outputPath = Paths.get(System.getProperty("OUTPUT_PATH"));
        String headerFileName = registryMapName + ".h";
        ExternalCommandRunner.runCommand("sh", "sysrdl2jinja/convert_to_cheader.sh", input.getAbsolutePath(), outputPath.resolve(headerFileName).toFile().getAbsolutePath());

        newOutputAsciidocLines.add(String.format("link:%s[%s]", headerFileName, "C header"));

        asccidocProcessor.parseContent(parent, newOutputAsciidocLines);

    }

}
