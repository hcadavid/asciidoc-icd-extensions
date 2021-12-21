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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
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

    private final static Path DIAGRAMS_FOLDER=Paths.get("images");
    private final static String PACKETDIAG_BIN="/usr/local/bin/packetdiag";
    
    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> map) {
        try{
            
            Logger.log("E>>>>>>>RANDOM DIAGRAM MACRO PROCESSOR:"+System.getProperty("OUTPUT_PATH"));        
            
            //target is expectd to be relative to ASCIIDOC document's source
            Path inputPath =Paths.get(System.getProperty("ASCIIDOC_SOURCE_PATH"));                        
            Path outputPath=Paths.get(System.getProperty("OUTPUT_PATH"));
            
            //resolve path of the diagram's source code (asciidoc source / target)
            Path diagramSourcePath = inputPath.resolve(target);                               
            
            //TODO to be defined: replicate hierarchy of the source? 
            String outputFileName = inputPath.getFileName()+".png";
            
            //create output folder if it doesnt exist
            Files.createDirectories(outputPath.resolve(DIAGRAMS_FOLDER));
            
            //output's absolute path
            Path genDiagramPath = outputPath.resolve(DIAGRAMS_FOLDER).resolve(outputFileName);

            Path relativePath = DIAGRAMS_FOLDER.resolve(outputFileName);
            
            ExternalCommandRunner.runCommand(PACKETDIAG_BIN,"-o", genDiagramPath.toFile().getAbsolutePath(), diagramSourcePath.toFile().getAbsolutePath());
            
            //get filename from source
            //runCoExtermmand("/usr/local/bin/packetdiag","/tmp/src/diags/tscp2.diag");
            Map<String,Object> attributes = new HashMap<>();
            attributes.put("target", "images/.png");

            //relative reference to generated image on the document
            return this.createBlock(parent, "image", relativePath.toString(), attributes);
            
        } catch (IOException e){
            return null;
        } catch (ExternalCommandExecutionException ex) {
            return new RuntimeException(ex);
        }
        
       
        //packetdiag tcp2.diag
        
        //createBlock(parent, "image", "", [
        //        target: imagePath
        //], [:])
    }

    private static Process executeCommands(String... commands) throws IOException{
        Process process = null;
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File("path_to_working_directory")); //Set current directory
        pb.redirectError(new File("path_to_log_file")); //Log errors in specified log file.        
        process = pb.start();      
        return process;
    }


}
