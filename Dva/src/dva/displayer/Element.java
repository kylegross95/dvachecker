/*
 * Element.java
 *
 * Created on May 17, 2007, 11:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.displayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ResourceBundle;

/**
 *
 * @author J-Chris
 */
public abstract class Element {
    
    public enum Orientation {
        RIGHT, BOTTOMRIGHT, BOTTOM, BOTTOMLEFT, LEFT, TOPLEFT, TOP, TOPRIGHT
    }; 
    
    /** Creates a new instance of Element */
    
    public Element() {
        setDefault();
    }
    
    public Element(Color color) {
        setDefault();
        this.color = color; 
    }
    
    public Element(Orientation orientation) {
        setDefault();
        this.orientation = orientation; 
    }
    
    public Element(Color color, Orientation orientation) {
        setDefault();
        this.color = color;
        this.orientation = orientation; 
    }
    public Element(int x, int y) {
        setDefault();
        this.x = x; 
        this.y = y; 
    }
    
    public Element(int x, int y, Color color) {
        setDefault();
        this.x = x; 
        this.y = y; 
        this.color = color;
    }
    
    public Element(int x, int y, Color color, Orientation orientation) {
        setDefault();
        this.x = x; 
        this.y = y; 
        this.color = color;
        this.orientation = orientation; 
    }

    public Color getColor(){
        return color; 
    }

    public void setColor(Color color){
        this.color = color; 
    }

    public int getX(){
        return x; 
    }

    public void setX(int x){
        this.x = x; 
    }

    public void setY(int y){
        this.y = y; 
    }

    public int getY(){
        return y; 
    }
    
    public Orientation getOrientation(){
        return orientation; 
    }
    
    public void setDefault(){
        x = resourceBundle.getInt("config.displayer.defaultX"); 
        y = resourceBundle.getInt("config.displayer.defaultY"); 
    }
    
    public abstract String toString(); 
    public abstract void setOrientation(Orientation orientation);
    public abstract Shape getShape();
    public abstract java.awt.Rectangle getBounds(); 
    public abstract void draw(Graphics2D g2D); 
    public abstract float getSize(); 
    
    public void setRatio(double ratio){
        this.ratio = ratio; 
    }
    
    public double getRatio(){
        return this.ratio; 
    }
    
    
    protected Color color = Color.BLACK; 
    protected int x = 100; 
    protected int y = 100; 
    protected double ratio; 
    private int pixelWidth = 0; 
    private int pixelHeight = 0; 
    protected Orientation orientation = Orientation.RIGHT;
    private dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N; 

    public int getPixelWidth() {
        return pixelWidth;
    }

    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
    }
}
