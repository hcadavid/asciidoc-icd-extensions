/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.sysrdl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.FilenameUtils;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import rug.icdtools.interfacing.externaltools.ExternalCommandExecutionException;
import rug.icdtools.logging.Logger;
import rug.icdtools.logging.Severity;

/**
 * MacroProcessor for systemrdl::rdlfilename[]
 *
 * @author hcadavid
 */
public class SystemRDLBlockMacroProcessor extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> map) {

        Logger.getInstance().log("BlockMacro processor with:" + target, Severity.DEBUG);

        try {

            //target is expectd to be relative to ASCIIDOC document's source
            Path inputPath = Paths.get(System.getProperty("ASCIIDOC_SOURCE_PATH"));

            //resolve path of the RDL file source code (asciidoc source / target)
            Path rdlSourcePath = inputPath.resolve(target);

            if (!rdlSourcePath.toFile().exists()) {
                Logger.getInstance().log(String.format("SystemRDL file given as a target (%s) in systemrdl:: macro (line %s in file %s) was not found:",target,parent.getSourceLocation().getLineNumber(),parent.getSourceLocation().getFile()), Severity.ERROR);
            } else {
                SystemRDL2AsciidocConverter.convertAndAddToOutput(FilenameUtils.removeExtension(target), rdlSourcePath.toFile(), parent, this);
            }

        } catch (ExternalCommandExecutionException ex) {
            Logger.getInstance().log(String.format("Error while evaluating the systemrdl:: macro (line %s in file %s): %s",parent.getSourceLocation().getLineNumber(),parent.getSourceLocation().getFile(),ex.getLocalizedMessage()), Severity.ERROR);
        } catch (IOException ex) {             
            Logger.getInstance().log(String.format("Internal error while executing the systemrdl:: macro (line %s in file %s): %s",parent.getSourceLocation().getLineNumber(),parent.getSourceLocation().getFile(),ex.getLocalizedMessage()), Severity.FATAL);            
        }
        
        //add no further elements to the document
        return null;
               

    }
}
