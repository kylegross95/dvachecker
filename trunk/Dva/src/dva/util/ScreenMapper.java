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
    
    static public double getRatio(double diagonalLength, double horizontalRes, double verticalRes, double characterResolution, double patientDistance, double staircaseInitialValue){
        //we will assume the scaling factor is the same on vertical and horizontal axis
        double screen_width = getScreenWidth(diagonalLength, horizontalRes / verticalRes, Units.INCHES);
        DvaLogger.debug(ScreenMapper.class, "screen_width:"+screen_width); 
        double pixel_width = getPixelWidth(screen_width, horizontalRes, Units.MM); 
        DvaLogger.debug(ScreenMapper.class, "pixel_width:"+pixel_width); 
        double character_width = va2size(0.1, patientDistance, Units.M); 
        DvaLogger.debug(ScreenMapper.class, "character_width:"+character_width); 
        double numberOfPixel = character_width / pixel_width;
        DvaLogger.debug(ScreenMapper.class, "numberOfPixel:"+numberOfPixel); 
        double ratio = (1 / characterResolution) * (numberOfPixel / staircaseInitialValue);
        DvaLogger.debug(ScreenMapper.class, "ratio:"+ratio); 
        return ratio; 
    }
    
}
