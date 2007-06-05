/*
 * Patient.java
 *
 * Created on May 7, 2007, 10:34 AM
 *
 */

package dva;

import dva.util.DvaLogger;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author J-Chris
 */
public class Patient {
    
    private String id = ""; 
    private String firstname = ""; 
    private String lastname = ""; 
    private String comment = ""; 
    private String age = "20";
    private String sex = "M";
    private File outputdir = null; 
    private File patientdir = null; 
    
    /** Creates a new instance of Patient */
    public Patient(File outputdir) {
        this.outputdir = outputdir; 
    }
    
    public String toString(){
        return lastname + " " + firstname + ", " + sex + ", " + age;
    }
    
    public String getAge(){
        return age;
    }
    
    public void setAge(String age){
        this.age = age; 
    }
    
    public String getSex(){
        return sex;
    }
    
    public void setSex(String sex){
        this.sex = sex; 
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }
    
    public void setId(long id){
        this.id = String.valueOf( id ); 
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void generateAndSetId(){
        this.id = String.valueOf( System.currentTimeMillis() ); 
    }
    
    public void createDirectory(){
        patientdir = new File(outputdir + ("/" + lastname + "_" + firstname));
        
        //create directory
        getPatientdir().mkdir(); 
    }
    
    public void toFile() throws PatientFileCreationException {
        
        patientdir = new File(outputdir + ("/" + lastname + "_" + firstname) );
        
        DvaLogger.debug(Patient.class, "patientdir:" + getPatientdir().getAbsolutePath() ); 
        
        //create directory
        getPatientdir().mkdir(); 
        
        File patientfile = null; 
        try {
            patientfile = new File(getPatientdir() + "/patient.xml");
            FileUtils.writeStringToFile(patientfile, toXml()); 
            
        } catch (IOException ioex){
            throw new PatientFileCreationException(patientfile, ioex);
        }
    }
    
    public String toXml(){
        
        StringBuffer sb = new StringBuffer("<patient id=\"");
        sb.append(id);
        sb.append("\">");
            sb.append("<firstname>");
            sb.append(this.firstname);
            sb.append("</firstname><lastname>");
            sb.append(this.lastname);
            sb.append("</lastname><age>");
            sb.append(this.age);
            sb.append("</age><sex>");
            sb.append(this.sex);
            sb.append("</sex><comment>");
            sb.append(this.comment);
            sb.append("</comment>");
        sb.append("</patient>");
        
        return sb.toString(); 
    }
    
    public boolean isPatientExist(){
        File tmp = new File(outputdir + "/" + lastname + "_" + firstname);
        return tmp.exists(); 
    }

    public File getPatientdir() {
        return patientdir;
    }
    
}
