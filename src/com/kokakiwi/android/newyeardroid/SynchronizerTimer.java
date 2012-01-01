package com.kokakiwi.android.newyeardroid;

import java.io.IOException;
import java.util.TimerTask;

import net.sf.atomicdate.Date;

public class SynchronizerTimer extends TimerTask
{
    private final NewYearDroidActivity activity;
    private final Date                 synchro;
    private long                       lastUpdate = 0;
    
    public SynchronizerTimer(NewYearDroidActivity activity) throws IOException
    {
        this.activity = activity;
        synchro = new Date("0.fr.pool.ntp.org");
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
            synchro.synchronize();
            lastUpdate = System.currentTimeMillis();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public long currentTimeMillis()
    {
        long current = System.currentTimeMillis();
        long diff = current - lastUpdate;
        
        long now = synchro.getTime() + diff;
        
        return now;
    }
    
    public java.util.Date getDate()
    {
        return new java.util.Date(currentTimeMillis());
    }
    
    public NewYearDroidActivity getActivity()
    {
        return activity;
    }
    
    public Date getSynchro()
    {
        return synchro;
    }
}
