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
public class AcuityTestDivergenceException extends AcuityTestException {
    
    /** Creates a new instance of AcuityTestConvergenceException */
    public AcuityTestDivergenceException() {
        super("Adaptive algo diverged");
    }
    
}
