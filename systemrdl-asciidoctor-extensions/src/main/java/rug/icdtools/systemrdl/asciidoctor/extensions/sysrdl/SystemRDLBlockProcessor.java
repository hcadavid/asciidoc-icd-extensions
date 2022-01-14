/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.systemrdl.asciidoctor.extensions.sysrdl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Reader;
import rug.icdtools.interfacing.externaltools.ExternalCommandExecutionException;
import rug.icdtools.logging.Logger;

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

            Logger.getInstance().log("$$$$$ - Block processor - ");

            return null;
        } catch (IOException | ExternalCommandExecutionException ex) {
            Logger.getInstance().log("ERROR >>>>" + ex.getLocalizedMessage());
            throw new RuntimeException(ex);
        }

    }

}
