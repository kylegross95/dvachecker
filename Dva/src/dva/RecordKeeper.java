package dva;
/*
 * RecordKeeper.java
 *
 * Created on May 20, 2007, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Roberto
 */
public class RecordKeeper {
    private int PatientSex; //male=0, female=1
    private int PatientAge; 
    //private int TestOrder; //0:FWR, 1:FRW, 2:WFR, 3:WRF, 4:RWF, 5:RFW
    private int finalDA; //final dynamic visual acuity value
   
    
    private char[] dispChar; //characters displayed 
    private int[][] resultsLog; 
    /*
     This matrix contains the experiment results
     0-49: exp1, 50-99: exp2, 100-149;exp3
     col 1:  char displayed, col 2:right/wrong, col3: time elapsed
     */
    
    /** Creates a new instance of RecordKeeper */
    public RecordKeeper() {
       //init data structures
         setResultsLog(new int[149][3]);
         
    }

    public int getPatientSex() {
        return PatientSex;
    }

    public void setPatientSex(int PatientSex) {
        this.PatientSex = PatientSex;
    }

    public int getPatientAge() {
        return PatientAge;
    }

    public void setPatientAge(int PatientAge) {
        this.PatientAge = PatientAge;
    }

    public int getTestOrder() {
        return TestOrder;
    }

    public void setTestOrder(int TestOrder) {
        this.TestOrder = TestOrder;
    }

    public int getFinalDA() {
        return finalDA;
    }

    public void setFinalDA(int finalDA) {
        this.finalDA = finalDA;
    }


    public int[][] getResultsLog() {
        return resultsLog;
    }

    public void setResultsLog(int[][] resultsLog) {
        this.resultsLog = resultsLog;
    }

    public char[] getDispChar() {
        return dispChar;
    }

    public void setDispChar(char[] dispChar) {
        this.dispChar = dispChar;
    }
    
}
