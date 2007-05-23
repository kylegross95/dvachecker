/*
 * DynamicAcuityTest.java
 *
 * Created on May 7, 2007, 7:16 PM
 *
 */

package dva.acuitytest;

import dva.acuitytest.AcuityTest.TestAnswer;
import dva.displayer.DisplayElement;
import dva.displayer.Element;
import dva.util.DvaLogger;

/**
 *
 * @author J-Chris
 */
public class DynamicAcuityTest extends AcuityTest{
    
    
    /** Creates a new instance of DynamicAcuityTest */
    public DynamicAcuityTest() {
    }
    
    public Element getNext() throws AcuityTestMaxStepException {
        //initial size
        float lastSize = 40; 
        
        //TO BE UPDATED
        if (getTestAnswers().size() == 20) {
            setTestDone(true);
        }
        
        if (getTestAnswers().size() > 0){
            //get size of the last element
            TestAnswer lastAnswer = getTestAnswers().get(getTestAnswers().size()-1);
            lastSize = lastAnswer.getElement().getSize(); 

            //decrease size - Basic Adaptive algo
            if (lastAnswer.isPatientAnswer()){
                lastSize-=(lastSize*0.2); 
            } else {
                lastSize = lastSize * 2; 
            }
            
            //font size should be an integer
            lastSize = Math.round(lastSize); 
        }
        
        //randomly pick a number between 0 and 9
        int number = getRandom().nextInt(10); //pi
        
        DvaLogger.debug(StaticAcuityTest.class, "number:"+number); 
        
        //create a new DisplayElement
        DisplayElement de = new DisplayElement(String.valueOf(number), lastSize);
        
        return de; 
    }
    
    public String getTestName(){
        return "dynamic"; 
    }
    
    public boolean isFailureFatal(){
        return false; 
    }
    
}
