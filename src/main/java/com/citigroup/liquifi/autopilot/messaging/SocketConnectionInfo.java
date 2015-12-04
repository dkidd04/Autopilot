package com.citigroup.liquifi.autopilot.messaging;

import java.nio.channels.SocketChannel;

public class SocketConnectionInfo {
	private String host;
	private String port;
	private String socketID;
    private SocketChannel channel;

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getSocketID() {
		return socketID;
	}
	public void setSocketID(String socketID) {
		this.socketID = socketID;
	}
	
	
	
}
