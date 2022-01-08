/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rug.icdtools.interfacing.externaltools;

/**
 *
 * @author hcadavid
 */
public class ExternalCommandExecutionException extends Exception{

    public ExternalCommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalCommandExecutionException(String message) {
        super(message);
    }
    
    
    
}
