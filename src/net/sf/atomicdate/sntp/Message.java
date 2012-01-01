/*
 * Message.java
 * 
 * 2003/03/02 - [AP] class created.
 * 2003/03/18 - [AP] attribute defining the maximum message size.
 *              [AP] documentation updated.
 * 2008/04/05 - [AP] code revision.
 * 2008/11/24 - [AP] proper timestamp representation.
 * 
 * Copyright 2003-2008 (C) by Arménio Pinto
 * Read license.txt for more details.
 */

package net.sf.atomicdate.sntp;

import java.util.Arrays;

/**
 * A Simple Network Time Protocol (SNTP) message.
 * <p>
 * <ul>
 * <li>NTP authentication scheme isn't supported.</li>
 * <li>Please refer to IETF RFC 2030 for more information.</li>
 * </ul>
 * 
 * For strict adherence to the RFC, the time values are represented in seconds.
 * 
 * @author Arm&eacute;nio Pinto (armenio[at]users.sourceforge.net)
 */
public final class Message
{
    
    // Class attributes.
    // **************************************************************************
    
    /** Leap Indicator: no warning. */
    public static final byte       LI_NO_WARN                   = 0x00;
    /** Leap Indicator: last minute has 61 seconds. */
    public static final byte       LI_61_SECS                   = 0x01;
    /** Leap Indicator: last minute has 59 seconds. */
    public static final byte       LI_59_SECS                   = 0x01;
    /** Leap Indicator: alarm condition. */
    public static final byte       LI_ALARM                     = 0x02;
    
    /** Version Number: v1 */
    public static final byte       VN_1                         = 0x01;
    /** Version Number: v2 */
    public static final byte       VN_2                         = 0x02;
    /** Version Number: v3 */
    public static final byte       VN_3                         = 0x03;
    /** Version Number: v4 */
    public static final byte       VN_4                         = 0x04;
    
    /** Mode: reserved. */
    public static final byte       MODE_RESERVED                = 0x00;
    /** Mode: symmetric active. */
    public static final byte       MODE_SYM_ACTIVE              = 0x01;
    /** Mode: symmetric passive. */
    public static final byte       MODE_SYM_PASSIVE             = 0x02;
    /** Mode: client. */
    public static final byte       MODE_CLIENT                  = 0x03;
    /** Mode: server. */
    public static final byte       MODE_SERVER                  = 0x04;
    /** Mode: broadcast. */
    public static final byte       MODE_BROADCAST               = 0x05;
    /** Mode: reserved for NTP control message. */
    public static final byte       MODE_RESERVED_NTP            = 0x06;
    /** Mode: reserved for private use. */
    public static final byte       MODE_RESERVED_PRIVATE        = 0x07;
    
    /** Stratum: unspecified. */
    public static final byte       STRATUM_UNSPECIFIED          = 0x00;
    /** Stratum: primary reference. */
    public static final byte       STRATUM_PRIMARY              = 0x01;
    
    /** Maximum message length (in bytes). */
    public static final int        MAXIMUM_LENGTH               = 384;              // without
                                                                                     // authentication.
                                                                                     
    // Default values:
    private static final byte      DEFAULT_POLL_INTERVAL        = 0;
    private static final byte      DEFAULT_PRECISION            = 0;
    private static final double    DEFAULT_ROOT_DELAY           = 0.0F;
    private static final double    DEFAULT_ROOT_DISPERSION      = 0.0F;
    private static final byte[]    DEFAULT_REFERENCE_IDENTIFIER = "LOCL".getBytes();
    private static final Timestamp DEFAULT_REFERENCE_TIMESTAMP  = Timestamp.ZERO;
    private static final Timestamp DEFAULT_ORIGINATE_TIMESTAMP  = Timestamp.ZERO;
    private static final Timestamp DEFAULT_RECEIVE_TIMESTAMP    = Timestamp.ZERO;
    private static final Timestamp DEFAULT_TRANSMIT_TIMESTAMP   = Timestamp.ZERO;
    
    // Instance attributes.
    // ***********************************************************************
    
    /** Leap Indicator. */
    private byte                   byLeapIndicator;
    
    /** Version Number. */
    private byte                   byVersionNumber;
    
    /** Mode. */
    private byte                   byMode;
    
    /** Stratum. */
    private byte                   byStratum;
    
    /** Poll Interval. */
    private byte                   byPollInterval;
    
    /** Precision. */
    private byte                   byPrecision;
    
    /** Rood Delay. */
    private double                 dRootDelay;
    
    /** Root Dispersion. */
    private double                 dRootDispersion;
    
    /** Reference Identifier. */
    private byte[]                 sReferenceIdentifier;
    
    /** Reference Timestamp. */
    private Timestamp              tReferenceTimestamp;
    
    /** Originate Timestamp. */
    private Timestamp              tOriginateTimestamp;
    
    /** Receive Timestamp. */
    private Timestamp              tReceiveTimestamp;
    
    /** Transmit Timestamp. */
    private Timestamp              tTransmitTimestamp;
    
    // Instance methods.
    // **************************************************************************
    
    /**
     * Default constructor. Builds an SNTP message ready for client mode
     * operation.
     */
    public Message()
    {
        // Values default to client mode:
        setLeapIndicator(LI_NO_WARN);
        setVersionNumber(VN_4);
        setMode(MODE_CLIENT);
        setStratum(STRATUM_UNSPECIFIED);
        setPollInterval(DEFAULT_POLL_INTERVAL);
        setPrecision(DEFAULT_PRECISION);
        setRootDelay(DEFAULT_ROOT_DELAY);
        setRootDispersion(DEFAULT_ROOT_DISPERSION);
        setReferenceIdentifier(DEFAULT_REFERENCE_IDENTIFIER);
        setReferenceTimestamp(DEFAULT_REFERENCE_TIMESTAMP);
        setOriginateTimestamp(DEFAULT_ORIGINATE_TIMESTAMP);
        setReceiveTimestamp(DEFAULT_RECEIVE_TIMESTAMP);
        setTransmitTimestamp(DEFAULT_TRANSMIT_TIMESTAMP);
    }
    
    /**
     * Returns the Leap Indicator.
     * 
     * @return the Leap Indicator.
     */
    public byte getLeapIndicator()
    {
        return byLeapIndicator;
    }
    
    /**
     * Sets the Leap Indicator.
     * 
     * @param byLeapIndicator
     *            the Leap Indicator.
     */
    public void setLeapIndicator(final byte byLeapIndicator)
    {
        this.byLeapIndicator = byLeapIndicator;
    }
    
    /**
     * Returns the Version Number.
     * 
     * @return the Version Number.
     */
    public byte getVersionNumber()
    {
        return byVersionNumber;
    }
    
    /**
     * Sets the Version Number.
     * 
     * @param byVersionNumber
     *            the Version Number.
     */
    public void setVersionNumber(final byte byVersionNumber)
    {
        this.byVersionNumber = byVersionNumber;
    }
    
    /**
     * Returns the Mode.
     * 
     * @return the Mode.
     */
    public byte getMode()
    {
        return byMode;
    }
    
    /**
     * Sets the Mode.
     * 
     * @param byMode
     *            the Mode.
     */
    public void setMode(final byte byMode)
    {
        this.byMode = byMode;
    }
    
    /**
     * Returns the Stratum.
     * 
     * @return the Stratum.
     */
    public byte getStratum()
    {
        return byStratum;
    }
    
    /**
     * Sets the Stratum.
     * 
     * @param byStratum
     *            the Stratum.
     */
    public void setStratum(final byte byStratum)
    {
        this.byStratum = byStratum;
    }
    
    /**
     * Returns the Poll Interval.
     * 
     * @return the Poll Interval.
     */
    public byte getPollInterval()
    {
        return byPollInterval;
    }
    
    /**
     * Sets the Poll Interval.
     * 
     * @param byPollInterval
     *            the Poll Interval.
     */
    public void setPollInterval(final byte byPollInterval)
    {
        this.byPollInterval = byPollInterval;
    }
    
    /**
     * Returns the Precision.
     * 
     * @return the Precision.
     */
    public byte getPrecision()
    {
        return byPrecision;
    }
    
    /**
     * Sets the Precision.
     * 
     * @param byPrecision
     *            the Precision.
     */
    public void setPrecision(final byte byPrecision)
    {
        this.byPrecision = byPrecision;
    }
    
    /**
     * Returns the Root Delay.
     * 
     * @return the Root Delay.
     */
    public double getRootDelay()
    {
        return dRootDelay;
    }
    
    /**
     * Sets the Root Delay.
     * 
     * @param dRootDelay
     *            the Root Delay.
     */
    public void setRootDelay(final double dRootDelay)
    {
        this.dRootDelay = dRootDelay;
    }
    
    /**
     * Returns the Root Dispersion.
     * 
     * @return the Root Dispersion.
     */
    public double getRootDispersion()
    {
        return dRootDispersion;
    }
    
    /**
     * Sets the Root Dispersion.
     * 
     * @param dRootDispersion
     *            the Root Dispersion.
     */
    public void setRootDispersion(final double dRootDispersion)
    {
        this.dRootDispersion = dRootDispersion;
    }
    
    /**
     * Returns the Reference Identifier.
     * 
     * @return the Reference Identifier.
     */
    public byte[] getReferenceIdentifier()
    {
        return sReferenceIdentifier;
    }
    
    /**
     * Sets the Reference Identifier.
     * 
     * @param sReferenceIdentifier
     *            the Reference Identifier.
     */
    public void setReferenceIdentifier(final byte[] sReferenceIdentifier)
    {
        this.sReferenceIdentifier = sReferenceIdentifier;
    }
    
    /**
     * Returns the Reference Timestamp.
     * 
     * @return the Reference Timestamp.
     */
    public Timestamp getReferenceTimestamp()
    {
        return tReferenceTimestamp;
    }
    
    /**
     * Sets the Reference Timestamp.
     * 
     * @param tReferenceTimestamp
     *            the Reference Timestamp.
     */
    public void setReferenceTimestamp(final Timestamp tReferenceTimestamp)
    {
        this.tReferenceTimestamp = tReferenceTimestamp;
    }
    
    /**
     * Returns the Originate Timestamp.
     * 
     * @return the Originate Timestamp.
     */
    public Timestamp getOriginateTimestamp()
    {
        return tOriginateTimestamp;
    }
    
    /**
     * Sets the Originate Timestamp.
     * 
     * @param tOriginateTimestamp
     *            the Originate Timestamp.
     */
    public void setOriginateTimestamp(final Timestamp tOriginateTimestamp)
    {
        this.tOriginateTimestamp = tOriginateTimestamp;
    }
    
    /**
     * Returns the Receive Timestamp.
     * 
     * @return the Receive Timestamp.
     */
    public Timestamp getReceiveTimestamp()
    {
        return tReceiveTimestamp;
    }
    
    /**
     * Sets the Receive Timestamp.
     * 
     * @param tReceiveTimestamp
     *            the Receive Timestamp.
     */
    public void setReceiveTimestamp(final Timestamp tReceiveTimestamp)
    {
        this.tReceiveTimestamp = tReceiveTimestamp;
    }
    
    /**
     * Returns the Transmit Timestamp.
     * 
     * @return the Transmit Timestamp.
     */
    public Timestamp getTransmitTimestamp()
    {
        return tTransmitTimestamp;
    }
    
    /**
     * Sets the Transmit Timestamp.
     * 
     * @param tTransmitTimestamp
     *            the Transmit Timestamp.
     */
    public void setTransmitTimestamp(final Timestamp tTransmitTimestamp)
    {
        this.tTransmitTimestamp = tTransmitTimestamp;
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
            if (obj != null && obj instanceof Message)
            {
                final Message other = (Message) obj;
                equals = other.byLeapIndicator == byLeapIndicator
                        && other.byVersionNumber == byVersionNumber
                        && other.byMode == byMode
                        && other.byStratum == byStratum
                        && other.byPollInterval == byPollInterval
                        && other.byPrecision == byPrecision
                        && other.dRootDelay == dRootDelay
                        && other.dRootDispersion == dRootDispersion
                        && Arrays.equals(other.sReferenceIdentifier,
                                sReferenceIdentifier)
                        && other.tReferenceTimestamp
                                .equals(tReferenceTimestamp)
                        && other.tOriginateTimestamp
                                .equals(tOriginateTimestamp)
                        && other.tReceiveTimestamp.equals(tReceiveTimestamp)
                        && other.tTransmitTimestamp.equals(tTransmitTimestamp);
            }
        }
        
        return equals;
    }
    
    // See Object for details.
    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("LeapIndicator=").append(byLeapIndicator).append(", ");
        sb.append("VersionNumber=").append(byVersionNumber).append(", ");
        sb.append("Mode=").append(byMode).append(", ");
        sb.append("Stratum=").append(byStratum).append(", ");
        sb.append("PollInterval=").append(byPollInterval).append(", ");
        sb.append("Precision=").append(byPrecision).append(", ");
        sb.append("RootDelay=").append(dRootDelay).append(", ");
        sb.append("RootDispersion=").append(dRootDispersion).append(", ");
        sb.append("ReferenceIdentifier=")
                .append(new String(sReferenceIdentifier)).append(", ");
        sb.append("ReferenceTimestamp=").append(tReferenceTimestamp)
                .append(", ");
        sb.append("OriginateTimestamp=").append(tOriginateTimestamp)
                .append(", ");
        sb.append("ReceiveTimetamp=").append(tReceiveTimestamp).append(", ");
        sb.append("TransmitTimestamp=").append(tTransmitTimestamp);
        
        return sb.toString();
    }
    
}

/* End of file. */