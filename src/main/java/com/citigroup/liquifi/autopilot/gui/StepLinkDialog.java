package com.citigroup.liquifi.autopilot.gui;

import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import com.citigroup.liquifi.AutoPilotAppl;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.gui.model.ClipboardInputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.InputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.OutputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.StepLinkTableModel;
import com.citigroup.liquifi.autopilot.gui.model.StepLinkTableModel.StepLinkEntry;
import com.citigroup.liquifi.autopilot.helper.TestCaseHelper;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.util.DBUtil;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTextField;

public class StepLinkDialog extends JDialog{
	private JTable table;
	private StepLinkTableModel tableModel = new StepLinkTableModel();
	private JComboBox inputStepsComboBox;
	//TestCaseHelper testcaseHelper = ApplicationContext.getTestcaseHelper();
	LFTestCase testcase = ApplicationContext.getTestcaseHelper().getTestcase();
	private JTextField outputStepTextField;
	private JTextField plcHolderTextField;
	private JComboBox outputStepsComboBox;
	private int selectedInputStepNum;
	private int selectedOutputStepNum;
	private JPopupMenu popupMenu;
	private JMenuItem deleteMenuItem;
	
	/** Creates new form PreferenceDialog */
	public StepLinkDialog(java.awt.Frame parent, boolean modal, int inputStepNum, int outputStepNum) {
		super(parent, modal);
		this.selectedInputStepNum = inputStepNum;
		this.selectedOutputStepNum = outputStepNum;
		initComponents();
			
	}
	
	void initComponents() {
		setTitle("Step Link");
		setIconImage(Toolkit.getDefaultToolkit().getImage(StepLinkDialog.class.getResource("/com/citigroup/liquifi/autopilot/gui/resources/icons/insert_link.png")));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setName("jScrollPane3");
		
		inputStepsComboBox = new JComboBox();
		inputStepsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				inputStepsComboBoxActionPerformed();
			}
		});
		
		JButton btnNewButton = new JButton("Link");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnNewButtonActionPerformed(evt);
			}
		});
		btnNewButton.setIcon(new ImageIcon(StepLinkDialog.class.getResource("/com/citigroup/liquifi/autopilot/gui/resources/icons/insert_link.png")));
		
		JLabel lblInputStep = new JLabel("Parent Step");
		
		JLabel lblOutputStep = new JLabel("Child (Output Step)");
		
		JLabel lblCurrentLinkingPairs = new JLabel("Current Linking Pair(s)");
		
		outputStepTextField = new JTextField();
		outputStepTextField.setColumns(10);
		outputStepTextField.setEnabled(false);
		
		JLabel lblPlaceholder = new JLabel("Placeholder");
		
		plcHolderTextField = new JTextField();
		plcHolderTextField.setColumns(10);
		
		outputStepsComboBox = new JComboBox();
		
		JLabel lblIn = new JLabel("Input");
		
		JLabel lblOut = new JLabel("Output");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblCurrentLinkingPairs))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(8)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(plcHolderTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton)
								.addComponent(lblPlaceholder, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
								.addComponent(outputStepTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblOutputStep)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblIn)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(lblInputStep)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(inputStepsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(lblOut, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(outputStepsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
							.addGap(2)))
					.addGap(5))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(12, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCurrentLinkingPairs)
						.addComponent(lblInputStep))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(inputStepsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblIn)
								.addComponent(outputStepsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblOut))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblOutputStep)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(outputStepTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblPlaceholder)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(plcHolderTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(11)
							.addComponent(btnNewButton))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		
		popupMenu = new javax.swing.JPopupMenu();
		deleteMenuItem = new JMenuItem();
		deleteMenuItem.setText("delete step link"); // NOI18N
		deleteMenuItem.setName("deleteMenuItem"); // NOI18N
		deleteMenuItem.setEnabled(true);
		deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteStepLink(evt);
            }
        });
		
		popupMenu.add(deleteMenuItem);
		
		table = new JTable();
		table.setModel(tableModel);
		scrollPane.setViewportView(table);
		table.setComponentPopupMenu(popupMenu);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tableMouseReleased(evt);
            }
            
        });
		
		getContentPane().setLayout(groupLayout);
	}

	protected void tableMouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger()){
			popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
		
	}

	protected void deleteStepLink(ActionEvent evt) {
		int selectedRow = table.getSelectedRow();
		StepLinkEntry entry = tableModel.getData().get(selectedRow);
		int[] parentStepArr = ApplicationContext.getTestcaseHelper().parseStep(entry.getParentStep());
		int[] childStepArr = ApplicationContext.getTestcaseHelper().parseStep(entry.getChildStep());
		int parentInputStep = parentStepArr[0] - 1;
		int childInputStep = childStepArr[0] - 1;
		int childOutputStep = childStepArr[1] - 1;
		String refTagValuePrefix = null;
		if(entry.getParentStep().split("\\]").length > 1){ //parent = output step
			int parentOutputStep = parentStepArr[1] - 1;
			LFOutputMsg parentStep = ((InputStepTableModel) ApplicationContext.getTestcasePanel().getInputStepTable().getModel()).getData().get(parentInputStep).getOutputStepList().get(parentOutputStep);
			// remove child ref from parent
			String oldChildrenOutputSteps = parentStep.getChildrenOutputSteps();
			String newChildrenOutputSteps = null;
			if(oldChildrenOutputSteps.contains(",")){
				if(oldChildrenOutputSteps.startsWith(entry.getChildStep() + ",")){
					newChildrenOutputSteps = oldChildrenOutputSteps.replace(entry.getChildStep() + ",", "");
				}else{
					newChildrenOutputSteps = oldChildrenOutputSteps.replace("," + entry.getChildStep(), "");
				}
			}else{
				newChildrenOutputSteps = oldChildrenOutputSteps.replace(entry.getChildStep(), "");
			}
			parentStep.setChildrenOutputSteps(newChildrenOutputSteps);
			refTagValuePrefix = "@OP";
		}else{ //parent = input step
			LFTestInputSteps parentStep = ((InputStepTableModel) ApplicationContext.getTestcasePanel().getInputStepTable().getModel()).getData().get(parentInputStep);
			// remove child ref from parent
			String oldChildrenOutputSteps = parentStep.getChildrenOutputSteps();
			String newChildrenOutputSteps = null;
			if(oldChildrenOutputSteps.contains(",")){
				if(oldChildrenOutputSteps.startsWith(entry.getChildStep() + ",")){
					newChildrenOutputSteps = oldChildrenOutputSteps.replace(entry.getChildStep() + ",", "");
				}else{
					newChildrenOutputSteps = oldChildrenOutputSteps.replace("," + entry.getChildStep(), "");
				}
			}else{
				newChildrenOutputSteps = oldChildrenOutputSteps.replace(entry.getChildStep(), "");
			}
			parentStep.setChildrenOutputSteps(newChildrenOutputSteps);
			refTagValuePrefix = "@IP";
		}
		//remove parent ref and tag from child
		LFOutputMsg childStep = ((InputStepTableModel) ApplicationContext.getTestcasePanel().getInputStepTable().getModel()).getData().get(childInputStep).getOutputStepList().get(childOutputStep);
		childStep.setParentPlaceHolder(null);
		childStep.setParentRefStep(null);
		for (Iterator<LFOutputTag> it = childStep.getOverrideTags().iterator(); it.hasNext(); ){
			LFOutputTag outputTag = it.next();
			if(outputTag.getTagID().startsWith("@APVAR") && outputTag.getTagValue().equals(refTagValuePrefix + entry.getParentStep())){
				//remove ref tag
				it.remove();
				break;
			}
		}
		
		tableModel.removeRow(selectedRow);
		
	}

	protected void btnNewButtonActionPerformed(ActionEvent evt) {
		//update DB and steplink table
		//int inputStepNum = inputStepsComboBox.getSelectedIndex() + 1;
		//LFOutputMsg outputStep = testcase.getInputStep().get(selectedInputStepNum).getOutputStepList().get(selectedoutputStepNum);
		
		if(selectedInputStepNum < 0 || selectedOutputStepNum < 0){
			JOptionPane.showMessageDialog(AutoPilotAppl.getApplication().getMainFrame(), "Please select an valid child (output) step to link to.");
			return;
		}
		
		Integer inputStepNum = (Integer) inputStepsComboBox.getSelectedItem();
		Integer outputStepNum = (Integer) outputStepsComboBox.getSelectedItem();
		
		if(inputStepNum == null || inputStepNum < 1 || (outputStepNum != null && outputStepNum < 1)){
			return;
		}
		
		if(outputStepNum == null){ //ref to input step
			// add child to parent
			LFTestInputSteps parent = testcase.getInputStepList().get(inputStepNum-1);
			parent.addChildOutputStep("[" + (selectedInputStepNum+1) + "][" + (selectedOutputStepNum+1) + "]");
			
			// add parent to child
			LFOutputMsg child = testcase.getInputStepList().get(selectedInputStepNum).getOutputStepList().get(selectedOutputStepNum);
			child.setParentRefStep("[" + inputStepNum + "]");
			child.setParentPlaceHolder(plcHolderTextField.getText());
			
			LFOutputTag tag = new LFOutputTag(plcHolderTextField.getText(), "@IP[" + inputStepNum +  "]");
			ApplicationContext.getTestcaseHelper().addOutputStepTag(tag);
		}else if(outputStepNum != null){ //ref to output step
			// add child to parent
			LFOutputMsg parent = testcase.getInputStepList().get(inputStepNum-1).getOutputStepList().get(outputStepNum-1);
			parent.addChildOutputStep("[" + (selectedInputStepNum+1) + "][" + (selectedOutputStepNum+1) + "]");

			// add parent to child
			LFOutputMsg child = testcase.getInputStepList().get(selectedInputStepNum).getOutputStepList().get(selectedOutputStepNum);
			child.setParentRefStep("[" + inputStepNum + "][" + outputStepNum + "]");
			child.setParentPlaceHolder(plcHolderTextField.getText());	
			
			LFOutputTag tag = new LFOutputTag(plcHolderTextField.getText(), "@OP[" + inputStepNum +  "][" + outputStepNum + "]");
			ApplicationContext.getTestcaseHelper().addOutputStepTag(tag);
		}
		
//		try {
//			DBUtil.getInstance().getOsm().addStepLinkToOutputMsg(plcHolderTextField.getText(), inputStepNum, outputStep);
//			DBUtil.getInstance().getIsm().addLinkedOutputMsgToInputStep(inputStepNum, outputStep);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		refreshEntryTable();
		
		//add link ref placeholder tag to output step
		ApplicationContext.getTestcaseHelper().setTestCaseChangesSavedStatus(false);

	}

	protected void inputStepsComboBoxActionPerformed() {
		// TODO Auto-generated method stub
		int inputStepNum = inputStepsComboBox.getSelectedIndex();
		List<LFTestInputSteps> inputSteps = testcase.getInputStep();
		if(inputStepNum < inputSteps.size()){
			List<LFOutputMsg> outputSteps = inputSteps.get(inputStepNum).getOutputStepList();
			Vector<Integer> outputStepsOption = new Vector<Integer>();
			if(outputSteps.size() > 0){
				outputStepsOption.add(null);
			}
			for(int i = 1; i <= outputSteps.size(); i++){
				outputStepsOption.add(i);
			}
			outputStepsComboBox.setModel(new javax.swing.DefaultComboBoxModel(outputStepsOption));
		}
		
		//System.out.println("inputStepsComboBox selection is'" + inputStepsComboBox.getSelectedItem() + "'");
		//System.out.println("outputStepsComboBox selection is'" + outputStepsComboBox.getSelectedItem() + "'");
	}

	public void populateForm(LFTestCase testcase, boolean b) {
		List<LFTestInputSteps> inputSteps = testcase.getInputStep();
		Vector<Integer> inputStepsOptions = new Vector();
		for(int i = 1; i <= inputSteps.size(); i++){
			inputStepsOptions.add(i);
		}
		inputStepsComboBox.setModel(new javax.swing.DefaultComboBoxModel(inputStepsOptions));
		if(inputSteps.size() > 0){
			List<LFOutputMsg> outputSteps = inputSteps.get(0).getOutputStepList();
			outputStepTextField.setText("Step# [" + (this.selectedInputStepNum+1) + "][" + (this.selectedOutputStepNum+1) + "]") ;
			inputStepsComboBox.setSelectedIndex(0);
			inputStepsComboBoxActionPerformed();
		}
		refreshEntryTable();
		plcHolderTextField.setText("@APVAR_REF");
	}
	
	public void refreshEntryTable(){
		
		//tableModel.setData(DBUtil.getInstance().getOsm().getStepLinkEntryTestcase(testcase.getTestID()));
		ArrayList<StepLinkTableModel.StepLinkEntry> newTableData = new  ArrayList<StepLinkTableModel.StepLinkEntry>();
		for(LFTestInputSteps inputStep : testcase.getInputStepList()){
			for(LFOutputMsg outputStep : inputStep.getOutputStepList()){
				if(outputStep.getParentRefStep() != null && outputStep.getParentRefStep().length() > 0){
					newTableData.add(new StepLinkTableModel.StepLinkEntry(outputStep.getParentRefStep(), outputStep.toString(), outputStep.getParentPlaceHolder()));
				}
			}
		}
		
		tableModel.setData(newTableData);
	}
}
