/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.bitwiseencoding;

import rug.icdtools.extensions.sysrdl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BaseProcessor;
import org.yaml.snakeyaml.Yaml;
import rug.icdtools.core.logging.DocProcessLogger;
import rug.icdtools.core.logging.Severity;
import rug.icdtools.interfacing.localcommands.CommandExecutionException;
import rug.icdtools.interfacing.localcommands.CommandGeneratedException;
import rug.icdtools.interfacing.localcommands.CommandRunner;

/**
 *
 * @author hcadavid
 */
public class KaitaiSpec2AsciidocConverter {

    private static String getKaitaiSpecId(File f) throws FileNotFoundException, MalformedKaitaiSpecificationException {
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(f);

        Object content = yaml.load(inputStream);

        String kaitaiSpecId=null;
        
        if (!(content instanceof Map)) {
            throw new MalformedKaitaiSpecificationException("Invalid KAITAI specification format");
        } else {
            Object metadata = ((Map<String, Object>) content).get("meta");
            if (metadata == null || !(metadata instanceof Map)) {
                throw new MalformedKaitaiSpecificationException("No metadata in KAITAI specification");
            } else {
                Object id = ((Map<String, Object>) metadata).get("id");
                if (id == null) {
                    throw new MalformedKaitaiSpecificationException("No ID defined in KAITAI metadata");
                } else {
                    kaitaiSpecId = (String) id;
                }
            }

        }
        
        return kaitaiSpecId;
    }

    
    
    
    public static void convertAndAddToOutput(File kaitaiSpecFile, StructuralNode parent, BaseProcessor asccidocProcessor) throws CommandGeneratedException, FileNotFoundException, IOException, CommandExecutionException, MalformedKaitaiSpecificationException {

        ///kaitai/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler
        //generate Java code
        //dot -Tsvg rs232test_2.dot > image.svg
        ///tmp/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler --target graphviz x.ksy
        
        Path outputPath = Paths.get(System.getProperty("OUTPUT_PATH"));
        
        String kaitaiSpecId=getKaitaiSpecId(kaitaiSpecFile);
        
        //System.out.println("ID:"+kaitaiSpecId);
        
        Path tempOutputFolder = Files.createTempDirectory("kaitai-svg-output-");
                
        CommandRunner.runCommand("/home/hcadavid/apps/kaitai/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler", "--target", "graphviz", "--outdir",tempOutputFolder.toString() , kaitaiSpecFile.getAbsolutePath());

        CommandRunner.runCommand("dot", "-Tsvg", tempOutputFolder.resolve(kaitaiSpecId+".dot").toFile().getAbsolutePath(), "-O" );
                       
        //.dov.csv file

        List<String> newOutputAsciidocLines=new LinkedList<>();
        
        newOutputAsciidocLines.add("pass:[");
        
        try ( Scanner s = new Scanner(tempOutputFolder.resolve(kaitaiSpecId+".dot.svg").toFile())) {
            while (s.hasNext()) {
                newOutputAsciidocLines.add(s.next());
            }
        }
        
        newOutputAsciidocLines.add("]");
        
        DocProcessLogger.getInstance().log("Generating Graphviz representation of bitwise encoding: "+kaitaiSpecId, Severity.INFO);
        
        asccidocProcessor.parseContent(parent, newOutputAsciidocLines);

    }

}
