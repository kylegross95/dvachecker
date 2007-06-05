/*
 * DisplayElement.java
 *
 * Created on May 8, 2007, 2:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.displayer;

import dva.displayer.Element.Orientation;
import dva.util.DvaLogger;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

/**
 *
 * @author J-Chris
 */
public class DisplayElement extends Element {

    public DisplayElement(String character, float size){
        this.character = character; 
        this.font = this.font.deriveFont(size); 
    }

     public DisplayElement(String character, float size, Orientation orientation){ 
        super(orientation); 
        this.character = character; 
        this.font = this.font.deriveFont(size); 
        //rotate font
        AffineTransform tx = new AffineTransform();
        tx.rotate( Math.toRadians(orientation.ordinal() * 45 ) ) ; 
        this.font = this.font.deriveFont(tx); 
    }

    public DisplayElement(int x, int y, String character, float size){
        super(x, y); 
        this.character = character; 
        this.font = this.font.deriveFont(size); 
    }

     public DisplayElement(int x, int y, String character, float size, Orientation orientation){
        super(x, y); 
        this.character = character; 
        this.font = this.font.deriveFont(size); 
        //rotate font
        AffineTransform tx = new AffineTransform();
        tx.rotate( Math.toRadians(orientation.ordinal() * 45 ) ) ; 
        this.font = this.font.deriveFont(tx); 
        
        this.orientation = orientation;
    }

    public DisplayElement(int x, int y, String character, Font font) {
        super(x, y);
        this.character = character; 
        this.font = font; 
    }

    /**
     * Creates a new instance of DisplayElement
     */
    public DisplayElement(int x, int y, String character, Font font, Orientation orientation) {
        super(x, y); 
        this.character = character; 
        this.font = font; 
        this.orientation = orientation; 
        //rotate font
        AffineTransform tx = new AffineTransform();
        tx.rotate( Math.toRadians(orientation.ordinal() * 45 ) ) ; 
        this.font = this.font.deriveFont(tx); 
    }
    
    public java.awt.Rectangle getBounds(FontRenderContext fontRenderContext){
        return (java.awt.Rectangle)font.getMaxCharBounds(fontRenderContext); 
    }
    
    public java.awt.Rectangle getBounds(){
        return null;
    }
    
     public Shape getShape(){
         return null; 
     }

    public String getCharacter(){
        return character;
    }

    public void setCharacter(String character){
        this.character = character; 
    }

    public Font getFont(){
        return font; 
    }

    public void setFont(Font font){
        this.font = font; 
    }

    

    public void setOrientation(Orientation orientation){
        this.orientation = orientation;
        //rotate font
        AffineTransform tx = new AffineTransform();
        tx.rotate( Math.toRadians(orientation.ordinal() * 45 ) ) ; 
        this.font = this.font.deriveFont(tx); 
    }
    
    public void reset(){
        font = new Font("Tahoma", Font.PLAIN, 30);
    }
    
    public float getSize(){
        DvaLogger.debug(DisplayElement.class, "Size:" + font.getSize());
        return font.getSize(); 
    }
    
    public String toString(){
        return character; 
    }

    public void setSize(float size){
        this.font = this.font.deriveFont(size); 
    }
    
    public void draw(Graphics2D g2D){
        g2D.setFont(getFont()); 
        g2D.setPaint(getColor()); 
        g2D.drawString(character, x, y);
    }
    
    public String toXml(){
        StringBuffer sb = new StringBuffer("<displayelement><character>");
        sb.append(character); 
        sb.append("</character><size>"); 
        sb.append(this.getSize()); 
        sb.append("</size></displayelement>"); 
        return sb.toString(); 
    }
 
    private String character = "0"; 
    private Font font = new Font("Tahoma", Font.PLAIN, 30); 
}
