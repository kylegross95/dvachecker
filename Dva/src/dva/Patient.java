/*
 * Patient.java
 *
 * Created on May 7, 2007, 10:34 AM
 *
 */

package dva;

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
    
    /** Creates a new instance of Patient */
    public Patient() {
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

    public void setId(String id) {
        this.id = id;
    }
    
    public void generateAndSetId(){
        this.id = String.valueOf( System.currentTimeMillis() ); 
    }
    
}
