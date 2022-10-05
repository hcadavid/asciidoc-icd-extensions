/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.extensions.bitwiseencoding;

import rug.icdtools.extensions.sysrdl.*;
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
import rug.icdtools.interfacing.localcommands.CommandExecutionException;
import rug.icdtools.interfacing.localcommands.CommandGeneratedException;
import rug.icdtools.core.logging.DocProcessLogger;
import rug.icdtools.core.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class KaitaiSpecBlockProcessor extends BlockProcessor {

    @SuppressWarnings("serial")
    private static Map<String, Object> configs = new HashMap<String, Object>() {
        {
            put("contexts", Arrays.asList(":listing"));
            put("content_model", ":compound");
        }
    };

    public KaitaiSpecBlockProcessor(String name, Map<String, Object> config) {
        super(name, configs);
    }

    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {

        try {
            
            //create temporal file with the Kaitai specification
            
            File tmpInput = File.createTempFile("kaitaispec", ".ksy", null);
            
            
            try ( FileWriter fileWriter = new FileWriter(tmpInput)) {
                List<String> lines = reader.readLines();

                for (String line : lines) {
                    fileWriter.write(line+"\n");
                }
            }
                        
            KaitaiSpec2AsciidocConverter.convertAndAddToOutput(tmpInput, parent, this);

            DocProcessLogger.getInstance().log("Executing inline rs232encoding Block Processor", Severity.DEBUG);

        } catch (CommandGeneratedException ex) {
            DocProcessLogger.getInstance().log(String.format("Error on the rs232encoding specification (line %s in file %s): %s", parent.getSourceLocation().getLineNumber(), parent.getSourceLocation().getFile(), ex.getLocalizedMessage()), Severity.FAILED_QGATE);
            parseContent(parent, Arrays.asList(new String[]{"WARNING: [rs232encoding] block not generated during the building process due to an error (see details on the log files)"}));
        } catch (IOException | CommandExecutionException ex) {
            DocProcessLogger.getInstance().log(String.format("Internal error while processing the [rs232encoding] macro (line %s in file %s): %s", parent.getSourceLocation().getLineNumber(), parent.getSourceLocation().getFile(), ex.getLocalizedMessage()), Severity.FATAL);
            parseContent(parent, Arrays.asList(new String[]{"WARNING: systemrdl:: block not generated during the building process due to an error (see details on the log files)"}));
        } catch (MalformedKaitaiSpecificationException ex) {
            DocProcessLogger.getInstance().log(String.format("Error on the rs232encoding specification (line %s in file %s): %s", parent.getSourceLocation().getLineNumber(), parent.getSourceLocation().getFile(), ex.getLocalizedMessage()), Severity.FAILED_QGATE);
            parseContent(parent, Arrays.asList(new String[]{"WARNING: [rs232encoding] block not generated during the building process due to an error (see details on the log files)"}));
        }

        //add no further elements to the document
        return null;

    }

}
