package se.iquest.stresstest.miner.exceptions;

public class ConnectionErrorException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 7953263564329954291L;
    
    public ConnectionErrorException()
    {
        super("Connection error");
    }
    
    public ConnectionErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ConnectionErrorException(String message)
    {
        super(message);
    }
    
    public ConnectionErrorException(Throwable cause)
    {
        super(cause);
    }
}