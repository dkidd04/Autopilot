package com.citigroup.liquifi.autopilot.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public abstract class ClientSocket implements Runnable {
	private String host;
	private Integer port;
    private String outbound;
    private String inbound;
    private SocketChannel channel = null;
	private Selector selector;
	private boolean connected = false;
	
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public String getOutbound() {
		return outbound;
	}
	public void setOutbound(String outbound) {
		this.outbound = outbound;
	}
	public String getInbound() {
		return inbound;
	}
	public void setInbound(String inbound) {
		this.inbound = inbound;
	}
    public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public void connect() {
		try {
			selector = Selector.open();
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			InetSocketAddress isa = new InetSocketAddress(host,port);
			channel.connect(isa);
			channel.register(selector, SelectionKey.OP_CONNECT);
			
			while(selector.select() > 0 ){
				Iterator<SelectionKey> readyItor = selector.selectedKeys().iterator();
				
				while(readyItor.hasNext()){
					SelectionKey key = readyItor.next();
					readyItor.remove();
				
					if(key.isConnectable()){
						SocketChannel keyChannel = (SocketChannel)key.channel();
						if(keyChannel.isConnectionPending()){
							keyChannel.finishConnect();
							System.out.println("Connection established with server");
							setup();
							connected = true;
						}
                      
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			
		}
	}
	
	public void run (){
		try {
			channel.register(selector, SelectionKey.OP_READ );
			
			while(selector.select() > 0 && connected){
				Iterator<SelectionKey> readyItor = selector.selectedKeys().iterator();
				
				while(readyItor.hasNext() && connected){
					SelectionKey key = readyItor.next();
					readyItor.remove();
				
					if (key.isReadable()){
						SocketChannel keyChannel = (SocketChannel)key.channel();
				        ByteBuffer byteBuffer = ByteBuffer.allocate( 1024 );
				        keyChannel.read(byteBuffer);
				        byteBuffer.flip();
						Charset charset = Charset.forName( "us-ascii" );
						CharsetDecoder decoder = charset.newDecoder();
						CharBuffer charBuffer = decoder.decode( byteBuffer );
						
						String[] msgs = charBuffer.toString().split("\n");
						for(int i = 0; i < msgs.length; i++) {
							receieve(msgs[i]);
						}
					}
				}
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			System.out.println("Channel open "+channel.isOpen());
			disconnect();
			System.out.println("Channel open "+channel.isOpen());
		} catch(Exception e) {
			System.out.println("Unable to close channel, possibly already closed)");
			e.printStackTrace();
		}
	}
	
	public void send(String message){
		System.out.println("SENDING "+message);
		try {
			ByteBuffer buf = ByteBuffer.wrap((message+"\n").getBytes());
			channel.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect(){
		if (channel.isOpen())
			try {
				connected = false;
				channel.close();
				System.out.println("Connection terminated with server");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public boolean isSocketFor(String name) {
		return (name.equals(inbound) || name.equals(outbound));
	}
	
	public abstract void setup();
	public abstract void receieve(String msg);
}
