/*
 * AcuityTestMaxStepException.java
 *
 * Created on May 9, 2007, 9:19 AM
 *
 */

package dva.acuitytest;

/**
 *
 * @author J-Chris
 */
public class AcuityTestMaxStepException extends Exception{
    
    public AcuityTestMaxStepException(String message) {
        super(message); 
    }
    
    /**
     * Creates a new instance of AcuityTestMaxStepException
     */
    public AcuityTestMaxStepException(String message, Throwable cause) {
        super(message, cause); 
    }
    
}
