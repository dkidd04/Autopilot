package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.entities.LFCommonOverwriteTag;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.Tag;

public class RepeatingGroupTableModel<X extends Tag> extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"Tag ID", "Value"};
	private List<X> data = new ArrayList<X>();
	private boolean dirty = false;
	private String groupNum = null;
	private boolean isInputStep = true;


	public String getGroupNum(){
		return this.groupNum;
	}
	
	public void setGroupNum(String groupNum){
		this.groupNum = groupNum;
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int i, int j) {
		switch (j) {
		case 0:
			return data.get(i).getTagID();
		case 1:
			return data.get(i).getTagValue();
		}
		
		return "";
	}
	
	public String getColumnName(int col){
		return columnNames[col];
	}
	
	public boolean isCellEditable(int row, int col){
		return true;
	}
	
	public void setValueAt(Object value, int row, int col){
		switch (col) {
		case 0:
			data.get(row).setTagID(value.toString());
			break;
		case 1:
			data.get(row).setTagValue(value.toString());
			Tag curTag = data.get(row);
			ApplicationContext.getTestcaseHelper().updateRepeatingGroupTag(curTag, this.groupNum, isInputStep);
		}
		dirty = true;
	}

	public List<X> getData() {
		return data;
	}

	public void setData(List<X> tagSet) {
		data = tagSet;
		fireTableDataChanged();
	}
	
	public void setListAsData(List<LFCommonOverwriteTag> tagSet) {
		data = new ArrayList<X>();
		for(LFCommonOverwriteTag t : tagSet){
			LFTag newTag = new LFTag(t.getTagID(), t.getTagValue());
			data.add((X) newTag);
		}
		fireTableDataChanged();
	}
	
	public void setTagListAsData(List<X> tagSet) {
		data = new ArrayList<X>();
		for(X t : tagSet){
			LFTag newTag = new LFTag(t.getTagID(), t.getTagValue());
			data.add((X) newTag);
		}
		fireTableDataChanged();
	}
	
	
	public void add(X tag){
		data.add(0, tag);
		dirty = true;
		fireTableDataChanged();
	}
	
	public void removeRow(int index){
		data.remove(index);
		dirty = true;
		fireTableRowsDeleted(index, index);
	}
	
	public X getRow(int i) {
		return data.get(i);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public boolean isInputStep() {
		return isInputStep;
	}

	public void setInputStep(boolean isInputStep) {
		this.isInputStep = isInputStep;
	}

	//public void clear() {
	//	data.clear();
	//	fireTableDataChanged();
	//}
}
