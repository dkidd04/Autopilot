package com.citigroup.liquifi.autopilot.gui.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.messaging.AutoPilotBrokerInfo;
import com.citigroup.liquifi.autopilot.socket.ClientSocket;

public class TopicManagerTableModel extends AbstractTableModel{
	
	Object[][] rowData = null;
	String[] columnNames = {"Topic", "Enable"};
	
	Map<String, Boolean> topicMap = null;
	
	public void init(){
		if(topicMap == null){
			topicMap = new HashMap<String, Boolean>();
		}
		if(rowData == null){
			Map<String, AutoPilotBrokerInfo> outboundTopicList = ApplicationContext.getBrokerFactory().getInitiatorBrokerMapping();
			Map<String, AutoPilotBrokerInfo> inboundTopicList = ApplicationContext.getBrokerFactory().getAcceptorBrokerMapping();
			
			List<ClientSocket> sockets = null;
			int socketSize = 0;
			if (ApplicationContext.getSocketFactory() != null) {
				sockets = ApplicationContext.getSocketFactory().getSockets();
				socketSize = sockets.size();
			}
			
			
			rowData = new Object[outboundTopicList.size() + inboundTopicList.size() + (socketSize*2)][2];
			int row = 0;
			for(String topic : outboundTopicList.keySet()){
				rowData[row][0] = topic;
				rowData[row][1] = Boolean.TRUE;
				topicMap.put(topic, true);
				row++;
			}
			for(String topic : inboundTopicList.keySet()){
				rowData[row][0] = topic;
				rowData[row][1] = Boolean.TRUE;
				topicMap.put(topic, true);
				row++;
			}
			if (ApplicationContext.getSocketFactory() != null) {
				for(ClientSocket socket : sockets) {
					rowData[row][0] = socket.getInbound();
					rowData[row][1] = Boolean.TRUE;
					topicMap.put(socket.getInbound(), true);
					row++;
					
					rowData[row][0] = socket.getOutbound();
					rowData[row][1] = Boolean.TRUE;
					topicMap.put(socket.getOutbound(), true);
					row++;
				}
			}
			fireTableDataChanged();
		}

	}
	
	public boolean isActiveTopic(String topic){
		if(topicMap == null){
			init();
		}
		Boolean result = topicMap.get(topic);
		return result == null ? false : result;
	}
	

	@Override
	public int getColumnCount() {
		return columnNames == null ? 0 : columnNames.length;
	}

	@Override
	public int getRowCount() {
		return rowData == null ? 0 : rowData.length;
	}
	
	public String getColumnName(int col){
		return columnNames == null ? null : columnNames[col];
	}
	
	public void setValueAt(Object value, int row, int col) {
		Boolean enabled = (Boolean) value;
		rowData[row][col] = enabled;
		String topicName = (String) rowData[row][0];
		topicMap.put(topicName, (Boolean) value);
		fireTableDataChanged();
	}
	
	public Class getColumnClass(int column) {
	    return (getValueAt(0, column).getClass());
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(this.rowData != null){
			return rowData[row][col];
		}
		return null;
	}
	
	public boolean isCellEditable(int row, int column) {
	    return (column != 0);
	}

}
