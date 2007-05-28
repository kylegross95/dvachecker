/*
 * Optotype.java
 *
 * Created on May 28, 2007, 12:47 PM
 *
 */

package dva.displayer;


import dva.util.DvaLogger;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;


/**
 *
 * @author J-Chris
 */
public class Optotype extends Element {
    
    
    public Optotype(String name, int x, int y, float size, Color color) {
        super(x, y, color);
        this.size = size;  
        this.name = name; 
        init();
    }
    
     public Optotype(String name, float size, Color color) {
        super(color);
        this.size = size;  
        this.name = name; 
        init();
    }
    
    public Optotype(String name, float size, Orientation orientation) {
        super(orientation);
        this.size = size;  
        this.name = name; 
        init();
    }
    
    public Optotype(String name, float size) {
        this.size = size;  
        this.name = name; 
        init();
    }
    
    private void init(){
        //open file
        URL url = this.getClass().getResource("/dva/ressources/"+name+".jpg"); 
        if (url!=null){
            try {
                bimg = ImageIO.read(url);
            } catch (IOException ex) {
                DvaLogger.fatal( Optotype.class, ex, "Failed to read image file " + url.getFile() ); 
            }
        } else {
            DvaLogger.fatal(Optotype.class, "Failed to access image " + url.getFile()); 
        }
    }

    public void setOrientation(Orientation orientation){

    }
    
    public String toString(){
        return "OPTOTYPE_" + name; 
    }

    // Return the circle as a Shape reference
    public Shape getShape() { 
        
        return null; 
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
        
        g2D.scale(0.2, 0.2); 
        
        g2D.drawImage(bimg, 0, 0, null); 
        //g2D.draw(area); 
        //g2D.fill(area);
    }

    // Return the rectangle bounding this circle
    public java.awt.Rectangle getBounds() { 
      return null;  
    }

    private BufferedImage bimg = null; 
    private float size; 
    protected String name = ""; 
}


