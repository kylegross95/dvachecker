/*
 * AcuityTestFileFilter.java
 *
 * Created on June 10, 2007, 8:33 AM
 *
 */

package dva.acuitytest;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author J-Chris
 */
public class AcuityTestFileFilter extends FileFilter {
    
    String pattern = "_acuitytest-data.xml"; 
    final static String  description = "Acuity test data (*.xml)";

   public AcuityTestFileFilter(){
   }

   public boolean accept(File f) {

     if (f.isDirectory()) {
         return true;
     }
     
     String s = f.getName();
     return s.endsWith(pattern); 
   }

   // filter description
   public String getDescription() {
       return description;
   }
}
