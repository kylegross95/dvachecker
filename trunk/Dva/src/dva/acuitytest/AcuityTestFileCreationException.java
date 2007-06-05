/*
 * AcuityTestFileCreationException.java
 *
 * Created on June 5, 2007, 3:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.acuitytest;

import dva.DvaCheckerException;
import java.io.File;

/**
 *
 * @author J-Chris
 */
public class AcuityTestFileCreationException extends DvaCheckerException {
    
    /** Creates a new instance of AcuityTestFileCreationException */
    public AcuityTestFileCreationException(File file) {
        super("Failed to create acuitytest file:" + (file != null ? file.getAbsoluteFile() : "NULL") ); 
    }
    
    public AcuityTestFileCreationException(File file, Throwable cause) {
        super("Failed to create acuitytest file:" + (file != null ? file.getAbsoluteFile() : "NULL"), cause); 
    }
    
}
