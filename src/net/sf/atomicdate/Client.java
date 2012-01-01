/*
 * Client.java
 * 
 * 2004/01/18 - [AP] class created.
 * 2008/04/05 - [AP] code revision.
 * 
 * Copyright 2004-2008 (C) by Arménio Pinto
 * Read license.txt for details.
 */

package net.sf.atomicdate;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import net.sf.atomicdate.sntp.Listener;
import net.sf.atomicdate.sntp.Message;
import net.sf.atomicdate.sntp.Sender;
import net.sf.atomicdate.sntp.Timestamp;

/**
 * An SNTP client.
 * <ul>
 * <li>The implementation is thread-safe but can only handle one request at a
 * time;</li>
 * <li>The returned values are the local time offset relative to the network
 * time.</li>
 * </ul>
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public class Client extends Listener
{
    
    // Class attributes.
    // **************************************************************************
    
    /** The default SNTP source and destination port. */
    public static final int     DEFAULT_SNTP_PORT = 123;
    
    /** The default server query timeout (in milliseconds). */
    public static final int     DEFAULT_TIMEOUT   = 10000;
    
    /**
     * The SNTP time is referenced to 01/01/1900-00:00. On the other hand, Unix
     * systems and Java reference time to 01/01/1970-00:00. This means that
     * convertion is necessary.
     */
    private static final long   SECS_1900_1970    = 2208988800L;
    
    // Instance attributes.
    // ***********************************************************************
    
    /** The received message holder. */
    private final MessageHolder holder;
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Default constructor.
     * 
     * @throws SocketException
     *             if an error occurs while creating the socket.
     */
    public Client() throws SocketException
    {
        this(DEFAULT_TIMEOUT);
    }
    
    /**
     * Constructor.
     * 
     * @param timeout
     *            the response timeout (in milliseconds).
     * @throws IllegalArgumentException
     *             if the timeout is invalid.
     * @throws SocketException
     *             if an error occurs while creating the socket.
     */
    public Client(final int timeout) throws SocketException
    {
        super(new DatagramSocket());
        if (timeout < 0)
        {
            throw new IllegalArgumentException("timeout<0");
        }
        if (timeout > 0)
        {
            super.getSocket().setSoTimeout(timeout);
        }
        holder = new MessageHolder();
        super.startListening();
    }
    
    /**
     * Releases the client resources.
     */
    public void close()
    {
        if (super.isListening())
        {
            super.stopListening();
        }
        super.getSocket().close();
    }
    
    /**
     * Retrieves the network time offset from an SNTP server. The default port
     * is used.
     * 
     * @param host
     *            the server host address (IP or DNS).
     * @return the network time offset (in milliseconds).
     * @throws NullPointerException
     *             if the argument is null.
     * @throws IllegalStateException
     *             if the client is closed.
     * @throws IOException
     *             if an error occurs while contacting the server.
     * @see #DEFAULT_SNTP_PORT
     */
    public long getOffset(final String host) throws IOException
    {
        return getOffset(host, DEFAULT_SNTP_PORT);
    }
    
    /**
     * Retrieves the network time offset from an SNTP server.
     * 
     * @param host
     *            the server host address (IP or DNS).
     * @param port
     *            the server port.
     * @return the network time offset (in milliseconds).
     * @throws NullPointerException
     *             if the address is null.
     * @throws IllegalArgumentException
     *             if the port is invalid.
     * @throws IllegalStateException
     *             if the client is closed.
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    public long getOffset(final String host, final int port) throws IOException
    {
        return getOffset(InetAddress.getByName(host), port);
    }
    
    /**
     * Retrieves the network time offset from an SNTP server. The default port
     * is used.
     * 
     * @param addr
     *            the server host address.
     * @return the network time offset (in milliseconds).
     * @throws NullPointerException
     *             if the argument is null.
     * @throws IllegalStateException
     *             if the client is closed.
     * @throws IOException
     *             if an error occurs while contacting the server.
     * @see #DEFAULT_SNTP_PORT
     */
    public long getOffset(final InetAddress addr) throws IOException
    {
        return getOffset(addr, DEFAULT_SNTP_PORT);
    }
    
    /**
     * Retrieves the network time offset from an SNTP server.
     * 
     * @param addr
     *            the server host address.
     * @param port
     *            the server port.
     * @return the network time offset (in milliseconds).
     * @throws NullPointerException
     *             if the address is null.
     * @throws IllegalArgumentException
     *             if the port is invalid.
     * @throws IllegalStateException
     *             if the client is closed.
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    public long getOffset(final InetAddress addr, final int port)
            throws IOException
    {
        return queryServer(addr, port);
    }
    
    /* See Listener for information. */
    @Override
    protected void onMessage(final Message message, final long time)
    {
        synchronized (holder)
        {
            holder.hold(message, time);
            holder.notify();
        }
    }
    
    // See Object for details.
    @Override
    protected void finalize() throws Throwable
    {
        close();
    }
    
    // See Object for details.
    @Override
    public String toString()
    {
        final DatagramSocket socket = super.getSocket();
        return socket.getInetAddress() + ":" + socket.getPort();
    }
    
    // Helper methods.
    // ****************************************************************************
    
    /**
     * Queries the SNTP server for the network time offset.
     * 
     * @param addr
     *            the server host address.
     * @param port
     *            the server port.
     * @return the network time offset (in milliseconds).
     * @throws NullPointerException
     *             if the address is null.
     * @throws IllegalStateException
     *             if the client is closed.
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    private long queryServer(final InetAddress addr, final int port)
            throws IOException
    {
        if (addr == null)
        {
            throw new NullPointerException("addr=null");
        }
        if (!super.isListening())
        {
            throw new IllegalStateException("Client closed.");
        }
        
        final Sender sender = new Sender(super.getSocket());
        final Message smessage = new Message();
        smessage.setTransmitTimestamp(toTimestamp(System.currentTimeMillis()));
        
        final Message rmessage;
        final long t4;
        sender.send(smessage, addr, port);
        synchronized (holder)
        {
            try
            {
                holder.wait(DEFAULT_TIMEOUT);
            }
            catch (final InterruptedException ie)
            {
                ie.printStackTrace(System.err);
                throw new IOException("Error waiting for the server answer.");
            }
            t4 = holder.getTimestamp();
            if (!holder.isHolding())
            {
                throw new IOException("Timed-out while querying the server.");
            }
            rmessage = holder.getMessage();
        }
        
        // t1 - original timestamp.
        // t2 - receive timestamp.
        // t3 - transmit timstamp.
        // t4 - destination timestamp.
        // rtd=(t2-t1)+(t4-t3)
        // off=t3-(t4-delay/2)=(t3-t4)+(t2-t1)/2-(t3-t4)/2=((t2-t1)+(t3-t4))/2
        final long t1 = fromTimestamp(rmessage.getOriginateTimestamp());
        final long t2 = fromTimestamp(rmessage.getReceiveTimestamp());
        final long t3 = fromTimestamp(rmessage.getTransmitTimestamp());
        
        return (t2 - t1 + t3 - t4) / 2;
    }
    
    /**
     * Converts Java time to an SNTP timestamp.
     * 
     * @param time
     *            the Java time (in milliseconds).
     * @return the SNTP timestamp.
     */
    protected static Timestamp toTimestamp(final long time)
    {
        final double temp = time / 1000D;
        final long integer = (long) Math.floor(temp);
        final long fraction = (long) ((temp - integer) * 0x100000000L);
        
        return new Timestamp(integer + SECS_1900_1970, fraction);
    }
    
    /**
     * Converts an SNTP timestamp to Java time.
     * 
     * @param timestamp
     *            the timestamp.
     * @return the Java time (in milliseconds).
     */
    protected static long fromTimestamp(final Timestamp timestamp)
    {
        long time = (timestamp.getInteger() - SECS_1900_1970) * 1000L;
        time += timestamp.getFraction() * 1000L / 0x100000000L;
        
        return time;
    }
    
    // Inner classes.
    // *****************************************************************************
    
    /**
     * A holder for the received messages.
     * 
     * @author Arm&eacute;nio Pinto
     */
    private class MessageHolder
    {
        
        /** The received message. */
        private Message message;
        
        /** The receive timestamp. */
        private long    time;
        
        /**
         * Default constructor.
         */
        public MessageHolder()
        {
            release();
        }
        
        /**
         * Indicates if the instance is holding a message.
         * 
         * @return true if it's holding, otherwise false.
         */
        public boolean isHolding()
        {
            return message != null;
        }
        
        /**
         * Holds a received message.
         * 
         * @param message
         *            the received message.
         * @param time
         *            the message local receive time.
         */
        public void hold(final Message message, final long time)
        {
            this.message = message;
            this.time = time;
        }
        
        /**
         * Returns the received message.
         * 
         * @return the received message.
         */
        public Message getMessage()
        {
            return message;
        }
        
        /**
         * Returns the receive timestamp.
         * 
         * @return the receive timestamp.
         */
        public long getTimestamp()
        {
            return time;
        }
        
        /**
         * Releases the received message.
         */
        public void release()
        {
            message = null;
        }
        
    }
    
}

/* End of file. */