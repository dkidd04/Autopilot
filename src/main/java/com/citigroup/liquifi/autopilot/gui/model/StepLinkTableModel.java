package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.entities.LFStepLinkEntry;
import com.citigroup.liquifi.entities.LFTestInputSteps;

public class StepLinkTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Parent Step",
									"Child Step",
									"Placeholder"
                                    };
	private final Class<?>[] columnClass = { String.class, String.class, String.class};
	private List<StepLinkEntry> data = new ArrayList<StepLinkEntry>();

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
			return data.get(i).getParentStep();
		case 1:
			return data.get(i).getChildStep();
		case 2:
			return data.get(i).getRefPlaceHolder();
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

	public List<StepLinkEntry> getData() {
		return data;
	}

	public void setData(List<StepLinkEntry> stepLinkEntries) {
		data = stepLinkEntries;
		fireTableDataChanged();
	}
	
	public StepLinkEntry getRow(int index){
		return data.get(index);
	}

	public void clear() {
		data.clear();
	}
	
	public void removeRow(int index){
		data.remove(index);
		fireTableRowsDeleted(index, index);
	}
	
	public static class StepLinkEntry{
		
		public String parentStep;
		public String childStep;
		public String refPlaceHolder;
		
		public StepLinkEntry(String parentStep, String childStep, String refPlaceHolder){
			this.parentStep = parentStep;
			this.childStep = childStep;
			this.refPlaceHolder = refPlaceHolder;
		}
		
		
		public String getChildStep() {
			return childStep;
		}

		public void setChildStep(String childStep) {
			this.childStep = childStep;
		}

		public String getRefPlaceHolder() {
			return refPlaceHolder;
		}

		public void setRefPlaceHolder(String refPlaceHolder) {
			this.refPlaceHolder = refPlaceHolder;
		}

		public String getParentStep() {
			return parentStep;
		}

		public void setParentStep(String parentStep) {
			this.parentStep = parentStep;
		}

		

		
	}
}