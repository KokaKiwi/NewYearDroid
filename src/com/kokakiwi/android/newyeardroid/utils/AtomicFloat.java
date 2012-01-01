package com.kokakiwi.android.newyeardroid.utils;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicFloat extends AtomicReference<Float>
{
    private static final long serialVersionUID = -9189135527831904430L;
    
    public AtomicFloat()
    {
        super();
    }
    
    public AtomicFloat(Float initialValue)
    {
        super(initialValue);
    }
    
    public void decrement(float value)
    {
        increment(-value);
    }
    
    public void increment(float value)
    {
        final float current = get();
        set(current + value);
    }
}
