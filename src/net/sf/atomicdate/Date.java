/*
 * Date.java
 * 
 * 2008/04/05 - [AP] class created.
 * 2008/11/07 - [AP] refactoring.
 * 
 * Copyright (C) 2008 by Arménio Pinto
 * Read license.txt for details.
 */

package net.sf.atomicdate;

import java.io.IOException;
import java.net.InetAddress;

/**
 * An extension to {@link java.util.Date} with SNTP support.
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public class Date extends java.util.Date
{
    
    // Class attributes.
    // **************************************************************************
    
    /** The class version for serialization purposes. */
    private static final long  serialVersionUID        = 1064411106013132402L;
    
    /**
     * The SNTP server address JVM property name. The value must be in form
     * <code>host:port</code>.
     */
    public static final String SERVER_ADDRESS_PROPERTY = "atomicdate.server.address";
    
    /** The default SNTP server address. */
    public static final String DEFAULT_SERVER_ADDRESS  = "time.nist.gov";
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Constructs a new date synchronized with the default SNTP server.
     * 
     * <ul>
     * <li>By default, the server address is specified by the JVM property
     * {@link #SERVER_ADDRESS_PROPERTY atomicdate.server.address}.</li>
     * <li>If the JVM property is not specified, the
     * {@link #DEFAULT_SERVER_ADDRESS default SNTP server address} is used
     * instead.</li>
     * <li>If any error occurs, the current JVM date is used instead are the
     * error details and outputed to <code>System.err</code>.</li>
     * </ul>
     * 
     * @see java.util.Date#Date()
     * @see #SERVER_ADDRESS_PROPERTY
     * @see #DEFAULT_SERVER_ADDRESS
     */
    public Date()
    {
        super();
        try
        {
            synchronize();
        }
        catch (final Throwable t)
        {
            System.err.println("AtomicDate: error synchronizing."
                    + " Details: " + t.getClass().getName() + ": "
                    + t.getMessage());
            super.setTime(new java.util.Date().getTime());
        }
    }
    
    /**
     * Constructs a new date synchronized with a given SNTP server. The default
     * port is used.
     * 
     * @param haddr
     *            the server host address.
     * @throws NullPointerException
     *             if the argument is null.
     * @throws IOException
     *             if an error occurs while contacting the server.
     * @see Client#DEFAULT_SNTP_PORT
     */
    public Date(final String haddr) throws IOException
    {
        this(haddr, Client.DEFAULT_SNTP_PORT);
    }
    
    /**
     * Constructs a new date synchronized with a given SNTP server.
     * 
     * @param haddr
     *            the server host address (IP or DNS).
     * @param port
     *            the server port.
     * @throws IllegalArgumentException
     *             if the port in invalid.
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    public Date(final String haddr, final int port) throws IOException
    {
        this(InetAddress.getByName(haddr), port);
    }
    
    /**
     * Constructs a new date synchronized with a given SNTP server. The default
     * port is used.
     * 
     * @param raddr
     *            the server host address.
     * @throws NullPointerException
     *             if the argument is null.
     * @throws IOException
     *             if an error occurs while contacting the server.
     * @see Client#DEFAULT_SNTP_PORT
     */
    public Date(final InetAddress raddr) throws IOException
    {
        this(raddr, Client.DEFAULT_SNTP_PORT);
    }
    
    /**
     * Constructs a new date synchronized with a given SNTP server.
     * 
     * @param addr
     *            the server host address.
     * @param port
     *            the server port.
     * @throws NullPointerException
     *             if the address is null.
     * @throws IllegalArgumentException
     *             if the port in invalid.
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    public Date(final InetAddress addr, final int port) throws IOException
    {
        super();
        synchronize(addr, port);
    }
    
    // Helper methods.
    // ****************************************************************************
    
    /**
     * Synchronizes the date with the default SNTP server.
     * 
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    public void synchronize() throws IOException
    {
        // Note: I don't use the split() method here because it only exists
        // since Java 1.4.
        final String p = System.getProperty(SERVER_ADDRESS_PROPERTY,
                DEFAULT_SERVER_ADDRESS);
        final int idx = p.indexOf(":");
        final String host;
        final int port;
        if (idx != -1)
        {
            host = p.substring(0, idx);
            try
            {
                port = Integer.parseInt(p.substring(idx + 1));
            }
            catch (final NumberFormatException nfe)
            {
                throw new RuntimeException("Invalid default SNTP server port.");
            }
        }
        else
        {
            host = p;
            port = Client.DEFAULT_SNTP_PORT;
        }
        
        synchronize(InetAddress.getByName(host), port);
    }
    
    /**
     * Synchronizes the date with an SNTP server.
     * 
     * @param addr
     *            the server host address.
     * @param port
     *            the server port.
     * @throws NullPointerException
     *             if the address is null.
     * @throws IllegalArgumentException
     *             if the port is invalid.
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    public void synchronize(final InetAddress addr, final int port)
            throws IOException
    {
        Client client = null;
        try
        {
            client = new Client();
            System.out.println(client.getOffset(addr, port));
            super.setTime(System.currentTimeMillis() + client.getOffset(addr));
        }
        finally
        {
            if (client != null)
            {
                client.close();
            }
        }
    }
    
}

/* End of file. */