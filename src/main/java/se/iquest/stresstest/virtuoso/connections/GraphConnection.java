package se.iquest.stresstest.virtuoso.connections;

import java.io.Reader;
import java.io.StringReader;

import org.eclipse.rdf4j.IsolationLevels;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;

import se.iquest.stresstest.miner.exceptions.ConnectionErrorException;
import se.iquest.stresstest.miner.exceptions.UploadErrorException;
import se.iquest.stresstest.miner.utils.UUIDEntity;
import virtuoso.rdf4j.driver.VirtuosoRepository;

public class GraphConnection extends GraphTransactionConnection
{
    /**
     * Maximum time in seconds that a query can take to execute before it is terminated.
     */
    private static final int MAX_SQL_QUERY_EXECUTION_TIME = 7100; // Slightly less than the timeout set in the database
    
    private VirtuosoRepository repository;
    
    private RepositoryConnection repositoryConnection;
    
    public VirtuosoRepository getRepository()
    {
        return repository;
    }
    
    public void setRepository(VirtuosoRepository repository)
    {
        this.repository = repository;
    }
    
    public RepositoryConnection getRepositoryConnection()
    {
        return repositoryConnection;
    }
    
    public void setRepositoryConnection(RepositoryConnection repositoryConnection)
    {
        this.repositoryConnection = repositoryConnection;
    }
    
    protected void setupRepository() throws ConnectionErrorException
    {
        try {
            if (this.isRepositoryConnectionActive()) {
                System.out.println("Repository connection is already active. Closing it and opening a new one");
                this.closeRepositoryConnection();
            }
            
            String url = String.format("jdbc:virtuoso://%s:%d", this.getAddress(), this.getPort());
            this.setRepository(new VirtuosoRepository(url, this.getUsername(), this.getPassword()));
            this.getRepository().initialize();
            this.getRepository().setQueryTimeout(MAX_SQL_QUERY_EXECUTION_TIME);
            /*
             * Optimistic Concurrency Control.
             *  - presumes that probability and frequency of multiple users and
             *    processes instigating changes to the same database records is low.
             *    As result when an end-user or process attempts to change records
             *    it first of all determines if the record values at the point of change are
             *    still the same as what they were at the time of retrieval.
             *    If they are unchanged at the point of change then the change occurs otherwise
             *    the change process is rejected and then re-attempted.
             *    Although this reduces concurrent user latency,
             *    it does have the knock on effect of reducing data integrity if changes rejections aren't managed carefully.
             
             * Pessimistic Concurrency Control.
             *  - presumes that the probability and frequency of multiple user processing and
             *    instigating changes to the same records is high.
             *    As a result an end-user or process attempts to changes records it
             *    first of all secures Exclusive Locks on the records in question, performs the changes,
             *    and then releases the locks.
             *    Although this increases and preserves data integrity it does introduce concurrent use latency,
             *    which is perceived as performance degradation by the end-user or application developer.
             */
            this.getRepository().setConcurrencyMode(VirtuosoRepository.CONCUR_OPTIMISTIC);
            
            this.setRepositoryConnection(this.getRepository().getConnection());
        } catch (Exception e) {
            throw new ConnectionErrorException("Failed to setup repository", e);
        }
    }
    
    private boolean isRepositoryInitialized()
    {
        return this.getRepository() != null && this.getRepository().isInitialized();
    }
    
    protected boolean isRepositoryConnectionInitialized()
    {
        return this.getRepositoryConnection() != null
                && this.getRepositoryConnection().isOpen();
    }
    
    /**
     * An active repository connection is one that is initialized and active
     * @return true if the repository connection is active, false otherwise
     */
    private boolean isRepositoryConnectionActive()
    {
        boolean isActive = false;
        try {
            isActive = this.isRepositoryInitialized()
                    && this.isRepositoryConnectionInitialized()
                    && this.getRepositoryConnection().isActive();
        } catch (Exception e) {
            isActive = false;
            System.err.println("Failed to check if repository connection is active");
            e.printStackTrace();
        }
        return isActive;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        // TODO add more checks
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
    
    @Override
    public String beginTransaction() throws ConnectionErrorException
    {
        this.setupRepository();
        
        String transactionID;
        try {
            this.getRepositoryConnection().begin(IsolationLevels.READ_UNCOMMITTED);
            transactionID = UUIDEntity.generateUUID().toString();
            System.out.println(
                    "Began transaction with internal transaction ID: " + transactionID + " with Isolation Level " + this.getRepositoryConnection().getIsolationLevel());
        } catch (Exception e) {
            throw new ConnectionErrorException("Failed to begin transaction", e);
        }
        
        return transactionID;
    }
    
    @Override
    public void commitTransaction(String transactionID) throws UploadErrorException
    {
        try {
            this.getRepositoryConnection().commit();
            System.out.println("Successfully committed transaction: " + transactionID);
        } catch (RepositoryException e) {
            throw new UploadErrorException("Failed to commit transaction " + transactionID, e);
        } finally {
            this.closeRepositoryConnection();
        }
    }
    
    @Override
    public void rollbackTransaction(String transactionID) throws UploadErrorException
    {
        try {
            System.out.println("Rolling back transaction: " + transactionID);
            this.getRepositoryConnection().rollback();
        } catch (Exception e) {
            throw new UploadErrorException("Failed to rollback transaction " + transactionID, e);
        } finally {
            this.closeRepositoryConnection();
        }
    }
    
    protected void closeRepositoryConnection()
    {
        try {
            if (this.isRepositoryConnectionInitialized()) {
                System.out.println("Closing Repository connection");
                this.getRepositoryConnection().close();
            } else {
                System.out.println("Can't close Repository connection. Repository connection is not initialized");
            }
        } catch (Exception e) {
            System.out.println("Failed to close repository connection");
            e.printStackTrace();
        }
        try {
            if (this.isRepositoryInitialized()) {
                System.out.println("Shutting down Repository");
                this.getRepository().shutDown();
            } else {
                System.out.println("Can't shut down Repository. Repository is not initialized");
            }
        } catch (Exception e) {
            System.out.println("Failed to shut down repository");
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if the named graph URI is valid.
     *
     * @param namedGraphURI
     *            The named graph URI to check.
     * @return True if the named graph URI is valid, false otherwise.
     */
    protected boolean isValidNamedGraphURI(String namedGraphURI)
    {
        // If no contexts are supplied the
        // method operates on the entire repository.
        // ... which is bad.
        return namedGraphURI != null && !namedGraphURI.isEmpty();
    }
    
    @Override
    public void addData(String transactionID, String namedGraphURI, String graph) throws UploadErrorException
    {
        try {
            if (!isValidNamedGraphURI(namedGraphURI)) {
                throw new IllegalArgumentException(String.format("Named graph URI cannot be: %s", namedGraphURI));
            }
            Reader graphReader = new StringReader(graph);
            IRI context = this.getRepositoryConnection().getValueFactory().createIRI(namedGraphURI);
    
            System.out.println("Adding data to transaction " + transactionID + " for named graph \"" + namedGraphURI + "\"");
            this.getRepositoryConnection().add(graphReader, namedGraphURI, RDFFormat.RDFXML, context);
        } catch (Exception e) {
            throw new UploadErrorException("Failed to upload graph", e);
        }
    }
    
    @Override
    public void addData_2(String transactionID, String namedGraphURI, RepositoryConnection mem) throws UploadErrorException
    {
        try {
            if (!isValidNamedGraphURI(namedGraphURI)) {
                throw new IllegalArgumentException(String.format("Named graph URI cannot be: %s", namedGraphURI));
            }
            IRI context = this.getRepositoryConnection().getValueFactory().createIRI(namedGraphURI);
            RepositoryResult<Statement> res = mem.getStatements(null, null, null, true);

            System.out.println("Adding data to transaction " + transactionID + " for named graph \"" + namedGraphURI + "\"");
            this.getRepositoryConnection().add(res, context);
        } catch (Exception e) {
            throw new UploadErrorException("Failed to upload graph", e);
        }
    }

    @Override
    public void clearData(String transactionID, String namedGraphURI) throws UploadErrorException
    {
        try {
            if (!isValidNamedGraphURI(namedGraphURI)) {
                throw new IllegalArgumentException(String.format("Named graph URI cannot be: %s", namedGraphURI));
            }
            IRI context = this.getRepositoryConnection().getValueFactory().createIRI(namedGraphURI);
    
            System.out.println("Clearing existing data for named graph \"" + namedGraphURI + "\" within " + transactionID);
            this.getRepositoryConnection().clear(context);
        } catch (Exception e) {
            throw new UploadErrorException("Could not clear data for named graph " + namedGraphURI, e);
        }
    }

}
