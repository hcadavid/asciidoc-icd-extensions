/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rug.icdtools.logging;

import java.util.ArrayList;
import java.util.List;
import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;

/**
 *
 * @author hcadavid
 */
public class ExtendedLoggerHandler implements LogHandler {

    
    @Override
    public void log(LogRecord logRecord) {
        System.out.println("CORE>>>>>>>>>>>>>!!!!!!#### "+logRecord.getMessage()+","+logRecord.getSeverity());
    }

}
