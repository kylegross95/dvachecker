/*
 * TestAnswer.java
 *
 * Created on June 8, 2007, 6:20 AM
 *
 */

package dva.acuitytest;

import dva.displayer.Element;

/**
 *
 * @author J-Chris
 */
public class TestAnswer {
        
    private Element element; 
    private long answerTime; 
    private boolean patientAnswer; 
    private String patientAnswerStr; 

    public String toXml(){
        StringBuffer sb = new StringBuffer("<answer value=\""); 
            sb.append(patientAnswer);
            sb.append("\" str=\"");
            sb.append(this.patientAnswerStr); 
            sb.append("\">");
            sb.append(element.toXml()); 
        sb.append("</answer>"); 
        return sb.toString(); 
    }
    
    public TestAnswer(boolean patientAnswer, Element element, String patientAnswerStr){
        this.element = element; 
        this.setPatientAnswer(patientAnswer); 
        this.patientAnswerStr = patientAnswerStr; 
    }

    public TestAnswer(long answerTime, boolean patientAnswer, Element element, String patientAnswerStr){
        this.answerTime = answerTime; 
        this.element = element; 
        this.setPatientAnswer(patientAnswer); 
        this.patientAnswerStr = patientAnswerStr; 
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

    public boolean getPatientAnswer() {
        return patientAnswer;
    }

    public void setPatientAnswer(boolean patientAnswer) {
        this.patientAnswer = patientAnswer;
    }

}
