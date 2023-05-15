package se.iquest.stresstest.miner.exceptions;

public class UploadErrorException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = -5092451401990480349L;
    
    public UploadErrorException()
    {
        super("Upload error");
    }
    
    public UploadErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public UploadErrorException(String message)
    {
        super(message);
    }
    
    public UploadErrorException(Throwable cause)
    {
        super(cause);
    }
}