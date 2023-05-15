package se.iquest.stresstest.miner.connections;

import java.util.UUID;

import se.iquest.stresstest.miner.utils.UUIDEntity;

public abstract class Connection implements UUIDEntity
{
    protected String name = "";
    protected String address = "localhost";
    protected int port = 0;
    protected String description = "";
    protected String explorer;
    protected UUID uid;
    
    /**
     * 
     */
    public Connection() {
        super();
    }
    
    /**
     * 
     * @param name
     * @param address
     * @param port
     */
    public Connection(String name, String address, int port)
    {
        this.name=name; 
        this.address = address;
        this.port = port;
    }
    
    /**
     * 
     * @param name
     * @param address
     * @param port
     * @param description
     */
    public Connection(String name, String address, int port, String description) 
    {
        super();
        this.name = name;
        this.address = address;
        this.port = port;
        this.description = description;
    }

    public UUID getUid()
    {
        return uid;
    }
    
    public void setUid(UUID uid)
    {
        this.uid = uid;
    }
    
    /**
     * 
     * @return
     */
    public synchronized String getName()
    {
        return this.name;
    }
    
    /**
     * 
     * @param name
     */
    public synchronized void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * 
     * @return
     */
    public synchronized String getAddress()
    {
        return this.address;
    }
    
    /**
     * 
     * @param address
     */
    public synchronized void setAddress(String address)
    {
        this.address = address;
    }
    
    /**
     * 
     * @return
     */
    public synchronized int getPort()
    {
        return this.port;
    }
    
    /**
     * 
     * @param port
     */
    public synchronized void setPort(int port)
    {
        this.port = port;
    }
    
    /**
     * 
     * @return
     */
    public synchronized String getDescription() {
        return this.description;
    }
    
    /**
     * 
     * @param description
     */
    public synchronized void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 
     * @return
     */
    public String getExplorer() {
        return explorer;
    }
    
    /**
     * 
     * @param explorer
     */
    public void setExplorer(String explorer) {
        this.explorer = explorer;
    }
    
    public Boolean isPortRequired()
    {
        return true;
    }
    
    public Boolean isAddressRequired()
    {
        return true;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((explorer == null) ? 0 : explorer.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + port;
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Connection other = (Connection) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (port != other.port)
            return false;
        return true;
    }
}