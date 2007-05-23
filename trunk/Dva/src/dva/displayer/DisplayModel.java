/*
 * DisplayModel.java
 *
 * Created on May 7, 2007, 5:14 PM
 *
 */

package dva.displayer;

import dva.acuitytest.AcuityTestManager;
import dva.acuitytest.AcuityTestMaxStepException;
import dva.util.DvaLogger;
import java.awt.Color;
import java.awt.Font;
import java.util.Observable;

/**
 *
 * @author J-Chris
 */
public class DisplayModel extends Observable {
    
    
    
    /**
     * Creates a new instance of DisplayModel
     */
    public DisplayModel() {
        setDefault();
    }
    
    public Element getCurrentDisplayedElement(){
        return currentElement; 
    }
    
    public void updateX(int x){
        currentElement.setX(x); 
        
        this.x = x; 
        
        setChanged(); 
        notifyObservers();
    }
    
    public void updateY(int y){
        currentElement.setY(y); 
        
        this.y = y; 
        
        setChanged(); 
        notifyObservers();
    }
    
    public void update(int x, int y){
        currentElement.setX(x); 
        currentElement.setY(y); 
        
        this.x = x; 
        this.y = y; 
        
        setChanged(); 
        notifyObservers();
    }
    
    public void update(Element element, int size){
        //keep current position
        element.setX(currentElement.getX());
        element.setY(currentElement.getY());
        
        this.currentElement = element; 
        
        setChanged(); 
        notifyObservers();
    }
    
     public double getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
        
        //notify ModelView
        setChanged(); 
        notifyObservers(DisplayModel.EventType.SCALING);
    }
    
    public void enableCallibration(){
        currentElement = new DisplayElement("+", 40);
    }
    
    public boolean setupAcuityTest() {
        try {
            AcuityTestManager.reset(); 
            AcuityTestManager.setNextAcuityTest();
            setMessageToDisplay(resourceBundle.getString("message.displayer.patientready"));
            return true; 
            
        } catch (Exception e){
            DvaLogger.error(DisplayModel.class, e); 
            return false; 
        }
    }
    
    public State getState(){
        return currentState; 
    }
    
    public Element notifyOperatorEvent(OperatorEvent operatorEvent) throws AcuityTestMaxStepException {
        DvaLogger.debug(DisplayModel.class, "state:"+currentState); 
        
        if (currentState == State.INIT){
            
            //save time
            this.savedTime = System.currentTimeMillis(); 
            
            //disable message
            disableMessage(); 
            
            //display next character
            currentElement = AcuityTestManager.getAcuityTest().getNext();
            
            //set new state
            this.currentState = State.TESTING; 
            
        } else if (currentState == State.TESTING){
            
            //compute answertime
            answerTime = savedTime - System.currentTimeMillis(); 
            
            //save patient response
            this.patientAnswer = operatorEvent == OperatorEvent.LEFT_CLICK; 
            
            //save patient answer
            AcuityTestManager.getAcuityTest().saveAnswer(answerTime, this.currentElement, patientAnswer);
            
            //update status
            AcuityTestManager.updateStatus(); 

            if (AcuityTestManager.getStatus() == AcuityTestManager.Status.TEST_RUNNING) {
                //if there is a pause between each character
                if (pauseBetween){

                    //display ready message
                    setMessageToDisplay(resourceBundle.getString("message.displayer.patientready"));

                    //set new state
                    this.currentState = State.PAUSE; 

                } else {
                    
                    disableMessage(); 
            
                    //display next character
                    currentElement = AcuityTestManager.getAcuityTest().getNext();
                } 
            } else if (AcuityTestManager.getStatus() == AcuityTestManager.Status.TEST_DONE){
                //update displayer
                setMessageToDisplay(resourceBundle.getString("message.displayer.patientready"));
            }
            
        } else if (currentState == State.PAUSE){
         
            disableMessage(); 
            
            //display next character
            currentElement = AcuityTestManager.getAcuityTest().getNext();
            
            //set new state
            this.currentState = State.TESTING; 
        }
        
      
        //notify ModelView
        setChanged(); 
        notifyObservers(DisplayModel.EventType.OPERATOR_EVENT);
        
        DvaLogger.debug(DisplayModel.class, "currentState:"+currentState); 
        
        return this.currentElement; 
    }
    
    public void setPauseBetween(boolean pauseBetween){
        this.pauseBetween = pauseBetween; 
    }
    
    public void setMessageToDisplay(String messageToDisplay){
        this.messageToDisplay = messageToDisplay; 
        this.message = true; 
        
        //notify ModelView
        setChanged(); 
        notifyObservers(DisplayModel.EventType.DISPLAY_MESSAGE);
    }
    
    public void disableMessage(){
        this.message = false; 
    }
    
    public String getMessageToDisplay(){
        return messageToDisplay; 
    }
    
    public Font getMessageFont(){
        return messageFont; 
    }
    
    public void setMessageFont(Font messageFont){
        this.messageFont = messageFont; 
    }
    
    public Color getMessageColor(){
        return this.messageColor; 
    }
    
    public void setMessageColor(Color messageColor){
        this.messageColor = messageColor; 
    }
    
    public boolean isMessage(){
        return this.message; 
    }
    
    public int getX(){
        return x; 
    }
    
    public int getY(){
        return y; 
    }
    
    public void setDefault(){
        x = resourceBundle.getInt("config.displayer.defaultX"); 
        y = resourceBundle.getInt("config.displayer.defaultY"); 
    }
   
    //state machine attributes
    public enum OperatorEvent {LEFT_CLICK, RIGHT_CLICK}; 
    public enum State { INIT, TESTING, PAUSE, END }; 
    public enum EventType { DISPLAY_MESSAGE, OPERATOR_EVENT, SCALING}; 
    boolean pauseBetween = false; 
    private State currentState = State.INIT; 
    
    //patient asnwer specific attribute
    private long savedTime; 
    private long answerTime; 
    private boolean patientAnswer = true; 
    
    //Graphics attribute
    private double scalingFactor = 1; 
    private int x; 
    private int y; 
    private Element currentElement = null; 
    
    //message attributes
    private String messageToDisplay = ""; 
    private boolean message = false; 
    private Font messageFont =  new Font("Tahoma", Font.PLAIN, 40);
    private Color messageColor = Color.BLACK; 
    
    //resources
    private dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N; 
}
