package se.iquest.stresstest;

import se.iquest.stresstest.dataset.Dataset;
import se.iquest.stresstest.dataset.DatasetHandler;

public class StressTestClient implements Runnable
{
    private final String address;
    private final int port;
    private final int nrOfUploads;
    private Upload upload;
    
    public StressTestClient(String address, int port, int nrOfUploads)
    {
        this.address = address;
        this.port = port;
        this.nrOfUploads = nrOfUploads;
    }
    
    public void setup()
    {
        Dataset dataset = DatasetHandler.createNewDataset();
        this.upload = new Upload(address, port, dataset);
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
