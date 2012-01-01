/*
 * Codec.java
 * 
 * 2003/03/10 - [AP] class created.
 * 2008/04/05 - [AP] code revision.
 * 2008/11/24 - [AP] fix for the timestamp and fixed-point mess.
 * 
 * Copyright 2003-2008 (C) by Arménio Pinto
 * Read license.txt for more details.
 */

package net.sf.atomicdate.sntp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A SNTP message encoder/decoder.
 * <p>
 * <ul>
 * <li>SNTP message bits are in big-endian format.</li>
 * <li>Please refer to IETF RFC 2030 for more information.</li>
 * </ul>
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public final class Codec
{
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Encodes an SNTP message to a byte stream.
     * 
     * @param message
     *            the message.
     * @param output
     *            the byte stream.
     */
    public static void encodeMessage(final Message message,
            final OutputStream output) throws IOException
    {
        byte flags = (byte) (message.getLeapIndicator() << 6);
        flags += (byte) (message.getVersionNumber() << 3);
        flags += message.getMode();
        output.write(flags);
        output.write(message.getStratum());
        output.write(message.getPollInterval());
        output.write(message.getPrecision());
        encodeFixedPoint(message.getRootDelay(), output);
        encodeFixedPoint(message.getRootDispersion(), output);
        encodeBitstring(message.getReferenceIdentifier(), output);
        encodeTimestamp(message.getReferenceTimestamp(), output);
        encodeTimestamp(message.getOriginateTimestamp(), output);
        encodeTimestamp(message.getReceiveTimestamp(), output);
        encodeTimestamp(message.getTransmitTimestamp(), output);
    }
    
    /**
     * Decodes an SNTP message from a byte stream.
     * 
     * @param input
     *            the byte stream.
     * @return the message.
     */
    public static Message decodeMessage(final InputStream input)
            throws IOException
    {
        final Message message = new Message();
        final byte flags = (byte) input.read();
        message.setLeapIndicator((byte) (flags >> 6));
        message.setVersionNumber((byte) (flags >> 3 & 0x07));
        message.setMode((byte) (flags & 0x07));
        message.setStratum((byte) input.read());
        message.setPollInterval((byte) input.read());
        message.setPrecision((byte) input.read());
        message.setRootDelay(decodeFixedPoint(input));
        message.setRootDispersion(decodeFixedPoint(input));
        message.setReferenceIdentifier(decodeBitstring(input));
        message.setReferenceTimestamp(decodeTimestamp(input));
        message.setOriginateTimestamp(decodeTimestamp(input));
        message.setReceiveTimestamp(decodeTimestamp(input));
        message.setTransmitTimestamp(decodeTimestamp(input));
        
        return message;
    }
    
    // Helper methods.
    // ****************************************************************************
    
    /**
     * Encodes a 32 bit number to a byte stream.
     * 
     * @param number
     *            the number to encode.
     * @param output
     *            the byte stream.
     * @throws IOException
     *             if an error occurs while writting to the stream.
     */
    protected static void encode32(final long number, final OutputStream output)
            throws IOException
    {
        for (int i = 3; i >= 0; i--)
        {
            output.write((int) (number >> 8 * i & 0xFF));
        }
    }
    
    /**
     * Decodes a 32 bit number from a byte stream.
     * 
     * @param input
     *            the byte stream.
     * @return the decoded number.
     * @throws IOException
     *             if an error occurs while reading from the stream.
     */
    protected static long decode32(final InputStream input) throws IOException
    {
        long number = 0;
        for (int i = 0; i < 4; i++)
        {
            number = (number << 8) + input.read();
        }
        
        return number;
    }
    
    /**
     * Encodes a 32-bit bitstring to a byte stream.
     * 
     * @param bitstring
     *            the bitstring to encode.
     * @param output
     *            the byte stream.
     * @throws IOException
     *             if an error occurs while writting to the stream.
     */
    protected static void encodeBitstring(final byte[] bitstring,
            final OutputStream output) throws IOException
    {
        final byte[] temp = { 0, 0, 0, 0 };
        System.arraycopy(bitstring, 0, temp, 0, bitstring.length);
        output.write(temp);
    }
    
    /**
     * Decodes a 32-bit bitstring from a byte stream.
     * 
     * @param input
     *            the byte stream.
     * @return the decoded string.
     * @throws IOException
     *             if an error occurs while reading from the stream.
     */
    protected static byte[] decodeBitstring(final InputStream input)
            throws IOException
    {
        final byte[] bitstring = new byte[4];
        input.read(bitstring, 0, 4);
        
        return bitstring;
    }
    
    /**
     * Encodes a 32 bit fixed-point number to a byte stream.
     * 
     * @param number
     *            the fixed-point number to encode.
     * @param output
     *            the byte stream.
     * @throws IOException
     *             if an error occurs while writting to the stream.
     */
    protected static void encodeFixedPoint(final double number,
            final OutputStream output) throws IOException
    {
        encode32((long) (number * 0x10000L), output);
    }
    
    /**
     * Decodes a 32 bit fixed-point number from a byte stream. The binary point
     * is between bits 15 and 16.
     * 
     * @param input
     *            the byte stream.
     * @return the decoded fixed-point number.
     * @throws IOException
     *             if an error occurs while reading from the stream.
     */
    protected static double decodeFixedPoint(final InputStream input)
            throws IOException
    {
        return (double) decode32(input) / 0x10000L;
    }
    
    /**
     * Encodes a timestamp to a byte stream.
     * 
     * @param timestamp
     *            the timestamp to encode.
     * @param output
     *            the byte stream.
     * @throws IOException
     *             if an error occurs while writting to the stream.
     */
    protected static void encodeTimestamp(final Timestamp timestamp,
            final OutputStream output) throws IOException
    {
        encode32(timestamp.getInteger(), output);
        encode32(timestamp.getFraction(), output);
    }
    
    /**
     * Decodes a timestamp from a byte stream.
     * 
     * @param input
     *            the byte stream.
     * @return the decoded timestamp.
     * @throws IOException
     *             if an error occurs while reading from the stream.
     */
    protected static Timestamp decodeTimestamp(final InputStream input)
            throws IOException
    {
        final long integer = decode32(input);
        final long fraction = decode32(input);
        
        return new Timestamp(integer, fraction);
    }
    
}

/* End of file. */