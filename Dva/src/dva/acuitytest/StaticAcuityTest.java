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
import dva.util.Staircase;
import java.lang.String;
import java.lang.Float;




/**
 *
 * @author J-Chris
 */
public class StaticAcuityTest  extends AcuityTest {
    
    float lastSize;
    float lastStep;
    Staircase sc;
    String cv;
    
    final static String charactersList[] = {"C"};
    
    /** Creates a new instance of StaticAcuityTest */
    public StaticAcuityTest() {
        //initial size
        lastSize = 200; 
        lastStep = 15;
        sc = new Staircase();
        sc.initSize(lastSize,lastStep);
        

    }
    
    public Element getNext() throws AcuityTestMaxStepException{
        

        
        
     /*   if (getTestAnswers().size() == this.getMaxStep()){
            throw new AcuityTestMaxStepException("Maximum number of step done!");
        }
        
        //TO BE UPDATED
        if (getTestAnswers().size() == 10) {
            setTestDone(true);
        }
        
*/
        
        if (getTestAnswers().size() > 0){
            //get size of the last element
            TestAnswer lastAnswer = getTestAnswers().get(getTestAnswers().size()-1);
            lastSize = lastAnswer.getElement().getSize(); 

            //decrease size - Basic adaptive algo
            if (lastAnswer.isPatientAnswer()){
                lastSize = sc.whatSize(true);
                //lastSize -= (lastSize * 0.2); 
            } else {
                lastSize = sc.whatSize(false);    
                //lastSize = lastSize * 2; 
            }
        }
        
        
                DvaLogger.debug(StaticAcuityTest.class, "New Size:"+lastSize); 
                
                
       if(lastSize == -1)  { //divergence
            throw new AcuityTestMaxStepException("Adaptive algo diverged (either up or down)");
       }
       if(lastSize == -2) { //exceeded steps
              throw new AcuityTestMaxStepException("Max number of steps exceeded)");
       }
       else if (lastSize == 0) { //convergence
          Float f = new Float(sc.getConvergenceValue());
          cv =  new String(f.toString());
          throw new AcuityTestMaxStepException(cv);
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
