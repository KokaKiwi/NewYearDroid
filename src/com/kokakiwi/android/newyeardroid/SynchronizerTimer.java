package com.kokakiwi.android.newyeardroid;

import java.io.IOException;
import java.util.TimerTask;

import net.sf.atomicdate.Client;

public class SynchronizerTimer extends TimerTask
{
    public final static String         NTP_SERVER = "0.fr.pool.ntp.org";
    
    private final NewYearDroidActivity activity;
    private final Client               synchro;
    private long                       offset     = 0;
    
    public SynchronizerTimer(NewYearDroidActivity activity) throws IOException
    {
        this.activity = activity;
        synchro = new Client();
    }
    
    @Override
    public void run()
    {
        sync();
    }
    
    public void sync()
    {
        try
        {
            offset = synchro.getOffset(NTP_SERVER, Client.DEFAULT_SNTP_PORT);
            System.out.println(synchro.getOffset(NTP_SERVER,
                    Client.DEFAULT_SNTP_PORT));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean cancel()
    {
        synchro.close();
        
        return super.cancel();
    }
    
    public long currentTimeMillis()
    {
        long current = System.currentTimeMillis() + offset;
        
        return current;
    }
    
    public java.util.Date getDate()
    {
        return new java.util.Date(currentTimeMillis());
    }
    
    public NewYearDroidActivity getActivity()
    {
        return activity;
    }
    
    public Client getSynchro()
    {
        return synchro;
    }
}
