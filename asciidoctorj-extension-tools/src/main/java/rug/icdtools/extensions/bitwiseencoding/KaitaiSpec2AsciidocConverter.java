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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

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

    private static Object parseYamlFile(File f) throws FileNotFoundException{
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(f);
        return yaml.load(inputStream);
    }
    
    
    private static String getKaitaiSpecId(Object content) throws FileNotFoundException, MalformedKaitaiSpecificationException {
        
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
        
        assert kaitaiSpecId!=null;
        return kaitaiSpecId;
    }

    private static boolean isEndiannessDefined(Object content) throws FileNotFoundException, MalformedKaitaiSpecificationException {        
        if (content instanceof Map){
            Object metadata = ((Map<String, Object>) content).get("meta");
            if (metadata!=null && metadata instanceof Map){
                Object endianness = ((Map<String, Object>) metadata).get("bit-endian");
                return (endianness!=null && endianness instanceof String);
            }
        }
        
        return false;
                
    }                
     
    static String readFileToString(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new String(encoded, encoding);
    }
    
    
    public static void convertAndAddToOutput(File kaitaiSpecFile, StructuralNode parent, BaseProcessor asccidocProcessor) throws CommandGeneratedException, FileNotFoundException, IOException, CommandExecutionException, MalformedKaitaiSpecificationException {

        ///kaitai/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler
        //generate Java code
        //dot -Tsvg rs232test_2.dot > image.svg
        ///tmp/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler --target graphviz x.ksy
        
        //Default command (defined in docker image)
        //TODO add environment variable for KAITAI location to make this context independent
        String kaitaiCommand = "/kaitai/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler";
        
        //String kaitaiCommand = "/home/hcadavid/apps/kaitai/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler";
        
        Path outputPath = Paths.get(System.getProperty("OUTPUT_PATH"));
        
        Object kaitaiSpec = parseYamlFile(kaitaiSpecFile);
        
        String kaitaiSpecId=getKaitaiSpecId(kaitaiSpec);
        
        if (!isEndiannessDefined(kaitaiSpec)){
            throw new MalformedKaitaiSpecificationException("The RS232 bitwise encoding definition does not explicitly define endiannes.");
        }
        
        //System.out.println("ID:"+kaitaiSpecId);
        
        Path tempOutputFolder = Files.createTempDirectory("kaitai-svg-output-");

        //Generate header
        DocProcessLogger.getInstance().log("Generating C header for handling bitwise encoding: "+kaitaiSpecId, Severity.INFO);
        CommandRunner.runCommand(kaitaiCommand, "--target", "cpp_stl", "--outdir",outputPath.toString() , kaitaiSpecFile.getAbsolutePath());        
        
        CommandRunner.runCommand(kaitaiCommand, "--target", "html", "--outdir",tempOutputFolder.toString() , kaitaiSpecFile.getAbsolutePath());        

        CommandRunner.runCommand(kaitaiCommand, "--target", "graphviz", "--outdir",tempOutputFolder.toString() , kaitaiSpecFile.getAbsolutePath());
        
        CommandRunner.runCommand("dot", "-Tsvg", tempOutputFolder.resolve(kaitaiSpecId+".dot").toFile().getAbsolutePath(), "-O" );
                               
        List<String> newOutputAsciidocLines=new LinkedList<>();
        
        //Embed html representation of the bitwise encoding
        String htmlRepresentation = readFileToString(tempOutputFolder.resolve(kaitaiSpecId+".html").toFile(),Charset.defaultCharset());
        String table = StringUtils.substringBetween(htmlRepresentation, "<div class=\"container\">", "</div>");
        //change table headers' size
        table = table.replaceAll("<\\/h[12]>", "</h4>").replaceAll("<h[12]>", "<h4>");
        
        newOutputAsciidocLines.add("pass:[");
        newOutputAsciidocLines.add(table);
        newOutputAsciidocLines.add("]");
        
        newOutputAsciidocLines.add("<h4>Bitwise encoding diagram</h4>");        
        
        //Embed SVG representation of the bitwise encoding        
        newOutputAsciidocLines.add("pass:[");
        
        try ( Scanner s = new Scanner(tempOutputFolder.resolve(kaitaiSpecId+".dot.svg").toFile())) {
            while (s.hasNext()) {
                newOutputAsciidocLines.add(s.next());
            }
        }
        
        newOutputAsciidocLines.add("]");
        
        DocProcessLogger.getInstance().log("Generating Graphviz representation of bitwise encoding: "+kaitaiSpecId, Severity.INFO);

        String copyHeaderButtonAction =  
                "var headerName='%s';var url = window.location.href.substring(0, window.location.href.lastIndexOf('/'))+'/'+headerName;" +
                "navigator.clipboard.writeText(url);"+
                "if (confirm('Copy the location of the generated header file to your clipboard? ('+url+')') == true) {"+
                "    navigator.clipboard.writeText(url); }";
                
                       
        newOutputAsciidocLines.add(String.format("pass:[<button title =\"Copy generated header file location to your clipboard\"          onClick=\""+copyHeaderButtonAction+"\">Copy header's file location</button>]",kaitaiSpecId+".h"));
        
        asccidocProcessor.parseContent(parent, newOutputAsciidocLines);

    }

}
