package se.iquest.stresstest;

import org.apache.commons.io.FileUtils;
import se.iquest.stresstest.dataset.DatasetHandler;
import se.iquest.stresstest.logging.LogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        // The address of the server to connect to
        String address = "127.0.0.1";
        // The port of the server to connect to
        int port = 1111;
        // How many iterations each thread should do
        int nrOfUploads = 10;
        // How many client threads to start
        int nrOfThreads = 5;

        boolean commitAfterClear = true;
        boolean useAutoCommit = false;

        LogManager.initLogging();
        
        removeOldDatasets();
        
        for (int i = 0; i < nrOfThreads; i++) {
            startStressTestThread(address, port, nrOfUploads, commitAfterClear, useAutoCommit);
        }
    }
    
    private static void startStressTestThread(String address, int port, int nrOfUploads, boolean commitAfterClear, boolean useAutoCommit) throws FileNotFoundException
    {
        StressTestClient stressTestClient = new StressTestClient(address, port, nrOfUploads, commitAfterClear, useAutoCommit);
        
        Thread thread = new Thread(stressTestClient);
        thread.setName("STClientThread" + thread.getId());
        
        LogManager.addThread(thread);
        
        System.out.println("Starting client " + thread.getName());
        thread.start();
    }
    
    private static void removeOldDatasets() throws IOException
    {
        // Remove all files in datasetDir
        File datasetDir = new File(DatasetHandler.DATASET_DIR_PATH);
        if (datasetDir.exists()) {
            FileUtils.cleanDirectory(datasetDir);
        }
    }
}