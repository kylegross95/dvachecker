/*
 * AcuityTestReader.java
 *
 * Created on June 7, 2007, 10:18 AM
 *
 */

package dva.xml;

import dva.DvaCheckerException;
import dva.acuitytest.AcuityTest;
import dva.acuitytest.StaticAcuityTest;
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
public class AcuityTestReader extends DefaultHandler {
    
    String text = "";
    
    private StaticAcuityTest acuityTest = new StaticAcuityTest(); 
    
    /**
     * 
     * @param connection
     * @param userBean
     * @return
     */
    final private static AcuityTestReader getInstance() {

        AcuityTestReader instance = new AcuityTestReader();

        return instance;
    }

    /**
     * 
     *
     */
    private AcuityTestReader() {
    }

    /**
     * 
     * @param connection
     * @param userBean
     * @param file
     * @throws DvaCheckerException
     */
    public static AcuityTest process(Reader reader) throws DvaCheckerException {

        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            ParserAdapter pa = new ParserAdapter(sp.getParser());

            AcuityTestReader acuityTestReader = AcuityTestReader.getInstance();

            pa.setContentHandler(acuityTestReader);

            pa.parse(new InputSource(reader));

            return acuityTestReader.acuityTest; 

        } catch (Exception e) {
            throw new DvaCheckerException("Failed to get acuityTest data", e);
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
        
        if (localName.equals("acuitytest")) {
            //patient.setLastname( getText() );
            
        } else if (localName.equals("answers")) {
            //patient.setFirstname( getText() );
            
        } else if (localName.equals("answer")) {
            //patient.setSex( getText() );
            
        } else if (localName.equals("optotype")) {
            //patient.setAge( getText() );
            
        } else if (localName.equals("name")) {
            //patient.setComment( getText() );
            
        } else if (localName.equals("acuity")) {
            //patient.setComment( getText() );
        }
    }
    
}
