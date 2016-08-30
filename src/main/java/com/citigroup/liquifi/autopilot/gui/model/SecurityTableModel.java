package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class SecurityTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] columnNames;
	private ArrayList<Object[]> data = new ArrayList<Object[]>();
	
	public SecurityTableModel(String[] columnNames){
		this.columnNames = columnNames;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int i, int j) {
		return data.get(i)[j];
	}
	
	public String getColumnName(int col){
		return columnNames[col];
	}
	
	public boolean isCellEditable(int row, int col){
		if(col == 1) {
			return true;
		}
		return false;
	}
	
	public void setValueAt(Object value, int row, int col){
		data.get(row)[col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public void setData(Map<Integer, String> securityMap){
		// find max value
		int max = 0;
		for(int i : securityMap.keySet()) {
			if (i > max) {
				max = i;
			}
		}
		
		data.clear();
		for(int i = 1; i <= max; i++){
			data.add(new Object[] {i, securityMap.get(i)});
		}
		fireTableDataChanged();
	}

	public ArrayList<Object[]> getData() {
		return data;
	}

	public void addEmptyRow(){
		data.add(new Object[] {"", ""});
		fireTableDataChanged();
	}
	
	public void removeRow(int index){
		data.remove(index);
		fireTableRowsDeleted(index, index);
	}
	
	public Map<String, String> getDataMap(){
		Map<String, String> securityProp = new HashMap<String, String>();
		for (int i = 0; i< data.size(); i++){
			securityProp.put((String)data.get(i)[0], (String)data.get(i)[1]);
		}
		return securityProp;
	}

}
