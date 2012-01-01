package com.kokakiwi.android.newyeardroid;

import java.io.IOException;
import java.util.Timer;

import android.app.Activity;
import android.os.Bundle;

import com.kokakiwi.android.newyeardroid.utils.NewYearLib;

public class NewYearDroidActivity extends Activity
{
    private NewYearLib        lib;
    
    private NewYearTimer      task;
    private SynchronizerTimer synchronizer;
    
    private final Timer       timer = new Timer("NewYearTimer", true);
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        lib = new NewYearLib(this);
        try
        {
            synchronizer = new SynchronizerTimer(this);
            task = new NewYearTimer(this);
            
            synchronizer.sync();
            
            timer.scheduleAtFixedRate(task, 0L, 1000L);
            timer.scheduleAtFixedRate(synchronizer, 0L, 30000L);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            finish();
        }
    }
    
    @Override
    protected void onDestroy()
    {
        timer.cancel();
        
        super.onDestroy();
    }
    
    public NewYearLib getLib()
    {
        return lib;
    }
    
    public NewYearTimer getTask()
    {
        return task;
    }
    
    public SynchronizerTimer getSynchronizer()
    {
        return synchronizer;
    }
    
    public Timer getTimer()
    {
        return timer;
    }
}