/*
 * MainFrame.java
 *
 * Created on May 7, 2007, 5:52 AM
 */

package dva;

import dva.acuitytest.AcuityTestManager;
import dva.displayer.DisplayModel;
import dva.displayer.Displayer;
import dva.util.DvaLogger;
import dva.util.GUIUtils;
import dva.util.ScreenMapper;
import dva.xml.PatientReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

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

        try {
            DisplayModel.EventType eventType= (DisplayModel.EventType)object; 

            if (!callibrating && eventType==DisplayModel.EventType.OPERATOR_EVENT ){
                AcuityTestManager.Status status = AcuityTestManager.getStatus();

                if ( status == AcuityTestManager.Status.TEST_RUNNING || status == AcuityTestManager.Status.INIT){
                    if (displayer.getDisplayModel().getState() == DisplayModel.State.PAUSE){
                        //jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue")); 

                        //enable the next button
                        this.jButtonDisplayNextOptotype.setEnabled(true); 

                    } else if (displayer.getDisplayModel().getState() == DisplayModel.State.TESTING){
                        //jLabelClickArea.setText(resourceBundle.getString("message.clickarea.waitanswer")); 
                        //enable the next button
                        this.jButtonDisplayNextOptotype.setEnabled(false); 

                        jLabelCharacter.setText(AcuityTestManager.getCurrentAcuityTest().getCurrentElement().toString()); 
                    }

                } else if ( status == AcuityTestManager.Status.TEST_FAILED ){
                    DvaLogger.debug(MainFrame.class, "TEST_FAILED");
                    String[] options = {"Continue", "Abort"}; 
                    int n = JOptionPane.showOptionDialog(this,
                            resourceBundle.getString("message.acuitytest."+AcuityTestManager.getCurrentAcuityTest().getTestName()+".failed"),
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
                    //jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue")); 

                    String finishedTestName = AcuityTestManager.getAcuityTestName().toUpperCase();

                    JOptionPane.showMessageDialog(this, resourceBundle.getString("message.acuitytest.finished", finishedTestName));

                    AcuityTestManager.setNextAcuityTest(patient.getPatientdir()); 

                    JOptionPane.showMessageDialog(this, AcuityTestManager.getCurrentAcuityTest().getOperatorInstruction() ); 

                }  else if ( status == AcuityTestManager.Status.ALL_TEST_DONE ){
                    DvaLogger.debug(MainFrame.class, "ALL_TEST_DONE");
                }
            }
        } catch (Exception e){
            GUIUtils.showWarning(this, "Problem !", e.getMessage()); 
            DvaLogger.error(MainFrame.class, e); 
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
        this.jLabelPatientName.setText( patient.getLastname() + " " + patient.getFirstname() );
        this.jLabelPatientSex.setText( patient.getSex() ); 
        this.jLabelPatientAge.setText( patient.getAge() );
        this.jTextAreaPatientComment.setText(patient.getComment()); 
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
            //int speeds[] = AcuityTestManager.proposeSpeedSet(); 
            //jLabelDialogPatientSpeedsSetValue.setText( AcuityTestManager.speedsSetToString(speeds) ); 
            
            //disable the next button
            jButtonDisplayNextOptotype.setEnabled(false); 
            
            //set no character
            jLabelCharacter.setText(" ");
            
            //reset Patient data dialog
            resetPatientDataDialog(); 
            
            //show new patient dialog
            GUIUtils.showDialog(jDialogPatientData, true, e);
        }
    }
    
    
    /**
     * New Experiment action
     */
    public class SetupDisplayerAction extends AbstractAction {
        
        public SetupDisplayerAction(String text, String icon, String desc) {
            super(text, GUIUtils.createNavigationIcon(icon));
            putValue(SHORT_DESCRIPTION, desc);
        }   
        
        public void actionPerformed(ActionEvent e) {
            
            Displayer.getInstance().getDisplayModel().enableCallibration();
            
            //show displayer setup dialog
            GUIUtils.showDialog(jDialogDisplayerOption, true, e);
        }
    }
    
    private void setupOutputDirectory(){
        // create filename
        outputdir = new File(SystemUtils.getUserHome() + "/dvachecker_data"); 
        
        // try to create directory
        outputdir.mkdir(); 
        
        DvaLogger.info(MainFrame.class, "Output directory:" + outputdir.getAbsolutePath()); 
    }
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
         
        initComponents();
        
        //init logger
        DvaLogger.initLogger(jTextAreaLog); 
        
        //setup output directory
        setupOutputDirectory();
        
        //create patient object
        patient = new Patient(outputdir); 
        
        //detect output screens
        ScreenMapper.getInstance().detectOutputScreen(); 
        
        //create displayer
        displayer = new Displayer(ScreenMapper.getInstance().getOutputGraphicsDevice().getDefaultConfiguration());
        
        displayer.getDisplayModel().addObserver(this); 
        
        //AcuityTestManager.proposeSpeedSet();
        
        //load existing client
        loadPatientsList(); 
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) { frameSize.height = screenSize.height; }
        if (frameSize.width > screenSize.width) { frameSize.width = screenSize.width; }
        this.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        
        //Dimension d = this.getToolkit().getScreenSize(); 
        //DvaLogger.debug(MainFrame.class, "height:" + d.getHeight() ); 
        //DvaLogger.debug(MainFrame.class, "width:" + d.getWidth() ); 
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
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldDialogPatientLastname = new javax.swing.JTextField();
        jTextFieldDialogPatientFirstname = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaDialogPatientComment = new javax.swing.JTextArea();
        jButtonDialogPatientLoadExisting = new javax.swing.JButton();
        jButtonPatientOk = new javax.swing.JButton();
        jButtonPatientCancel = new javax.swing.JButton();
        buttonGroupDialogPatientSex = new javax.swing.ButtonGroup();
        jDialogAbout = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jDialogDisplayerOption = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldDialogSetupDisplayerPatientDistance = new javax.swing.JTextField();
        jLabelDialogSetupDisplayerPatientDistance = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jSliderDialogSetupDisplayerCalibrationSlider = new javax.swing.JSlider();
        jLabelDialogSetupDisplayerCalibrationValue = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldDialogSetupDisplayerHorizRes = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldDialogSetupDisplayerVertRes = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldDialogSetupDisplayerDiagonalLength = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jButtonDialogSetupDisplayerCancel = new javax.swing.JButton();
        jButtonDialogSetupDisplayerOk = new javax.swing.JButton();
        jButtonDialogSetupDisplayerApply = new javax.swing.JButton();
        jPanelPatientData = new javax.swing.JPanel();
        jLabelPatientSex = new javax.swing.JLabel();
        jLabelPatientName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabelPatientAge = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaPatientComment = new javax.swing.JTextArea();
        jPanelAcuityTest = new javax.swing.JPanel();
        jButtonStartAcuityTest = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabelTreadmillSpeed = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabelAcuityTestDateTime = new javax.swing.JLabel();
        jPanelResultsValidation = new javax.swing.JPanel();
        jPanelDisplayedCharacter = new javax.swing.JPanel();
        jButtonOptotypeC = new javax.swing.JButton();
        jButtonOptotypeD = new javax.swing.JButton();
        jButtonOptotypeH = new javax.swing.JButton();
        jButtonOptotypeK = new javax.swing.JButton();
        jButtonOptotypeN = new javax.swing.JButton();
        jButtonOptotypeO = new javax.swing.JButton();
        jButtonOptotypeR = new javax.swing.JButton();
        jButtonOptotypeS = new javax.swing.JButton();
        jButtonOptotypeV = new javax.swing.JButton();
        jButtonOptotypeZ = new javax.swing.JButton();
        jButtonDontKnow = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabelCharacter = new javax.swing.JLabel();
        jButtonDisplayNextOptotype = new javax.swing.JButton();
        jPanelLog = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaLog = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNewExperiment = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemQuit = new javax.swing.JMenuItem();
        jMenuOptions = new javax.swing.JMenu();
        jMenuItemSetupDisplayer = new javax.swing.JMenuItem();
        jCheckBoxMenuItemPauseBetween = new javax.swing.JCheckBoxMenuItem();
        jMenuView = new javax.swing.JMenu();
        jMenuViewDisplayer = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuAbout = new javax.swing.JMenuItem();

        jDialogPatientData.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialogPatientData.setTitle("Enter patient data");
        jDialogPatientData.setModal(true);
        jDialogPatientData.getAccessibleContext().setAccessibleParent(this);
        jPanelDialogPatientData.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient data"));
        jLabelDialogPatientSex.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDialogPatientSex.setText("Sex: ");

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
        jLabelDialogPatientAge.setText("Age: ");

        jTextFieldDialogPatientAge.setText("20");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("LastName: ");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("FirstName: ");

        jLabel12.setText("Comment:");

        jTextAreaDialogPatientComment.setColumns(15);
        jTextAreaDialogPatientComment.setFont(new java.awt.Font("Tahoma", 0, 12));
        jTextAreaDialogPatientComment.setRows(3);
        jScrollPane3.setViewportView(jTextAreaDialogPatientComment);

        jButtonDialogPatientLoadExisting.setText("...");
        jButtonDialogPatientLoadExisting.setToolTipText("Load existing patient data");
        jButtonDialogPatientLoadExisting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDialogPatientLoadExistingActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelDialogPatientDataLayout = new org.jdesktop.layout.GroupLayout(jPanelDialogPatientData);
        jPanelDialogPatientData.setLayout(jPanelDialogPatientDataLayout);
        jPanelDialogPatientDataLayout.setHorizontalGroup(
            jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel12)
                    .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(jLabelDialogPatientSex, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
                    .add(jLabelDialogPatientAge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                        .add(jRadioButtonDialogPatientSexM)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButtonDialogPatientSexF))
                    .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                            .add(jTextFieldDialogPatientAge, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)))
                    .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                        .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jTextFieldDialogPatientFirstname)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jTextFieldDialogPatientLastname, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonDialogPatientLoadExisting, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelDialogPatientDataLayout.setVerticalGroup(
            jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextFieldDialogPatientLastname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonDialogPatientLoadExisting))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(jTextFieldDialogPatientFirstname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButtonDialogPatientSexM)
                    .add(jRadioButtonDialogPatientSexF)
                    .add(jLabelDialogPatientSex))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelDialogPatientAge)
                    .add(jTextFieldDialogPatientAge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                        .add(jLabel12)
                        .addContainerGap(44, Short.MAX_VALUE))
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)))
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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap(191, Short.MAX_VALUE)
                .add(jButtonPatientOk, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonPatientCancel)
                .addContainerGap())
            .add(jDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDialogPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        jDialogAbout.getAccessibleContext().setAccessibleParent(this);
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
        jDialogDisplayerOption.setTitle("Displayer options");
        jDialogDisplayerOption.getAccessibleContext().setAccessibleParent(this);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Setup Displayer"));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Patient distance:");

        jTextFieldDialogSetupDisplayerPatientDistance.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDialogSetupDisplayerPatientDistance.setText("6.00");

        jLabelDialogSetupDisplayerPatientDistance.setText("meters");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Calibration:");

        jSliderDialogSetupDisplayerCalibrationSlider.setMajorTickSpacing(25);
        jSliderDialogSetupDisplayerCalibrationSlider.setMaximum(200);
        jSliderDialogSetupDisplayerCalibrationSlider.setMinorTickSpacing(10);
        jSliderDialogSetupDisplayerCalibrationSlider.setValue(100);
        jSliderDialogSetupDisplayerCalibrationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderDialogSetupDisplayerCalibrationSliderStateChanged(evt);
            }
        });

        jLabelDialogSetupDisplayerCalibrationValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDialogSetupDisplayerCalibrationValue.setText("100");

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Screen size (width * height):");

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Diagonal length:");

        jTextFieldDialogSetupDisplayerHorizRes.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDialogSetupDisplayerHorizRes.setText("1200");

        jLabel16.setText("px *");

        jTextFieldDialogSetupDisplayerVertRes.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDialogSetupDisplayerVertRes.setText("800");

        jLabel17.setText("px");

        jTextFieldDialogSetupDisplayerDiagonalLength.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDialogSetupDisplayerDiagonalLength.setText("12.1");

        jLabel18.setText("inches");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                    .add(jLabel19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                    .add(jLabel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jSliderDialogSetupDisplayerCalibrationSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabelDialogSetupDisplayerCalibrationValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jTextFieldDialogSetupDisplayerPatientDistance)
                            .add(jTextFieldDialogSetupDisplayerHorizRes))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel16)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextFieldDialogSetupDisplayerVertRes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(4, 4, 4)
                                .add(jLabel17))
                            .add(jLabelDialogSetupDisplayerPatientDistance)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jTextFieldDialogSetupDisplayerDiagonalLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel18)))
                .addContainerGap())
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .add(258, 258, 258))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(jTextFieldDialogSetupDisplayerPatientDistance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelDialogSetupDisplayerPatientDistance))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(jTextFieldDialogSetupDisplayerHorizRes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel16)
                    .add(jTextFieldDialogSetupDisplayerVertRes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel17))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(jTextFieldDialogSetupDisplayerDiagonalLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabelDialogSetupDisplayerCalibrationValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jSliderDialogSetupDisplayerCalibrationSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jLabel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addContainerGap())
        );

        jButtonDialogSetupDisplayerCancel.setText("Cancel");
        jButtonDialogSetupDisplayerCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDialogSetupDisplayerCancelActionPerformed(evt);
            }
        });

        jButtonDialogSetupDisplayerOk.setText("OK");
        jButtonDialogSetupDisplayerOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDialogSetupDisplayerOkActionPerformed(evt);
            }
        });

        jButtonDialogSetupDisplayerApply.setText("Apply");
        jButtonDialogSetupDisplayerApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDialogSetupDisplayerApplyActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jDialogDisplayerOptionLayout = new org.jdesktop.layout.GroupLayout(jDialogDisplayerOption.getContentPane());
        jDialogDisplayerOption.getContentPane().setLayout(jDialogDisplayerOptionLayout);
        jDialogDisplayerOptionLayout.setHorizontalGroup(
            jDialogDisplayerOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogDisplayerOptionLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogDisplayerOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogDisplayerOptionLayout.createSequentialGroup()
                        .add(jButtonDialogSetupDisplayerApply)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonDialogSetupDisplayerOk, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonDialogSetupDisplayerCancel)))
                .addContainerGap())
        );
        jDialogDisplayerOptionLayout.setVerticalGroup(
            jDialogDisplayerOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogDisplayerOptionLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialogDisplayerOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonDialogSetupDisplayerCancel)
                    .add(jButtonDialogSetupDisplayerOk)
                    .add(jButtonDialogSetupDisplayerApply))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dynamic Visual Acuity Checker");
        setResizable(false);
        jPanelPatientData.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient data"));
        jLabelPatientSex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel6.setForeground(new java.awt.Color(51, 94, 168));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Name:");

        jLabel7.setForeground(new java.awt.Color(51, 94, 168));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Sex:");

        jLabel9.setForeground(new java.awt.Color(51, 94, 168));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Age:");

        jLabel11.setForeground(new java.awt.Color(51, 94, 168));
        jLabel11.setText("Comment:");

        jTextAreaPatientComment.setColumns(15);
        jTextAreaPatientComment.setEditable(false);
        jTextAreaPatientComment.setFont(new java.awt.Font("Tahoma", 0, 12));
        jTextAreaPatientComment.setRows(3);
        jTextAreaPatientComment.setTabSize(4);
        jScrollPane2.setViewportView(jTextAreaPatientComment);

        org.jdesktop.layout.GroupLayout jPanelPatientDataLayout = new org.jdesktop.layout.GroupLayout(jPanelPatientData);
        jPanelPatientData.setLayout(jPanelPatientDataLayout);
        jPanelPatientDataLayout.setHorizontalGroup(
            jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .add(jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelPatientDataLayout.createSequentialGroup()
                        .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabelPatientAge)
                            .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabelPatientSex)
                                .add(jLabelPatientName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)))
                        .add(85, 85, 85))
                    .add(jPanelPatientDataLayout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanelPatientDataLayout.setVerticalGroup(
            jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelPatientDataLayout.createSequentialGroup()
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPatientName)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPatientSex)
                    .add(jLabel7))
                .add(5, 5, 5)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPatientAge)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel11)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel2.setForeground(new java.awt.Color(51, 94, 168));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Treadmill speed:");

        jLabel8.setText("km/h");

        jLabelTreadmillSpeed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTreadmillSpeed.setText("0");

        jLabel13.setForeground(new java.awt.Color(51, 94, 168));
        jLabel13.setText("Date / Time:");

        jLabelAcuityTestDateTime.setText(" ");

        org.jdesktop.layout.GroupLayout jPanelAcuityTestLayout = new org.jdesktop.layout.GroupLayout(jPanelAcuityTest);
        jPanelAcuityTest.setLayout(jPanelAcuityTestLayout);
        jPanelAcuityTestLayout.setHorizontalGroup(
            jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelAcuityTestLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonStartAcuityTest, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                    .add(jPanelAcuityTestLayout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabelTreadmillSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                    .add(jPanelAcuityTestLayout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabelAcuityTestDateTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelAcuityTestLayout.setVerticalGroup(
            jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelAcuityTestLayout.createSequentialGroup()
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(jLabelAcuityTestDateTime))
                .add(6, 6, 6)
                .add(jButtonStartAcuityTest)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelTreadmillSpeed)
                    .add(jLabel8)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanelResultsValidation.setBorder(javax.swing.BorderFactory.createTitledBorder("Operator real-time results validation"));
        jPanelDisplayedCharacter.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient answer"));
        jButtonOptotypeC.setText("C");
        jButtonOptotypeC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeCActionPerformed(evt);
            }
        });

        jButtonOptotypeD.setText("D");
        jButtonOptotypeD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeDActionPerformed(evt);
            }
        });

        jButtonOptotypeH.setText("H");
        jButtonOptotypeH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeHActionPerformed(evt);
            }
        });

        jButtonOptotypeK.setText("K");
        jButtonOptotypeK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeKActionPerformed(evt);
            }
        });

        jButtonOptotypeN.setText("N");
        jButtonOptotypeN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeNActionPerformed(evt);
            }
        });

        jButtonOptotypeO.setText("O");
        jButtonOptotypeO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeOActionPerformed(evt);
            }
        });

        jButtonOptotypeR.setText("R");
        jButtonOptotypeR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeRActionPerformed(evt);
            }
        });

        jButtonOptotypeS.setText("S");
        jButtonOptotypeS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeSActionPerformed(evt);
            }
        });

        jButtonOptotypeV.setText("V");
        jButtonOptotypeV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeVActionPerformed(evt);
            }
        });

        jButtonOptotypeZ.setText("Z");
        jButtonOptotypeZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeZActionPerformed(evt);
            }
        });

        jButtonDontKnow.setText("Don't know !");
        jButtonDontKnow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDontKnowActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelDisplayedCharacterLayout = new org.jdesktop.layout.GroupLayout(jPanelDisplayedCharacter);
        jPanelDisplayedCharacter.setLayout(jPanelDisplayedCharacterLayout);
        jPanelDisplayedCharacterLayout.setHorizontalGroup(
            jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelDisplayedCharacterLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonOptotypeC)
                    .add(jButtonOptotypeO))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDisplayedCharacterLayout.createSequentialGroup()
                        .add(jButtonOptotypeD)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeH)
                        .add(6, 6, 6)
                        .add(jButtonOptotypeK)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeN))
                    .add(jPanelDisplayedCharacterLayout.createSequentialGroup()
                        .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jButtonDontKnow, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelDisplayedCharacterLayout.createSequentialGroup()
                                .add(jButtonOptotypeR)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonOptotypeS)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonOptotypeV)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeZ)))
                .addContainerGap())
        );
        jPanelDisplayedCharacterLayout.setVerticalGroup(
            jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDisplayedCharacterLayout.createSequentialGroup()
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonOptotypeC)
                    .add(jButtonOptotypeD)
                    .add(jButtonOptotypeH)
                    .add(jButtonOptotypeK)
                    .add(jButtonOptotypeN))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonOptotypeO)
                    .add(jButtonOptotypeR)
                    .add(jButtonOptotypeS)
                    .add(jButtonOptotypeV)
                    .add(jButtonOptotypeZ))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButtonDontKnow))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel3.setForeground(new java.awt.Color(51, 94, 168));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Character:");

        jLabelCharacter.setFont(new java.awt.Font("Tahoma", 0, 18));
        jLabelCharacter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCharacter.setText(" ");

        jButtonDisplayNextOptotype.setText("Next");
        jButtonDisplayNextOptotype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplayNextOptotypeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelResultsValidationLayout = new org.jdesktop.layout.GroupLayout(jPanelResultsValidation);
        jPanelResultsValidation.setLayout(jPanelResultsValidationLayout);
        jPanelResultsValidationLayout.setHorizontalGroup(
            jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelResultsValidationLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelResultsValidationLayout.createSequentialGroup()
                        .add(jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanelDisplayedCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanelResultsValidationLayout.createSequentialGroup()
                                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelResultsValidationLayout.createSequentialGroup()
                        .add(jButtonDisplayNextOptotype)
                        .add(110, 110, 110))))
        );
        jPanelResultsValidationLayout.setVerticalGroup(
            jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelResultsValidationLayout.createSequentialGroup()
                .add(jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabelCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDisplayedCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonDisplayNextOptotype)
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
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLogLayout.setVerticalGroup(
            jPanelLogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLogLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
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
        jMenuItemSetupDisplayer.setAction(new SetupDisplayerAction("Setup Displayer", "setupdis24", "Setup Displayer"));
        jMenuOptions.add(jMenuItemSetupDisplayer);

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
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanelAcuityTest, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanelPatientData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelResultsValidation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanelLog, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(jPanelPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelAcuityTest, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanelResultsValidation, 0, 239, Short.MAX_VALUE))
                .add(9, 9, 9)
                .add(jPanelLog, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDialogPatientLoadExistingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDialogPatientLoadExistingActionPerformed
        
        if (patients.size() <= 0) { 
            DvaLogger.info(MainFrame.class, "There is no existing patient data!"); 
            return;
        } 
        
        //Display selection dialog
        String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select a patient name from the list:",
                    "Load patient data",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    patients.keySet().toArray(),
                    patients.keySet().toArray()[0]);
        //If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            
            DvaLogger.debug(MainFrame.class, "Load patient:" + s); 
            //load patient
            patient = patients.get(s); 
            
            //updatge GUI
            updateJLabelPatientData(patient); 
            
            //set patient directory
            AcuityTestManager.setPatientDirectory( patient.getPatientdir(outputdir) ); 

            //enable StartAcuityTest button
            jButtonStartAcuityTest.setEnabled(true); 
            
            //close patient dialog
            GUIUtils.showDialog(this.jDialogPatientData, false, evt); 
            return;
        }
    }//GEN-LAST:event_jButtonDialogPatientLoadExistingActionPerformed

    private void jSliderDialogSetupDisplayerCalibrationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderDialogSetupDisplayerCalibrationSliderStateChanged
        JSlider source = (JSlider)evt.getSource();
        
        //update JLabel 
        jLabelDialogSetupDisplayerCalibrationValue.setText( String.valueOf( ((float)source.getValue()) / 100 ) ); 
        
        double value = ((float)source.getValue()) / 100; 
        
        Displayer.getInstance().getDisplayModel().setScaleCorrectionFactor(value); 
        
        DvaLogger.debug(MainFrame.class, "slider value:" + value );
        
//        if (!source.getValueIsAdjusting()) {
//            float value = (float)source.getValue();
//            DvaLogger.debug(MainFrame.class, "Calibration value:" + value);
//            
//            //set text into calibration value label
//            this.jLabelDialogSetupDisplayerCalibrationValue.setText( String.format("%.2f", value ) ); 
//        }
    }//GEN-LAST:event_jSliderDialogSetupDisplayerCalibrationSliderStateChanged

    private void jButtonDontKnowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDontKnowActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_DONTKNOW);
    }//GEN-LAST:event_jButtonDontKnowActionPerformed

    private void jButtonDialogSetupDisplayerApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDialogSetupDisplayerApplyActionPerformed
        //get options
        String horizontalRes = this.jTextFieldDialogSetupDisplayerHorizRes.getText(); 
        String verticalRes = this.jTextFieldDialogSetupDisplayerVertRes.getText(); 
        String diagonalLength = this.jTextFieldDialogSetupDisplayerDiagonalLength.getText(); 
        String patientDistance = this.jTextFieldDialogSetupDisplayerPatientDistance.getText(); 
        String scaleCorrectionFactor = this.jLabelDialogSetupDisplayerCalibrationValue.getText(); 
        
        ScreenMapper.getInstance().setDisplayerOptions(Integer.valueOf(horizontalRes), 
                Integer.valueOf(verticalRes), 
                Float.valueOf(diagonalLength),
                Float.valueOf(patientDistance), Float.valueOf(scaleCorrectionFactor)); 
        //update model
//        ScreenMapper.getInstance().setDisplayerOptions(Integer.valueOf(horizontalRes), 
//                Integer.valueOf(verticalRes), 
//                Float.valueOf(diagonalLength), 
//                Float.valueOf(patientDistance) );
        
    }//GEN-LAST:event_jButtonDialogSetupDisplayerApplyActionPerformed

    private void jButtonDialogSetupDisplayerCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDialogSetupDisplayerCancelActionPerformed
        GUIUtils.showDialog(jDialogDisplayerOption, false, evt); 
    }//GEN-LAST:event_jButtonDialogSetupDisplayerCancelActionPerformed

    private void jButtonDialogSetupDisplayerOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDialogSetupDisplayerOkActionPerformed
        
//        //get options
//        String horizontalRes = this.jTextFieldDialogSetupDisplayerHorizRes.getText(); 
//        String verticalRes = this.jTextFieldDialogSetupDisplayerVertRes.getText(); 
//        String diagonalLength = this.jTextFieldDialogSetupDisplayerDiagLength.getText(); 
//        String patientDistance = this.jTextFieldDialogSetupDisplayerPatientDistance.getText(); 
//        
//        //update model
//        ScreenMapper.getInstance().setDisplayerOptions(Integer.valueOf(horizontalRes), 
//                Integer.valueOf(verticalRes), 
//                Float.valueOf(diagonalLength), 
//                Float.valueOf(patientDistance) );
        
        String horizontalRes = this.jTextFieldDialogSetupDisplayerHorizRes.getText(); 
        String verticalRes = this.jTextFieldDialogSetupDisplayerVertRes.getText(); 
        String diagonalLength = this.jTextFieldDialogSetupDisplayerDiagonalLength.getText(); 
        String patientDistance = this.jTextFieldDialogSetupDisplayerPatientDistance.getText(); 
        String scaleCorrectionFactor = this.jLabelDialogSetupDisplayerCalibrationValue.getText(); 
        
        ScreenMapper.getInstance().setDisplayerOptions(Integer.valueOf(horizontalRes), 
                Integer.valueOf(verticalRes), 
                Float.valueOf(diagonalLength),
                Float.valueOf(patientDistance), Float.valueOf(scaleCorrectionFactor)); 
        
        GUIUtils.showDialog(jDialogDisplayerOption, false, evt); 
    }//GEN-LAST:event_jButtonDialogSetupDisplayerOkActionPerformed

    private void jButtonOptotypeZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeZActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_Z);
    }//GEN-LAST:event_jButtonOptotypeZActionPerformed

    private void jButtonOptotypeVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeVActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_V);
    }//GEN-LAST:event_jButtonOptotypeVActionPerformed

    private void jButtonOptotypeSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeSActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_S);
    }//GEN-LAST:event_jButtonOptotypeSActionPerformed

    private void jButtonOptotypeRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeRActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_R);
    }//GEN-LAST:event_jButtonOptotypeRActionPerformed

    private void jButtonOptotypeOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeOActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_O);
    }//GEN-LAST:event_jButtonOptotypeOActionPerformed

    private void jButtonOptotypeNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeNActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_N);
    }//GEN-LAST:event_jButtonOptotypeNActionPerformed

    private void jButtonOptotypeKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeKActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_K);
    }//GEN-LAST:event_jButtonOptotypeKActionPerformed

    private void jButtonOptotypeHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeHActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_H);
    }//GEN-LAST:event_jButtonOptotypeHActionPerformed

    private void jButtonOptotypeDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeDActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_D);

    }//GEN-LAST:event_jButtonOptotypeDActionPerformed

    private void jButtonOptotypeCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeCActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_C);
    }//GEN-LAST:event_jButtonOptotypeCActionPerformed

    private void jButtonDisplayNextOptotypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplayNextOptotypeActionPerformed
        displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.NEXT_OPTOTYPE);
    }//GEN-LAST:event_jButtonDisplayNextOptotypeActionPerformed

    private void jCheckBoxMenuItemPauseBetweenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemPauseBetweenActionPerformed
        this.displayer.getDisplayModel().setPauseBetween(jCheckBoxMenuItemPauseBetween.isSelected()); 
    }//GEN-LAST:event_jCheckBoxMenuItemPauseBetweenActionPerformed

    private void jButtonStartAcuityTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartAcuityTestActionPerformed
        

        //get treadmill speed from user input
        String treadmillSpeed = (String)JOptionPane.showInputDialog(
                    this,
                    "Enter treadmill speed:",
                    "Treadmill setup",
                    JOptionPane.PLAIN_MESSAGE);

        //If a string was returned, say so.
        if ((treadmillSpeed != null) && (treadmillSpeed.length() > 0)) {

            //setup acuitytest
            this.displayer.getDisplayModel().setupAcuityTest(patient.getPatientdir()); 
            
            //update mainframe GUI
            this.jLabelTreadmillSpeed.setText( treadmillSpeed );

            AcuityTestManager.getCurrentAcuityTest().setTreadmillSpeed(Float.valueOf(treadmillSpeed)); 
        
            //check if displayer is visible
            if (!displayer.isVisible()) displayer.setVisible(true);
            
            //enable the next button
            this.jButtonDisplayNextOptotype.setEnabled(true); 
            
            //set no character
            jLabelCharacter.setText(" "); 

            //disable start button
            this.jButtonStartAcuityTest.setEnabled(false); 

            //set date and time
            this.jLabelAcuityTestDateTime.setText(AcuityTestManager.getCurrentAcuityTest().getStartDateAsString());
        }
        
    }//GEN-LAST:event_jButtonStartAcuityTestActionPerformed

    private void jMenuViewDisplayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuViewDisplayerActionPerformed
        displayer.setVisible(!displayer.isVisible());
    }//GEN-LAST:event_jMenuViewDisplayerActionPerformed

    private void jRadioButtonDialogPatientSexMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDialogPatientSexMActionPerformed
// TODO add your handling code here:
        
    }//GEN-LAST:event_jRadioButtonDialogPatientSexMActionPerformed

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
        try {
            
            //get new patient data
            String firstname = StringUtils.capitalize( this.jTextFieldDialogPatientFirstname.getText().trim() ); 
            String lastname = StringUtils.capitalize( this.jTextFieldDialogPatientLastname.getText().trim() ); 
            String comment = this.jTextAreaDialogPatientComment.getText().trim(); 
            String sex = GUIUtils.getSelection(buttonGroupDialogPatientSex).getText().trim();
            String age = this.jTextFieldDialogPatientAge.getText().trim(); 
            
            if (lastname.equals("")){
                GUIUtils.showWarning(this, "Patient creation problem", "Lastname field is empty !"); 
                return; 
            } else if ( firstname.equals("") ){
                GUIUtils.showWarning(this, "Patient creation problem", "Firstname field is empty !"); 
                return; 
            } else if (age.equals("")){
                GUIUtils.showWarning(this, "Patient creation problem", "Age field is empty !"); 
                return; 
            } else if (!NumberUtils.isNumber(age)){
                GUIUtils.showWarning(this, "Patient creation problem", "Age field is not a valid number !"); 
                return; 
            }
            
            //generate a patient id number
            //long id = System.currentTimeMillis(); 
            
            //update patient
            //getCurrentPatient().setId(id); 
            getCurrentPatient().setFirstname(firstname); 
            getCurrentPatient().setLastname(lastname); 
            getCurrentPatient().setComment(comment); 
            getCurrentPatient().setSex(sex); 
            getCurrentPatient().setAge(age); 

             //check if patient exists - if yes, ask for operator confirmation
            if (patient.isPatientExist()){
                DvaLogger.debug(MainFrame.class, "Patient '" + lastname + "-" + firstname + "' exists !");
                if ( GUIUtils.askOperator(this, "Patient creation", "Existing patient - Data will be updated\n Do you want to continue ?") == JOptionPane.NO_OPTION)
                    return; 
            }

            //write patient data to file
            patient.toFile();
            
            //add ptient to the list
            patients.put(getCurrentPatient().getDirectoryName(), getCurrentPatient()); 
            
            AcuityTestManager.setPatientDirectory( patient.getPatientdir() ); 
            
            //int speeds[] = AcuityTestManager.acceptProposedSpeedsSet(); 
            //jLabelPatientSpeedsSetValue.setText( AcuityTestManager.speedsSetToString(speeds) ); 

            //enable StartAcuityTest button
            jButtonStartAcuityTest.setEnabled(true); 

            //update GUI
            updateJLabelPatientData(getCurrentPatient());
            
            //close dialog
            GUIUtils.showDialog(jDialogPatientData, false, evt);
            
        } catch (PatientFileCreationException pfcex) {
            
            DvaLogger.error(MainFrame.class, pfcex); 
            //close dialog
            GUIUtils.showDialog(jDialogPatientData, false, evt);
            
        }
    }//GEN-LAST:event_jButtonPatientOkActionPerformed
    
    public static File getOutputDirectory(){
        return outputdir; 
    }
    
    public void resetPatientDataDialog(){
        this.jTextFieldDialogPatientFirstname.setText(""); 
        this.jTextFieldDialogPatientLastname.setText(""); 
        this.jTextAreaDialogPatientComment.setText(""); 
        this.jTextFieldDialogPatientAge.setText(""); 
    }
    
    void loadPatientsList() {
        
        //check if it's a directory and if we can read it
        if (outputdir.canRead() && outputdir.isDirectory()){

                File patientdirs[] = outputdir.listFiles();

                for (File patientdir : patientdirs){

                        try {
                            DvaLogger.debug(MainFrame.class, "patient:"+patientdir.getPath());

                            File patientfile = new File(patientdir + "/patient.xml"); 

                            //load patient from file
                            FileReader fr = new FileReader(patientfile);

                            //read patient xml file
                            Patient p = PatientReader.process( fr ); 

                            //close reader
                            fr.close();

                            //add patient to list
                            patients.put( p.getDirectoryName(), p ); 
                        }catch (Exception ex){
                            DvaLogger.error(MainFrame.class, ex); 

                        }
                }
        }
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
    private Patient patient = null;  
    
    //list of Patient loaded from the user directory
    HashMap<String, Patient> patients = new HashMap<String, Patient>();
    
    //GUI
    private Displayer displayer = null; 
    private boolean callibrating = false;
    
    //resources
    private dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N;  
    private static File outputdir = null; 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupDialogPatientSex;
    private javax.swing.JButton jButtonDialogPatientLoadExisting;
    private javax.swing.JButton jButtonDialogSetupDisplayerApply;
    private javax.swing.JButton jButtonDialogSetupDisplayerCancel;
    private javax.swing.JButton jButtonDialogSetupDisplayerOk;
    private javax.swing.JButton jButtonDisplayNextOptotype;
    private javax.swing.JButton jButtonDontKnow;
    private javax.swing.JButton jButtonOptotypeC;
    private javax.swing.JButton jButtonOptotypeD;
    private javax.swing.JButton jButtonOptotypeH;
    private javax.swing.JButton jButtonOptotypeK;
    private javax.swing.JButton jButtonOptotypeN;
    private javax.swing.JButton jButtonOptotypeO;
    private javax.swing.JButton jButtonOptotypeR;
    private javax.swing.JButton jButtonOptotypeS;
    private javax.swing.JButton jButtonOptotypeV;
    private javax.swing.JButton jButtonOptotypeZ;
    private javax.swing.JButton jButtonPatientCancel;
    private javax.swing.JButton jButtonPatientOk;
    private javax.swing.JButton jButtonStartAcuityTest;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemPauseBetween;
    private javax.swing.JDialog jDialogAbout;
    private javax.swing.JDialog jDialogDisplayerOption;
    private javax.swing.JDialog jDialogPatientData;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAcuityTestDateTime;
    private javax.swing.JLabel jLabelCharacter;
    private javax.swing.JLabel jLabelDialogPatientAge;
    private javax.swing.JLabel jLabelDialogPatientSex;
    private javax.swing.JLabel jLabelDialogSetupDisplayerCalibrationValue;
    private javax.swing.JLabel jLabelDialogSetupDisplayerPatientDistance;
    private javax.swing.JLabel jLabelPatientAge;
    private javax.swing.JLabel jLabelPatientName;
    private javax.swing.JLabel jLabelPatientSex;
    private javax.swing.JLabel jLabelTreadmillSpeed;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemNewExperiment;
    private javax.swing.JMenuItem jMenuItemQuit;
    private javax.swing.JMenuItem jMenuItemSetupDisplayer;
    private javax.swing.JMenu jMenuOptions;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JMenuItem jMenuViewDisplayer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelAcuityTest;
    private javax.swing.JPanel jPanelDialogPatientData;
    private javax.swing.JPanel jPanelDisplayedCharacter;
    private javax.swing.JPanel jPanelLog;
    private javax.swing.JPanel jPanelPatientData;
    private javax.swing.JPanel jPanelResultsValidation;
    private javax.swing.JRadioButton jRadioButtonDialogPatientSexF;
    private javax.swing.JRadioButton jRadioButtonDialogPatientSexM;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSlider jSliderDialogSetupDisplayerCalibrationSlider;
    private javax.swing.JTextArea jTextAreaDialogPatientComment;
    private javax.swing.JTextArea jTextAreaLog;
    private javax.swing.JTextArea jTextAreaPatientComment;
    private javax.swing.JTextField jTextFieldDialogPatientAge;
    private javax.swing.JTextField jTextFieldDialogPatientFirstname;
    private javax.swing.JTextField jTextFieldDialogPatientLastname;
    private javax.swing.JTextField jTextFieldDialogSetupDisplayerDiagonalLength;
    private javax.swing.JTextField jTextFieldDialogSetupDisplayerHorizRes;
    private javax.swing.JTextField jTextFieldDialogSetupDisplayerPatientDistance;
    private javax.swing.JTextField jTextFieldDialogSetupDisplayerVertRes;
    // End of variables declaration//GEN-END:variables
    
}
