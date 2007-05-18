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
    
    /** Creates a new instance of Patient */
    public Patient() {
    }
    
    public String toString(){
        return sex+"-"+age;
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

    private String age = "20";
    private String sex = "M";
    
}
