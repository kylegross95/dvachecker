/*
 * OptotypeC.java
 *
 * Created on May 17, 2007, 12:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.displayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author J-Chris
 */
public class OptotypeC extends Element {
    
    
    public OptotypeC(int x, int y, float size, Color color) {
        super(x, y, color);
        this.size = size;  
        init();
    }
    
     public OptotypeC(float size, Color color) {
        super(color);
        this.size = size;  
        init();
    }
    
    public OptotypeC(float size, Orientation orientation) {
        super(orientation);
        this.size = size;  
        init();
    }
    
    public OptotypeC(float size) {
        this.size = size;  
        init();
    }
    
    private void init(){
        float gapsize = size / 5; 
        float sizeIn = size - gapsize * 2; 
      
        Ellipse2D.Float circleOut = new Ellipse2D.Float(x - size / 2, y - size / 2, size, size); 
           
        Ellipse2D.Float circleIn = new Ellipse2D.Float(x - sizeIn/2, y - sizeIn / 2, sizeIn, sizeIn); 
      
        Rectangle2D.Float gap = new Rectangle2D.Float(x, y - gapsize /2, size / 2, gapsize); 
      
        // Create Areas from the shapes.
        area = new Area(circleOut);
        area.subtract( new Area(circleIn) ); 
        area.subtract( new Area(gap) );
    }

    public void setOrientation(Orientation orientation){

    }
    
    public String toString(){
        return "OPTOTYPE_C"; 
    }

    // Return the circle as a Shape reference
    public Shape getShape() { 
        
        return area; 
    }
    
    public float getSize() {
        return size; 
    }
    
    public void draw(Graphics2D g2D){
        AffineTransform tx = new AffineTransform();
        //translate to origine
        tx.translate(x, y); 
        //rotate
        tx.rotate(Math.toRadians( orientation.ordinal() * 45 ) ); 
        //translate back
        tx.translate(-x, -y);
        //apply transform
        g2D.setTransform(tx);
        
        g2D.setPaint(this.color); 
        g2D.draw(area); 
        g2D.fill(area);
    }
    
     public String toXml(){ return ""; }

    // Return the rectangle bounding this circle
    public java.awt.Rectangle getBounds() { 
      return area.getBounds();  
    }

    
    private float size; 
    private Area area; 
}
