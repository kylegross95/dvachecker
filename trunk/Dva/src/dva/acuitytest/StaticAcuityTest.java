/*
 * StaticAcuityTest.java
 *
 * Created on May 7, 2007, 7:16 PM
 *
 */

package dva.acuitytest;

import dva.acuitytest.AcuityTest.TestAnswer;
import dva.displayer.Element;
import dva.displayer.LandoltC;
import dva.util.DvaLogger;


/**
 *
 * @author J-Chris
 */
public class StaticAcuityTest  extends AcuityTest {
    
    final static String charactersList[] = {"C"};
    
    /** Creates a new instance of StaticAcuityTest */
    public StaticAcuityTest() {
    }
    
    public Element getNext() throws AcuityTestMaxStepException{
        
        if (getTestAnswers().size() == this.getMaxStep()){
            throw new AcuityTestMaxStepException("Maximum number of step done!");
        }
        
        //TO BE UPDATED
        if (getTestAnswers().size() == 10) {
            setTestDone(true);
        }
        
        //initial size
        float lastSize = 200; 
        
        if (getTestAnswers().size() > 0){
            //get size of the last element
            TestAnswer lastAnswer = getTestAnswers().get(getTestAnswers().size()-1);
            lastSize = lastAnswer.getElement().getSize(); 

            //decrease size
            lastSize--; 
        }
        
        //randomly pick on orientation
        Element.Orientation orientations[] = Element.Orientation.values();
        int randomindex = getRandom().nextInt(8); 
        
        DvaLogger.debug(StaticAcuityTest.class, "Orientation:"+orientations[randomindex]); 
        
        //create a new DisplayElement
        LandoltC lc = new LandoltC(lastSize, orientations[randomindex]);
        
        return lc; 
    }
    
    public String getTestName(){
        return "static"; 
    }
    
    public boolean isFailureFatal(){
        return true; 
    }
}
