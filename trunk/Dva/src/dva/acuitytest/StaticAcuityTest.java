/*
 * StaticAcuityTest.java
 *
 * Created on May 7, 2007, 7:16 PM
 *
 */

package dva.acuitytest;

import dva.acuitytest.AcuityTest.TestAnswer;
import dva.displayer.Element;
import dva.displayer.Optotype;
import dva.util.DvaLogger;
import dva.util.Staircase;

/**
 *
 * @author J-Chris
 */
public class StaticAcuityTest  extends AcuityTest {
    
    float lastSize;
    float lastStep;
    Staircase sc;
    String cv;
    int runCnt = 0; 
    
    final static String charactersList[] = {"C", "D", "H", "K", "N", "O", "R", "S", "V", "Z"};
    
    /** Creates a new instance of StaticAcuityTest */
    public StaticAcuityTest() {
        //initial size
        lastSize = resourceBundle.getFloat("config.staircase.initialsize"); 
        lastStep = resourceBundle.getFloat("config.staircase.initialstep");
        runCnt = 0;
        sc = new Staircase();
        sc.initSize(lastSize,lastStep);
        

    }
    
    public Element getNext() throws AcuityTestException{

//        if (getTestAnswers().size() == this.getMaxStep()){
//            throw new AcuityTestException("Maximum number of step done!");
//        }
//        
//        //TO BE UPDATED
//        if (getTestAnswers().size() == 10) {
//            setTestDone(true);
//        }
        
        if (getTestAnswers().size() > 0){
            //get size of the last element
            TestAnswer lastAnswer = getTestAnswers().get(getTestAnswers().size()-1);
            lastSize = lastAnswer.getElement().getSize(); 

            //decrease size - Basic adaptive algo
            if (lastAnswer.isPatientAnswer()){
                lastSize = sc.whatSize(true);
            } else {
                lastSize = sc.whatSize(false);    
            }
        }
        
        
        DvaLogger.debug(StaticAcuityTest.class, "New Size:"+lastSize); 
        
        if (++runCnt==50) sc.doGraph("at_20"); 
       
       if(lastSize == -1)  { //divergence
           sc.doGraph("divergence"); 
           throw new AcuityTestException("Adaptive algo diverged (either up or down)");         
       }
       if(lastSize == -2) { //exceeded steps
           sc.doGraph("exceededSteps");  
           throw new AcuityTestException("Max number of steps exceeded)");
       }
       else if (lastSize == 0) { //convergence
          Float f = new Float(sc.getConvergenceValue());
          cv =  new String(f.toString());
          sc.doGraph("convergence"); 
          throw new AcuityTestException(cv);
       }
        
        //randomly pick a number between 0 and 9
        int randomindex = getRandom().nextInt(10); //pi
        
        DvaLogger.debug(StaticAcuityTest.class, "Character:"+charactersList[randomindex]); 
        
        //create a new DisplayElement
        setCurrentElement( new Optotype(charactersList[randomindex], lastSize) ); 
        
        return this.getCurrentElement(); 
    }
    
    /*
     *
     */
    public String getTestName(){
        return "static"; 
    }
    
    /*
     *
     */
    public boolean isFailureFatal(){
        return true; 
    }
}
