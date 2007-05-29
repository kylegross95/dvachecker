/*
 * DisplayView.java
 *
 * Created on May 7, 2007, 2:48 PM
 *
 */

package dva.displayer;

import dva.actions.CallibrationAction;
import dva.util.DvaLogger;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author J-Chris
 */
public class DisplayView extends JPanel implements Observer {
    
    
    /**
     * Creates a new instance of DisplayView
     */
    public DisplayView(Displayer cd) {
        this.cd = cd; 
        this.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        setBackground(Color.WHITE);
        createPopupMenu();
    }
    
    public void createPopupMenu() {
        JMenuItem menuItem;

        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        
        //item resize
        menuItem = new JMenuItem("Maximize/Restaure window");
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cd.resizeDisplayer(); 
            }
        });
        popup.add(menuItem);
        
        //item calibration
        menuItem = new JMenuItem("Calibrate displayer");
        menuItem.addActionListener(new CallibrationAction("Calibrate Displayer", "calibrate24", "Calibrate Displayer")); 
        
//        menuItem.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                GUIUtils.showDialog(cd.getJDialogCalibration(), true, evt);
//            }
//        });
        popup.add(menuItem);

        //Add listener to the text area so the popup menu can come up.
        DisplayerMouseListener displayerMouseListener = new DisplayerMouseListener(popup);
        addMouseListener(displayerMouseListener);
        addMouseMotionListener(displayerMouseListener); 
    }
    
    public void update(Observable o, Object Rectangle){
        repaint();
    }
    
    public void paint(Graphics g){
        super.paint(g); 
        Graphics2D g2D = (Graphics2D)g; 
        
        //g2D.clearRect(0,0,this.getWidth(), this.getHeight()); 
                
        AffineTransform tx = new AffineTransform();
        //translate character
        tx.translate( translateX, translateY );
        //scale
        //tx.scale(cd.getDisplayModel().getScalingFactor(), cd.getDisplayModel().getScalingFactor());
        //translate to origine
        //tx.translate(de.getX(), de.getY()); 
        //rotate
        //tx.rotate(de.getOrientation().ordinal() * 45); 
        //translate back
        //tx.translate(-de.getX(), -de.getY());
        //apply transform
        g2D.setTransform(tx);
        
        g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
       // g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
       //         RenderingHints.VALUE_ANTIALIAS_ON);
            
       // g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
       //     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (cd.getDisplayModel().isMessage()){
            g2D.setFont(cd.getDisplayModel().getMessageFont()); 
            g2D.setPaint(cd.getDisplayModel().getMessageColor()); 
            g2D.drawString(cd.getDisplayModel().getMessageToDisplay(), cd.getDisplayModel().getX(), cd.getDisplayModel().getY());
            
        } else {
            //tx = new AffineTransform();
            tx.scale(cd.getDisplayModel().getScalingFactor(), cd.getDisplayModel().getScalingFactor());
            g2D.setTransform(tx);
              
            cd.getDisplayModel().getCurrentDisplayedElement().draw(g2D); 
            
            
        }
        
        //help garbage
        tx = null; 
    }
    
    JPopupMenu popupMenu; 
    private Displayer cd; 
    private int translateX = 0; 
    private int translateY = 0; 
    
    
    class DisplayerMouseListener extends MouseAdapter implements MouseMotionListener {
        
        private JPopupMenu popup;
        private int lastOffsetX;
        private int lastOffsetY;

        DisplayerMouseListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            // capture starting point
            lastOffsetX = e.getX();
            lastOffsetY = e.getY();
                        
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
        
        //mousemotionlistener methods
        public void mouseMoved(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {
            // new x and y are defined by current mouse location subtracted
            // by previously processed mouse location
            int newX = e.getX() - lastOffsetX;
            int newY = e.getY() - lastOffsetY;

            // increment last offset to last processed by drag event.
            lastOffsetX += newX;
            lastOffsetY += newY;

            // update the character location
            translateX += newX; 
            translateY += newY; 

            // schedule a repaint.
            repaint();
        }
        
    }
    
}
