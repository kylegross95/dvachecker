/*
 * Optotype.java
 *
 * Created on May 28, 2007, 12:47 PM
 *
 */

package dva.displayer;


import dva.util.DvaLogger;
import dva.util.ScreenMapper;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;


/**
 *
 * @author J-Chris
 */
public class Optotype extends Element {
    
    
    public Optotype(String name, int x, int y, float size, Color color) {
        super(x, y, color);
        this.size = size;  
        this.va = ScreenMapper.getVA( Math.round(this.size) ); 
        this.name = name; 
        init();
    }
    
     public Optotype(String name, float size, Color color) {
        super(color);
        this.size = size;  
        this.va = ScreenMapper.getVA( Math.round(this.size) ); 
        this.name = name; 
        init();
    }
    
    public Optotype(String name, float size, Orientation orientation) {
        super(orientation);
        this.size = size;  
        this.va = ScreenMapper.getVA( Math.round(this.size) ); 
        this.name = name; 
        init();
    }
    
    public Optotype(String name, float size) {
        this.size = size;  
        this.va = ScreenMapper.getVA( Math.round(this.size) ); 
        this.name = name; 
        init();
    }
    
    public Optotype() {
        this.size = 14;  
        this.va = ScreenMapper.getVA( Math.round(this.size) ); 
        this.name = "square"; 
        init();
    }
    
    private void init(){
        //check if bufferedImage is already existing
        bimg = bimgList.get(name); 
        
        if (bimg==null) {
            //open file
            URL url = this.getClass().getResource("/dva/ressources/"+name+".jpg"); 
            if (url!=null){
                try {
                    bimg = ImageIO.read(url);
                    bimgList.put(name, bimg); 
                } catch (IOException ex) {
                    DvaLogger.fatal( Optotype.class, ex, "Failed to read image file " + url.getFile() ); 
                }
            } else {
                DvaLogger.fatal(Optotype.class, "Failed to access image " + url.getFile()); 
            }
            
        } else {
            DvaLogger.debug(Optotype.class, "Re-use bufferedimage named '" + name + "'");
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
    
    public double getVa(){
        return this.va; 
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
        
        //set background color
        g2D.setPaint(this.color); 
        
        DvaLogger.debug(Optotype.class, "size:" + size);
        
        //translate and scale optotype
        ScreenMapper sm = ScreenMapper.getInstance(); 
        sm.scale(this, Displayer.getInstance()); 
        sm.center(this, Displayer.getInstance()); 
        
        //apply transformation to graphics object
        g2D.translate(this.getX(), this.getY()); 
        
        g2D.scale(getRatio(), getRatio()); 
        
        DvaLogger.debug(ScreenMapper.class, "translate x/y:" + this.getX() + "/" + this.getY() ); 
        
        //draw the element
        g2D.drawImage(bimg, 0, 0, null);
    }
    
    public BufferedImage getBimg(){
        return this.bimg; 
    }

    // Return the rectangle bounding this circle
    public java.awt.Rectangle getBounds() { 
      return null;  
    }
    
    public String toXml(){
        StringBuffer sb = new StringBuffer("<optotype><name>");
        sb.append(this.name); 
        sb.append("</name><acuity>"); 
        String vaStr = String.valueOf( va );  
        vaStr = vaStr == null ? "" : vaStr; 
        DvaLogger.debug("va:" + vaStr); 
        sb.append( vaStr ); 
        sb.append("</acuity></optotype>"); 
        return sb.toString();
    }
    
    public void setName(String name){
        this.name = name; 
    }
    
    public void setVa(double va){
        this.va = va; 
    }
    
    //list of already instancied bufferedimage
    private static HashMap<String, BufferedImage> bimgList = new HashMap<String, BufferedImage>();

    private BufferedImage bimg = null; 
    private float size; 
    private double va; 
    protected String name = ""; 
}


