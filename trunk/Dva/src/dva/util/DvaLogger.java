/*
 * DvaLogger.java
 *
 * Created on May 8, 2007, 3:42 AM
 *
 */

package dva.util;

import java.awt.Color;

/**
 *
 * @author J-Chris
 */
public class DvaLogger {
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR, FATAL
    }
    
    public static void debug(String msg){
        log(LogLevel.DEBUG, msg);
    }
    
    public static void debug(Class clazz, String msg){
        log(LogLevel.DEBUG, clazz, null, msg);
    }
    
    public static void info(String msg){
        log(LogLevel.INFO, msg);
    }
    
     public static void info(Class clazz, String msg){
        log(LogLevel.INFO, clazz, null, msg);
    }
    
    public static void warn(String msg){
        log(LogLevel.WARN, msg);
    }
    
    public static void warn(Class clazz, String msg){
        log(LogLevel.WARN, clazz, null, msg);
    }
     
    public static void warn(Class clazz, Exception e){
        log(LogLevel.WARN, clazz, e, null);
    }
    
    public static void warn(Class clazz, Exception e, String msg){
        log(LogLevel.WARN, clazz, e, msg);
    }
    
     public static void error(Class clazz, String msg){
        log(LogLevel.ERROR, clazz, null, msg);
    }
     
    public static void error(Class clazz, Exception e){
        log(LogLevel.ERROR, clazz, e, null);
    }
    
    public static void error(Class clazz, Exception e, String msg){
        log(LogLevel.ERROR, clazz, e, msg);
    }
    
    public static void fatal(String msg){
        log(LogLevel.FATAL, msg);
    }
    
    public static void fatal(Class clazz, String msg){
        log(LogLevel.FATAL, clazz,  null, msg);
    }
    
    public static void fatal(Class clazz, Exception e){
        log(LogLevel.FATAL, clazz, e, null);
    }
    
    public static void fatal(Class clazz, Exception e, String msg){
        log(LogLevel.FATAL, clazz, e, msg);
    }
    
    public static void log(LogLevel level, String msg){
        log(level, null, null, msg); 
    }
    
    public static void log(LogLevel level, Exception e, String msg){
        log(level, null, e, msg); 
    }
    /**
     * Append a String to the jTextAreaLog maintaining the log size below a given limit
     */
    public static void log(LogLevel level, Class clazz, Exception e, String msg){
        if (jTextAreaLog==null) return; 
        
        if ( currentLogLevel.ordinal() <= level.ordinal() ){
            if (msg==null) msg = ""; 
            
            if (e!=null){
                msg+=" " + e;
            }
            jTextAreaLog.append(">" + (clazz!=null ? "[" + clazz.getName() + "] ": "") + msg + "\n");
            // if (e!=null){
            //     StackTraceElement stackTraceElement[] = e.getStackTrace(); 
            //     for (int i=0; i < stackTraceElement.length; i++){
            //          jTextAreaLog.append("\t" + stackTraceElement[i] ); 
            //     }
            // }
            if (jTextAreaLog.getLineCount() == logLineCount){
                logCurrentTruncSize = jTextAreaLog.getDocument().getLength();
            }
            //check log size
            if (jTextAreaLog.getLineCount() == logLineCount + logLineCount * 0.2){
                 jTextAreaLog.replaceRange("", 0, logCurrentTruncSize);
                 
            }
        }
    }
    
    public static void initLogger(javax.swing.JTextArea _jTextAreaLog){
        jTextAreaLog = _jTextAreaLog; 
    }
    
    private static int logLineCount = 100; 
    private static int logCurrentTruncSize = 0; 
    private static LogLevel currentLogLevel = LogLevel.DEBUG;
    private static javax.swing.JTextArea jTextAreaLog; 
}
