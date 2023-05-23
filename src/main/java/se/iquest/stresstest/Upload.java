package se.iquest.stresstest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import se.iquest.stresstest.dataset.Dataset;
import se.iquest.stresstest.miner.exceptions.ConnectionErrorException;
import se.iquest.stresstest.miner.exceptions.UploadErrorException;
import se.iquest.stresstest.virtuoso.connections.GraphConnection;

public class Upload
{
    private final GraphConnection connection;
    private final Dataset dataset;
    private SailRepository m_repo;
    private String minerUUID;
    private String namedGraphURI;
    
    public Upload(String address, int port, Dataset dataset)
    {
        this.dataset = dataset;
        if (!Files.exists(Paths.get(dataset.getDatasetDirPath()))) {
            System.err.println("Dataset directory does not exist: " + dataset.getDatasetDirPath());
            System.exit(1);
        }
        System.out.println("Using Graphs from directory: " + dataset.getDatasetDirPath());
        connection = new GraphConnection();
        System.out.println("Setting upload address to: " + address);
        connection.setAddress(address);
        connection.setPort(port);
        connection.setUsername("dba");
        connection.setPassword("dba");
        connection.setDatabase("Whatever");

        minerUUID = this.dataset.getMinerUUID();
        namedGraphURI = ":" + minerUUID;

        System.out.println(String.format("Prepare dataset \"%s\"", namedGraphURI));
        m_repo = new SailRepository(new MemoryStore());
        m_repo.init();

        try (RepositoryConnection mconn = m_repo.getConnection()) {
            IRI context = mconn.getValueFactory().createIRI(namedGraphURI);
            readGraphsFromFile_2(Paths.get(this.dataset.getDatasetDirPath()), mconn, namedGraphURI, context);
        }
        System.out.println(String.format("Dataset was prepared"));
    }
    


    public void mine()
    {
        System.out.println(String.format(
                "Starting to upload graphs to db \"%s\" at %s:%s",
                connection.getDatabase(),
                connection.getAddress(),
                connection.getPort()));
        
        String transactionID = null;
        try {
            transactionID = connection.beginTransaction();
            
            // Clear existing data for named graph before adding new data
            connection.clearData(transactionID, namedGraphURI);
            addData_2(transactionID, namedGraphURI, connection);
            connection.commitTransaction(transactionID);
        } catch (ConnectionErrorException cee) {
            System.err.println(
                    String.format(
                            "Error while connecting to db \"%s\" at %s:%s",
                            connection.getDatabase(),
                            connection.getAddress(),
                            connection.getPort())
                    );
            cee.printStackTrace();
        } catch (UploadErrorException uee) {
            System.err.println(
                    String.format(
                            "Error while uploading graphs to db \"%s\" at %s:%s",
                            connection.getDatabase(),
                            connection.getAddress(),
                            connection.getPort())
                    );
            uee.printStackTrace();
            rollbackTransaction(connection, transactionID);
        } catch (Exception e) {
            System.err.println("Failed to export graphs");
            e.printStackTrace();
            rollbackTransaction(connection, transactionID);
        }
    }
    
    /**
     * Adds data for a named graph within the transaction
     * 
     * @throws UploadErrorException if the data could not be added within the transaction
     */
    private void addData_2(String transactionID, String namedGraphURI, GraphConnection connection) throws UploadErrorException
    {
        System.out.println(String.format("Beginning to add data for named graph \"%s\"", namedGraphURI));
        try (RepositoryConnection mconn = m_repo.getConnection()) {
            connection.addData_2(transactionID, namedGraphURI, mconn);
        } catch (UploadErrorException uee) {
            System.err.println("Failed to add data for named graph " + namedGraphURI);
            uee.printStackTrace();
        }
    }

    /**
     * Rolls back a transaction with the given transaction ID
     */
    private void rollbackTransaction(GraphConnection connection, String transactionID)
    {
        try {
            connection.rollbackTransaction(transactionID);
        } catch (Exception e) {
            System.err.println("Failed to rollback transaction");
            e.printStackTrace();
        }
    }
    
    
    private void readGraphsFromFile_2(Path graphsDirPath, RepositoryConnection con, String namedGraphURI, IRI context) {
        // Read all files in the graphs directory
        // and add the contents to a list of strings
        // where each string is a graph
        File graphsDir = graphsDirPath.toFile();

        if (graphsDir.isDirectory()) {
            File[] listOfFiles = graphsDir.listFiles();
            
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try (Reader in = new BufferedReader(new FileReader(file))){
                        con.add(in, namedGraphURI, RDFFormat.RDFXML, context);
                    } catch (IOException e) {
                        System.err.println("Failed to read file " + file.getName());
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    readGraphsFromFile_2(file.toPath(), con, namedGraphURI, context);
                } else {
                    System.err.println("Unknown file type: " + file.getName());
                }
            }
        }
    }

}