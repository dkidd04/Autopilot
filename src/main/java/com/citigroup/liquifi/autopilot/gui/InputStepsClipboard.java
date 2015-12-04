package com.citigroup.liquifi.autopilot.gui;

/*
 * PreferenceDialog.java
 *
 * Created on September 22, 2008, 12:15 PM
 */

import com.citigroup.liquifi.AutoPilotAppl;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.gui.TestRunPanel.TableTransferHandler;
import com.citigroup.liquifi.autopilot.gui.TestRunPanel.TreeTransferHandler;
import com.citigroup.liquifi.autopilot.gui.dndTree.TestCaseTreeNode;
import com.citigroup.liquifi.autopilot.gui.model.ClipboardInputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.ClipboardOutputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.InputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.ResultTableModel;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.util.DBUtil;

import javax.swing.GroupLayout.Alignment;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JCheckBox;

/**
 *
 * @author  yh95657
 */
public class InputStepsClipboard extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private String id;
	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());
	private JTable inputStepsTable;
	private JTable outputStepsTable;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JButton btnClose;
	private ClipboardOutputStepTableModel outputStepsTableModel = new ClipboardOutputStepTableModel();
	private ClipboardInputStepTableModel inputStepsTableModel = new ClipboardInputStepTableModel();
	private ArrayList<LFTestCase> passedCases = new ArrayList<LFTestCase>();
	private ArrayList<LFTestCase> failedCases = new ArrayList<LFTestCase>();
	private JLabel lblInputStepContents;
	private JLabel lblOutputStepContents;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JTextPane inputStepContentsTextPane;
	private JTextPane outputStepContentsTextPane;
	JCheckBox chckbxAlwaysOnTop;
	

	/** Creates new form PreferenceDialog */
	public InputStepsClipboard(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		this.id = UUID.randomUUID() + "";
		setIconImage(Toolkit.getDefaultToolkit().getImage(InputStepsClipboard.class.getResource("/com/citigroup/liquifi/autopilot/gui/resources/icons/clipboard_down.png")));
		setTitle("InputSteps Clipboard");
		initComponents();
		init();
	}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        this.addWindowListener(new WindowAdapter() 
        {
          public void windowClosed(WindowEvent e)
          {
        	  ApplicationContext.getTestcasePanel().getClipboardsMap().remove(id);
          }
        });
        
        setName("InputSteps Clipboard"); // NOI18N
        setResizable(false);
        
        scrollPane = new JScrollPane();
        
        scrollPane_1 = new JScrollPane();
        
        btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		ApplicationContext.getTestcasePanel().getClipboardsMap().remove(id);
        		dispose();
        	}
        });
        
        JLabel lblInputSteps = new JLabel("Input Steps ( Drag/Drop input steps to/from testcases )");
        lblInputSteps.setIcon(new ImageIcon(InputStepsClipboard.class.getResource("/com/citigroup/liquifi/autopilot/gui/resources/icons/drop_test_case.png")));
        
        JLabel lblOutputSteps = new JLabel("Output Steps");
        
        lblInputStepContents = new JLabel("Input Step Contents");
        
        lblOutputStepContents = new JLabel("Output Step Contents");
        
        scrollPane_2 = new JScrollPane();
        scrollPane_2.setName("jScrollPane5");
        
        scrollPane_3 = new JScrollPane();
        scrollPane_3.setName("jScrollPane5");
        
        chckbxAlwaysOnTop = new JCheckBox("Always on top");
        chckbxAlwaysOnTop.setSelected(true);
        chckbxAlwaysOnTop.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		setThisAlwaysOnTop(chckbxAlwaysOnTop.isSelected());
        	}
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(23)
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(chckbxAlwaysOnTop)
        					.addPreferredGap(ComponentPlacement.RELATED, 197, Short.MAX_VALUE)
        					.addComponent(btnClose)
        					.addGap(288))
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addGroup(layout.createSequentialGroup()
        							.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
        								.addComponent(lblInputSteps, 0, 0, Short.MAX_VALUE)
        								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        							.addGroup(layout.createParallelGroup(Alignment.LEADING)
        								.addGroup(layout.createSequentialGroup()
        									.addGap(28)
        									.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
        								.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        									.addPreferredGap(ComponentPlacement.RELATED)
        									.addComponent(lblInputStepContents)
        									.addGap(61))))
        						.addGroup(layout.createSequentialGroup()
        							.addGroup(layout.createParallelGroup(Alignment.LEADING)
        								.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE)
        								.addComponent(lblOutputSteps))
        							.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
        							.addGroup(layout.createParallelGroup(Alignment.LEADING)
        								.addComponent(scrollPane_3, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
        								.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        									.addComponent(lblOutputStepContents)
        									.addGap(54)))))
        					.addContainerGap())))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(14)
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(lblInputSteps)
        				.addComponent(lblInputStepContents))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
        				.addComponent(scrollPane_2)
        				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(lblOutputSteps)
        				.addComponent(lblOutputStepContents))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
        				.addComponent(scrollPane_3)
        				.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnClose)
        				.addComponent(chckbxAlwaysOnTop))
        			.addContainerGap())
        );
        
        outputStepContentsTextPane = new JTextPane();
        outputStepContentsTextPane.setEditable(false);
        scrollPane_3.setViewportView(outputStepContentsTextPane);
        
        inputStepContentsTextPane = new JTextPane();
        inputStepContentsTextPane.setEditable(false);
        scrollPane_2.setViewportView(inputStepContentsTextPane);
        
        outputStepsTable = new JTable();
        outputStepsTable = new javax.swing.JTable();
        outputStepsTable.setRowSelectionAllowed(true);
        outputStepsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        outputStepsTable.setModel(outputStepsTableModel);
        outputStepsTable.setName("outputStepsTable"); // NOI18N
        TableRowSorter<TableModel> failedCasesSorter = new TableRowSorter<TableModel> (outputStepsTable.getModel());
        outputStepsTable.setRowSorter(failedCasesSorter);
        outputStepsTable.getSelectionModel().addListSelectionListener(new RowListener(outputStepsTable));
        
        scrollPane_1.setViewportView(outputStepsTable);
        
        inputStepsTable = new JTable();
        inputStepsTable = new javax.swing.JTable();
        inputStepsTable.setRowSelectionAllowed(true);
        inputStepsTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        inputStepsTable.setModel(inputStepsTableModel);
        inputStepsTable.setName("inputStepsTable"); // NOI18N
        TableRowSorter<TableModel> passedCasesSorter = new TableRowSorter<TableModel> (inputStepsTable.getModel());
        inputStepsTable.setRowSorter(passedCasesSorter);
        //testCaseTree.setTransferHandler(new TreeTransferHandler());
        inputStepsTable.setDragEnabled(true);
        inputStepsTable.setDropMode(DropMode.ON_OR_INSERT);
        inputStepsTable.setTransferHandler(new InputStepsTableTransferHandler());
        inputStepsTable.setFillsViewportHeight(true);
        inputStepsTable.getSelectionModel().addListSelectionListener(new RowListener(inputStepsTable));
        inputStepsTable.getColumnModel().getColumn(1).setMaxWidth(40);
        inputStepsTable.getColumnModel().getColumn(2).setMaxWidth(190);
        inputStepsTable.getColumnModel().getColumn(3).setMaxWidth(65);
        scrollPane.setViewportView(inputStepsTable);
        getContentPane().setLayout(layout);

        pack();
    }// </editor-fold>//GEN-END:initComponents
	
	protected void outputTestResultToFile(File file){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		
		 try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				String userName = System.getProperty("user.name");
				bw.write("Run By (SOEID)," + userName + "\r\n");
				bw.write("Test Date," + date + "\r\n");
				bw.write("Total Number of Tests," + (passedCases.size() + failedCases.size()) + "\r\n");
				bw.write("Number of Passed Tests," + passedCases.size() + "\r\n");
				bw.write("Number of Failed Tests," + failedCases.size() + "\r\n");
				bw.write("\r\nResult,TestID,Test Case Name,Category,Region,Release Num, Test Date \r\n");
				
				for(LFTestCase pcase : passedCases){
					bw.write("Passed," + pcase.getTestID() + "," + pcase.getName() + "," + pcase.getCategory() + "," + pcase.getRegion() + "," + pcase.getReleaseNum() + "," + date + "\r\n");
				}
				for(LFTestCase fcase : failedCases){
					bw.write("Failed," + fcase.getTestID() + "," + fcase.getName() + "," + fcase.getCategory() + "," + fcase.getRegion() + "," + fcase.getReleaseNum() + "," + date + "\r\n");
				}
				
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    //Now you have your file to do whatever you want to do
	}


	
//	private void viewSelectedPassedTestCase() {
//		updateInputTestCasePanelWithSelectedPassedTestCase();
//		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showMainPanel();
//	}
//
//	public void updateInputTestCasePanelWithSelectedPassedTestCase() {
//		InputTestCasePanel inputTestCasePanel = ApplicationContext.getTestcasePanel();
//		LFTestCase testcase = passedCases.get(inputStepsTable.convertRowIndexToModel(inputStepsTable.getSelectedRow()));
//		inputTestCasePanel.setTestcase(testcase);
//	}
//
//	protected void doubleClickFailedTestCasePerformed(MouseEvent e) {
//		viewSelectedFailedTestCase();
//		
//	}
//	
//	private void viewSelectedFailedTestCase() {
//		updateInputTestCasePanelWithSelectedFailedTestCase();
//		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showMainPanel();
//	}
//
//	public void updateInputTestCasePanelWithSelectedFailedTestCase() {
//		InputTestCasePanel inputTestCasePanel = ApplicationContext.getTestcasePanel();
//		LFTestCase testcase = failedCases.get(outputStepsTable.convertRowIndexToModel(outputStepsTable.getSelectedRow()));
//		inputTestCasePanel.setTestcase(testcase);
//	}

	public void init(){
		setThisAlwaysOnTop(true);
	}
	
	

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				InputStepsClipboard dialog = new InputStepsClipboard(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}
	
	public ClipboardOutputStepTableModel getOutputStepsTableModel() {
		return outputStepsTableModel;
	}

	public ClipboardInputStepTableModel getInputStepsTableModel() {
		return inputStepsTableModel;
	}

	
	public String getId() {
		return id;
	}
	
	class InputStepsTableTransferHandler extends TransferHandler{

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		protected Transferable createTransferable(JComponent c) {
            JTable table = (JTable)c;
            int[] values = table.getSelectedRows();
    
            StringBuffer buff = new StringBuffer();

            //reverse order so 
            
            for (int i = values.length-1; i >= 0; i--) {
                Object val = values[i];
                buff.append("from_InputStepsClipboard");
                buff.append(id+"|");
                buff.append(val == null ? "" : val.toString());
                if (i != 0) {
                    buff.append("\n");
                }
            }
            return new StringSelection(buff.toString());
		}

		//Bundle up the selected items in the JTable
		//as a single string, for export.
		
		protected String exportString(JComponent c) {
			//JTable table = (JTable)c;
//			return "category_" + testCaseTree.getLastSelectedPathComponent().toString(); 
			return null;
		}

		protected void exportDone(JComponent c, Transferable data, int action) {

			// clear moved node
			if(action == TransferHandler.MOVE){
				//TestCaseTreeNode oldNode = (TestCaseTreeNode) testCaseTree.getLastSelectedPathComponent();
				//((DefaultTreeModel) testCaseTree.getModel()).removeNodeFromParent(oldNode);
				//TODO: update database
			}

		}

		public boolean canImport(TransferHandler.TransferSupport info) {
			// Check for String flavor
			if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}
			try {
				String sourceRow = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
				
				//incorrect source
				if(!sourceRow.startsWith("from_InputTestCasePanel")){
					return false;
				}
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			return true;
		}

		public boolean importData(TransferHandler.TransferSupport info) {
			// if we can't handle the import, say so
			if (!canImport(info)) {
				return false;
			}
			JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
			int targetRow = dl.getRow();
			
			// fetch the data and bail if this fails
			String sourceDataStr;
			try {
				sourceDataStr = ((String) info.getTransferable().getTransferData(DataFlavor.stringFlavor)).replace("from_InputTestCasePanel", "");
				
				String[] draggedLines = sourceDataStr.split("\n");

				for(String line: draggedLines){
					String sourceCaseName = ApplicationContext.getTestcaseHelper().getTestcase().getName();
					//System.out.println("importing step: " + sourceRow + " from Testcase: " + sourceCaseName);
					InputStepTableModel tableModel = ApplicationContext.getTestcaseHelper().getInputStepModel();
					LFTestInputSteps clonedSourceStep = (LFTestInputSteps)tableModel.getRow(Integer.parseInt(line)).clone();
					//System.out.println("clined step is: " + clonedSourceStep);
					clonedSourceStep.setTestCase(sourceCaseName);
					//System.out.println(targetRow);

					inputStepsTableModel.addRow(clonedSourceStep);
				}

            	//System.out.println("size of data in inputStepsTableModel is: " + inputStepsTableModel.getRowCount());
			
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}

			

			return true;
		}
	}
	
	private class RowListener implements ListSelectionListener {
		
		private JTable selectedTable;

		public RowListener(JTable table){
			selectedTable = table;
		}

		public void valueChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting()) {
				
				if(selectedTable == inputStepsTable){
					int pointer = inputStepsTable.getSelectedRow();

					if(pointer == -1){
						// If pointer is -1 then remove input tags and message box
						inputStepContentsTextPane.setText("");

						// ... and remove output steps, output tags and message box
						outputStepsTableModel.setData(new ArrayList<LFOutputMsg>());
						outputStepContentsTextPane.setText("");
					} else if(pointer > -1) {			
						//inputStepsTable.getSelectionModel().setSelectionInterval(pointer, pointer);
						refreshInputMessageBox();

						// Output Steps
						outputStepsTableModel.setData(inputStepsTableModel.getRow(pointer).getOutputStepList());
						if(outputStepsTableModel.getRowCount() > 0) {
							outputStepsTable.getSelectionModel().setSelectionInterval(0, 0);
						}
						refreshOutputMessageBox();
					}
				}else{
					int pointer = outputStepsTable.getSelectedRow();

					if(pointer == -1){
						// If pointer is -1 then remove output tags and message box
						outputStepContentsTextPane.setText("");
					} else if(pointer > -1) {
						outputStepsTable.getSelectionModel().setSelectionInterval(pointer, pointer);
						refreshOutputMessageBox();
					}
				}

			}
		}
		
		private void refreshInputMessageBox() {
			int inputRowPointer = inputStepsTable.getSelectedRow();
			StyledDocument doc = inputStepContentsTextPane.getStyledDocument();
			if(inputRowPointer > -1) {
				ApplicationContext.getTestcaseHelper().format(inputStepsTableModel.getRow(inputRowPointer), doc);
			}
		}
		
		private void refreshOutputMessageBox() {
			int outputRowPointer = outputStepsTable.getSelectedRow();
			StyledDocument doc = outputStepContentsTextPane.getStyledDocument();
			if(outputRowPointer > -1) {
				ApplicationContext.getTestcaseHelper().format(outputStepsTableModel.getRow(outputRowPointer), doc);
			}
		}
	}
	
	public boolean getThisAlwaysOnTop() {
		return isAlwaysOnTop();
	}
	public void setThisAlwaysOnTop(boolean alwaysOnTop) {
		setAlwaysOnTop(alwaysOnTop);
	}
}
