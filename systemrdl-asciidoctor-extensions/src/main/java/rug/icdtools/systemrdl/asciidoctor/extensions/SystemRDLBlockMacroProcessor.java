/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.commons.io.FilenameUtils;
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
                      
            //resolve path of the RDL file source code (asciidoc source / target)
            Path rdlSourcePath = inputPath.resolve(target);

            
            Logger.getInstance().log("$$$%%%>>>" + new File(".").getAbsolutePath());

            SystemRDL2AsciidocConverter.convertAndAddToOutput(FilenameUtils.removeExtension(target),rdlSourcePath.toFile(), parent, this);
            
            return null;

        } catch (ExternalCommandExecutionException ex) {
            Logger.getInstance().log("ERROR >>>>" + ex.getLocalizedMessage());
            throw new RuntimeException(ex);
        } catch (FileNotFoundException ex) {
            Logger.getInstance().log("ERROR >>>>" + ex.getLocalizedMessage());
            java.util.logging.Logger.getLogger(SystemRDLBlockMacroProcessor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            Logger.getInstance().log("ERROR >>>>" + ex.getLocalizedMessage());
            throw new RuntimeException(ex);
            
        }

    }
}
