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
public class AcuityTestConvergenceException extends AcuityTestException {
    
    /** Creates a new instance of AcuityTestConvergenceException */
    public AcuityTestConvergenceException(double value, double stddev) {
        super("The converged value is " + value + " with stddev: " + stddev);
    }
    
}
