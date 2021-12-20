package rug.icdtools.dummy.extension;

import com.sun.tools.sjavac.Log;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.extension.InlineMacroProcessor;

import java.util.HashMap;
import java.util.Map;
import org.asciidoctor.log.Severity;
import rug.icdtools.logging.Logger;



public class DummyMacro extends InlineMacroProcessor {

    private final static String LOGGER_NAME = "asciidoctor";
    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(LOGGER_NAME);    
   
    
    public DummyMacro(String macroName) {
        super(macroName);        
        //Logger.getLogger("asciidoctor").setUseParentHandlers(false);
    }

    @Override
    public Object process(ContentNode contentNode, String macroValue, Map<String, Object> attributes) {

        String twitterLink=null;
        String twitterLinkText=null;                                
        
        //if (true) throw new RuntimeException("&&&&& cause");
        
        if (macroValue == null || macroValue.isEmpty() || macroValue.equals("dummy")) {
            Logger.log("TEST OF A CUSTOM LOGGER WITHIN DUMMY MACRO");
            
            org.asciidoctor.log.LogRecord log=new org.asciidoctor.log.LogRecord(Severity.INFO, "TEST OF ASCIID LOGGER WITHIN DUMMY MACRO");
            LOGGER.log(asciidocLogToJavalog(log));
            
        } else {
            twitterLink = "https://www.twitter.com/" + macroValue;
            // Prepend twitterHandle with @ as text link:
            twitterLinkText = "@:\\---" + macroValue;
        }

        // Define options for an 'anchor' element:
        Map<String, Object> options = new HashMap<>();
        options.put("type", ":link");
        options.put("target", macroValue);

        // Create the 'anchor' node:
        PhraseNode inlineTwitterLink = createPhraseNode(contentNode, "anchor", twitterLinkText, attributes, options);

        // Convert to String value:
        return inlineTwitterLink.convert();
    }
    
    
    private static java.util.logging.LogRecord asciidocLogToJavalog(org.asciidoctor.log.LogRecord logRecord) {
        final java.util.logging.LogRecord julLogRecord =
            new java.util.logging.LogRecord(
                mapSeverity(logRecord.getSeverity()),
                logRecord.getCursor() != null ? logRecord.getCursor().toString() + ": " + logRecord.getMessage() : logRecord.getMessage());

        julLogRecord.setSourceClassName(logRecord.getSourceFileName());
        julLogRecord.setSourceMethodName(logRecord.getSourceMethodName());
        julLogRecord.setParameters(new Object[] { logRecord.getCursor() });
        julLogRecord.setLoggerName(LOGGER_NAME);
        return julLogRecord;
    }

    private static java.util.logging.Level mapSeverity(org.asciidoctor.log.Severity severity) {
        switch (severity) {
            case DEBUG:
                return java.util.logging.Level.FINEST;
            case INFO:
                return java.util.logging.Level.INFO;
            case WARN:
                return java.util.logging.Level.WARNING;
            case ERROR:
                return java.util.logging.Level.SEVERE;
            case FATAL:
                return java.util.logging.Level.SEVERE;
            case UNKNOWN:
            default:
                return java.util.logging.Level.INFO;
        }
    }
    
    
}
