package se.iquest.stresstest.dataset;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import se.iquest.stresstest.miner.utils.UUIDEntity;

public final class DatasetHandler {
    
    public static final String GRAPHS_DIR_PATH = "graphs/";
    public static final String DATASET_DIR_PATH = GRAPHS_DIR_PATH + "datasets/";
    public static final String SAMPLE_DATASET_DIR_PATH = GRAPHS_DIR_PATH + "sample";
    
    public static final String MINER_FILE_NAME = "iqmine.xml";
    public static final String CONNECTION_FILE_NAME = "wdc_connection.xml";
    public static final String JOB_FILE_NAME = "wdc_job.xml";
    public static final String ITEMS_DIR_NAME = "items";
    
    public static final String TO_REPLACE_MINER_UUID = "REPLACE_WITH_MINER_UUID";
    public static final String TO_REPLACE_CONNECTION_UUID = "REPLACE_WITH_CONNECTION_UUID";
    public static final String TO_REPLACE_JOB_UUID = "REPLACE_WITH_JOB_UUID";
    public static final String TO_REPLACE_MINER_NAME = "REPLACE_WITH_MINER_NAME";
    public static final String TO_REPLACE_JOB_NAME = "REPLACE_WITH_JOB_NAME";
    
    private DatasetHandler() {
        // Prevent instantiation
    }
    
    public static Dataset createNewDataset() {
        
        Dataset dataset = new Dataset();
        
        copySampleDataset(dataset.getDatasetDirPath());
        
        File jobGraphFile = new File(dataset.getJobFilePath());
        List<String> itemUUIDs = getItemUUIDsInJobGraph(jobGraphFile);
        
        // Replace UUIDs and miner name in miner file
        System.out.println("Replacing UUIDs and miner name in miner file");
        File minerFile = new File(dataset.getMinerFilePath());
        replaceStringInFile(minerFile, TO_REPLACE_MINER_UUID, dataset.getMinerUUID());
        replaceStringInFile(minerFile, TO_REPLACE_CONNECTION_UUID, dataset.getConnectionUUID());
        replaceStringInFile(minerFile, TO_REPLACE_MINER_NAME, dataset.getMinerName());
        
        // Replace UUIDs and miner name in connection file
        System.out.println("Replacing UUIDs and miner name in connection file");
        File connectionFile = new File(dataset.getConnectionFilePath());
        replaceStringInFile(connectionFile, TO_REPLACE_MINER_UUID, dataset.getMinerUUID());
        replaceStringInFile(connectionFile, TO_REPLACE_CONNECTION_UUID, dataset.getConnectionUUID());
        replaceStringInFile(connectionFile, TO_REPLACE_JOB_UUID, dataset.getJobUUID());
        replaceStringInFile(connectionFile, TO_REPLACE_MINER_NAME, dataset.getMinerName());
        replaceStringInFile(connectionFile, TO_REPLACE_JOB_NAME, dataset.getJobName());
        
        // Replace UUIDs and miner name in job file
        File jobFile = new File(dataset.getJobFilePath());
        System.out.println("Replacing UUIDs and miner name in job file");
        replaceStringInFile(jobFile, TO_REPLACE_JOB_UUID, dataset.getJobUUID());
        replaceStringInFile(jobFile, TO_REPLACE_CONNECTION_UUID, dataset.getConnectionUUID());
        replaceStringInFile(jobFile, TO_REPLACE_MINER_NAME, dataset.getMinerName());
        replaceStringInFile(jobFile, TO_REPLACE_JOB_NAME, dataset.getJobName());
        
        File itemsDir = new File(dataset.getDatasetDirPath() + "/" + ITEMS_DIR_NAME);
        File[] itemFiles = itemsDir.listFiles();
        
        
        // Create a Map of old UUIDs (itemUUIDs) and generated new UUIDs
        Map<String, String> itemUUIDMap = new HashMap<>();
        for (String itemUUID : itemUUIDs) {
            String newUUID = UUIDEntity.generateUUID().toString();
            itemUUIDMap.put(itemUUID, newUUID);
        }
        
        // Replace UUIDs in item graphs
        System.out.println("Replacing  in item graphs");
        for (File itemFile : itemFiles) {
            System.out.println("Replacing job UUID and miner name in item graph: " + itemFile.getName());
            replaceStringInFile(itemFile, TO_REPLACE_JOB_UUID, dataset.getJobUUID());
            replaceStringInFile(itemFile, TO_REPLACE_MINER_NAME, dataset.getMinerName());
            // for (Entry<String, String> itemUUIDPair : itemUUIDMap.entrySet()) {
            //     System.out.println("Replacing UUID in item graph: " + itemFile.getName() + " UUID: " + itemUUIDPair.getKey() + " with UUID: " + itemUUIDPair.getValue());
            //     replaceStringInFile(itemFile, itemUUIDPair.getKey(), itemUUIDPair.getValue());
            //     replaceStringInFile(jobFile, itemUUIDPair.getKey(), itemUUIDPair.getValue());
            // }
        }
        
        return dataset;
    }
    
    private static void copySampleDataset(String destinationPath) {
        // Copy sample dataset to destination path
        File sampleDatasetDir = new File(SAMPLE_DATASET_DIR_PATH);
        File destinationDir = new File(destinationPath);
        
        try {
            FileUtils.copyDirectory(sampleDatasetDir, destinationDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void replaceStringInFile(File file, String toReplace, String toReplaceWith) {
        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            content = content.replace(toReplace, toReplaceWith);
            FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static List<String> getItemUUIDsInJobGraph(File jobGraphFile) {
        // Find all lines in the job graph file that contain "item/"
        // and extract the UUIDs from those lines
        List<String> itemUUIDs = new ArrayList<>();
        
        try {
            List<String> lines = Files.readAllLines(jobGraphFile.toPath());
            for (String line : lines) {
                if (line.contains("item/")) {
                    String[] parts = line.split("/");
                    int index = parts.length - 2;
                    String itemUUID = parts[index].replace("\"", "");
                    itemUUIDs.add(itemUUID);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read file " + jobGraphFile.getName());
            e.printStackTrace();
        }
        
        return itemUUIDs;
    }
}
