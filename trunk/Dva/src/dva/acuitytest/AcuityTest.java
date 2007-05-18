/*
 * AcuityTest.java
 *
 * Created on May 7, 2007, 7:12 PM
 *
 */

package dva.acuitytest;

import dva.displayer.Element;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author J-Chris
 */
public abstract class AcuityTest {
    
    /** Creates a new instance of AcuityTest */
    public AcuityTest() {
    }
    
    public void saveAnswer(long answerTime, Element element, boolean patientAnswer){
        //add the patient answer to the answer list
        getTestAnswers().add( new TestAnswer(answerTime, patientAnswer, element) ); 
    }
    
    public abstract String getTestName(); 
    
    public abstract Element getNext() throws AcuityTestMaxStepException; 
    
    protected void setMaxStep(int maxStep){
        this.maxStep = maxStep; 
    }
    
    public abstract boolean isFailureFatal(); 
    
    public boolean isTestFailed(){
        return testFailed; 
    }
    
    protected void setTestFailed(boolean testFailed){
        this.testFailed = testFailed; 
    }
    
    protected int getMaxStep(){
        return maxStep; 
    }
    
    public boolean isTestDone(){
        return testDone;
    }
    
    protected void setTestDone(boolean testDone){
        this.testDone = testDone; 
    }
    
    protected Random getRandom(){
        return random; 
    }
    

    
    public String getOperatorInstruction(){

        return resourceBundle.getString("message.acuitytest."+getTestName()+".operatorinstruction", String.valueOf(treadmillSpeed) ); 
    }
   
    private ArrayList<TestAnswer> testAnswers = new ArrayList<TestAnswer>();; 
    private Random random = new Random(); 
    private int maxStep = 20; 
    private boolean testDone = false; 
    private float treadmillSpeed = 0; 
    private boolean testFailed = false; 
    
    //resources
    private dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N; 
    
    class TestAnswer {
        
        private Element element; 
        private long answerTime; 
        private boolean patientAnswer; 

        public TestAnswer(long answerTime, boolean patientAnswer, Element element){
            this.answerTime = answerTime; 
            this.element = element; 
            this.setPatientAnswer(patientAnswer); 
        }
                
        public Element getElement() {
            return element;
        }

        public void setElement(Element element) {
            this.element = element;
        }

        public float getAnswerTime() {
            return answerTime;
        }

        public void setAnswerTime(long answerTime) {
            this.answerTime = answerTime;
        }

        public boolean isPatientAnswer() {
            return patientAnswer;
        }

        public void setPatientAnswer(boolean patientAnswer) {
            this.patientAnswer = patientAnswer;
        }
    
    }

    public ArrayList<TestAnswer> getTestAnswers() {
        return testAnswers;
    }
    
    protected TestAnswer getLastAnswer(){
        return getTestAnswers().get(getTestAnswers().size()); 
    }

    public float getTreadmillSpeed() {
        return treadmillSpeed;
    }

    public void setTreadmillSpeed(float treadmillSpeed) {
        this.treadmillSpeed = treadmillSpeed;
    }
    
}
