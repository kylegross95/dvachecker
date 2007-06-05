/*
 * PatientReader.java
 *
 * Created on June 5, 2007, 7:36 AM
 *
 */

package dva.xml;

import dva.DvaCheckerException;
import dva.Patient;
import dva.util.DvaLogger;
import java.io.CharArrayWriter;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

/**
 *
 * @author J-Chris
 */
public class PatientReader extends DefaultHandler {

    String text = "";
    
    private Patient patient = new Patient(); 
    
    /**
     * 
     * @param connection
     * @param userBean
     * @return
     */
    final private static PatientReader getInstance() {

        PatientReader instance = new PatientReader();

        return instance;
    }

    /**
     * 
     *
     */
    private PatientReader() {
    }

    /**
     * 
     * @param connection
     * @param userBean
     * @param file
     * @throws DvaCheckerException
     */
    public static Patient process(Reader reader) throws DvaCheckerException {

        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            ParserAdapter pa = new ParserAdapter(sp.getParser());

            PatientReader patientReader = PatientReader.getInstance();

            pa.setContentHandler(patientReader);

            pa.parse(new InputSource(reader));

            return patientReader.patient; 

        } catch (Exception e) {
            throw new DvaCheckerException("Failed to get patient data", e);
        }

    }

    /**
     * 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespace, String localName, String qName, Attributes atts) {
        text = ""; 
    }
    
    public String getText(){
        return text.trim();
    }
    
    public void characters(char[] ch, int start, int length)
    {
        text = new String(ch, start, length);
        //DvaLogger.debug(PatientReader.class, "value:"+text );
    }

    /**
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String namespace, String localName, String qName) {
        //DvaLogger.debug(PatientReader.class, "startElement/localName:"+localName + ", text:" + getText());
        
        if (localName.equals("lastname")) {
            
            patient.setLastname( getText() );
            
        } else if (localName.equals("firstname")) {
            patient.setFirstname( getText() );
            
        } else if (localName.equals("sex")) {
            patient.setSex( getText() );
            
        } else if (localName.equals("age")) {
            patient.setAge( getText() );
            
        } else if (localName.equals("comment")) {
            patient.setComment( getText() );
        }
    }
}

