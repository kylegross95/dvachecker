/*
 * PatientFileCreationException.java
 *
 * Created on June 5, 2007, 12:56 AM
 *
 */

package dva;

import java.io.File;

/**
 *
 * @author J-Chris
 */
public class PatientFileCreationException extends DvaCheckerException {
    
    /** Creates a new instance of PatientFileCreationException */
    public PatientFileCreationException(File file) {
        super("Failed to create patient file:" + (file != null ? file.getAbsoluteFile() : "NULL") ); 
    }
    
    public PatientFileCreationException(File file, Throwable cause) {
        super("Failed to create patient file:" + (file != null ? file.getAbsoluteFile() : "NULL"), cause); 
    }

    
}
