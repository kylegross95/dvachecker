/*
 * Staircase.java
 *
 * Created on May 25, 2007, 12:17 AM
 *
 */

package dva.util;

import java.io.IOException;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.title.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.*;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.annotations.XYImageAnnotation;
import java.io.File;
import java.lang.String;
import java.lang.Math;
import java.awt.*;
import java.awt.image.*;
import java.awt.Paint;
import java.io.*;

/**
 * Staircase algo
 *
 * @author Roberto
 *
 * Instructions
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
    private double peaks[]; //vector with peaks
    private int valleyIdx;
    private double valleys[]; //vector with valleys
    private double Lvalleys[];
    private double Lpeaks[];
    private boolean peaker; //var to identify double peaks
    private float minSizeCnt; 
    //private int lastPositive; //0: single, 1: double (peak)
    private boolean converged; //whether the algo has converged yet
    private double convergenceVal;
    private double convergenceValStdDev;
    private int seriesCnt;
    private double[] curValHistory;
    private boolean[] curValResponses;
    
    private int numCVals; //# of consecutive valleys at size=1
    
    //private final float INIT_STEP_SIZE = 15.0f;
    //private final float INIT_STIM_SIZE = 200;
    private final float LIMIT_UP = 15;
    private final float LIMIT_DOWN = 1.0f;
    private final float MIN_STEPSIZE = 1.00f;
    private final int MAX_RUNS = 60;
    
    //chart output
    private XYSeries series;
    private XYSeries series2;
    private XYSeries seriesRunDir;
    private XYSeries seriesPeaker;
    
    //snelling scale
        
        double[] sScale  = {0.1,0.13,0.17,0.2,0.25,0.33,0.4,0.5,0.67,0.8,1.0,1.25,1.67,2.0};
 
    /**
     * Creates a new instance of Staircase
     */
    public Staircase() {
        peaks = new double[50];
        peakIdx = 0;
        valleys = new double[50];
        valleyIdx = 0;
        curValHistory = new double[50];
        curValResponses = new boolean[50];
        //lastPositive = 1; //to avoid step resizing at init
        series = new XYSeries("Values-Snellen Scale");
        series2 = new XYSeries("Values-Linear");
        seriesRunDir = new XYSeries("RunDir");
        seriesPeaker  = new XYSeries("Peaker");
        numCVals = 0;
        
    }
    
   
    public void initSize (float initSize, float initStepSize) {
        runNumber = 1;
        runDir = 1;
       // curVal = INIT_STIM_SIZE; NOT USED
        curVal = initSize;
        //stepSize = INIT_STEP_SIZE; NOT USED
        stepSize = initStepSize;
        seriesCnt = 1;
        //start logging curVal
        curValHistory[0] = initSize;
        series.add(seriesCnt,initSize);
        series2.add(seriesCnt,initSize);
        seriesCnt++;
        peaker = false;
        minSizeCnt = 0;
    }
    
 
    public float whatSize(boolean answer) {
        //prepare for next round
        prevStepSize = stepSize;
        prevVal = curVal;  
        
        if(runDir == -1) { //descending
            if(answer && !peaker) { //check again
                stepSize = prevStepSize;
                peaker = true;
                //lastPositive = 0;
                //runDir = runDir;
            }//close if answer
            else if (answer && peaker) { //keep descending if checked again
                stepSize = prevStepSize;
                curVal = prevVal - stepSize; 
                peaker = false; //potential double correct 
                //runDir = runDir 
            }//close else if
            else if (!answer) { //reverse direction, start climbing
                stepSize = prevStepSize;
                curVal = prevVal + stepSize;
                runDir = 1;
                valleys[valleyIdx] = sScale[(int)curVal-1]; //store valley information
                valleyIdx++;
                if (peaker) peaker = false; //in case of false double correct
            }//close elseif answer

           }
        else if (runDir == 1) { //climbing
            if(!answer && !peaker) { //keep climbing
                stepSize = prevStepSize;
                curVal = prevVal + stepSize;
                
            //runDir = runDir;
            }
            
            else if (!answer && peaker) { //keep on climbing, false alarm for double correct
                stepSize = prevStepSize;
                curVal = prevVal + stepSize;
                peaker = false;
                //runDir = runDir;  
                //lastPositive = 0; // not sure
                  
            }     
            
            else if(answer && !peaker) { //check again for a positive response
                stepSize = prevStepSize; 
                curVal = prevVal;
               // runDir = runDir;
                peaker = true; //we are at a potential double peak  
                }
            else if(answer && peaker) { //we are at a double peak, invert direction
               if(runNumber>1) stepSize = (prevStepSize/2 >= MIN_STEPSIZE)? prevStepSize/2 : MIN_STEPSIZE; 
               curVal = prevVal - stepSize; 
               runDir = -1;  //direction inversion
               peaker = false; //reset this var
               if (stepSize == MIN_STEPSIZE) numCVals++;
               //lastPositive = 1; //log the double peak
               peaks[peakIdx] = sScale[(int)curVal-1]; //log the peak
               peakIdx++;

            }
            
    DvaLogger.info(Staircase.class, "CurVal is '" + curVal+ "'"); 
    
        } 
    
    runNumber = (peaker) ? runNumber : runNumber+1; //do not increase number of runs if we are in potential double peak
    if(stepSize == MIN_STEPSIZE && !peaker) minSizeCnt++;
    
    double oldCurVal = 0;
    
    //check for convergence
    if (minSizeCnt>=8 && numCVals == 4){ 
        convergenceVal = (valleys[valleyIdx]+valleys[valleyIdx-1]+valleys[valleyIdx-2]+valleys[valleyIdx-3]+valleys[valleyIdx-4]+valleys[valleyIdx-5]);
        convergenceVal = convergenceVal + (peaks[peakIdx]+peaks[peakIdx-1]+peaks[peakIdx-2]+peaks[peakIdx-3]+peaks[peakIdx-4]+peaks[peakIdx-5]);    
        convergenceVal = convergenceVal/12;
        Lvalleys = new double[3];
        Lpeaks = new double[3];
        Lvalleys[2] = (double) valleys[valleyIdx];
        Lvalleys[1] = (double) valleys[valleyIdx-1];
        Lvalleys[0] = (double) valleys[valleyIdx-2];
        Lpeaks[2] = (double) peaks[valleyIdx];
        Lpeaks[1] = (double) peaks[valleyIdx-1];
        Lpeaks[0] = (double) peaks[valleyIdx-2];
        double sumPeaks = java.lang.Math.pow(Lpeaks[0]-convergenceVal,2.0)+java.lang.Math.pow(Lpeaks[1]-convergenceVal,2.0)+java.lang.Math.pow(Lpeaks[2]-convergenceVal,2.0);
        double sumValleys = java.lang.Math.pow(Lvalleys[0]-convergenceVal,2.0)+java.lang.Math.pow(Lvalleys[1]-convergenceVal,2.0)+java.lang.Math.pow(Lvalleys[2]-convergenceVal,2.0);
        convergenceValStdDev =  java.lang.Math.sqrt((1/12)*(sumPeaks+sumValleys));
        converged = true; 
        oldCurVal = curVal;
        curVal = 0;
        }//close if convergence
    
    
    //check min limit has not been surpassed
      if (curVal < LIMIT_DOWN && curVal != 0) curVal = LIMIT_DOWN; //check if lower bound has been surpassed
    
    //plotting
    if (!converged)  {  
        series.add(seriesCnt,(-1)*sScale[14-(int)curVal]); 
        series2.add(seriesCnt,curVal);
        curValHistory[seriesCnt-1] = sScale[(int)curVal-1];
        if(seriesCnt>0) curValResponses[seriesCnt-1] = answer;
    }
    else { 
        series.add(seriesCnt,curVal); 
        series2.add(seriesCnt,oldCurVal); 
        curValHistory[seriesCnt] = curVal;
    
    }
    
    
    /* // for debugging purposes
    seriesRunDir.add(seriesCnt,runDir);
    int tmpVal = (peaker) ? 1 : 0;
    seriesPeaker.add(seriesCnt,stepSize);
     */
    
    seriesCnt++;
    
    //various checks
    if (runNumber > MAX_RUNS) curVal = -2; //check if max number of runs exceeded
    if (curVal > LIMIT_UP) curVal = -1; //check if upper bound has been surpassed
  
    return curVal; 
    }//close whatSize
    

    public double getConvergenceValue() {
        if (converged) {
            DvaLogger.info(Staircase.class, "CurValHistory is '" + curValHistory + "'"); 
            DvaLogger.info(Staircase.class, "CurValResponses is '" + curValResponses + "'"); 
            return convergenceVal;
            
        }
        
        else return -1;
    }
    

    public double getConvergenceValueStdDev() {
        if (converged) return convergenceValStdDev;
    else return -1;
    }
    
    
    public void doGraph(String param) {
        XYSeriesCollection xyc = new XYSeriesCollection();
        XYSeriesCollection xyc2 = new XYSeriesCollection();
        xyc.addSeries(series);
        xyc2.addSeries(series2);
        xyc.addSeries(seriesRunDir);
        xyc.addSeries(seriesPeaker);
       /* org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createXYLineChart
                     ("DVA Experimental Values - Snellen Scale",  // Title
                      "Epoch",           // X-Axis label
                      "Value",           // Y-Axis label
                      xyc,          // Dataset
                       org.jfree.chart.plot.PlotOrientation.VERTICAL,
                      true,                // Show legend
                      true,   
                      true
                     ); */
        
                org.jfree.chart.JFreeChart chart2 = org.jfree.chart.ChartFactory.createXYLineChart
                     ("DVA Experimental Values - Linear Scale",  // Title
                      "Run",           // X-Axis label
                      "Value",           // Y-Axis label
                      xyc2,          // Dataset
                       org.jfree.chart.plot.PlotOrientation.VERTICAL,
                      true,                // Show legend
                      true,   
                      true
                     );
       /* //annotation attempt for + & - symbols
        XYPlot plot = chart.getXYPlot();
        Image image = Toolkit.getDefaultToolkit().getImage("c:/temp/plus.jpg");
        XYImageAnnotation ant = new XYImageAnnotation(500.0, 300.0, image);
        //plot.setSeriesPaint(new Paint[]{Color.green,Color.orange,Color.red});
        plot.addAnnotation(ant);
        chart.setBackgroundPaint(Color.yellow); */
        
        //String filenameSnellen = "c:/temp/chart" + param + "-snellen.jpg";
        String filenameLinear = "c:/temp/chart" + param + "-linear.jpg";
        
        try {
          //      ChartUtilities.saveChartAsJPEG(new File(filenameSnellen), chart, 1000, 600);
                ChartUtilities.saveChartAsJPEG(new File(filenameLinear), chart2, 1000, 600);
                DvaLogger.info(Staircase.class, "Result saved under '" + filenameLinear + "'");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    
    }
            
    
    
    