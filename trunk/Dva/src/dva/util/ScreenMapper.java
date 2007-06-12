/*
 * ScreenMapper.java
 *
 * Created on May 29, 2007, 5:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.util;

import dva.displayer.Displayer;
import dva.displayer.Element;
import dva.displayer.Optotype;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author J-Chris
 */
public class ScreenMapper {
    
    final public static DvaLogger.LogLevel level = DvaLogger.LogLevel.INFO; 
    
    private static double visualAcuityCharts[] = {
        0.1,    // 14
        0.13,   // 13
        0.17,   // 12
        0.2,    // 11
        0.25,   // 10
        0.33,   // 9
        0.4,    // 8
        0.5,    // 7
        0.67,   // 6
        0.8,    // 5
        1,      // 4
        1.25,   // 3
        1.67,   // 2
        2       // 1
    };
    
    private static String visualAcuityChartsAsStr[] = {

        "", 
        "2.00",      // 1
        "1.67",   // 2
        "1.25",   // 3
        "1.00",      // 4
        "0.80",    // 5
        "0.67",   // 6
        "0.50",    // 7
        "0.40",    // 8
        "0.33",   // 9
        "0.25",   // 10
        "0.20",    // 11
        "0.17",   // 12
        "0.13",   // 13
        "0.10"    // 14
    };
    
    private double horizontalRes = 1280;
    private double verticalRes = 800;
    private float diagonalLength = 12.1f;
    private float patientDistance = 6f;
    private double screen_width = 0;
    private double pixel_width = 0;
    private double characterResolution = 1400;
    
    
    public static String[] getVisualAcuityChartsAsStr(){
        return visualAcuityChartsAsStr; 
    }
    /*
     *
     */
    public static double getVA(int step){
       return visualAcuityCharts[14 - (step < 1 ? 1 : step)];
       //return visualAcuityCharts[14 - step];
    }
    
    private GraphicsDevice outputGraphicsDevice = null;
    
    static private ScreenMapper instance = null;
    
    private static dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N;
    
    /** prevente from instanciation */
    private ScreenMapper() {
        screen_width = getScreenWidth(diagonalLength, horizontalRes / verticalRes, Units.INCHES);
        pixel_width = getPixelWidth(screen_width, horizontalRes, Units.MM);
        characterResolution = resourceBundle.getDouble("config.character.resolution");
    }
    
    /**
     *
     */
    public static ScreenMapper getInstance(){
        if (instance==null){
            instance = new ScreenMapper();
        }
        return instance;
    }
    
    /**
     *
     */
    public enum Units {MM, M, INCHES}
    
    /**
     *
     */
    public double inches2mm(double inches) {
        return inches * 25.4;
    }
    
    /**
     *
     */
    public double getScreenWidth(double diagonalLength, double aspectRatio, Units unit){
        if (unit == Units.INCHES) {
            diagonalLength = inches2mm( diagonalLength );
        }
        double w = Math.sqrt( Math.pow( diagonalLength, 2) / ( 1 + (1 / Math.pow(aspectRatio, 2))) );
        return w;
    }
    
    public int getScreenWidth(){
        return this.outputGraphicsDevice.getDisplayMode().getWidth();
    }
    
    /**
     *
     */
    public double getScreenHeight(double diagonalLength, double aspectRatio, Units unit){
        if (unit == Units.INCHES) {
            diagonalLength = inches2mm( diagonalLength );
        }
        double w = Math.sqrt( Math.pow( diagonalLength, 2) / ( 1 + Math.pow(aspectRatio, 2)) );
        return w;
    }
    
    public int getScreenHeight(){
        return this.outputGraphicsDevice.getDisplayMode().getHeight();
    }
    
    /**
     *
     */
    public double getPixelWidth(double screenWidth, double horizontalRes, Units unit){
        if (unit == Units.INCHES) {
            screenWidth = inches2mm( screenWidth );
        }
        return screenWidth / horizontalRes;
    }
    
    /**
     *
     */
    public double getPixelHeight(double screenHeight, double verticalRes, Units unit){
        if (unit == Units.INCHES) {
            screenHeight = inches2mm( screenHeight );
        }
        return screenHeight / verticalRes;
    }
    
    /**
     *
     */
    public double va2size(double va, double patientDistance, Units unit){
        //convert to MM
        if (unit == Units.M){
            patientDistance = patientDistance * 1000;
        }
        double size = 2 * (patientDistance * Math.tan( Math.toRadians( 1 / (va * 60)) / 2 ));
        return size * 5;
    }
    
    public void setDisplayerOptions(double _horizontalRes, double _verticalRes, float _diagonalLength, float _patientDistance, float _scaleCorrectionFactor){
        this.horizontalRes = _horizontalRes;
        this.verticalRes = _verticalRes;
        this.diagonalLength = _diagonalLength;
        this.patientDistance = _patientDistance;
        
    }
    
    
    /**
     *
     */
//    public void setDisplayerOptions(double _horizontalRes, double _verticalRes, float _diagonalLength, float _patientDistance){
//        horizontalRes = _horizontalRes;
//        verticalRes = _verticalRes;
//        diagonalLength = _diagonalLength;
//        patientDistance = _patientDistance;
//        DvaLogger.debug(ScreenMapper.class, "horizontalRes:"+horizontalRes+", verticalRes:"+verticalRes+", diagonalLength:"+diagonalLength+", patientDistance:"+patientDistance);
//
//        characterResolution = resourceBundle.getDouble("config.character.resolution");
//        double aspect_ratio = horizontalRes / verticalRes;
//        DvaLogger.debug(ScreenMapper.class, "aspect_ratio:"+aspect_ratio);
//        screen_width = getScreenWidth(diagonalLength, aspect_ratio, Units.INCHES);
//        DvaLogger.debug(ScreenMapper.class, "screen_width:"+screen_width);
//        DvaLogger.debug(ScreenMapper.class, "width(pixel):" + millimeterAsPixel(screen_width, Displayer.getInstance()) );
//        DvaLogger.debug(ScreenMapper.class, "height(pixel):" + millimeterAsPixel(getScreenHeight(diagonalLength, aspect_ratio, Units.INCHES), Displayer.getInstance()) );
//        pixel_width = getPixelWidth(screen_width, horizontalRes, Units.MM);
//        DvaLogger.debug(ScreenMapper.class, "pixel_width:"+pixel_width);
//
//    }
    
    
    public void scale(Element e, Component c){
        
        int chartLevel = Math.round( e.getSize() );
        
        DvaLogger.debug(ScreenMapper.class, "chartLevel:"+chartLevel+", index:"+(visualAcuityCharts.length - chartLevel));
        
        //we will assume the scaling factor is the same on vertical and horizontal axis
        double va = visualAcuityCharts[visualAcuityCharts.length - chartLevel];
        DvaLogger.debug(ScreenMapper.class, "va:"+va);
        
        double character_width = va2size(va, this.patientDistance, Units.M);
        DvaLogger.debug(ScreenMapper.class, "character_width:"+character_width);
        
        double numberOfPixel = millimeterAsPixel(character_width, c);//character_width / pixel_width;
        //DvaLogger.debug(ScreenMapper.class, "numberOfPixel:"+numberOfPixel);
        
        //get scalingCorrectionFactor
        double scaleCorrectionFactor = Displayer.getInstance().getDisplayModel().getScaleCorrectionFactor(); 
        
        e.setPixelWidth( (int) Math.round( numberOfPixel * scaleCorrectionFactor) ); 
        e.setPixelHeight((int) Math.round( numberOfPixel * scaleCorrectionFactor) ); 
        
        DvaLogger.debug(ScreenMapper.class, "pixelWidth:" + e.getPixelWidth() ); 
        DvaLogger.debug(ScreenMapper.class, "pixelHeight:" + e.getPixelHeight() ); 
        
        double ratio =  ( numberOfPixel / this.characterResolution ) * scaleCorrectionFactor;
        DvaLogger.debug(ScreenMapper.class, "ratio:"+ratio);
        
        e.setRatio(ratio); 
    }
    
    public final static BufferedImage scale(Optotype o) {
        if (o == null) return null;

        AffineTransform scaler = new AffineTransform();
        scaler.scale(o.getRatio(), o.getRatio());
        AffineTransformOp op = new AffineTransformOp(scaler, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage scaledImage = new BufferedImage(o.getPixelWidth(), o.getPixelHeight(), BufferedImage.TYPE_INT_ARGB);

        scaler = null;

        return op.filter(o.getBimg(), scaledImage);
    }
    
    public void center(Element e, Component c){
        //int sh = this.getScreenHeight(); 
        //int sw = this.getScreenWidth(); 
        
        int sw = c.getWidth(); 
        int sh = c.getHeight(); 
        
        DvaLogger.debug(ScreenMapper.class, "output Width/Height:" + sw + "/" + sh ); 
        
        e.setX((sw - e.getPixelWidth())/ 2); 
        e.setY((sh - e.getPixelHeight())/ 2); 
    }
    
    /**
     * Converts Inches and returns pixels using the specified resolution.
     *
     * @param in         the Inches
     * @param component  the component that provides the graphics object
     * @return the given Inches as pixels
     */
    public int inchAsPixel(double in, Component component) {
        return inchAsPixel(in, getScreenResolution(component));
    }
    
    
    /**
     * Converts Millimeters and returns pixels using the resolution of the
     * given component's graphics object.
     *
     * @param mm            Millimeters
     * @param component    the component that provides the graphics object
     * @return the given Millimeters as pixels
     */
    public int millimeterAsPixel(double mm, Component component) {
        return millimeterAsPixel(mm, getScreenResolution(component));
    }
    
    
    /**
     * Converts Centimeters and returns pixels using the resolution of the
     * given component's graphics object.
     *
     * @param cm            Centimeters
     * @param component    the component that provides the graphics object
     * @return the given Centimeters as pixels
     */
    public int centimeterAsPixel(double cm, Component component) {
        return centimeterAsPixel(cm, getScreenResolution(component));
    }
    
    /**
     * Converts Inches and returns pixels using the specified resolution.
     *
     * @param in    the Inches
     * @param dpi   the resolution
     * @return the given Inches as pixels
     */
    protected final int inchAsPixel(double in, int dpi) {
        return (int) Math.round(dpi * in);
    }
    
    
    /**
     * Converts Millimeters and returns pixels using the specified resolution.
     *
     * @param mm    Millimeters
     * @param dpi   the resolution
     * @return the given Millimeters as pixels
     */
    protected final int millimeterAsPixel(double mm, int dpi) {
        return (int) Math.round(dpi * mm * 10 / 254);
    }
    
    
    /**
     * Converts Centimeters and returns pixels using the specified resolution.
     *
     * @param cm    Centimeters
     * @param dpi   the resolution
     * @return the given Centimeters as pixels
     */
    protected final int centimeterAsPixel(double cm, int dpi) {
        return (int) Math.round(dpi * cm * 100 / 254);
    }
    
    /**
     * Returns the components screen resolution or the default screen
     * resolution if the component is null or has no toolkit assigned yet.
     *
     * @param c the component to ask for a toolkit
     * @return the component's screen resolution
     */
    protected int getScreenResolution(Component c) {
        int res = 0;
        if (c == null){
            res=  getDefaultScreenResolution();
        } else {
            Toolkit toolkit = c.getToolkit();
            res = toolkit != null
                    ? toolkit.getScreenResolution()
                    : getDefaultScreenResolution();
        }
        DvaLogger.debug(ScreenMapper.class, "res:" + res);
        return res;
    }
    
    
    private static int defaultScreenResolution = -1;
    
    
    /**
     * Computes and returns the default resolution.
     *
     * @return the default screen resolution
     */
    protected int getDefaultScreenResolution() {
        if (defaultScreenResolution == -1) {
            defaultScreenResolution =
                    Toolkit.getDefaultToolkit().getScreenResolution();
        }
        return defaultScreenResolution;
    }
    
    /**
     *
     */
    public GraphicsDevice getOutputGraphicsDevice(){
        return outputGraphicsDevice;
    }
    
    /**
     *
     */
    public void detectOutputScreen(){
        //get graphic env.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        //get screen devices
        GraphicsDevice gs[] = ge.getScreenDevices();
        
        DvaLogger.debug("There are " + gs.length + " screen(s) available");
        
        if (gs.length == 1){
            DvaLogger.info(ScreenMapper.class, "One output device available, device id:1 selected");
            outputGraphicsDevice = gs[0];
            
        } else if (gs.length == 2){
            DvaLogger.info(ScreenMapper.class, "Two output devices available - device id:2 selected");
            //assume the desired output device is the second on
            outputGraphicsDevice = gs[1];
            
        } else {
            //more than 2 available output device - ask operator
            // TO BE IMPLEMENTED
            
            //by default the second one is selected
            outputGraphicsDevice = gs[1];
            DvaLogger.info(ScreenMapper.class, "More than two output devices available - Device 2 selected");
        }
    }
    
}
