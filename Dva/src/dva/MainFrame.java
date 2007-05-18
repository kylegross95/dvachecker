/*
 * MainFrame.java
 *
 * Created on May 7, 2007, 5:52 AM
 */

package dva;

import dva.actions.CallibrationAction;
import dva.acuitytest.AcuityTestManager;
import dva.acuitytest.AcuityTestMaxStepException;
import dva.displayer.DisplayModel;
import dva.displayer.Displayer;
import dva.displayer.Element;
import dva.util.DvaLogger;
import dva.util.GUIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 *
 * @author  J-Chris
 */
public class MainFrame extends javax.swing.JFrame implements Observer {
    
    
    /**
     * @param o
     * @param object
     */
    public void update(Observable o, Object object){

        if (!callibrating){
            AcuityTestManager.Status status = AcuityTestManager.getStatus();

            if ( status == AcuityTestManager.Status.TEST_RUNNING || status == AcuityTestManager.Status.INIT){
                if (displayer.getDisplayModel().getState() == DisplayModel.State.PAUSE){
                    jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue")); 

                } else if (displayer.getDisplayModel().getState() == DisplayModel.State.TESTING){
                    jLabelClickArea.setText(resourceBundle.getString("message.clickarea.waitanswer")); 
                }

            } else if ( status == AcuityTestManager.Status.TEST_FAILED ){
                DvaLogger.debug(MainFrame.class, "TEST_FAILED");
                String[] options = {"Continue", "Abort"}; 
                int n = JOptionPane.showOptionDialog(this,
                        resourceBundle.getString("message.acuitytest."+AcuityTestManager.getAcuityTest().getTestName()+".failed"),
                        "Test Failure",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (n == JOptionPane.YES_OPTION){
                    //abort experiment
                    DvaLogger.debug(MainFrame.class, "Abort");
                } else {
                    //continue experiment
                    DvaLogger.debug(MainFrame.class, "Continue");
                }

            }  else if ( status == AcuityTestManager.Status.TEST_DONE ){
                DvaLogger.debug(MainFrame.class, "TEST_DONE");

                //update click area
                jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue")); 

                String finishedTestName = AcuityTestManager.getAcuityTestName().toUpperCase();

                JOptionPane.showMessageDialog(this, resourceBundle.getString("message.acuitytest.finished", finishedTestName));

                AcuityTestManager.setNextAcuityTest(); 

                JOptionPane.showMessageDialog(this, AcuityTestManager.getAcuityTest().getOperatorInstruction() ); 

            }  else if ( status == AcuityTestManager.Status.ALL_TEST_DONE ){
                DvaLogger.debug(MainFrame.class, "ALL_TEST_DONE");

            }
        }
    }
    
    // specific methods
    /**
     * Return the current patient invloved in the experiment
     */
    Patient getCurrentPatient(){
        return patient; 
    }
    
    /**
     * Coninent method to update the Patient data label
     */
    void updateJLabelPatientData(Patient patient){
        DvaLogger.debug("Updated patient data: \"" + patient.toString() + "\"");
        this.jLabelPatientData.setText( patient.toString() ); 
    }
    
    
    // Specific actions and listerners
    
    /**
     * New Experiment action
     */
    public class NewExperimentAction extends AbstractAction {
        
        public NewExperimentAction(String text, String icon, String desc) {
            super(text, GUIUtils.createNavigationIcon(icon));
            putValue(SHORT_DESCRIPTION, desc);
        }
        
        public void actionPerformed(ActionEvent e) {
            
            //propose a speeds set
            int speeds[] = AcuityTestManager.proposeSpeedSet(); 
            jLabelDialogPatientSpeedsSetValue.setText( AcuityTestManager.speedsSetToString(speeds) ); 
            
            //show new patient dialog
            GUIUtils.showDialog(jDialogPatientData, true, e);
        }
    }

    /**
     * MouseListener allowing to distinguish between Left and right click of the user
     */
    public class OperatorClickMouseListener implements MouseListener {
        
        public void mouseClicked(MouseEvent e){
            
            if (!enableClickArea) return; 
            
            //DvaLogger.debug(MainFrame.class, "MouseClicked:" + e.getButton()+", ModelState:"+displayer.getDisplayModel().getState()); 

            try {
                if (e.getButton() == MouseEvent.BUTTON1){
                    //left button on a right-handed mouse

                    Element element = displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.LEFT_CLICK);
                    
                    //update character position and orientation label
                    jLabelCharacter.setText(element.toString()); 
                    jLabelOrientation.setText(element.getOrientation().toString()); 
                    

                } else if (e.getButton() == MouseEvent.BUTTON3) { 
                    //right button on a right-handed mouse

                    Element element = displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.RIGHT_CLICK);

                    //update character position and orientation label
                    jLabelCharacter.setText(element.toString()); 
                    jLabelOrientation.setText(element.getOrientation().toString());
                }
                
            } catch (AcuityTestMaxStepException atmsex){
                DvaLogger.error(MainFrame.class, atmsex); 
            }

        }
        
        public void mousePressed(MouseEvent e){
            if (!enableClickArea) return; 
            
            DvaLogger.debug("MousePressed:" + e.getButton()+", ModelState:"+displayer.getDisplayModel().getState()); 
            
            if (displayer.getDisplayModel().getState() == DisplayModel.State.TESTING){
                if (e.getButton() == MouseEvent.BUTTON1){
                    jPanelClickArea.setBackground(Color.GREEN); 

                } else if (e.getButton() == MouseEvent.BUTTON3){
                    jPanelClickArea.setBackground(Color.RED); 
                }
            }
   
        }
        
        public void mouseReleased(MouseEvent e){
            if (!enableClickArea) return; 
            jPanelClickArea.setBackground(Color.DARK_GRAY);
        }
        
        public void mouseEntered(MouseEvent e){
            if (!enableClickArea) return; 
            DvaLogger.debug("Mouse ENTERED validation area");
            
            jPanelClickArea.setBackground(Color.DARK_GRAY);
        }
        
        public void mouseExited(MouseEvent e) {
            if (!enableClickArea) return; 
            DvaLogger.debug("Mouse EXITED validation area");
            
            jPanelClickArea.setBackground(jLabelClickAreaBackgroundColor);
        }
    }
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
         
        initComponents();
        
        //init logger
        DvaLogger.initLogger(jTextAreaLog); 
        
        //save jLabelClickArea background color
        jLabelClickAreaBackgroundColor = jLabelClickArea.getBackground();
        
        jLabelClickArea.addMouseListener(new OperatorClickMouseListener()); 
        
        //create displayer
        displayer = new Displayer();
        
        displayer.getDisplayModel().addObserver(this); 
        
        AcuityTestManager.proposeSpeedSet();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) { frameSize.height = screenSize.height; }
        if (frameSize.width > screenSize.width) { frameSize.width = screenSize.width; }
        this.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jDialogPatientData = new javax.swing.JDialog();
        jPanelDialogPatientData = new javax.swing.JPanel();
        jLabelDialogPatientSex = new javax.swing.JLabel();
        jRadioButtonDialogPatientSexM = new javax.swing.JRadioButton();
        jRadioButtonDialogPatientSexF = new javax.swing.JRadioButton();
        jLabelDialogPatientAge = new javax.swing.JLabel();
        jTextFieldDialogPatientAge = new javax.swing.JTextField();
        jLabelDialogPatientSpeedsSet = new javax.swing.JLabel();
        jLabelDialogPatientSpeedsSetValue = new javax.swing.JLabel();
        jButtonPatientOk = new javax.swing.JButton();
        jButtonPatientCancel = new javax.swing.JButton();
        buttonGroupDialogPatientSex = new javax.swing.ButtonGroup();
        jDialogAbout = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanelPatientData = new javax.swing.JPanel();
        jLabelPatientData = new javax.swing.JLabel();
        jButtonEditPatient = new javax.swing.JButton();
        jPanelAcuityTest = new javax.swing.JPanel();
        jButtonStartAcuityTest = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelCurrentTest = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabelTreadmillSpeed = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabelPatientSpeedsSetValue = new javax.swing.JLabel();
        jPanelResultsValidation = new javax.swing.JPanel();
        jPanelClickArea = new javax.swing.JPanel();
        jLabelClickArea = new javax.swing.JLabel();
        jPanelDisplayedCharacter = new javax.swing.JPanel();
        jLabelCharacter = new javax.swing.JLabel();
        jLabelOrientation = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanelLog = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaLog = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNewExperiment = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemQuit = new javax.swing.JMenuItem();
        jMenuOptions = new javax.swing.JMenu();
        jMenuCallibration = new javax.swing.JMenuItem();
        jCheckBoxMenuItemPauseBetween = new javax.swing.JCheckBoxMenuItem();
        jMenuView = new javax.swing.JMenu();
        jMenuViewDisplayer = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuAbout = new javax.swing.JMenuItem();

        jDialogPatientData.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialogPatientData.setTitle("Enter patient data");
        jDialogPatientData.setModal(true);
        jPanelDialogPatientData.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient data"));
        jLabelDialogPatientSex.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDialogPatientSex.setText("Sex");

        buttonGroupDialogPatientSex.add(jRadioButtonDialogPatientSexM);
        jRadioButtonDialogPatientSexM.setSelected(true);
        jRadioButtonDialogPatientSexM.setText("M");
        jRadioButtonDialogPatientSexM.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonDialogPatientSexM.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonDialogPatientSexM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDialogPatientSexMActionPerformed(evt);
            }
        });

        buttonGroupDialogPatientSex.add(jRadioButtonDialogPatientSexF);
        jRadioButtonDialogPatientSexF.setText("F");
        jRadioButtonDialogPatientSexF.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonDialogPatientSexF.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabelDialogPatientAge.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDialogPatientAge.setText("Age");

        jTextFieldDialogPatientAge.setText("20");

        jLabelDialogPatientSpeedsSet.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDialogPatientSpeedsSet.setText("Dynamic test order:");

        jLabelDialogPatientSpeedsSetValue.setForeground(new java.awt.Color(51, 94, 168));
        jLabelDialogPatientSpeedsSetValue.setText("FIX WALK RUN");

        org.jdesktop.layout.GroupLayout jPanelDialogPatientDataLayout = new org.jdesktop.layout.GroupLayout(jPanelDialogPatientData);
        jPanelDialogPatientData.setLayout(jPanelDialogPatientDataLayout);
        jPanelDialogPatientDataLayout.setHorizontalGroup(
            jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jLabelDialogPatientAge, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabelDialogPatientSex, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                    .add(jLabelDialogPatientSpeedsSet))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jTextFieldDialogPatientAge)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelDialogPatientDataLayout.createSequentialGroup()
                            .add(jRadioButtonDialogPatientSexM)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jRadioButtonDialogPatientSexF)))
                    .add(jLabelDialogPatientSpeedsSetValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelDialogPatientDataLayout.setVerticalGroup(
            jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelDialogPatientSex)
                    .add(jRadioButtonDialogPatientSexM)
                    .add(jRadioButtonDialogPatientSexF))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelDialogPatientAge)
                    .add(jTextFieldDialogPatientAge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelDialogPatientSpeedsSet)
                    .add(jLabelDialogPatientSpeedsSetValue)))
        );

        jButtonPatientOk.setText("Ok");
        jButtonPatientOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPatientOkActionPerformed(evt);
            }
        });

        jButtonPatientCancel.setText("Cancel");
        jButtonPatientCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPatientCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jDialogPatientDataLayout = new org.jdesktop.layout.GroupLayout(jDialogPatientData.getContentPane());
        jDialogPatientData.getContentPane().setLayout(jDialogPatientDataLayout);
        jDialogPatientDataLayout.setHorizontalGroup(
            jDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jDialogPatientDataLayout.createSequentialGroup()
                        .add(jButtonPatientOk)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonPatientCancel))
                    .add(jPanelDialogPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jDialogPatientDataLayout.setVerticalGroup(
            jDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDialogPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonPatientCancel)
                    .add(jButtonPatientOk))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialogAbout.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialogAbout.setTitle("About");
        jDialogAbout.setModal(true);
        jLabel4.setText("Roberto Cardona");

        jLabel5.setText("Jean-Christophe Fillion-Robin");

        org.jdesktop.layout.GroupLayout jDialogAboutLayout = new org.jdesktop.layout.GroupLayout(jDialogAbout.getContentPane());
        jDialogAbout.getContentPane().setLayout(jDialogAboutLayout);
        jDialogAboutLayout.setHorizontalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogAboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jLabel5))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialogAboutLayout.setVerticalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogAboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dynamic Visual Acuity Checker");
        setResizable(false);
        jPanelPatientData.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient data"));
        jLabelPatientData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPatientData.setText("M-20");

        jButtonEditPatient.setText("Edit");
        jButtonEditPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditPatientActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelPatientDataLayout = new org.jdesktop.layout.GroupLayout(jPanelPatientData);
        jPanelPatientData.setLayout(jPanelPatientDataLayout);
        jPanelPatientDataLayout.setHorizontalGroup(
            jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonEditPatient)
                .add(18, 18, 18))
        );
        jPanelPatientDataLayout.setVerticalGroup(
            jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelPatientDataLayout.createSequentialGroup()
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonEditPatient)
                    .add(jLabelPatientData))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanelAcuityTest.setBorder(javax.swing.BorderFactory.createTitledBorder("Acuity Test"));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dva/Bundle"); // NOI18N
        jButtonStartAcuityTest.setText(bundle.getString("button.mainframe.startacuitytest")); // NOI18N
        jButtonStartAcuityTest.setEnabled(false);
        jButtonStartAcuityTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartAcuityTestActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(51, 94, 168));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Current Test:");

        jLabel2.setForeground(new java.awt.Color(51, 94, 168));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Treadmill speed:");

        jLabelCurrentTest.setText("Landolt C test");

        jLabel8.setText("km/h");

        jLabelTreadmillSpeed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTreadmillSpeed.setText("0");

        jLabel7.setForeground(new java.awt.Color(51, 94, 168));
        jLabel7.setText("Dynamic test order:");

        jLabelPatientSpeedsSetValue.setText(" ");

        org.jdesktop.layout.GroupLayout jPanelAcuityTestLayout = new org.jdesktop.layout.GroupLayout(jPanelAcuityTest);
        jPanelAcuityTest.setLayout(jPanelAcuityTestLayout);
        jPanelAcuityTestLayout.setHorizontalGroup(
            jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelAcuityTestLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonStartAcuityTest, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                    .add(jPanelAcuityTestLayout.createSequentialGroup()
                        .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelAcuityTestLayout.createSequentialGroup()
                                .add(jLabelTreadmillSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                            .add(jLabelCurrentTest, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
                    .add(jPanelAcuityTestLayout.createSequentialGroup()
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabelPatientSpeedsSetValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelAcuityTestLayout.setVerticalGroup(
            jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelAcuityTestLayout.createSequentialGroup()
                .add(jButtonStartAcuityTest)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelCurrentTest)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelTreadmillSpeed)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jLabelPatientSpeedsSetValue))
                .addContainerGap())
        );

        jPanelResultsValidation.setBorder(javax.swing.BorderFactory.createTitledBorder("Operator real-time results validation"));
        jPanelClickArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jLabelClickArea.setFont(jLabelClickArea.getFont().deriveFont(jLabelClickArea.getFont().getSize()+9f));
        jLabelClickArea.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout jPanelClickAreaLayout = new org.jdesktop.layout.GroupLayout(jPanelClickArea);
        jPanelClickArea.setLayout(jPanelClickAreaLayout);
        jPanelClickAreaLayout.setHorizontalGroup(
            jPanelClickAreaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabelClickArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
        );
        jPanelClickAreaLayout.setVerticalGroup(
            jPanelClickAreaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabelClickArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        jPanelDisplayedCharacter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabelCharacter.setFont(new java.awt.Font("Tahoma", 0, 18));
        jLabelCharacter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCharacter.setText("0");

        jLabelOrientation.setFont(new java.awt.Font("Tahoma", 0, 18));
        jLabelOrientation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelOrientation.setText("-");

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel3.setForeground(new java.awt.Color(51, 94, 168));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Character:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel6.setForeground(new java.awt.Color(51, 94, 168));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Orientation:");

        org.jdesktop.layout.GroupLayout jPanelDisplayedCharacterLayout = new org.jdesktop.layout.GroupLayout(jPanelDisplayedCharacter);
        jPanelDisplayedCharacter.setLayout(jPanelDisplayedCharacterLayout);
        jPanelDisplayedCharacterLayout.setHorizontalGroup(
            jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelDisplayedCharacterLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .add(25, 25, 25)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabelOrientation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabelCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelDisplayedCharacterLayout.setVerticalGroup(
            jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDisplayedCharacterLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .add(jLabelCharacter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelOrientation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanelResultsValidationLayout = new org.jdesktop.layout.GroupLayout(jPanelResultsValidation);
        jPanelResultsValidation.setLayout(jPanelResultsValidationLayout);
        jPanelResultsValidationLayout.setHorizontalGroup(
            jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelResultsValidationLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelClickArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelDisplayedCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelResultsValidationLayout.setVerticalGroup(
            jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelResultsValidationLayout.createSequentialGroup()
                .add(jPanelDisplayedCharacter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelClickArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelLog.setBorder(javax.swing.BorderFactory.createTitledBorder("Log"));
        jTextAreaLog.setColumns(20);
        jTextAreaLog.setEditable(false);
        jTextAreaLog.setLineWrap(true);
        jTextAreaLog.setRows(5);
        jScrollPane1.setViewportView(jTextAreaLog);

        org.jdesktop.layout.GroupLayout jPanelLogLayout = new org.jdesktop.layout.GroupLayout(jPanelLog);
        jPanelLog.setLayout(jPanelLogLayout);
        jPanelLogLayout.setHorizontalGroup(
            jPanelLogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLogLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLogLayout.setVerticalGroup(
            jPanelLogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLogLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenuFile.setText("File");
        jMenuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileActionPerformed(evt);
            }
        });

        jMenuItemNewExperiment.setAction(new NewExperimentAction("New Experiment", "newexp24", "Create a new experiment"));
        jMenuItemNewExperiment.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuFile.add(jMenuItemNewExperiment);

        jMenuFile.add(jSeparator1);

        jMenuItemQuit.setText("Quit");
        jMenuFile.add(jMenuItemQuit);

        jMenuBar1.add(jMenuFile);

        jMenuOptions.setText("Options");
        jMenuCallibration.setAction(new CallibrationAction("Callibrate Displayer", "calibrate24", "Callibrate Displayer"));
        jMenuOptions.add(jMenuCallibration);

        jCheckBoxMenuItemPauseBetween.setText("Set Pauses");
        jCheckBoxMenuItemPauseBetween.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemPauseBetweenActionPerformed(evt);
            }
        });

        jMenuOptions.add(jCheckBoxMenuItemPauseBetween);

        jMenuBar1.add(jMenuOptions);

        jMenuView.setText("View");
        jMenuViewDisplayer.setText("Show/Hide \"DVA Displayer\"");
        jMenuViewDisplayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuViewDisplayerActionPerformed(evt);
            }
        });

        jMenuView.add(jMenuViewDisplayer);

        jMenuBar1.add(jMenuView);

        jMenuHelp.setText("Help");
        jMenuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuHelpActionPerformed(evt);
            }
        });

        jMenuAbout.setText("About");
        jMenuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAboutActionPerformed(evt);
            }
        });

        jMenuHelp.add(jMenuAbout);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelLog, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanelAcuityTest, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanelPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 237, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelResultsValidation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanelPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelAcuityTest, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanelResultsValidation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelLog, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxMenuItemPauseBetweenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemPauseBetweenActionPerformed
        this.displayer.getDisplayModel().setPauseBetween(jCheckBoxMenuItemPauseBetween.isSelected()); 
    }//GEN-LAST:event_jCheckBoxMenuItemPauseBetweenActionPerformed

    private void jButtonStartAcuityTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartAcuityTestActionPerformed
        //check if displayer is visible
        if (!displayer.isVisible()) displayer.setVisible(true);
        
        //setup acuitytest
        this.displayer.getDisplayModel().setupAcuityTest(); 
        
        //set clickarea message
        jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue"));
        
        //enable click area
        enableClickArea(true);
        
        //disable start button
        this.jButtonStartAcuityTest.setEnabled(false); 
        
    }//GEN-LAST:event_jButtonStartAcuityTestActionPerformed

    private void jMenuViewDisplayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuViewDisplayerActionPerformed
        displayer.setVisible(!displayer.isVisible());
    }//GEN-LAST:event_jMenuViewDisplayerActionPerformed

    private void jRadioButtonDialogPatientSexMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDialogPatientSexMActionPerformed
// TODO add your handling code here:
        
    }//GEN-LAST:event_jRadioButtonDialogPatientSexMActionPerformed

    private void jButtonEditPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditPatientActionPerformed
        int[] speeds = AcuityTestManager.acceptProposedSpeedsSet();
        jLabelDialogPatientSpeedsSetValue.setText( AcuityTestManager.speedsSetToString(speeds) ); 
        GUIUtils.showDialog(jDialogPatientData, true, evt);
    }//GEN-LAST:event_jButtonEditPatientActionPerformed

    private void jMenuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHelpActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jMenuHelpActionPerformed

    private void jMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutActionPerformed
        GUIUtils.showDialog(jDialogAbout, true, evt); 
    }//GEN-LAST:event_jMenuAboutActionPerformed

    private void jMenuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFileActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jMenuFileActionPerformed

    private void jButtonPatientCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPatientCancelActionPerformed
        GUIUtils.showDialog(jDialogPatientData, false, evt); 
    }//GEN-LAST:event_jButtonPatientCancelActionPerformed

    private void jButtonPatientOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPatientOkActionPerformed
        //get new patient data
        String sex = GUIUtils.getSelection(buttonGroupDialogPatientSex).getText();
        String age = this.jTextFieldDialogPatientAge.getText(); 
        
        //update patient
        getCurrentPatient().setSex(sex); 
        getCurrentPatient().setAge(age); 
        
        int speeds[] = AcuityTestManager.acceptProposedSpeedsSet(); 
        jLabelPatientSpeedsSetValue.setText( AcuityTestManager.speedsSetToString(speeds) ); 
        
        
        //enable StartAcuityTest button
        jButtonStartAcuityTest.setEnabled(true); 

        
        //update GUI
        updateJLabelPatientData(getCurrentPatient());
        
        //close dialog
        GUIUtils.showDialog(jDialogPatientData, false, evt); 
    }//GEN-LAST:event_jButtonPatientOkActionPerformed
    
    public void enableClickArea(boolean state){
        this.enableClickArea = state; 
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    
    //Model
    private Patient patient = new Patient();  
    
    //GUI
    private Color jLabelClickAreaBackgroundColor = null; 
    private Displayer displayer = null; 
    private boolean enableClickArea = false; 
    private boolean callibrating = false;
    
    //resources
    private dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N; 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupDialogPatientSex;
    private javax.swing.JButton jButtonEditPatient;
    private javax.swing.JButton jButtonPatientCancel;
    private javax.swing.JButton jButtonPatientOk;
    private javax.swing.JButton jButtonStartAcuityTest;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemPauseBetween;
    private javax.swing.JDialog jDialogAbout;
    private javax.swing.JDialog jDialogPatientData;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelCharacter;
    private javax.swing.JLabel jLabelClickArea;
    private javax.swing.JLabel jLabelCurrentTest;
    private javax.swing.JLabel jLabelDialogPatientAge;
    private javax.swing.JLabel jLabelDialogPatientSex;
    private javax.swing.JLabel jLabelDialogPatientSpeedsSet;
    private javax.swing.JLabel jLabelDialogPatientSpeedsSetValue;
    private javax.swing.JLabel jLabelOrientation;
    private javax.swing.JLabel jLabelPatientData;
    private javax.swing.JLabel jLabelPatientSpeedsSetValue;
    private javax.swing.JLabel jLabelTreadmillSpeed;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuCallibration;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemNewExperiment;
    private javax.swing.JMenuItem jMenuItemQuit;
    private javax.swing.JMenu jMenuOptions;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JMenuItem jMenuViewDisplayer;
    private javax.swing.JPanel jPanelAcuityTest;
    private javax.swing.JPanel jPanelClickArea;
    private javax.swing.JPanel jPanelDialogPatientData;
    private javax.swing.JPanel jPanelDisplayedCharacter;
    private javax.swing.JPanel jPanelLog;
    private javax.swing.JPanel jPanelPatientData;
    private javax.swing.JPanel jPanelResultsValidation;
    private javax.swing.JRadioButton jRadioButtonDialogPatientSexF;
    private javax.swing.JRadioButton jRadioButtonDialogPatientSexM;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaLog;
    private javax.swing.JTextField jTextFieldDialogPatientAge;
    // End of variables declaration//GEN-END:variables
    
}
