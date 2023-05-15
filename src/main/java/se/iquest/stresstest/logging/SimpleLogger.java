package se.iquest.stresstest.logging;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SimpleLogger extends PrintStream
{
    private final PrintStream originalStream;
    private final HashMap<Thread, PrintStream> loggerStreams = new HashMap<>();
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    
    public SimpleLogger(PrintStream originalStream)
    {
        super(originalStream);
        this.originalStream = originalStream;
    }
    
    public synchronized void logForThread(Thread threadToLogFor, PrintStream streamToLogTo)
    {
        loggerStreams.put(threadToLogFor, streamToLogTo);
    }
    
    @Override
    public synchronized void println(String ln)
    {
        Thread currentThread = Thread.currentThread();
        PrintStream logPS = loggerStreams.get(Thread.currentThread());
        
        String logMsg = currentThread.getName() + " " + dateFormat.format(new Date()) + ": " + ln;
        if (logPS != null) {
            logPS.println(logMsg);
        }
        originalStream.println(logMsg);
    }
    
    @Override
    public synchronized void println(Object o)
    {
        if (o != null) {
            println(o.toString());
        }
    }
}