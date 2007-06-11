/*
 * StaticAcuityTest.java
 *
 * Created on May 7, 2007, 7:16 PM
 *
 */

package dva.acuitytest;

import dva.DvaCheckerException;
import dva.displayer.Element;
import dva.displayer.Optotype;
import dva.util.DvaLogger;
import dva.util.Staircase;
import dva.xml.AcuityTestReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author J-Chris
 */
public class StaticAcuityTest  extends AcuityTest {
    
    float lastSize;
    float lastStep;
    Staircase sc;
    //int runCnt = 0; 
    
    final static String charactersList[] = {"C", "D", "H", "K", "N", "O", "R", "S", "V", "Z"};
    
    /** Creates a new instance of StaticAcuityTest */
    public StaticAcuityTest() {
        //initial size
        lastSize = 14.0f;
        lastStep = 4.0f;
    }
    
    /*
     *
     */
    public void init(){
        sc = new Staircase();
        sc.initSize(lastSize,lastStep, this.getPatientdir() , this.getFileDesc());
    }
    
    /*
     *
     */
    public Staircase getStaircase(){
        return this.sc; 
    }
    
    /*
     *
     */
    public static void reprocess(File acuitytestFile){
        
        try {
            //load actuiyTest from file
            FileReader fr = new FileReader(acuitytestFile);
            
            //extract date
            
            //extract eye

            //read actuiyTest xml file
            StaticAcuityTest acuityTest = AcuityTestReader.process( fr ); 
            
            acuityTest.setPatientdir( acuitytestFile.getParentFile() ); 
            acuityTest.setStartDate(); 
            acuityTest.init(); 
            
            for (TestAnswer ta : acuityTest.getTestAnswers() ){

                boolean currentPatientAnswer = ta.getPatientAnswer(); 
                //DvaLogger.debug(StaticAcuityTest.class, "currentPatientAnswer:" + currentPatientAnswer); 
                
                int computedSize = (int)acuityTest.getStaircase().whatSize( currentPatientAnswer ); 
                //DvaLogger.debug(StaticAcuityTest.class, "computedsize:" + computedSize); 
                
                switch (computedSize){
                    case 0:
                        DvaLogger.info(StaticAcuityTest.class, "=> convergence"); 
                        acuityTest.getStaircase().doGraph("_convergence-NEW");
                        break; 
                    case -1:
                        DvaLogger.info(StaticAcuityTest.class, "=> divergence"); 
                        acuityTest.getStaircase().doGraph("_divergence-NEW");
                        break;
                    case -2:
                        DvaLogger.info(StaticAcuityTest.class, "=> exceed max step"); 
                        acuityTest.getStaircase().doGraph("_exceededSteps-NEW");
                        break; 
                    default:
                        /* ignore */
                        break;
                }
            }
            
        } catch (Exception dcex){
            DvaLogger.error(StaticAcuityTest.class, dcex); 
        }
        
    }
    
 
    /*
     *
     */
    public Element getNext() throws DvaCheckerException {
        
        if (getTestAnswers().size() > 0){
            //get size of the last element
            TestAnswer lastAnswer = getTestAnswers().get(getTestAnswers().size()-1);

            //decrease size - adaptive algo
            lastSize = sc.whatSize( lastAnswer.getPatientAnswer() );
        }
        
        
        DvaLogger.debug(StaticAcuityTest.class, "size:"+lastSize); 
       
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
          //this.setConvergenceStdDev( sc.getConvergenceValueStdDev() ); 
          this.setTestDone(true); 
          //AcuityTestManager.toFile();
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
