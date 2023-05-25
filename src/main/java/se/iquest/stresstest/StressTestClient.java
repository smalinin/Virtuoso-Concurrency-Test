package se.iquest.stresstest;

import se.iquest.stresstest.dataset.Dataset;
import se.iquest.stresstest.dataset.DatasetHandler;

public class StressTestClient implements Runnable
{
    private final String address;
    private final int port;
    private final int nrOfUploads;
    private final boolean commitAfterClear;
    private final boolean useAutoCommit;
    private Upload upload;
    
    public StressTestClient(String address, int port, int nrOfUploads, boolean commitAfterClear, boolean useAutoCommit)
    {
        this.address = address;
        this.port = port;
        this.nrOfUploads = nrOfUploads;
        this.commitAfterClear = commitAfterClear;
        this.useAutoCommit = useAutoCommit;
    }
    
    public void setup()
    {
        Dataset dataset = DatasetHandler.createNewDataset();
        this.upload = new Upload(address, port, dataset, commitAfterClear, useAutoCommit);
    }
    
    @Override
    public void run()
    {
        setup();
        
        for (int i = 0; i < nrOfUploads; i++) {
            upload.mine();
        }
    }
}
