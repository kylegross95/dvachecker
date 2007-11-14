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
import dva.displayer.Optotype;
import dva.util.DvaLogger;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

/**
 *
 * @author J-Chris
 */
public class AcuityTestReader extends DefaultHandler {
    
    Optotype opto = null;
    String text = "";
    
    String answerValue = ""; 
    String answerStr = ""; 
    
    private StaticAcuityTest acuityTest = null; 
    
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
        acuityTest = new StaticAcuityTest();
    }

    /**
     * 
     * @param connection
     * @param userBean
     * @param file
     * @throws DvaCheckerException
     */
    public static StaticAcuityTest process(Reader reader) throws DvaCheckerException {

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
        
        if (localName.equals("acuitytest")) {
            String treadmillspeed = atts.getValue("treadmillspeed"); 
            if (!StringUtils.isEmpty(treadmillspeed)) { 
                acuityTest.setTreadmillSpeed( Float.valueOf( treadmillspeed )); 
            }
            
            String startdate = atts.getValue("date"); 
            if (!StringUtils.isEmpty(startdate)) { 
                try {
                    acuityTest.setStartDate( AcuityTest.getDateFormatter().parse( startdate) ); 
                    
                } catch (Exception e){
                    //ignore
                    DvaLogger.error(AcuityTestReader.class, "Fail to parse date:" + e.getMessage()); 
                }
            }
            
            String eye = atts.getValue("eye"); 
            if (!StringUtils.isEmpty(eye)) { 
                acuityTest.setEye( eye );
            }
            
        } else if (localName.equals("answer")){
            answerValue = atts.getValue("value");
            answerStr = atts.getValue("str");
            
        } else if (localName.equals("optotype")){
            opto = new Optotype(false); 
        }
    }
    
    public String getText(){
        return text.trim();
    }
    
    public void characters(char[] ch, int start, int length)
    {
        text = new String(ch, start, length);
    }

    /**
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String namespace, String localName, String qName) {
        //DvaLogger.debug(PatientReader.class, "startElement/localName:"+localName + ", text:" + getText());
        
        if (localName.equals("acuitytest")) {
            //get treadmill speed
            
        } else if (localName.equals("answers")) {
            //patient.setFirstname( getText() );
            
        } else if (localName.equals("answer")) {
            //
            acuityTest.saveAnswer(opto, Boolean.valueOf(answerValue), answerStr); 
            
        } else if (localName.equals("optotype")) {
            
            
            
        } else if (localName.equals("name")) {
            opto.setName(getText());

            
        } else if (localName.equals("acuity")) {
            //DvaLogger.debug(AcuityTestReader.class, "va:" + Double.valueOf( getText() ) ); 
            opto.setVa( Double.valueOf( getText() ) );
        }
    }
    
}