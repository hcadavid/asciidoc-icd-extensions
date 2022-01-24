/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.logging.guiceconfig;

import com.google.inject.AbstractModule;
import rug.icdtools.logging.loggers.StdoutLogger;
import rug.icdtools.logging.AbstractLogger;

/**
 *
 * @author hcadavid
 */
public class LoggingDIModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(AbstractLogger.class).to(StdoutLogger.class);
    }
    
    
}
