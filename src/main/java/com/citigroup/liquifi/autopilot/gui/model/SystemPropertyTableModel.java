package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;
import com.citigroup.liquifi.entities.LFTag;

public class SystemPropertyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] columnNames = {"Name", "Value"};
	private ArrayList<Object[]> data = new ArrayList<Object[]>();

	
	public SystemPropertyTableModel(){
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
		return true;
	}
	
	public void setValueAt(Object value, int row, int col){
		data.get(row)[col] = value;
		fireTableCellUpdated(row, col);
	}

	public ArrayList<Object[]> getData() {
		return data;
	}

	public void setData(Set<LFTag> tagSet) {
		this.data.clear();
		for(LFTag tag : tagSet) {
			this.data.add(new Object[]{tag.getTagID(), tag.getTagValue()});
		}
		fireTableDataChanged();
	}
	
	
	public Set<LFTag> getDataSet(String testID, int previousSelectedRow){
		Set<LFTag> tagSet = new HashSet<LFTag>();
		for(int i=0; i < data.size(); i++){
			Object[] obj = data.get(i);
			LFTag tag = new LFTag((String)obj[0], (String)obj[1]);
			tag.setActionSequence(previousSelectedRow);
			tag.setTestID(testID);
			tagSet.add(tag);
		}
		return tagSet;
	}	
	
	public void addEmptyRow(){
		data.add(new Object[] {"", ""});
		fireTableDataChanged();
	}
	
	public LFTag removeRow(int index){
		boolean isEmpty = isEmpty(index);
		Object[] rowData = data.remove(index);
		fireTableRowsDeleted(index, index);
		
		if(isEmpty){
			return null;
		}
		
		LFTag tag = new LFTag();
		tag.setTagID((String)rowData[0]);
		tag.setTagValue((String)rowData[1]);
		return tag;
	}
	
	
	private boolean isEmpty(int index){
		if("".equals((String)data.get(index)[0]) && "".equals((String)data.get(index)[1])){
			return true;
		}
		return false;
	}



}
