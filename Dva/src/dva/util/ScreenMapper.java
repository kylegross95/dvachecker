/*
 * ScreenMapper.java
 *
 * Created on May 29, 2007, 5:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.util;

/**
 *
 * @author J-Chris
 */
public class ScreenMapper {
    
    private static double visualAcuityCharts[] = {0.1, 0.13, 0.17, 0.2, 0.25, 0.33, 0.4, 0.5, 0.67, 0.8, 1, 1.25, 1.67, 2};
    
    private static double horizontalRes = 1280; 
    private static double verticalRes = 800; 
    private static float diagonalLength = 12.1f; 
    private static float patientDistance = 6f; 
    private static double screen_width = 0; 
    private static double pixel_width = 0; 
    private static double characterResolution = 1400;
    
    static {
        screen_width = getScreenWidth(diagonalLength, horizontalRes / verticalRes, Units.INCHES); 
        pixel_width = getPixelWidth(screen_width, horizontalRes, Units.MM); 
    }
    
    private static dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N;
            
    /** prevente from instanciation */
    private ScreenMapper() {}
    
    public enum Units {MM, M, INCHES}
    
    static public double inches2mm(double inches) {
        return inches * 25.4; 
    }
    
    static public double getScreenWidth(double diagonalLength, double aspectRatio, Units unit){
        if (unit == Units.INCHES) {
            diagonalLength = inches2mm( diagonalLength );
        }
        double w = Math.sqrt( Math.pow( diagonalLength, 2) / ( 1 + (1 / Math.pow(aspectRatio, 2))) ); 
        return w; 
    }
    
    static public double getScreenHeight(double diagonalLength, double aspectRatio, Units unit){
        if (unit == Units.INCHES) {
            diagonalLength = inches2mm( diagonalLength );
        }
        double w = Math.sqrt( Math.pow( diagonalLength, 2) / ( 1 + (1 / Math.pow(aspectRatio, 2))) ); 
        return w; 
    }
    
    static public double getPixelWidth(double screenWidth, double horizontalRes, Units unit){
        if (unit == Units.INCHES) {
            screenWidth = inches2mm( screenWidth );
        }
        return screenWidth / horizontalRes; 
    }
    
    static public double getPixelHeight(double screenHeight, double verticalRes, Units unit){
        if (unit == Units.INCHES) {
            screenHeight = inches2mm( screenHeight );
        }
        return screenHeight / verticalRes; 
    }
    
    static public double va2size(double va, double patientDistance, Units unit){
        //convert to MM
        if (unit == Units.M){
            patientDistance = patientDistance * 1000; 
        }
        double size = 2 * (patientDistance * Math.tan( Math.toRadians( 1 / (va * 60)) / 2 ));
        return size * 5;
    }
    
    public static void setDisplayerOptions(double _horizontalRes, double _verticalRes, float _diagonalLength, float _patientDistance){
        horizontalRes = _horizontalRes;
        verticalRes = _verticalRes;
        diagonalLength = _diagonalLength;
        patientDistance = _patientDistance;
        DvaLogger.debug(ScreenMapper.class, "horizontalRes:"+horizontalRes+", verticalRes:"+verticalRes+", diagonalLength:"+diagonalLength+", patientDistance:"+patientDistance);
        
        characterResolution = resourceBundle.getDouble("config.character.resolution");
        double aspect_ratio = horizontalRes / verticalRes;
        DvaLogger.debug(ScreenMapper.class, "aspect_ratio:"+aspect_ratio);
        screen_width = getScreenWidth(diagonalLength, aspect_ratio, Units.INCHES);
        DvaLogger.debug(ScreenMapper.class, "screen_width:"+screen_width); 
        pixel_width = getPixelWidth(screen_width, horizontalRes, Units.MM); 
        DvaLogger.debug(ScreenMapper.class, "pixel_width:"+pixel_width); 
        
    }
    
    static public double getRatio(int chartLevel){
        DvaLogger.debug(ScreenMapper.class, "chartLevel:"+chartLevel+", index:"+(visualAcuityCharts.length - chartLevel));
        //we will assume the scaling factor is the same on vertical and horizontal axis
        double va = visualAcuityCharts[visualAcuityCharts.length - chartLevel]; 
        DvaLogger.debug(ScreenMapper.class, "va:"+va); 
        double character_width = va2size(va, patientDistance, Units.M); 
        DvaLogger.debug(ScreenMapper.class, "character_width:"+character_width); 
        double numberOfPixel = character_width / pixel_width;
        DvaLogger.debug(ScreenMapper.class, "numberOfPixel:"+numberOfPixel); 
        double ratio =  numberOfPixel / (characterResolution * 14);
        DvaLogger.debug(ScreenMapper.class, "ratio:"+ratio); 
        return ratio; 
    }
    
}
