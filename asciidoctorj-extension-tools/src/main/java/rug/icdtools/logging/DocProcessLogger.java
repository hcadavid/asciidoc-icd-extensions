/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.logging;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import rug.icdtools.logging.guiceconfig.LoggingDIModule;
import rug.icdtools.logging.loggers.InMemoryErrorLogger;


/**
 *
 * @author hcadavid
 */
public class DocProcessLogger {
       
    private static AbstractLogger instance = new InMemoryErrorLogger();
        
    
    public static AbstractLogger getInstance(){
        return instance;
    }
    
    
}
