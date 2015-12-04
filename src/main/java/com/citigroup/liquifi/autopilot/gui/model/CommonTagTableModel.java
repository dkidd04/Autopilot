package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.entities.LFCommonOverwriteTag;;

public class CommonTagTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] columnNames = {"Tag ID", "Value"};
	private ArrayList<Object[]> data = new ArrayList<Object[]>();
	private boolean dirty = false;

	
	public CommonTagTableModel(){
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
		dirty = true;
		fireTableCellUpdated(row, col);
	}

	public ArrayList<Object[]> getData() {
		return data;
	}

	public void setData(Set<LFCommonOverwriteTag> tagSet) {
		this.data.clear();
		for(LFCommonOverwriteTag tag : tagSet) {
			this.data.add(new Object[]{tag.getTagID(), tag.getTagValue()});
		}
		fireTableDataChanged();
	}
	
	
	public Set<LFCommonOverwriteTag> getDataSet(){
		Set<LFCommonOverwriteTag> tagSet = new HashSet<LFCommonOverwriteTag>();
		for(int i=0; i < data.size(); i++){
			Object[] obj = data.get(i);
			LFCommonOverwriteTag tag = new LFCommonOverwriteTag((String)obj[0], (String)obj[1]);
			//tag.setCommonOverwriteTagListName(commonOverwriteTagListName);	
			tagSet.add(tag);
		}
		return tagSet;
	}	
	
	public void addEmptyRow(){
		data.add(new Object[] {"", ""});
		dirty = true;
		fireTableDataChanged();
	}
	
	public void removeRow(int index){
		data.remove(index);
		dirty = true;
		fireTableRowsDeleted(index, index);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
