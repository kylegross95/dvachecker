/*
 * ImagesBuffer.java
 *
 * Created on June 11, 2007, 3:54 AM
 *
 */

package dva.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author J-Chris
 */
public class ImagesBuffer {
    
    //list of already instancied bufferedimage
    private static HashMap<String, BufferedImage> bimgList = new HashMap<String, BufferedImage>();
    
    //prevent from instanciation
    private ImagesBuffer() {
    }
    
    public static BufferedImage get(String name){
        //check if bufferedImage is already existing
        BufferedImage bimg = bimgList.get(name); 
        
        if (bimg==null) {
            //open file
            //URL url = this.getClass().getResource("/dva/ressources/"+name+".jpg"); 
            URL url = BufferedImage.class.getResource("/dva/ressources/"+name+".jpg"); 
            if (url!=null){
                try {
                    bimg = ImageIO.read(url);
                    bimgList.put(name, bimg); 
                } catch (IOException ex) {
                    DvaLogger.fatal( ImagesBuffer.class, ex, "Failed to read image file " + url.getFile() ); 
                }
            } else {
                DvaLogger.fatal(ImagesBuffer.class, "Failed to access image " + "/dva/ressources/"+name+".jpg"); 
            }
            
        } else {
            DvaLogger.debug(ImagesBuffer.class, "Re-use bufferedimage named '" + name + "'");
        }
        
        return bimg; 
    }
    
}
