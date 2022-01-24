/*
 * Copyright (C) 2022 hcadavid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package rug.icdtools.logging.postprocessors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Postprocessor;
import rug.icdtools.logging.loggers.InMemoryErrorLogger;
import org.apache.commons.io.FilenameUtils;
import rug.icdtools.logging.AbstractLogger;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class JsonErrorLoggerPostProcessor extends Postprocessor {

    @Override
    public String process(Document dcmnt, String output) {

        File adocSourceFile = new File(((StructuralNode) dcmnt).getSourceLocation().getFile());
        
        String docFileName = FilenameUtils.removeExtension(adocSourceFile.getName());
        
        Path outputPath = Paths.get(System.getProperty("OUTPUT_PATH"));
        
        Path errorFilePath = outputPath.resolve(docFileName + ".errlogs");
        
        
        AbstractLogger logger = DocProcessLogger.getInstance();

        //DocProcessLogger.getInstance().log("", Severity.DEBUG);

        if (logger instanceof InMemoryErrorLogger) {

            //System.out.println("Postprocessor .2>>>>>>"+dcmnt.toString());
            InMemoryErrorLogger mlogger = (InMemoryErrorLogger) logger;

            if (!mlogger.isErrorLogsEmpty()) {
                ObjectMapper mapper = new ObjectMapper();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                BuildProcessOutputDescription docBuildProcessErrorsDescription = new BuildProcessOutputDescription();
                docBuildProcessErrorsDescription.setDate(dtf.format(now));
                docBuildProcessErrorsDescription.setdocName(docFileName);
                docBuildProcessErrorsDescription.setErrors(mlogger.getErrors());
                docBuildProcessErrorsDescription.setFatalErrors(mlogger.getFatalErrors());

                try ( PrintWriter out = new PrintWriter(errorFilePath.toFile())) {
                    out.println(mapper.writeValueAsString(docBuildProcessErrorsDescription));
                } catch (JsonProcessingException | FileNotFoundException ex) {
                    DocProcessLogger.getInstance().log("There were errors during document build process, but the report file couldn't be generated due to an internal error. Aborting generation with error code 1. Cause:"+ex, Severity.FATAL);
                    System.exit(1);
                } 
                
                mlogger.resetErrorLogs();
       
            }

        }

        return output;

    }
    

    private class BuildProcessOutputDescription {

        String date;

        String adocName;

        List<String> errors;

        List<String> fatalErrors;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public List<String> getFatalErrors() {
            return fatalErrors;
        }

        public void setFatalErrors(List<String> fatalErrors) {
            this.fatalErrors = fatalErrors;
        }

        public String getdocName() {
            return adocName;
        }

        public void setdocName(String adocName) {
            this.adocName = adocName;
        }

    }

}
