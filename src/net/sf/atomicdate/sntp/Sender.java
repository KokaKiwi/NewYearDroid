/*
 * Sender.java
 * 
 * 2003/03/14 - [AP] class created.
 * 2008/04/05 - [AP] code revision.
 * 
 * Copyright 2003-2008 (C) by Arménio Pinto
 * Read license.txt for more details.
 */

package net.sf.atomicdate.sntp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * An SNTP message sender.
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public final class Sender
{
    
    // Instance attributes.
    // ***********************************************************************
    
    /** The UDP socket to use. */
    private final DatagramSocket socket;
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Constructor.
     * 
     * @param socket
     *            the UDP socket to use.
     * @throws NullPointerException
     *             if the argument is null.
     */
    public Sender(final DatagramSocket socket)
    {
        if (socket == null)
        {
            throw new NullPointerException("socket=null");
        }
        this.socket = socket;
    }
    
    /**
     * Sends an SNTP message to a server.
     * 
     * @param message
     *            the message.
     * @param addr
     *            the server host address.
     * @param port
     *            the server port.
     * @throws NullPointerException
     *             if any argument is null.
     * @throws IllegalArgumentException
     *             if the port is invalid.
     * @throws IOException
     *             if an error occurs while contacting the server.
     */
    public void send(final Message message, final InetAddress addr,
            final int port) throws IOException
    {
        if (message == null)
        {
            throw new NullPointerException("message=null");
        }
        if (addr == null)
        {
            throw new NullPointerException("addr=null");
        }
        if (port < 0)
        {
            throw new IllegalArgumentException("port<0");
        }
        
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        Codec.encodeMessage(message, output);
        final byte[] data = output.toByteArray();
        output.close();
        
        final DatagramPacket packet = new DatagramPacket(data, data.length,
                addr, port);
        socket.send(packet);
    }
    
}

/* End of file. */