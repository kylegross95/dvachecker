/*
 * AdaptResp.java
 *
 * Created on May 25, 2007, 12:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.util;

/**
 *
 * @author Roberto
 */
public class AdaptResp {
    
    private int stepSize;
    private int runDir; //1 going up, -1 going down
    private int outcome; //stimulus in matlab, 1 recognized, -1 not recog, 0 do not change stimulus
    private int curVal; //CurrVar, current value
    private int runNum; //run, number of current run
    private int peaks[]; //vector with peaks
    private int valleys[]; //vector with valleys
    private int idxFalse[]; //vector containing the index of fault anwers
    private int idxCorrect[]; //vector containing the index of correct answers
    private int numStim; //number of stimuli currently displayed
    private int correctA[]; //vector containing correct answers
    private int wrongA[]; //vector containing false answers
    private int convergence; //1: convergence, 0: no conv, -1: divergence
    
       
    private final int LIMITE_UP;
    private final int LIMITE_DOWN;
    private final int MIN_STEPSIZE;
    private final int NR_SEG_MAX;
    
    
    
    
    
    
    
    /** Creates a new instance of AdaptResp */
    public AdaptResp() {
        super();
    }
    public int whatSize(int rd) {
        runDir = rd;
        switch(runDir) {
            case 1: bla;
            case -1:ble;
        }
                              
                                
                    
        
    
    }
    
}
