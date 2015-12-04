package com.citigroup.liquifi.autopilot.messaging;

import java.util.ArrayList;
import java.util.List;

import com.citigroup.liquifi.autopilot.socket.ClientSocket;

public class AutoPilotSocketConnectionFactory {
	List<SocketConnectionInfo> socketList = new ArrayList<SocketConnectionInfo> ();
	List<ClientSocket> sockets = new ArrayList<ClientSocket> ();

	public List<SocketConnectionInfo> getSocketList() {
		return socketList;
	}

	public void setSocketList(List<SocketConnectionInfo> socketList) {
		this.socketList = socketList;
	}
	
	public List<ClientSocket> getSockets() {
		return sockets;
	}

	public void setSockets(List<ClientSocket> sockets) {
		this.sockets = sockets;
	}

	public String[] getSocketIDArray (){
		String[] socketIds = new String[socketList.size()];
		for(int i =0; i<socketList.size(); i++){
			socketIds[i] = socketList.get(i).getSocketID();
		}
		return socketIds;
	}
	
	public SocketConnectionInfo getSocketConnByID (String socketID){

		for (SocketConnectionInfo socket : socketList){
			if (socket.getSocketID().equals(socketID)){
				return socket;
			}
		}
		return null;
	}
	
	public void setupSocket(String name) throws Exception {
		int attemps = 3;
		for(ClientSocket socket : sockets) {
			if(socket.isSocketFor(name) && !socket.isConnected()) {
				while(!socket.isConnected() && attemps > 0) {
					try {
						socket.connect();
					} catch (Exception e) {
						System.out.println("unable to connect, trying again");
					}
					attemps--;
				}
				if (!socket.isConnected()) {
					throw new Exception ("unable to connect on socket");
				}
				Thread t = new Thread(socket);
				t.start();
				break;
			}
		}
	}
	
	public void closeSocket(String name) {
		for(ClientSocket socket : sockets) {
			if(socket.isSocketFor(name) && socket.isConnected()) {
				socket.disconnect();
				break;
			}
		}
	}
	
	public boolean isSocket(String name) {
		for(ClientSocket socket : sockets) {
			if(socket.isSocketFor(name)) {
				return true;
			}
		}
		return false;
	}
}
