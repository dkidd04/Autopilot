package com.citigroup.liquifi.autopilot.socket;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketWriter {

    public static void send(String socketID, String message) {
        SocketChannel channel = ApplicationContext.getSocketFactory().getSocketConnByID(socketID).getChannel();
        ByteBuffer buf = ByteBuffer.wrap(message.getBytes());
        try {
            channel.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
