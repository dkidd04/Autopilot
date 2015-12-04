package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;


public class ClipboardInputStepTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Test Case Name",
									"Step#",
									"Template",  
									"MsgType", 
                                    };
	private final Class<?>[] columnClass = { String.class, String.class, String.class, String.class};
	private List<LFTestInputSteps> data = new ArrayList<LFTestInputSteps>();

//	public ClipboardInputStepTableModel(){
//		data.add(ApplicationContext.getTestcaseHelper().getTestcase().getInputStepList().get(0));
//	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}
	
	public Class<?> getColumnClass(int col) {
		return columnClass[col];
	}

	public Object getValueAt(int i, int j) {
		switch(j) {
		case 0:
			return data.get(i).getTestCase();
		case 1:
			return data.get(i).getActionSequence();
		case 2:
			return data.get(i).getTemplate();
		case 3:
			return data.get(i).getMsgType();
		}
		
		return "";
	}
	
	public String getColumnName(int col){
		return columnNames[col];
	}


    public boolean isCellEditable(int row, int col){
		return false;
	}
	
	public void setValueAt(Object value, int row, int col){
	}

	public List<LFTestInputSteps> getData() {
		return data;
	}

	public void setData(List<LFTestInputSteps> inputStepSet) {
		data = inputStepSet;
		fireTableDataChanged();
	}
	
	
	public void updateRow(int rowIndex, LFTestInputSteps inputStep){
		data.set(rowIndex, inputStep);
	}
	
	public void insertRow(LFTestInputSteps temp){
		int rowIndex = temp.getActionSequence()-1;
		if(rowIndex != -1){
			data.add(rowIndex, temp);
			fireTableRowsInserted(rowIndex, rowIndex);
		}
	}
	

	public void removeRow(int index){
		data.remove(index);
		fireTableDataChanged();
	}

	public void addRow(int rowIndex, LFTestInputSteps inputStep) {
		data.add(rowIndex, inputStep);
		System.out.println("data.size is " + data.size());
		fireTableDataChanged();
	}
	
	public void addRow(LFTestInputSteps inputStep){
		data.add(inputStep);
		fireTableDataChanged();
	}
	
	
	public LFTestInputSteps getRow(int index){
		return data.get(index);
	}

	public void clear() {
		data.clear();
	}
}
