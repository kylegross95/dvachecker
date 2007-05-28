/*
 * AcuityTestManager.java
 *
 * Created on May 9, 2007, 6:15 PM
 *
 */

package dva.acuitytest;

import dva.*;
import dva.displayer.DisplayModel;
import dva.util.DvaLogger;
import dva.util.MessageResources;
import java.util.Random;

/**
 *
 * @author J-Chris
 */
public  class AcuityTestManager {
    
    // prevent from instanciation
    private AcuityTestManager() {
        
    }
   
    
    public static AcuityTest getAcuityTest(){
        return acuityTest; 
    }
    
    public static void reset(){
        currentAcuityTestId = -1; 
    }
    
    public static void setNextAcuityTest(){
        try {
            currentAcuityTestId++;
            if (currentAcuityTestId >= acuityTestIds.length) {
                acuityTest = null; 
                return;
            } 
            
            Class clazz = Class.forName("dva.acuitytest." + acuityTestIds[currentAcuityTestId] + "AcuityTest");
            acuityTest = (AcuityTest) clazz.newInstance(); 
            
            //set start date
            acuityTest.setStartDate(); 
            
            //if (acuityTest instanceof DynamicAcuityTest){
            //    acuityTest.setTreadmillSpeed(getSpeedValue()); 
            //}
        } catch (Exception e){
            DvaLogger.error(DisplayModel.class, e); 
        }
    }
    
    public static Status getStatus(){
        return status; 
    }
    
    public static String getAcuityTestName(){
        return acuityTestIds[currentAcuityTestId]; 
    }
    
    public static void updateStatus(){
        
        if (acuityTest.isTestFailed()) {
            status = Status.TEST_FAILED; 
            
        } else if ( acuityTest.isTestDone() ){
            if (currentAcuityTestId + 1 == acuityTestIds.length){
                status = Status.ALL_TEST_DONE; 
                
            } else {
                 status = Status.TEST_DONE;
            }
        } else {
            status = Status.TEST_RUNNING; 
        }
    }
    
    public static int[] proposeSpeedSet(){
        
        proposedSpeedsSet[0] = 0;
        proposedSpeedsSet[1] = 1;
        proposedSpeedsSet[2] = 2;
        
        for (int i=0; i<proposedSpeedsSet.length; i++) {
            int randomPosition = random.nextInt(proposedSpeedsSet.length);
            int temp = proposedSpeedsSet[i];
            proposedSpeedsSet[i] = proposedSpeedsSet[randomPosition];
            proposedSpeedsSet[randomPosition] = temp;
        }
        
        return proposedSpeedsSet; 
    }
    
    public static String speedsSetToString(int[] speedsSet){
        String speedsinfo = ""; 
        
        for (int i=0; i < speedsSet.length; i++){
            speedsinfo += resourceBundle.getString("message.acuitytest.speedset."+speedsSet[i])+" "; 
        }
        return speedsinfo; 
    }
    
    public static float getSpeedValue(){
        return (currentAcuityTestId>0 && currentAcuityTestId<acuityTestIds.length) ? speedsValue[currentSpeedsSet[currentAcuityTestId-1]] : 0.0f; 
    }
    
    public static int[] acceptProposedSpeedsSet(){
        currentSpeedsSet = (int[]) proposedSpeedsSet.clone(); 
        return currentSpeedsSet; 
    }
    
    
    
    
    private static int proposedSpeedsSet[] = new int[3]; 
    private static int currentSpeedsSet[];  
    private static float speedsValue[] = {0, 4, 8}; 
    
    //resources
    private static MessageResources resourceBundle = new MessageResources("dva/Bundle"); // NOI18N; 
    
    public enum Status { INIT, TEST_RUNNING, TEST_FAILED, TEST_DONE, ALL_TEST_DONE }; 
    private static Random random = new Random();
    private static Status status = Status.INIT; 
    private static AcuityTest acuityTest; 
    //private static String acuityTestIds[] = {"Static", "Dynamic", "Dynamic", "Dynamic"}; 
    private static String acuityTestIds[] = {"Static"}; 
    
    private static int currentAcuityTestId = -1; 
    
}
