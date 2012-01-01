/*
 * Listener.java
 * 
 * 2003/03/14 - [AP] class created.
 * 2008/04/05 - [AP] code revision.
 * 2008/11/05 - [AP] support for the local receive timestamp.
 * 
 * Copyright 2003-2008 (C) by Arménio Pinto
 * Read license.txt for more details.
 */

package net.sf.atomicdate.sntp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * An SNTP message listener. The method {@link #onMessage(Message, long)} is
 * invoked when a message is received.
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public abstract class Listener extends Thread
{
    
    // Class attributes.
    // **************************************************************************
    
    /** The listener thread name. */
    private static final String  THREAD_NAME = "AtomicDate";
    
    // Instance attributes.
    // ***********************************************************************
    
    /** The listening UDP socket. */
    private final DatagramSocket socket;
    
    /** The listening flag. */
    private boolean              listening;
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Constructor.
     * 
     * @param socket
     *            the listening UDP socket.
     * @throws NullPointerException
     *             if the argument is null.
     */
    public Listener(final DatagramSocket socket)
    {
        if (socket == null)
        {
            throw new NullPointerException("socket=null");
        }
        this.socket = socket;
        super.setName(THREAD_NAME + "-" + socket.getPort());
        listening = false;
    }
    
    /**
     * Returns the listening UDP socket.
     * 
     * @return the listening UDP socket.
     */
    protected DatagramSocket getSocket()
    {
        return socket;
    }
    
    /**
     * Indicates if the listener is listening!
     * 
     * @return true if it's listening, otherwise false.
     */
    public boolean isListening()
    {
        return listening;
    }
    
    /**
     * Starts the reception of SNTP messages.
     */
    public void startListening()
    {
        listening = true;
        super.start();
    }
    
    /**
     * Gracefully stops the reception of SNTP messages.
     */
    public void stopListening()
    {
        listening = false;
    }
    
    /**
     * Service method.
     */
    @Override
    public void run()
    {
        while (listening)
        {
            try
            {
                final DatagramPacket packet = new DatagramPacket(
                        new byte[Message.MAXIMUM_LENGTH],
                        Message.MAXIMUM_LENGTH);
                socket.receive(packet);
                final long time = System.currentTimeMillis();
                onMessage(Codec.decodeMessage(new ByteArrayInputStream(packet
                        .getData())), time);
                
            }
            catch (final IOException ioe)
            {
                if (isListening())
                {
                    System.err
                            .println("AtomicDate: error receiving a message.");
                    ioe.printStackTrace(System.err);
                }
            }
        }
    }
    
    /**
     * This method is called everytime a message is received.
     * 
     * @param message
     *            the received message.
     * @param time
     *            the value of {@link System#currentTimeMillis()} when the
     *            message was received.
     */
    protected abstract void onMessage(Message message, long time);
    
}

/* End of file. */