package se.iquest.stresstest.virtuoso.connections;

import se.iquest.stresstest.miner.connections.HttpConnection;
import se.iquest.stresstest.miner.exceptions.ConnectionErrorException;
import se.iquest.stresstest.miner.exceptions.UploadErrorException;

import org.eclipse.rdf4j.repository.RepositoryConnection;

public abstract class GraphTransactionConnection extends HttpConnection
{
    private String database;
    
    protected GraphTransactionConnection()
    { }
    
    public String getDatabase()
    {
        return this.database;
    }
    
    public void setDatabase(String database)
    {
        this.database = database;
    }
    
    public abstract void initializeConnection() throws ConnectionErrorException;

    /**
     * Begins a transaction
     * 
     * @return The transaction ID for the transaction
     * @throws ConnectionErrorException If the connection fails
     */
    public abstract String beginTransaction() throws ConnectionErrorException;
    
    /**
     * Clears data for a named graph
     * 
     * @param transactionID
     *            The transaction ID for the transaction
     * @param namedGraphURI
     *            The URI/IRI for the named graph
     * @throws UploadErrorException
     *             If the request fails
     */
    public abstract void clearData(String transactionID, String namedGraphURI) throws UploadErrorException;
    
    /**
     * Adds data for a named graph within the transaction
     * 
     * @param transactionID
     *            The transaction ID for the transaction
     * @param namedGraphURI
     *            The URI/IRI for the named graph
     * @param graph
     *           The graph data to add
     * @throws UploadErrorException
     *             if the data could not be added within the transaction
     */
    public abstract void addData(String transactionID, String namedGraphURI, String graph) throws UploadErrorException;

    public abstract void addData_2(String transactionID, String namedGraphURI, RepositoryConnection mem) throws UploadErrorException;
    
    /**
     * Commits a transaction
     * 
     * @throws UploadErrorException If the transaction could not be committed
     */
    public abstract void commitTransaction(String transactionID) throws UploadErrorException;
    
    /**
     * Rolls back a transaction with the given transaction ID
     * 
     * @throws UploadErrorException If the transaction could not be rolled back
     */
    public abstract void rollbackTransaction(String transactionID)  throws UploadErrorException;
}
