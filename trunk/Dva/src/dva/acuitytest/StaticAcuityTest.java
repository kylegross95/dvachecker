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
    int runCnt = 0; 
    
    final static String charactersList[] = {"C", "D", "H", "K", "N", "O", "R", "S", "V", "Z"};
    
    /** Creates a new instance of StaticAcuityTest */
    public StaticAcuityTest() {
        //initial size
        lastSize=14.0f;
        lastStep=4.0f;
        runCnt = 0;
        sc = new Staircase();
        sc.initSize(lastSize,lastStep);
        

    }
    
    public Element getNext() throws AcuityTestException{
        
        if (getTestAnswers().size() > 0){
            //get size of the last element
            TestAnswer lastAnswer = getTestAnswers().get(getTestAnswers().size()-1);
            lastSize = lastAnswer.getElement().getSize(); 

            //decrease size - adaptive algo
            if (lastAnswer.isPatientAnswer()){
                lastSize = sc.whatSize(true);
            } else {
                lastSize = sc.whatSize(false);    
            }
        }
        
        
        DvaLogger.debug(StaticAcuityTest.class, "New Size:"+lastSize); 
        
        if (++runCnt==50) sc.doGraph("_at50"); 
       
       if(lastSize == -1)  { //divergence
           sc.doGraph("_divergence");   
           this.setTestDone(true); 
           throw new AcuityTestDivergenceException();         
       }
       if(lastSize == -2) { //exceeded steps
           sc.doGraph("_exceededSteps");  
           this.setTestDone(true); 
           throw new AcuityTestMaxStepException();
       }
       else if (lastSize == 0) { //convergence
          sc.doGraph("_convergence"); 
          this.setConvergenceValue( sc.getConvergenceValue() );
          this.setConvergenceStdDev( sc.getConvergenceValueStdDev() ); 
          this.setTestDone(true); 
          throw new AcuityTestConvergenceException(sc.getConvergenceValue(), sc.getConvergenceValueStdDev());
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
