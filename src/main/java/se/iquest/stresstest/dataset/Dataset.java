package se.iquest.stresstest.dataset;

import java.util.Random;

import se.iquest.stresstest.miner.utils.UUIDEntity;

public class Dataset {
    
    private String minerUUID;
    private String connectionUUID;
    private String jobUUID;
    private final String minerName;
    private final String jobName;
    
    private final String datasetDirPath;
    private final String minerFilePath;
    private final String connectionFilePath;
    private final String jobFilePath;
    
    public Dataset() {
        this(UUIDEntity.generateUUID().toString(),
            UUIDEntity.generateUUID().toString(),
            UUIDEntity.generateUUID().toString());
    }
    
    public Dataset(String minerUUID, String connectionUUID, String jobUUID) {
        this.minerUUID = minerUUID;
        this.connectionUUID = connectionUUID;
        this.jobUUID = jobUUID;
        
        this.datasetDirPath = DatasetHandler.DATASET_DIR_PATH + minerUUID + "/";
        this.minerFilePath = this.datasetDirPath + DatasetHandler.MINER_FILE_NAME;
        this.connectionFilePath = this.datasetDirPath + DatasetHandler.CONNECTION_FILE_NAME;
        this.jobFilePath = this.datasetDirPath + DatasetHandler.JOB_FILE_NAME;
        this.minerName = "iqmine" + new Random().nextInt(Integer.MAX_VALUE);
        this.jobName = "job" + new Random().nextInt(Integer.MAX_VALUE);
    }
    
    public String getMinerName()
    {
        return minerName;
    }
    
    public String getJobName()
    {
        return jobName;
    }
    
    public String getDatasetDirPath() {
        return datasetDirPath;
    }
    
    public String getMinerFilePath()
    {
        return minerFilePath;
    }
    
    public String getConnectionFilePath()
    {
        return connectionFilePath;
    }
    
    public String getJobFilePath()
    {
        return jobFilePath;
    }
    
    
    public String getMinerUUID() {
        return minerUUID;
    }
    
    public void setMinerUUID(String minerUUID) {
        this.minerUUID = minerUUID;
    }
    
    public String getConnectionUUID() {
        return connectionUUID;
    }
    
    public void setConnectionUUID(String connectionUUID) {
        this.connectionUUID = connectionUUID;
    }
    
    public String getJobUUID() {
        return jobUUID;
    }
    
    public void setJobUUID(String jobUUID) {
        this.jobUUID = jobUUID;
    }
}
