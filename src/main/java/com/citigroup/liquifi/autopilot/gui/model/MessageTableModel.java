package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.autopilot.message.FIXMessage;


public class MessageTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	private final String[] columnNames = {"Status", 
									"MsgType",
									"Topic",
									"OrderID",
									"Symbol",  
									"ClOrdID", 
									"Side",
                                    "Price",
                                    "Qty",
                                    "LeaveQty"};

	private final List<FIXMessage> messageList = new ArrayList<FIXMessage>();
	private final List<Object[]> data = new ArrayList<Object[]>();
	
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
    
	public Class<?> getColumnClass(int col) {
    	return String.class;
    }

    public Object getValueAt(int row, int col) {
    	if (row < data.size())
        	return data.get(row)[col];
    	else
    		return null;
    }

    public boolean isCellEditable(int row, int col) {
    	return false;
    }

    public void setValueAt(Object value, int row, int col) {
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
    }

	public List<Object[]> getData() { 
		return data;
	}

	public void addRow(String status, String topic, FIXMessage msg){
    	data.add(new Object[] {status, msg.getTag(35), topic, msg.getTag(37), msg.getTag(55), msg.getTag(11), msg.getTag(54),  msg.getTag(44), msg.getTag(38), msg.getTag(151)});
    	messageList.add(msg);
    	fireTableDataChanged();
	}

	public List<FIXMessage> getMessageList() {
		return messageList;
	}

	public void clear(){
		data.clear();
		messageList.clear();
		fireTableDataChanged();
	}
}
