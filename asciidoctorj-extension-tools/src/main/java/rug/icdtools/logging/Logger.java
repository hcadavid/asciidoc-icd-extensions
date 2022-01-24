/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.logging;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import rug.icdtools.logging.guiceconfig.LoggingDIModule;


/**
 *
 * @author hcadavid
 */
public class Logger {
       
    private static final Logger instance=new Logger();
    
    private AbstractLogger logger = null;
    
    private Logger(){
        Injector injector = Guice.createInjector(new LoggingDIModule());
        logger = injector.getInstance(AbstractLogger.class);
    }
    
    public static Logger getInstance(){
        return instance;
    }
    
    public void log(String log, Severity severity){
        logger.log(log,severity);
    }
    
    
}
