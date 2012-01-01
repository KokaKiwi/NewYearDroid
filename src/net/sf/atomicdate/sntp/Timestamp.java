/*
 * Timestamp.java
 * 
 * 2008/11/24 - [AP] class created.
 * 
 * Copyright 2008 (C) by Arménio Pinto
 * Read license.txt for more details.
 */

package net.sf.atomicdate.sntp;

/**
 * An SNTP timestamp.
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public final class Timestamp
{
    
    // Class attributes.
    // **************************************************************************
    
    /** The zero timestamp. */
    public static final Timestamp ZERO = new Timestamp(0, 0);
    
    // Instance attributes.
    // ***********************************************************************
    
    /** The timestamp integer part. */
    private final long            integer;
    
    /** The timestamp fractional part. */
    private final long            fraction;
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Constructor.
     * 
     * @param integer
     *            the timestamp integer part.
     * @param fraction
     *            the timestamp fractional part.
     * @throws IllegalArgumentException
     *             in any of the arguments is negative.
     */
    public Timestamp(final long integer, final long fraction)
    {
        if (integer < 0)
        {
            throw new IllegalArgumentException("integer<0");
        }
        if (fraction < 0)
        {
            throw new IllegalArgumentException("fraction<0");
        }
        this.integer = integer;
        this.fraction = fraction;
    }
    
    /**
     * Returns the timestamp integer part.
     * 
     * @return the timestamp integer part.
     */
    public long getInteger()
    {
        return integer;
    }
    
    /**
     * Returns the timestamp fractional part.
     * 
     * @return the timestamp fractional part.
     */
    public long getFraction()
    {
        return fraction;
    }
    
    // See Object for details.
    @Override
    public boolean equals(final Object obj)
    {
        boolean equals = false;
        if (obj == this)
        {
            equals = true;
        }
        else
        {
            if (obj != null && obj instanceof Timestamp)
            {
                final Timestamp other = (Timestamp) obj;
                equals = other.integer == integer && other.fraction == fraction;
            }
        }
        
        return equals;
    }
    
    // See Object for details.
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
    
    // See Object for details.
    @Override
    public String toString()
    {
        return integer + "." + fraction;
    }
    
}

/* End of file. */