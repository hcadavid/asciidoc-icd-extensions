/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.sysrdl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Reader;
import rug.icdtools.interfacing.externaltools.CommandExecutionException;
import rug.icdtools.interfacing.externaltools.CommandGeneratedException;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class SystemRDLBlockProcessor extends BlockProcessor {

    @SuppressWarnings("serial")
    private static Map<String, Object> configs = new HashMap<String, Object>() {
        {
            put("contexts", Arrays.asList(":listing"));
            put("content_model", ":compound");
        }
    };

    public SystemRDLBlockProcessor(String name, Map<String, Object> config) {
        super(name, configs);
    }

    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {

        try {
            //create temporal file with the SystemRDL with the inline SystemRDL specification
            File tmpInput = File.createTempFile("sysrdl2asciidoc", ".rdlsource", null);

            try (FileWriter fileWriter = new FileWriter(tmpInput)) {
                List<String> lines = reader.readLines();
                
                for (String line : lines) {
                    fileWriter.write(line);
                }
            }
                       
            String regMapName;
            Object regMapNameAttValue=attributes.get("regmapname");
            
            regMapName = regMapNameAttValue!=null && regMapNameAttValue instanceof String?(String)regMapNameAttValue:RandomStringUtils.randomAlphabetic(12).toUpperCase();
            SystemRDL2AsciidocConverter.convertAndAddToOutput(regMapName,tmpInput, parent, this);

            DocProcessLogger.getInstance().log("Executing inline SystemRDL Block Processor",Severity.DEBUG);
    
        } catch (CommandGeneratedException ex) {
            DocProcessLogger.getInstance().log(String.format("Error while evaluating the embedded systemrdl specification given on the [systemrdl] macro (line %s in file %s): %s",parent.getSourceLocation().getLineNumber(),parent.getSourceLocation().getFile(),ex.getLocalizedMessage()), Severity.ERROR);
            parseContent(parent, Arrays.asList(new String[]{"WARNING: [systemrdl] block not generated during the building process due to an error (see details on the log files)"}));
        } catch (IOException | CommandExecutionException ex) {             
            DocProcessLogger.getInstance().log(String.format("Internal error while evaluating the embedded systemrdl specification given on the [systemrdl] macro (line %s in file %s): %s",parent.getSourceLocation().getLineNumber(),parent.getSourceLocation().getFile(),ex.getLocalizedMessage()), Severity.FATAL);            
            parseContent(parent, Arrays.asList(new String[]{"WARNING: systemrdl:: block not generated during the building process due to an error (see details on the log files)"})); 
        } 
        
        //add no further elements to the document
        return null;


    }

}
