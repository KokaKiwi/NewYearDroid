package com.kokakiwi.android.newyeardroid;

import java.sql.Timestamp;
import java.util.TimerTask;

import android.util.TypedValue;
import android.widget.TextView;

import com.kokakiwi.android.newyeardroid.utils.AtomicFloat;

public class NewYearTimer extends TimerTask
{
    private final NewYearDroidActivity activity;
    
    public NewYearTimer(NewYearDroidActivity activity)
    {
        this.activity = activity;
    }
    
    @Override
    public void run()
    {
        final long diff = activity.getLib().diffNewYear();
        final Timestamp ts = new Timestamp(diff);
        
        final CharSequence hours = ts.getHours() < 10 ? new StringBuilder()
                .append('0').append(ts.getHours()) : new StringBuilder()
                .append(ts.getHours());
        final CharSequence minutes = ts.getMinutes() < 10 ? new StringBuilder()
                .append('0').append(ts.getMinutes()) : new StringBuilder()
                .append(ts.getMinutes());
        final CharSequence seconds = ts.getSeconds() < 10 ? new StringBuilder()
                .append('0').append(ts.getSeconds()) : new StringBuilder()
                .append(ts.getSeconds());
        
        final StringBuilder sb = new StringBuilder();
        final AtomicFloat size = new AtomicFloat(50.0F);
        
        if (ts.getHours() > 0)
        {
            sb.append(hours);
            sb.append(" : ");
            size.decrement(3.0F);
        }
        
        if (ts.getMinutes() > 0)
        {
            sb.append(minutes);
            sb.append(" : ");
            size.decrement(3.0F);
        }
        
        sb.append(seconds);
        
        activity.runOnUiThread(new Runnable() {
            
            @Override
            public void run()
            {
                final TextView timer = (TextView) activity
                        .findViewById(R.id.timer);
                timer.setText(sb);
                timer.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.get());
            }
        });
    }
}
