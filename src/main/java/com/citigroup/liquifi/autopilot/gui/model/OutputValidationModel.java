/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.citigroup.liquifi.autopilot.gui.model;

/**
 *
 * @author zy63334
 */


import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class OutputValidationModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] columnNames = {"Output Step", "TagID", "Expected Output", "Actual Output"};
	private ArrayList<Object[]> data = new ArrayList<Object[]>();

	
	public OutputValidationModel(){
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
	
    @Override
	public String getColumnName(int col){
		return columnNames[col];
	}
	
    @Override
	public boolean isCellEditable(int row, int col){
		return true;
	}
	
    @Override
	public void setValueAt(Object value, int row, int col){
		data.get(row)[col] = value;
		fireTableCellUpdated(row, col);
	}

	public ArrayList<Object[]> getData() {
		return data;
	}

	public void addRow(int stepNum, String tagID, String expValue, String actualValue) {
		data.add(new Object[]{stepNum,tagID,expValue,actualValue});
		fireTableDataChanged();
	}
	
	public void clear(){
		data.clear();
		fireTableDataChanged();
	}


}
