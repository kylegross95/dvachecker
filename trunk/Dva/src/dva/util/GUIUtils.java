/*
 * GUIUtils.java
 *
 * Created on May 7, 2007, 10:56 AM
 *
 */

package dva.util;

import java.awt.Component;
import java.awt.Container;
import java.util.Enumeration;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

/**
 *
 * @author J-Chris
 */
public class GUIUtils {
    
    /**
     * The System property key for the user home directory.
     */
    private static final String USER_HOME_KEY = "user.home";

    /**
     * The System property key for the user directory.
     */
    private static final String USER_DIR_KEY = "user.dir";
    
    /**
     * Prevent from instanciation
     */
    private GUIUtils() {
    }
    
    static public void showDialog(Container parent, JDialog dialog, boolean state, java.awt.event.ActionEvent evt){
        if (state){
            dialog.pack(); 
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        } else {
            dialog.setVisible(false);
            dialog.dispose();
        }
    }
    
    /** 
     * Returns an ImageIcon, or null if the path was invalid. 
    */
    public static ImageIcon createNavigationIcon(String imageName) {
        String imgLocation = "images/"
                             + imageName
                             + ".gif";
        java.net.URL imageURL = GUIUtils.class.getResource(imgLocation);

        if (imageURL == null) {
            System.err.println("Resource not found: "
                               + imgLocation);
            return null;
        } else {
            return new ImageIcon(imageURL);
        }
    }
    
    /**
     * Get the selected radioButton from a given buttonGroup
     */
    public static JRadioButton getSelection(ButtonGroup group) {
        for (Enumeration e=group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if (b.getModel() == group.getSelection()) {
                return b;
            }
        }
        return null;
    }
    
    public static void showWarning(Component parent, String title, String message){
        JOptionPane.showMessageDialog(parent,
                    message,
                    title,
                    JOptionPane.WARNING_MESSAGE);
    }
    
    public static int askOperator(Component parent, String title, String message){
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
    }

}
