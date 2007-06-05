/*
 * AcuityTest.java
 *
 * Created on May 7, 2007, 7:12 PM
 *
 */

package dva.acuitytest;

import dva.DvaCheckerException;
import dva.displayer.Element;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author J-Chris
 */
public abstract class AcuityTest {
    
    /** Creates a new instance of AcuityTest */
    public AcuityTest() {
    }
    
    public void setName(String testName){
        this.testName = testName; 
    }
    
    public void saveAnswer(long answerTime, Element element, boolean patientAnswer){
        //add the patient answer to the answer list
        getTestAnswers().add( new TestAnswer(answerTime, patientAnswer, element) ); 
    }
    
    public abstract String getTestName(); 
    
    public void setCurrentElement(Element current){
        this.current = current; 
    }
    public Element getCurrentElement(){
        return this.current; 
    }
    
    public abstract Element getNext() throws DvaCheckerException; 
    
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
    
    public String toXml(){
        StringBuffer sb = new StringBuffer("<acuitytest name=\"");
            sb.append( this.getTestName() );
            sb.append("\"><result value=\"");
            sb.append( this.getConvergenceValue() ); 
            sb.append("\" stddev=\"");
            sb.append( this.getConvergenceStdDev() ); 
            sb.append("\">");
            sb.append( this.getResultComment() ); 
            sb.append("</result>"); 
            sb.append("<answers>"); 
            for (TestAnswer ta : testAnswers){
                
                sb.append( ta.toXml() ); 
            }
        sb.append("</answers></acuitytest>");
        
        sb.toString(); 
        
        return sb.toString(); 
    }
   
    private ArrayList<TestAnswer> testAnswers = new ArrayList<TestAnswer>();; 
    private Random random = new Random(); 
    private int maxStep = 20; 
    private boolean testDone = false; 
    private float treadmillSpeed = 0; 
    private boolean testFailed = false; 
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
    private Date startDate = null; 
    private Element current = null; 
    private String testName = ""; 
    private String resultComment = ""; 
    private double convergenceValue = 0.0; 
    private double convergenceStdDev = 0.0; 
    
    //resources
    protected dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N; 
    
    class TestAnswer {
        
        private Element element; 
        private long answerTime; 
        private boolean patientAnswer; 
        
        public String toXml(){
            StringBuffer sb = new StringBuffer("<answer value=\""); 
                sb.append(patientAnswer); 
                sb.append("\">");
                sb.append(element.toXml()); 
            sb.append("</answer>"); 
            return sb.toString(); 
        }

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

    public Date getStartDate() {
        return startDate;
    }
    
    public String getStartDateAsString(){
        return formatter.format(startDate); 
    }
    
    public void setStartDate(){
        startDate = new Date(); 
    }

    public void setResult(String resultComment) {
        this.resultComment = resultComment;
    }

    public double getConvergenceValue() {
        return convergenceValue;
    }

    public void setConvergenceValue(double convergenceValue) {
        this.convergenceValue = convergenceValue;
    }

    public double getConvergenceStdDev() {
        return convergenceStdDev;
    }

    public void setConvergenceStdDev(double convergenceStdDev) {
        this.convergenceStdDev = convergenceStdDev;
    }

    public String getResultComment() {
        return resultComment;
    }
    
}
