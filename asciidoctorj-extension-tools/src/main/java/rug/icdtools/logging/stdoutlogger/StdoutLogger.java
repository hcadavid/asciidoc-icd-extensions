/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.logging.stdoutlogger;

import rug.icdtools.logging.AbstractLogger;

/**
 *
 * @author hcadavid
 */
public class StdoutLogger implements AbstractLogger{

    @Override
    public void log(String l) {
              System.out.println(l);
    }
    
    
}
