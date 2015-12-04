/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package progress.message.jclient;

import java.io.Serializable;
import java.util.Hashtable;
import javax.jms.Destination;
import javax.jms.JMSException;

// Referenced classes of package progress.message.jclient:
//            Channel

public interface Message
    extends javax.jms.Message, Serializable
{

    public abstract void acknowledgeAndForward(Destination destination, int i, int j, long l)
        throws JMSException;

    public abstract void acknowledgeAndForward(Destination destination)
        throws JMSException;

    public abstract Channel getChannel()
        throws JMSException;

    public abstract void setChannel(Channel channel)
        throws JMSException;

    public abstract void setChannel(Message message)
        throws JMSException;

    public abstract boolean hasChannel()
        throws JMSException;

    public abstract int getBodySize()
        throws JMSException;

    public abstract Hashtable getProperties();

    public abstract boolean isDestinationProperty(String s);

    public abstract Destination getDestinationProperty(String s)
        throws JMSException;

    public abstract void setDestinationProperty(String s, Destination destination)
        throws JMSException;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\perforce\liquifi\liquifi\mex\1.0\mainline\LiqFiFramework\lib\sonic_Client.jar
	Total time: 125 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/