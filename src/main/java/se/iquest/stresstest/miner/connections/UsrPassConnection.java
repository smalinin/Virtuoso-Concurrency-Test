package se.iquest.stresstest.miner.connections;

public class UsrPassConnection extends Connection
{
    protected String username = "";
    protected String password = "";
    
    public UsrPassConnection()
    {
        super();
    }
    
    public UsrPassConnection(String name, String address, int port, String username, String password)
    {
        super(name, address, port);
        this.username = username;
        this.password = password;
    }
    
    public UsrPassConnection(String name, String address, int port, String description, String username, String password)
    {
        super(name, address, port, description);
        this.username = username;
        this.password = password;
    }
    
    /**
     * 
     * @return
     */
    public synchronized String getUsername()
    {
        return this.username;
    }
    
    /**
     * 
     * @param username
     */
    public synchronized void setUsername(String username)
    {
        this.username = username;
    }
    
    /**
     * 
     * @return
     */
    public synchronized String getPassword()
    {
        return this.password;
    }
    
    /**
     * 
     * @param password
     */
    public synchronized void setPassword(String password)
    {
        this.password = password;
    }
    
    protected boolean userPassRequired() 
    {
        return true;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        UsrPassConnection other = (UsrPassConnection) obj;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
