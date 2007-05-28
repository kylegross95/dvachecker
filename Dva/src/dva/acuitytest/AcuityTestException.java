/*
 * AcuityTestException.java
 *
 * Created on May 9, 2007, 9:19 AM
 *
 */

package dva.acuitytest;

/**
 *
 * @author J-Chris
 */
public class AcuityTestException extends Exception{
    
    public AcuityTestException(String message) {
        super(message); 
    }
    
    /**
     * Creates a new instance of AcuityTestException
     */
    public AcuityTestException(String message, Throwable cause) {
        super(message, cause); 
    }
    
}
