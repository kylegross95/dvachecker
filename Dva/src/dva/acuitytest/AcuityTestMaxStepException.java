/*
 * AcuityTestConvergenceException.java
 *
 * Created on June 4, 2007, 10:21 PM
 *
 */

package dva.acuitytest;

/**
 *
 * @author J-Chris
 */
public class AcuityTestMaxStepException extends AcuityTestException {
    
    /** Creates a new instance of AcuityTestConvergenceException */
    public AcuityTestMaxStepException() {
        super("Max number of steps exceeded");
    }
    
}
