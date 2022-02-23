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
package rug.icdtools.dashboard.postprocessors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Postprocessor;
import rug.icdtools.docsapiclient.APIAccessException;
import rug.icdtools.docsapiclient.DashboardAPIClient;
import rug.icdtools.logging.AbstractLogger;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.Severity;
import rug.icdtools.logging.loggers.InMemoryErrorLogger;

/**
 *
 * @author hcadavid
 */
public class DocumentMetadataPostProcessor extends Postprocessor {

    private static boolean visitingFirstDocument = true;

    private static Set<String> notVisited;

    @Override
    public String process(Document dcmnt, String output) {

        //This post-processor if used onlt if BACKEND_URL variable (dashboard API URL) is defined
        String backendURL = System.getProperty("BACKEND_URL");
        if (backendURL != null && !backendURL.trim().equals("")) {
            try {
                DocProcessLogger.getInstance().log("BACKEND_URL environment variable set to [" + backendURL + "]", Severity.INFO);
                postToAPIWhenBuildingFinished(dcmnt, backendURL);
            } catch (FailedMetadataReportException ex) {
                //ex.printStackTrace();
                //If this exception takes place, there are no means to post documentation
                //metadata to the dashboard API, despite it has been configured to do so.
                //Therefore, for consistency, it must exit with a non-zero result to make sure build process is
                //reported as failed (no document is published)
                DocProcessLogger.getInstance().log("Unable to publish metadata of the published document. Building process must be stopped."+ex.getLocalizedMessage(), Severity.FATAL);
                System.exit(1);
            }
        } else {
            DocProcessLogger.getInstance().log("BACKEND_URL environment variable not set. Documentation dashboard API won't be used to post published document metadata.", Severity.INFO);
        }

        return output;

    }

    private void postToAPIWhenBuildingFinished(Document dcmnt, String backendURL) throws FailedMetadataReportException {
        StructuralNode stdoc = (StructuralNode) dcmnt;

        String currentFilePath = Paths.get(stdoc.getSourceLocation().getDir()).resolve(stdoc.getSourceLocation().getFile()).toString();

        //identify the documents to be visited by the post-processor so it can be identified which is the last one
        if (visitingFirstDocument) {

            try {
                try ( Stream<Path> walk = Files.walk(Paths.get(((StructuralNode) dcmnt).getSourceLocation().getDir()))) {
                    notVisited = walk
                            .filter(p -> !Files.isDirectory(p))
                            //.peek(p -> System.out.println(p+","+p.toString().endsWith(".adoc")))
                            .filter(f -> f.toString().endsWith(".adoc"))
                            .map(p -> p.toString())
                            .collect(Collectors.toSet());
                }

            } catch (IOException ex) {
                Logger.getLogger(DocumentMetadataPostProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                visitingFirstDocument = false;
                DocProcessLogger.getInstance().log("Documents to be visited before executing the post-processor:" + notVisited.size(), Severity.INFO);
            }
        }
        boolean currentDocVisited = notVisited.remove(currentFilePath);
        DocProcessLogger.getInstance().log("Removing from documents to be visited :" + currentFilePath + ":" + currentDocVisited, Severity.INFO);

        //True if this is the last document of the building process
        if (notVisited.isEmpty()) {
            AbstractLogger logger = DocProcessLogger.getInstance();

            //Post metadata to the dashboard API as succesfully published document
            if (logger instanceof InMemoryErrorLogger) {
                InMemoryErrorLogger mlogger = (InMemoryErrorLogger) logger;
                if (mlogger.getGlobalFatalErrorsCount()==0 && mlogger.getGlobalErrorsCount()==0 && mlogger.getGlobalFailedQualityGatesCount()==0) {
                    DocProcessLogger.getInstance().log("Documents built with no internal errors, document building errors, or failed quality gates. Posting metadata to " + backendURL, Severity.INFO);
                    postToAPI(backendURL);
                }
                else{
                    DocProcessLogger.getInstance().log(String.format("Documents built with %d internal errors, %d document building error, and %d failed quality gates. No metadata will be posted to the API.",mlogger.getGlobalFatalErrorsCount(),mlogger.getGlobalErrorsCount(),mlogger.getGlobalFailedQualityGatesCount()), Severity.INFO);
                }
            }

        }

    }

    private void postToAPI(String backendURL) throws FailedMetadataReportException {
        String[] envVars = new String[]{
            "BACKEND_CREDENTIALS",
            "PIPELINE_ID",
            "PROJECT_NAME",
            "PIPELINE_ID",
            "DEPLOYMENT_URL",
            "SOURCE_URL",
            "COMMIT_AUTHOR",
            "CREATION_DATE",
            "COMMIT_TAG"
        };

        Map<String, String> cicdEnvProperties = new HashMap<>();
        for (String envVar : envVars) {
            String varValue = System.getProperty(envVar);
            if (varValue == null) {
                throw new FailedMetadataReportException("The document build process was expected to report published document's metadata to [" + backendURL + "], but required environment variables was not set:" + envVar);
            } else {
                cicdEnvProperties.put(envVar, varValue);
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        PublishedICDMetadata icdMetadata = new PublishedICDMetadata();
        icdMetadata.setMetadata(cicdEnvProperties);

        try {
            //PUT to https://[apiurl]/v1/icds/{icdid}/current")
            String jsonIcdMetadata = mapper.writeValueAsString(icdMetadata);
            DashboardAPIClient apiClient = new DashboardAPIClient(cicdEnvProperties.get("BACKEND_CREDENTIALS"), backendURL);
            String urlPath = String.format("/v1/icds/%s/current", cicdEnvProperties.get("PROJECT_NAME"));            
            apiClient.putResource(urlPath, jsonIcdMetadata);
            DocProcessLogger.getInstance().log("Metadata posted to " + backendURL+urlPath, Severity.INFO);
            
        } catch (JsonProcessingException | APIAccessException ex) {
            throw new FailedMetadataReportException("The document build process was expected to report errors to the API at " + backendURL + ", but the request failed or coldn't be performed:" + ex.getLocalizedMessage(), ex);
        }

    }

}

class PublishedICDMetadata implements Serializable {

    private Map<String, String> metadata;

    private List<String> referencedDocs;

    private List<String> warnings;

    public List<String> getReferencedDocs() {
        return referencedDocs;
    }

    public void setReferencedDocs(List<String> referencedDocs) {
        this.referencedDocs = referencedDocs;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

}
