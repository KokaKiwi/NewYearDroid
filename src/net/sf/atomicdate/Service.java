/*
 * Service.java
 * 
 * 2008/11/02 - [AP] class created.
 * 
 * Copyright (C) 2008 by Arménio Pinto
 * Read license.txt for details.
 */

package net.sf.atomicdate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An SNTP service bean, suitable for POJO containers. The service can be
 * manually or automatically (in fixed intervals) synchronized with the SNTP
 * server. By default, the service starts in manual synchronization mode.
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public class Service extends TimerTask
{
    
    // Instance attributes.
    // ***********************************************************************
    
    /** The SNTP server host address. */
    private InetAddress host;
    
    /** The SNTP server port. */
    private int         port;
    
    /** The automatic synchronization timer. */
    private Timer       timer;
    
    /** The SNTP client. */
    private Client      client;
    
    /** The local time offset to the network time (in milliseconds). */
    private long        offset;
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Default constructor. The default SNTP server address is used.
     * 
     * @throws UnknownHostException
     *             if the default SNMP server host address is unknown.
     * @throws SocketException
     *             if an error occurs while creating the SNTP client.
     * @see Date#DEFAULT_SERVER_ADDRESS
     * @see Client#DEFAULT_SNTP_PORT
     */
    public Service() throws SocketException
    {
        host = null;
        setServerPort(Client.DEFAULT_SNTP_PORT);
        client = new Client();
        offset = -1;
        setSyncPeriod(0);
    }
    
    /**
     * Closes the service, releasing all the held resources.
     */
    public void close()
    {
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
        if (client != null)
        {
            client.close();
            client = null;
        }
    }
    
    /**
     * Configures the SNTP server host address.
     * 
     * @param host
     *            the new host address.
     * @throws NullPointerException
     *             if the argument is null.
     * @throws UnknownHostException
     *             if the host is unknown.
     */
    public void setServerHost(final String host) throws UnknownHostException
    {
        if (host == null)
        {
            throw new NullPointerException("host=null");
        }
        this.host = InetAddress.getByName(host);
    }
    
    /**
     * Configures the SNTP server port.
     * 
     * @param port
     *            the new port.
     * @throws IllegalArgumentException
     *             if the port is invalid.
     */
    public void setServerPort(final int port)
    {
        if (port <= 0)
        {
            throw new NullPointerException("port<=0");
        }
        this.port = port;
    }
    
    /**
     * Configures the automatic synchronization period. A value of 0 deactivates
     * the automatic synchronization feature.
     * 
     * @param period
     *            the new period.
     * @throws IllegalArgumentException
     *             if the argument is negative.
     */
    public synchronized void setSyncPeriod(final long period)
    {
        if (period < 0)
        {
            throw new IllegalArgumentException("period<0");
        }
        
        this.cancel();
        if (period != 0)
        {
            if (timer != null)
            {
                timer = new Timer();
            }
            timer.schedule(this, period, period);
        }
        else
        {
            if (timer != null)
            {
                timer.cancel();
                timer = null;
            }
        }
    }
    
    /**
     * Returns the network time.
     * 
     * @return the network time (in milliseconds).
     * @throws IllegalStateException
     *             if the service wasn't synchronized yet.
     * @throws IOException
     */
    public long getTime() throws IOException
    {
        if (offset == -1)
        {
            throw new IllegalStateException("Not synchronized.");
        }
        
        return System.currentTimeMillis() + offset;
    }
    
    /**
     * Return the network date.
     * 
     * @author Koka El Kiwi
     * @return
     * @throws IOException
     */
    public java.util.Date getDate() throws IOException
    {
        return new java.util.Date(getTime());
    }
    
    // See TimerTask for details.
    @Override
    public void run()
    {
        try
        {
            offset = client.getOffset(host, port);
        }
        catch (final IOException ioe)
        {
            System.err
                    .println("AtomicDate: error synchronizing the SNTP service.");
            ioe.printStackTrace(System.err);
        }
    }
    
}

/* End of file. */