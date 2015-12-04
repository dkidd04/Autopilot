package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.entities.LFTestCase;


public class ResultTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	public enum COLUMN {REGION, CATEGORY, TESTID, TESTNAME, DESCRIPTION, RELEASENUM};

	private String[] columnNames = {"Region",
									"Category",
									"TestID",  
									"Name", 
                                    "Description",
                                    "ReleaseNum"
                                    };
	private final Class<?>[] columnClass = { String.class, String.class, String.class, String.class, String.class, String.class};
    private ArrayList<Object[]> data = new ArrayList<Object[]>();

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    @SuppressWarnings("unchecked")
	public Class getColumnClass(int col) {
    	return columnClass[col];
    }

    public Object getValueAt(int row, int col) {
    	if (row < data.size())
        	return data.get(row)[col];
    	else
    		return null;
    }


    public void setValueAt(Object value, int row, int col) {
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
    }
    
    public void initCase(LFTestCase tc) {
    	data.add(new Object[] {Boolean.FALSE, 1, 0, 0, tc});
    	fireTableDataChanged();
    }
    
    public void removeCase(int row) {
    	if (row >= data.size()) return;
    	data.remove(row);
    	fireTableDataChanged();
    }
    
    public String getTestCase(int row) {
    	if (row < data.size())
    		return (String) data.get(row)[COLUMN.TESTID.ordinal()];
    	else 
    		return null;
    }
    

	public ArrayList<Object[]> getData() {
		return data;
	}

	public void setData(ArrayList<Object[]> data) {
		this.data = data;
	}
	
	public void addRow(LFTestCase tc){
    	data.add(new Object[] {tc.getRegion(), tc.getCategory(), tc.getTestID(), tc.getName(), tc.getDescription(),  tc.getReleaseNum(), Boolean.FALSE, 1, 0, 0});
	}
}
