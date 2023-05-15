package se.iquest.stresstest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import se.iquest.stresstest.dataset.Dataset;
import se.iquest.stresstest.miner.exceptions.ConnectionErrorException;
import se.iquest.stresstest.miner.exceptions.UploadErrorException;
import se.iquest.stresstest.virtuoso.connections.GraphConnection;

public class Upload
{
    private final GraphConnection connection;
    private final Dataset dataset;
    
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
            String minerUUID = this.dataset.getMinerUUID();
            String namedGraphURI = ":" + minerUUID;
            
            transactionID = connection.beginTransaction();
            
            // Clear existing data for named graph before adding new data
            connection.clearData(transactionID, namedGraphURI);
            addData(transactionID, namedGraphURI, connection);
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
    private void addData(String transactionID, String namedGraphURI, GraphConnection connection) throws UploadErrorException
    {
        System.out.println(String.format("Beginning to add data for named graph \"%s\"", namedGraphURI));
        for (String graph : this.readGraphsFromFile(Paths.get(this.dataset.getDatasetDirPath()))) {
            try {
                connection.addData(transactionID, namedGraphURI, graph);
                
            } catch (UploadErrorException uee) {
                System.err.println("Failed to add data for named graph " + namedGraphURI);
                uee.printStackTrace();
            }
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
    
    
    private List<String> readGraphsFromFile(Path graphsDirPath) {
        // Read all files in the graphs directory
        // and add the contents to a list of strings
        // where each string is a graph
        
        List<String> graphs = new ArrayList<>();
        
        File graphsDir = graphsDirPath.toFile();
        if (graphsDir.isDirectory()) {
            File[] listOfFiles = graphsDir.listFiles();
            
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        graphs.add(new String(Files.readAllBytes(file.toPath())));
                    } catch (IOException e) {
                        System.err.println("Failed to read file " + file.getName());
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    graphs.addAll(readGraphsFromFile(file.toPath()));
                } else {
                    System.err.println("Unknown file type: " + file.getName());
                }
            }
        }
        return graphs;
    }
}