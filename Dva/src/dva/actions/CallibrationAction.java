/*
 * CalibrationAction.java
 *
 * Created on May 18, 2007, 10:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.actions;

import dva.displayer.Displayer;
import dva.util.GUIUtils;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author J-Chris
 */
public class CallibrationAction extends AbstractAction {

    public CallibrationAction(String text, String icon, String desc) {
        super(text, GUIUtils.createNavigationIcon(icon));
        putValue(SHORT_DESCRIPTION, desc);
    }

    public void actionPerformed(ActionEvent e) {

        //set callibration element
        Displayer.getInstance().getDisplayModel().enableCallibration(); 
        if (!Displayer.getInstance().isVisible()) Displayer.getInstance().setVisible(true);
        GUIUtils.showDialog(Displayer.getInstance().getJDialogCalibration(), true, e);
    }
}
