package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class PropertyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] columnNames;
	private ArrayList<String[]> data = new ArrayList<String[]>();
	private Map<String, String> change = new HashMap<String, String>();
	
	public PropertyTableModel(String[] columnNames){
		this.columnNames = columnNames;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public String getValueAt(int i, int j) {
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
		data.get(row)[col] = value.toString();
		change.put(data.get(row)[0].toString(), value.toString());
		fireTableCellUpdated(row, col);
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public void addEmptyRow(){
		data.add(new String[] {"", ""});
		fireTableDataChanged();
	}
	
	public void removeRow(int index){
		data.remove(index);
		fireTableRowsDeleted(index, index);
	}

	public void setData(Map<String, String> systemProperty) {
		change.clear();
		data.clear();
		for(String property : systemProperty.keySet()){
			data.add(new String[] {property, systemProperty.get(property)});
		}
		fireTableDataChanged();
	}
	
	public Map<String, String> getDataMap(){
		Map<String, String> systemProp = new HashMap<String, String>();
		for (int i =0; i< data.size(); i++){
			systemProp.put((String)data.get(i)[0], (String)data.get(i)[1]);
		}
		return systemProp;
	}

	public Map<String, String> getChange() {
		return change;
	}
}
