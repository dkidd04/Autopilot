package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;

public class ClipboardOutputStepTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] columnNames = {"Template", "OutputMsgID", "OutputMsg", "TopicID"};	
	private List<LFOutputMsg> data = new ArrayList<LFOutputMsg>();
	private final Class<?>[] columnClass = {String.class, Integer.class, String.class, String.class};
	
	
	public ClipboardOutputStepTableModel(){
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int i, int j) {
		switch(j){
		case 0:
			return data.get(i).getTemplate();
		case 1:
			return data.get(i).getOutputMsgID();
		case 2:
			return data.get(i).getOutputMsg();
		case 3:
			return data.get(i).getTopicID();
		}

		return "";
	}
	
	public Class<?> getColumnClass(int col) {
		return columnClass[col];
	}
	
	public String getColumnName(int col){
		return columnNames[col];
	}
	
	public boolean isCellEditable(int row, int col){
		return false;
	}
	
	public void setValueAt(Object value, int row, int col){
		//data.get(row)[col] = value;
		//fireTableCellUpdated(row, col);
	}

	public List<LFOutputMsg> getData() {
		return data;
	}

	public void setData(List<LFOutputMsg> data) {
		this.data = data;
		fireTableDataChanged();
	}
	
	
	public void addEmptyRow(int step){
		data.add(new LFOutputMsg());
		fireTableDataChanged();
	}
	
	public void removeRow(int index){
		data.remove(index);
		refractor(index);
		fireTableRowsDeleted(index, index);
	}

	public void updateRow(int row, LFOutputMsg output) {
		data.set(row, output);
		fireTableDataChanged();
	}
	
	public void addRow(int row, LFOutputMsg output){
		data.add(row, output);
		refractor(row);
		fireTableDataChanged();
	}
	
	public LFOutputMsg getRow(int index){
		return data.get(index);
		//output.setCustValidationClass((String)obj[5]);
	}

	public List<LFOutputMsg> getOutputList(){
		 return data;
	}

	public void clear() {
		data.clear();
	}
	
	public void insertRow(LFOutputMsg temp){
		int rowIndex = temp.getOutputMsgID()-1;
		if(rowIndex != -1){
			data.add(rowIndex, temp);
			refractor(rowIndex+1);
			fireTableRowsInserted(rowIndex, rowIndex);
		}
	}
	
	private void refractor(int rowIndex) {
		for (int i=rowIndex; i<data.size(); i++){
			int msgID = i+1;
			data.get(i).setOutputMsgID(msgID);
			
			// Update each output step tags
			for(LFOutputTag tag : data.get(i).getOutputTagList()) {
				tag.setOutputMsgID(msgID);
			}
		}
	}

}
