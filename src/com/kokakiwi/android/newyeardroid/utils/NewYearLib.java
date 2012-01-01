package com.kokakiwi.android.newyeardroid.utils;

import java.util.Date;

import com.kokakiwi.android.newyeardroid.NewYearDroidActivity;

public class NewYearLib
{
    private final NewYearDroidActivity activity;
    
    public NewYearLib(NewYearDroidActivity activity)
    {
        this.activity = activity;
    }
    
    public long diffNewYear()
    {
        final Date date = activity.getSynchronizer().getDate();
        final long current = date.getTime();
        
        final int nextYear = date.getYear() + 1;
        
        final Date newYearDate = new Date(nextYear, 0, 1, 0, 0, 0);
        final long newYearTimestamp = newYearDate.getTime();
        
        final long diff = newYearTimestamp - current;
        
        return diff;
    }
    
    public NewYearDroidActivity getActivity()
    {
        return activity;
    }
}
