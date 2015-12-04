package com.citigroup.liquifi.autopilot.gui.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.citigroup.liquifi.entities.LFTestCase;


public class TestTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	public enum COLUMN {REGION, CATEGORY, TESTID, TESTNAME, DESCRIPTION, RELEASENUM, ENABLED, REPEAT, PASSED, FAILED};

	private String[] columnNames = {"Region",
									"Category",
									"TestID",  
									"Name", 
                                    "Description",
                                    "ReleaseNum",
                                    "Enable",
                                    "Repeat",
                                 	"Pass",
                                    "Fail"};
	private final Class<?>[] columnClass = { String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class, Integer.class, Integer.class, Integer.class };
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

    public boolean isCellEditable(int row, int col) {
        if (col == COLUMN.ENABLED.ordinal() || col == COLUMN.REPEAT.ordinal()) {
            return true;
        } else {
            return false;
        }
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
    
    public Boolean isCaseEnabled(int row) {
    	if (row < data.size())
    		return (Boolean) data.get(row)[COLUMN.ENABLED.ordinal()];
    	else 
    		return Boolean.FALSE;
    }
    
    public int getRepeatTimes(int row) {
    	if (row < data.size())
    		return (Integer) data.get(row)[COLUMN.REPEAT.ordinal()];
    	else 
    		return 0;
    }
    
    public String getTestCase(int row) {
    	if (row < data.size())
    		return (String) data.get(row)[COLUMN.TESTID.ordinal()];
    	else 
    		return null;
    }
    
    public void updateResult(int row, boolean Result) {
    	if (Result) {
	    	data.get(row)[COLUMN.PASSED.ordinal()] = (Integer) data.get(row)[COLUMN.PASSED.ordinal()] + 1;
	    	fireTableCellUpdated(row, COLUMN.PASSED.ordinal());
    	}
    	else {
	    	data.get(row)[COLUMN.FAILED.ordinal()] = (Integer) data.get(row)[COLUMN.FAILED.ordinal()] + 1;
	    	fireTableCellUpdated(row, COLUMN.FAILED.ordinal());
    	}
    }
    
    public void batchEnable(boolean onlyReverse) {
    	for (int i = 0; i < data.size(); i++) {
    		if(!"0".equals(data.get(i)[COLUMN.RELEASENUM.ordinal()])) {
    			data.get(i)[COLUMN.ENABLED.ordinal()] = onlyReverse ? new Boolean(!((Boolean) data.get(i)[COLUMN.ENABLED.ordinal()])) : Boolean.TRUE;
    		}
    	}
    	fireTableDataChanged();
    }
    
    public void batchDisable() {
    	for (int i = 0; i < data.size(); i++) {
    		data.get(i)[COLUMN.ENABLED.ordinal()] = false;
    	}
    	fireTableDataChanged();
    }
    
    public void resetResult(int row) {
    	data.get(row)[COLUMN.PASSED.ordinal()] = 0;
    	fireTableCellUpdated(row, COLUMN.PASSED.ordinal());

    	data.get(row)[COLUMN.FAILED.ordinal()] = 0;
    	fireTableCellUpdated(row, COLUMN.FAILED.ordinal());
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
