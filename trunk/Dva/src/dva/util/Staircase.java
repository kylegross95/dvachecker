/*
 * Staircase.java
 *
 * Created on May 25, 2007, 12:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.util;

/**
 *Staircase algo
 *
 * @author Roberto
 *
 *Instructions
 *
  1) Create instance of object 
  2) Call whatSize method with right or wrong arg to obtain the next size of char to display 
  3) This method will return either the new size, -1 if the algo has diverged, -2 if max runs exceeded and 0 if it has converged
  4)  If the return value stays the same the same stimulus must be displayed
  5)  Call the getConvergenceValue to obtain the converged value
  6)  It is assumed that the history of right and wrong responses will be stored by another object.
 */
public class Staircase {
    
    //state variables
    private float stepSize; //current step size
    private float prevStepSize; //previous run step size
    private float curVal; //current value/level/size
    private float prevVal; //previous run value/level/size
    private int runDir; //1 going up, -1 going down
    private int runNumber; //number of runs so far
    private int peakIdx;
    private float peaks[]; //vector with peaks
    private int valleyIdx;
    private float valleys[]; //vector with valleys
    private boolean peaker; //var to identify double peaks
    private int lastPositive; //0: single, 1: double (peak)
    private boolean converged; //whether the algo has converged yet
    private float convergenceVal;

    private final float INIT_STEP_SIZE = 0.1f;
    private final float INIT_STIM_SIZE = 1;
    private final float LIMIT_UP = 10000;
    private final float LIMIT_DOWN = 0;
    private final float MIN_STEPSIZE = 0.01f;
    private final int MAX_RUNS = 500;
    
       
    /**
     * Creates a new instance of Staircase
     */
    public Staircase() {
        peaks = new float[50];
        peakIdx = 0;
        valleys = new float[50];
        valleyIdx = 0;
        peaker = false;
        lastPositive = 1; //to avoid step resizing at init
        
    }
    
    public void initSize (float initSize, float initStepSize) {
        runNumber = 1;
        runDir = 1;
       // curVal = INIT_STIM_SIZE;
        curVal = initSize;
        
        //stepSize = INIT_STEP_SIZE;
        stepSize = initStepSize;
        
        //return curVal;
    }
    public float whatSize(boolean answer) {
    prevStepSize = stepSize;
    prevVal = curVal;

    if(runDir == -1) { //descending
        if(answer) { //keep descending
            stepSize = prevStepSize;
            curVal = prevVal - stepSize; 
            lastPositive = 0;
            //runDir = runDir;
        }//close if answer
        else if (!answer) { //reverse direction, start climbing
            stepSize = prevStepSize;
            curVal = prevVal + stepSize;
            runDir = 1;
            valleys[valleyIdx] = curVal; //store valley information
            valleyIdx++;
        }//close elseif answer
        
       }
    else if (runDir == 1) { //
       
        if(!answer) { //keep climbing -- FIXME: NOT SURE ABOUT THIS DECISION
            stepSize = prevStepSize;
            curVal = prevVal + stepSize;
            //runDir = 1;
        }
        else if(answer && !peaker) { //check again for a positive response
            stepSize = prevStepSize; 
            curVal = prevVal;
           // runDir = runDir;
            peaker = true; //we are at a potential double peak
            //lastPositive should be changed but its deferred to next round
            System.out.print("HERE1");
            }
        else if(answer && peaker) { //we are at a double peak, invert direction
           if (lastPositive == 1) { //check for last positive response
               stepSize = prevStepSize;
           } else if (lastPositive == 0) {
               stepSize = (prevStepSize/2 >= MIN_STEPSIZE)? prevStepSize/2 : MIN_STEPSIZE; 
           }
           curVal = prevVal - stepSize; 
           runDir = -1;  //direction inversion
           peaker = false; //reset this var
           lastPositive = 1; //log the double peak
           peaks[peakIdx] = curVal; //log the peak
           peakIdx++;
           System.out.print("HERE2");
        }
        else if (!answer && peaker) { //FIXME: not sure about this 
            stepSize = prevStepSize;
            curVal = prevVal + stepSize;
            peaker = false;
            lastPositive = 0; //FIXME: not sure
            //runDir = 1;     
        }     
    } 
    runNumber = (peaker) ? runNumber : runNumber+1; //do not increase number of runs if we are in potential double peak
    if (runNumber > MAX_RUNS) curVal = -2; //check if max number of runs exceeded
    if (curVal > LIMIT_UP) curVal = -1; //check if upper bound has been surpassed
    else if (curVal < LIMIT_DOWN) curVal = -1; //check if lower bound has been surpassed
    
    //check for convergence
    if (valleyIdx>3 && peakIdx>3 &&
            valleys[valleyIdx] == valleys[valleyIdx-1] && 
            valleys[valleyIdx] == valleys[valleyIdx-2] && 
            valleys[valleyIdx] == valleys[valleyIdx-3] && 
            peaks[peakIdx] == peaks[peakIdx-1] &&
            peaks[peakIdx] == peaks[peakIdx-2] &&
            peaks[peakIdx] == peaks[peakIdx-3]) {
            convergenceVal = (valleys[valleyIdx]+peaks[peakIdx])/2;
            converged = true; 
            curVal = 0;
        }
        
          
    return curVal; 
    
    }//close whatSize
    
    public float getConvergenceValue() {
    if (converged) return convergenceVal;
    else return -1;
    
    }
    
    }
            
    
    
    