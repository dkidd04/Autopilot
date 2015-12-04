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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.controller.OutputMsgNotifier;


public class ClientNioSocket implements Runnable{
	private String server;
	private int port;
	private BlockingQueue<String> messageQueue;
	private Selector selector;
	private SocketChannel channel;
	private boolean stopThread;
	private Map<SocketChannel, String> socketIDMap = new HashMap<SocketChannel, String> ();
	
	public ClientNioSocket(String server, int port){
		this.server = server;
		this.port = port;
		messageQueue = new LinkedBlockingQueue<String>();
	}
	
	
	public boolean connect(String socketID) {
		try {
			selector = Selector.open();
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			InetSocketAddress isa = new InetSocketAddress(server,port);
			channel.connect(isa);
			channel.register(selector, SelectionKey.OP_CONNECT);
			
			while(selector.select() > 0 ){
				Set<?> readyKeys = selector.selectedKeys();
				Iterator<?> readyItor = readyKeys.iterator();
				
				while(readyItor.hasNext()){
					SelectionKey key = (SelectionKey) readyItor.next();
					readyItor.remove();
				
					if(key.isConnectable()){
						SocketChannel keyChannel = (SocketChannel)key.channel();
						socketIDMap.put(keyChannel, socketID);
						if(keyChannel.isConnectionPending()){
							keyChannel.finishConnect();
							System.out.println("Connection established with server");
							if (ApplicationContext.getSocketFactory() != null) {
								ApplicationContext.getSocketFactory().getSocketConnByID(socketID).setChannel(keyChannel);
							}
							return true;
						}
                      
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void run (){
		try {
			channel.register(selector, SelectionKey.OP_READ );
			
			while(selector.select() > 0  && !stopThread){
				Set<?> readyKeys = selector.selectedKeys();
				Iterator<?> readyItor = readyKeys.iterator();
				
				while(readyItor.hasNext()){
					SelectionKey key = (SelectionKey) readyItor.next();
					readyItor.remove();
				
					if (key.isReadable()){
						SocketChannel keyChannel = (SocketChannel)key.channel();
						String socketID = socketIDMap.get(keyChannel);
				        ByteBuffer byteBuffer = ByteBuffer.allocate( 1024 );
				        byteBuffer.flip();
						Charset charset = Charset.forName( "us-ascii" );
						CharsetDecoder decoder = charset.newDecoder();
						CharBuffer charBuffer = decoder.decode( byteBuffer );
						String result = charBuffer.toString();
						System.out.println(result);
						//TODO Call InboundFramework to process socket connections
						
						if(result.indexOf("35=")!=-1){
							OutputMsgNotifier.INSTANCE.processOutputMsg(result, socketID);
						}
					}
				}
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public void send(String message){
		try {
			messageQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		ClientNioSocket clientSocket = new ClientNioSocket("150.110.2.86", 2115);
		clientSocket.connect(null);
	
		clientSocket.send("SEND_SYMBOLS,AAL.L");
		
	}

	public boolean isStopThread() {
		return stopThread;
	}

	public void setStopThread(boolean stopThread) {
		this.stopThread = stopThread;
	}
	
	public void disconnect(){
		if (channel.isOpen())
			try {
				channel.close();
				System.out.println("Connection terminated with server");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
