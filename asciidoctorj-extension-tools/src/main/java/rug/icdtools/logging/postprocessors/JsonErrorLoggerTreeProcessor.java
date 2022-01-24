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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.asciidoctor.extension.Treeprocessor;
import rug.icdtools.logging.AbstractLogger;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.loggers.InMemoryErrorLogger;



/**
 *
 * @author hcadavid
 */
public class JsonErrorLoggerTreeProcessor extends Treeprocessor{

 
    public String xprocess(Document dcmnt, String output) {
        AbstractLogger logger = DocProcessLogger.getInstance();
        
        System.out.println("Postprocessor .>>>>>>");
        
        if (logger instanceof InMemoryErrorLogger){
            
            System.out.println("Postprocessor .2>>>>>>"+dcmnt.toString());
            
            InMemoryErrorLogger mlogger = (InMemoryErrorLogger)logger;
 
            ObjectMapper mapper = new ObjectMapper();
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();  
            
            BuildProcessOutputDescription docBuildProcessErrorsDescription=new BuildProcessOutputDescription();
            docBuildProcessErrorsDescription.setDate(dtf.format(now));
            docBuildProcessErrorsDescription.setErrors(mlogger.getErrors());
            docBuildProcessErrorsDescription.setFatalErrors(mlogger.getFatalErrors());
            
            
            try{
                System.out.println(mapper.writeValueAsString(docBuildProcessErrorsDescription));
                
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
                //Logger.getLogger(JsonErrorLoggerPostProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
                    
        }
        
        return output;

    }

    @Override
    public Document process(Document dcmnt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private class BuildProcessOutputDescription{

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
        
        String date;
        
        List<String> errors;
        
        List<String> fatalErrors;
        
    }


    
}
