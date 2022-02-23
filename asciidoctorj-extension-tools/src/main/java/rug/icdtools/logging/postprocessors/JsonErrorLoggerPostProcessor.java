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
import java.io.Serializable;
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
import rug.icdtools.docsapiclient.APIAccessException;
import rug.icdtools.docsapiclient.DashboardAPIClient;
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
        
        if (logger instanceof InMemoryErrorLogger) {

            try {
                DocProcessLogger.getInstance().log("Dumping error details on "+docFileName + ".errlogs file", Severity.INFO);
                InMemoryErrorLogger mlogger = (InMemoryErrorLogger) logger;
                dumpToFile(mlogger, docFileName, errorFilePath);

                //If BACKEND_URL system property is defined, also dump the error details there
                String backendURL = System.getProperty("BACKEND_URL");
                if (backendURL != null && !backendURL.trim().equals("")) {
                    DocProcessLogger.getInstance().log("Posting errors/failed quality gates on "+docFileName+" to the API at "+backendURL, Severity.INFO);
                    postToAPI(mlogger, docFileName, backendURL);
                }
                else{
                    DocProcessLogger.getInstance().log("BACKEND_URL environment variable not set. Documentation dashboard API won't be used.", Severity.INFO);
                }
                
                //clear error logs, as this postprocessor could be applied on other documents
                mlogger.resetErrorLogs();       
                
            } catch (FailedErrorReportException ex) {
                //ex.printStackTrace();
                //If this exception happens, there are no means to report previous errors
                //so it must exit with a non-zero result to make sure build process is
                //reported as failed.
                DocProcessLogger.getInstance().log("Unable to report previous errors. Building process must be stopped."+ex.getLocalizedMessage(), Severity.FATAL);
                System.exit(1);
            }

        }

        return output;

    }
    
    /**
     * 
     * @param logger
     * @param docFileName
     * @param logFilePath
     * @throws FailedErrorReportException 
     */
    private void dumpToFile(InMemoryErrorLogger logger,String docFileName, Path logFilePath) throws FailedErrorReportException{
        if (!logger.isErrorLogsEmpty()) {

            ObjectMapper mapper = new ObjectMapper();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            PipelineFailureDetails docBuildProcessErrorsDescription = new PipelineFailureDetails();
            docBuildProcessErrorsDescription.setDate(dtf.format(now));
            docBuildProcessErrorsDescription.setdocName(docFileName);
            docBuildProcessErrorsDescription.setErrors(logger.getErrors());
            docBuildProcessErrorsDescription.setFatalErrors(logger.getFatalErrors());

            String jsonObject;

            try ( PrintWriter out = new PrintWriter(logFilePath.toFile())) {
                jsonObject = mapper.writeValueAsString(docBuildProcessErrorsDescription);
                out.println(jsonObject);

            } catch (JsonProcessingException | FileNotFoundException ex) {
                throw new FailedErrorReportException("There were errors during document build process, but the report file couldn't be generated due to an internal error.", ex);
            }
        }
    }

    /**
     * 
     * @param logger
     * @param docFileName
     * @param backendURL 
     */
    private void postToAPI(InMemoryErrorLogger logger,String docFileName, String backendURL) throws FailedErrorReportException{
        if (!logger.isErrorLogsEmpty()) {
            String credentials = System.getProperty("BACKEND_CREDENTIALS"); 
            String pipelineId = System.getProperty("PIPELINE_ID");
            String icdId = System.getProperty("PROJECT_NAME");
            String versionTag = System.getProperty("COMMIT_TAG");

           
            if (credentials==null){
                throw new FailedErrorReportException("The document build process was expected to report errors to the API at ["+backendURL+"], but no credentials were provided (BACKEND_CREDENTIALS sysenv)");
            }
            else if (pipelineId == null || icdId == null){
                throw new FailedErrorReportException("The document build process was expected to report errors to the API at ["+backendURL+"], but required GitLab environment variables were not found (the process is expected to run within a GitLab CI/CD environment)");                
            }
            else{
                ObjectMapper mapper = new ObjectMapper();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                PipelineFailureDetails docBuildingFailureDetails = new PipelineFailureDetails();
                docBuildingFailureDetails.setDate(dtf.format(now));
                docBuildingFailureDetails.setdocName(docFileName);
                docBuildingFailureDetails.setErrors(logger.getErrors());
                docBuildingFailureDetails.setFatalErrors(logger.getFatalErrors());
                docBuildingFailureDetails.setFailedQualityGates(logger.getFailedQualityGates());

                String jsonObject;
                try {
                    //Posting to https://[apiurl]/v1/icds/{icdid}/{version}/{pipelineid}/errors")
                    jsonObject = mapper.writeValueAsString(docBuildingFailureDetails);
                    DashboardAPIClient apiClient = new DashboardAPIClient(credentials,backendURL);
                    String urlPath = String.format("/v1/icds/%s/%s/%s/errors",icdId,versionTag,pipelineId);
                    apiClient.postResource(urlPath, jsonObject);

                } catch (JsonProcessingException | APIAccessException ex) {
                    throw new FailedErrorReportException("The document build process was expected to report errors to the API at " + backendURL + ", but the request failed or coldn't be performed:" + ex.getLocalizedMessage(), ex);
                }

            }
        }
        
    }

    private class PipelineFailureDetails implements Serializable{

        String date;

        String adocName;

        List<String> errors;

        List<String> fatalErrors;
        
        List<String> failedQualityGates;

        public List<String> getFailedQualityGates() {
            return failedQualityGates;
        }

        public void setFailedQualityGates(List<String> failedQualityGates) {
            this.failedQualityGates = failedQualityGates;
        }

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
