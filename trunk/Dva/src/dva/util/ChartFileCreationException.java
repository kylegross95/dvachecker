/*
 * ChartFileCreationException.java
 *
 * Created on June 7, 2007, 7:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.util;

import dva.DvaCheckerException;
import java.io.File;

/**
 *
 * @author J-Chris
 */
public class ChartFileCreationException extends DvaCheckerException {
    
    /** Creates a new instance of AcuityTestFileCreationException */
    public ChartFileCreationException(File file) {
        super("Failed to create chart file:" + (file != null ? file.getAbsoluteFile() : "NULL") ); 
    }
    
    public ChartFileCreationException(File file, Throwable cause) {
        super("Failed to create chart file:" + (file != null ? file.getAbsoluteFile() : "NULL"), cause); 
    }
}

