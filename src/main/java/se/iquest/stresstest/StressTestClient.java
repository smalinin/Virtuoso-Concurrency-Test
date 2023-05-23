package se.iquest.stresstest;

import se.iquest.stresstest.dataset.Dataset;
import se.iquest.stresstest.dataset.DatasetHandler;

public class StressTestClient implements Runnable
{
    private final String address;
    private final int port;
    private final int nrOfUploads;
    private final boolean commitAfterClear;
    private Upload upload;
    
    public StressTestClient(String address, int port, int nrOfUploads, boolean commitAfterClear)
    {
        this.address = address;
        this.port = port;
        this.nrOfUploads = nrOfUploads;
        this.commitAfterClear = commitAfterClear;
    }
    
    public void setup()
    {
        Dataset dataset = DatasetHandler.createNewDataset();
        this.upload = new Upload(address, port, dataset, commitAfterClear);
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
