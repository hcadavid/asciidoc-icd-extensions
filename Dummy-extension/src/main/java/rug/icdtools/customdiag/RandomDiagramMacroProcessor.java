/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rug.icdtools.customdiag;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.asciidoctor.extension.Name;
import rug.icdtools.logging.AbstractLogger;
import rug.icdtools.logging.Logger;
import rug.icdtools.logging.Severity;

/**
 *
 * @author hcadavid base:
 * https://github.com/sclassen/sessions/blob/1c64202b3f5cdea5fcfd46bf2cefeffad22cca9c/javaland2017/asciidoctor-extensions/asciidoctor-demo/README.adoc
 * dummydiag::sampleparam[]private final static Path DIAGRAMS_FOLDER =
 * Paths.get("images");
 */
public class RandomDiagramMacroProcessor extends BlockMacroProcessor {

    //asciidoctorj convention - do not change
    private final static Path DIAGRAMS_FOLDER = Paths.get("images");

    //private final static String PACKETDIAG_BIN = "/usr/local/bin/packetdiag";
    private final static String PACKETDIAG_BIN = "/home/hcadavid/.local/bin/packetdiag";

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> map) {
        try {

            Logger.getInstance().log("E>>>>>>>RANDOM DIAGRAM MACRO PROCESSOR:" + target,Severity.DEBUG);            

            //target is expectd to be relative to ASCIIDOC document's source
            Path inputPath = Paths.get(System.getProperty("ASCIIDOC_SOURCE_PATH"));
            Path outputPath = Paths.get(System.getProperty("OUTPUT_PATH"));

            //resolve path of the diagram's source code (asciidoc source / target)
            Path diagramSourcePath = inputPath.resolve(target);

            //TODO to be defined: replicate hierarchy of the source? 
            String outputFileName = diagramSourcePath.getFileName() + ".png";

            //create output folder if it doesnt exist
            Files.createDirectories(outputPath.resolve(DIAGRAMS_FOLDER));
            
            //output's absolute path
            Path outputAbsolutePath = outputPath.resolve(DIAGRAMS_FOLDER).resolve(outputFileName);

            Path outputRelativePath = DIAGRAMS_FOLDER.resolve(outputFileName);

            ExternalCommandRunner.runCommand(PACKETDIAG_BIN, "-o", outputAbsolutePath.toFile().getAbsolutePath(), diagramSourcePath.toFile().getAbsolutePath());

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("target", outputRelativePath.getFileName().toString());

            List<String> lines = Arrays.asList(
                    "[width=\"15%\"]",
                    "|=======",
                    "|1 |2 |A",
                    "|3 |4 |B",
                    "|5 |6 |C",
                    "|=======",
                    "https://asciidoctor.org[]"
                    );

            parseContent(parent, lines);
           

            return this.createBlock(parent, "pass", "<a href=\"rover.rdl\">rover.rdl</a>");

        } catch (IOException e) {
            Logger.getInstance().log("ERROR >>>>"+e.getLocalizedMessage(),Severity.ERROR);
        } catch (ExternalCommandExecutionException ex) {            
            Logger.getInstance().log("ERROR >>>>"+ex.getLocalizedMessage(),Severity.ERROR);
            throw new RuntimeException(ex);
        } finally {
            return null;
        }
            

    }

}
