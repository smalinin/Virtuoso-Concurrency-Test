package se.iquest.stresstest.logging;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class LogManager
{
    public static final String LOG_DIR = "logs/";
    
    private static SimpleLogger logger;
    
    private LogManager() { }
    
    public static void initLogging() throws IOException
    {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdir();
        } else {
            clearLogs();
        }
        
        Thread.currentThread().setName("Main");
        logger = new SimpleLogger(System.out);
        logger.logForThread(Thread.currentThread(), new PrintStream(LOG_DIR + "main.log"));
        System.setOut(logger);
        System.setErr(logger);
        
        System.out.println("Logging initialized");
    }
    
    private static void clearLogs() throws IOException
    {
        // Remove all files in logDir
        FileUtils.cleanDirectory(new File(LOG_DIR));
    }
    
    public static void addThread(Thread thread) throws FileNotFoundException
    {
        if (logger == null) {
            throw new IllegalStateException("LogManager not initialized");
        }
        
        String logFileName = LOG_DIR + thread.getName() + ".log";
        PrintStream logFile = new PrintStream(logFileName);
        
        logger.logForThread(thread, logFile);
    }
}
