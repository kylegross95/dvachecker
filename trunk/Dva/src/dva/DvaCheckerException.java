/*
 * DvaCheckerException.java
 *
 * Created on June 5, 2007, 12:59 AM
 *
 */

package dva;

/**
 *
 * @author J-Chris
 */
public class DvaCheckerException extends Exception {
    
    /** Creates a new instance of DvaCheckerException */
    public DvaCheckerException(String message) {
        super(message);
    }
    
    public DvaCheckerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
