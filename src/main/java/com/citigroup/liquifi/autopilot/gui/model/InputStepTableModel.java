package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;

public class InputStepTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	//private LFTestCase testcase = ApplicationContext.getTestcaseHelper().getTestcase();
	private String[] columnNames = {"Step", "Template", "MsgType", "Topic", "Comments", "Message"};
	private final Class<?>[] columnClass = { Integer.class, String.class, String.class, String.class, String.class, String.class};
	private List<LFTestInputSteps> data = new ArrayList<LFTestInputSteps>();
	
	
	public InputStepTableModel(){
	}
	
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
			return data.get(i).getActionSequence();
		case 1:
			return data.get(i).getTemplate();
		case 2:
			return data.get(i).getMsgType();
		case 3:
			return data.get(i).getTopicID();
		case 4:
			return data.get(i).getComments();
		case 5:
			return data.get(i).getMessage();
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
			refractor(rowIndex+1);
			fireTableRowsInserted(rowIndex, rowIndex);
		}
	}
	
	private void refractor(int rowIndex) {
		for (int i=rowIndex; i<data.size(); i++){
			// Update each input step
			int actionSequence = i+1;
			//ApplicationContext.getTestcasePanel().getRefactoredStepMap().put(data.get(i).getActionSequence(), actionSequence);
			data.get(i).setActionSequence(actionSequence);
			// Update each input step tags
			for(LFTag tag : data.get(i).getInputTagsValueList()) {
				tag.setActionSequence(actionSequence);
			}
			// Update each output step
			for(LFOutputMsg output : data.get(i).getOutputStepList()) {
				output.setActionSequence(actionSequence);
				
				// Update each output step override tag
				for(LFOutputTag tag : output.getOverrideTags()) {
					tag.setActionSequence(actionSequence);
				}
			}
			
//			// update each reference child step
//			for(String refStepStr : data.get(i).getChildrenStepsArray()){
//				int[] stepArr = parseStep(refStepStr);
//				int childInputStepNum = stepArr[0]-1;
//				int childOutputStepNum = stepArr[1]-1;
//				if(childInputStepNum >= rowIndex){ //adjust affected offset
//					childInputStepNum++;
//				}
//				
//				LFOutputMsg childStep = data.get(childInputStepNum).getOutputStepList().get(childOutputStepNum);
//				String refPlcHolder = childStep.getParentPlaceHolder();
//				for(LFOutputTag outputTag : childStep.getOverrideTags()){
//					if(outputTag.getTagID().equals(refPlcHolder)){
//						outputTag.setTagValue("@IP[" + actionSequence + "]"); //inputStep
//						ApplicationContext.getTestcaseHelper().getOutputTagModel().fireTableDataChanged();
//						break;
//					}
//				}
//			}
		}
	}
	
	
	public void removeRow(int index){
		data.remove(index);
		refractor(index);
		fireTableDataChanged();
	}
	
	
	
	

	public void addRow(int rowIndex, LFTestInputSteps inputStep) {
		data.add(rowIndex, inputStep);
		refractor(rowIndex);
		fireTableDataChanged();
	}
	
	
	public LFTestInputSteps getRow(int index){
		return data.get(index);
	}

	public void clear() {
		data.clear();
	}

}
