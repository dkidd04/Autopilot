package com.citigroup.liquifi.autopilot.gui;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.citigroup.liquifi.AutoPilotAppl;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.gui.model.RepeatingGroupTableModel;
import com.citigroup.liquifi.autopilot.gui.model.TagTableModel;
import com.citigroup.liquifi.autopilot.helper.TestCaseHelper;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.message.FIXMessage;
import com.citigroup.liquifi.entities.LFCommonOverwriteTag;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.entities.Step;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.DBUtil;

import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.TableModelEvent;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.ImageIcon;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author  yh95657
 */
public class SmartTagDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());
	private Map<String, LFTemplate> templateMap = DBUtil.getInstance().getTem().getAllTemplateMap();

	private JPanel useValidationTemplatePanel;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JButton cancelButton_1;
	private JButton clearButton_1;
	private JLabel inputStepNumLabel;
	private JLabel inputStepNumLabel_1;
	private JLabel tagTypeLabel;
	private JLabel tagTypeLabel_1;
	private JLabel msgLabel;
	private JButton addButton;
	private JButton addButton_1;
	private JDialog saveDialog;
	private JTextField inputStepNumTextField;
	private JComboBox tagTypeComboBox;
	private JComboBox tagValueComboBox;
	// End of variables declaration//GEN-END:variables

	private LFTestInputSteps inputStep = null;
	private LFOutputMsg outputStep = null;

	private Map <String, List<LFCommonOverwriteTag> > commonOverwriteTagMap = new TreeMap<String, List<LFCommonOverwriteTag>>();
	private JLabel tagValueContentLabel;
	private JLabel tagTypeContentLabel;
	private JLabel tagTypeContentLabel_1;
	private JLabel optionalLabel1;
	private JLabel optionalLabel2;
	private JLabel outputStepNumLabel;
	private JTextField tagTypeContentTextField;
	private JTextField tagValueContentTextField;
	private JTextField optionalTextField1;
	private JTextField optionalTextField2;
	private JLabel tagValueLabel;

	private boolean isInputStep;
	private JTextField outputStepNumTextField;
	private JLabel val_StepOptionLabel;
	private JComboBox val_inputOutputStepComboBox;
	private JLabel val_InputStepLabel;
	private JLabel val_outputStepLabel;
	private JTextField val_inputStepTextField;
	private JTextField val_outputStepTextField;
	private JLabel val_refPlaceHolderLabel;
	private JTextField val_refPlaceHolderTextField;
	private JTable validateTagTable;
	private JScrollPane scrollPane;
	private JPanel addNewTagPanel;
	private JTabbedPane tabbedPane;
	private JComboBox validationTemplateComboBox;
	private JComboBox repeatingGroupComboBox;
	private JLabel validationTemplateLabel;

	private TestCaseHelper testCaseHelper = ApplicationContext.getTestcaseHelper();
	private TagTableModel<LFTag> validationTableModel = testCaseHelper.getValidationTagModel();
	private TagTableModel<LFTag> validtionTamplateTableModel = testCaseHelper.getValidationTemplateTableModel();
	private RepeatingGroupTableModel<Tag> repeatingGroupTagTableModel = testCaseHelper.getRepeatingGroupTagTableModel();
	private JLabel lblTemplateContents;
	private JPanel addValidationTemplatePanel;
	private JPanel repeatingGroupPanel;
	private JLabel newTemplateNameLabel;
	private JLabel lblRepeatingGroup;
	private JTextField newTemplateNameTextField;
	private JTextField newValidationTemplateTagTextField;
	private JTable newTemplateContentTable;
	private JButton addNewValidationTemplateButton;
	private JButton deleteTagFromTemplateButton;
	private javax.swing.JButton okButton;
	private JLabel templateTagTypeLabel;
	private JComboBox templateTagTypeComboBox;
	private JScrollPane scrollPane_2;
	private JTable repeatingGroupTagsTable;
	private JScrollPane scrollPane_1;
	private JLabel lblTagToAdd;

	private Map<String, RepeatingGroup> repeatingGroups;

	/** Creates new form PreferenceDialog */
	public SmartTagDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		setTitle("SmartTag");
		commonOverwriteTagMap=DBUtil.getInstance().getCom().getCommonOverwriteTagMap();
		initComponents();
		init();
	}

	private void init() {		
		repeatingGroups = new TreeMap<String, RepeatingGroup>();
		validationTableModel.setData(new ArrayList<LFTag>());
		validtionTamplateTableModel.setData(new ArrayList<LFTag>());
		//injectValidationTags();
		//TODO for demo purpose only
		//setupSampleTag();
		val_inputOutputStepComboBox.setEnabled(false);
		val_inputStepTextField.setEnabled(false);
		val_outputStepTextField.setEnabled(false);
		val_refPlaceHolderTextField.setEnabled(false);
	}

	private void loadRepeatingGroupInMessage(boolean isInputStep){
		
		String fixMsgStr;
		FIXMessage fixMsg;
		
		
		if(isInputStep){
			fixMsgStr = templateMap.get(inputStep.getTemplate()).getMsgTemplate();
			fixMsg = ApplicationContext.getFIXFactory().genFIXMessage(fixMsgStr, inputStep.getTags());
		}else{
			fixMsgStr = templateMap.get(outputStep.getTemplate()).getMsgTemplate();
			fixMsg = ApplicationContext.getFIXFactory().genFIXMessageForOutputStep(fixMsgStr, outputStep.getTags());
		}
		
		// load group number >= 2
		for(Entry<String, String> e : fixMsg.getTagMap().entrySet()){
			String tagKey = e.getKey();
			if(tagKey.contains("#")){
				int groupNum = getRepeatingGroupNum(tagKey); //e.g. groupNum of "###123" = 4;
				RepeatingGroup rptGrp = this.repeatingGroups.get(String.valueOf(groupNum));
				if(rptGrp == null){
					rptGrp = new RepeatingGroup();
					this.repeatingGroups.put(String.valueOf(groupNum), rptGrp);
				}
				rptGrp.addTag(tagKey.replaceAll("#", ""), e.getValue());
			}
		}
		
		// load group number == 1
		if(this.repeatingGroups.size() > 0){
			RepeatingGroup existingGrp = this.repeatingGroups.get("2"); //should at least have group 2 to be considered at repeating group
			RepeatingGroup newGrp = new RepeatingGroup();
			for(Tag tag : existingGrp.getTags()){
				String tagId = tag.getTagID();
				newGrp.addTag(tagId, fixMsg.getTagMap().get(tagId));
			}
			this.repeatingGroups.put("1", newGrp);
		}
		
		repeatingGroupComboBox.setModel(new javax.swing.DefaultComboBoxModel((String[]) this.repeatingGroups.keySet().toArray(new String[this.repeatingGroups.keySet().size()])));
	}

	private void setupSampleTag() {

		String refPlaceHolder = val_refPlaceHolderTextField.getText();
		ArrayList<Tag> tagList = new ArrayList<Tag>();

		//TODO for testing only
		LFTag tag1 = new LFTag("32", "@" + refPlaceHolder + ".getTag(32)");
		tagList.add(tag1);

		LFTag tag2 = new LFTag("40", "@" + refPlaceHolder + ".getTag(40)");
		tagList.add(tag2);

		LFTag tag3 = new LFTag("10625", "@" + refPlaceHolder + ".getTag(10625)");
		tagList.add(tag3);

		LFTag tag4 = new LFTag("10626", "@" + refPlaceHolder + ".getTag(10626)");
		tagList.add(tag4);


		//validationTemplateComboBox.setModel(new javax.swing.DefaultComboBoxModel(ApplicationContext.getTestRunPanel().templateToTagMap.keySet().toArray()));
		//validationTemplateComboBox.setSelectedItem("Sample Template");
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("Form"); // NOI18N
		setResizable(true);


		saveDialog = new javax.swing.JDialog();
		msgLabel = new javax.swing.JLabel();
		inputStepNumLabel = new javax.swing.JLabel();
		tagTypeLabel = new javax.swing.JLabel();
		tagTypeContentLabel = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();

		okButton.setText("OK"); // NOI18N
		okButton.setName("okButton"); // NOI18N
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		//String[] tagTypeOptions = new String[] { "Other", "ClientID", "Side", "OrderPrice", "OrderQuanity", "FarPrice", "NearPrice", "Reference To Input Step", "Reference To Output Step", "ExpireTime", "OrderID", "Price Of This(Last) Fill", "Qty On This(Last) Fill"  };
		String[] tagTypeOptions = new String[] { "Other", "ClientID", "Side", "OrderPrice", "OrderQuanity", "FarPrice", "NearPrice", "ExpireTime", "OrderID", "Price Of This(Last) Fill", "Qty On This(Last) Fill"  };
		String[] validationTagTypeOptions = new String[] { "Other", "ClientID", "Side", "OrderPrice", "OrderQuanity", "FarPrice", "NearPrice", "ExpireTime", "OrderID", "Price Of This(Last) Fill", "Qty On This(Last) Fill"  };
		String[] tagValueOptions = new String[] { "Other", "Get Tag Value From Referenced Step", "Remove Tag from Msg"};
		//String[] valInputOutputStepOptions = new String[] {"Reference To Input Step", "Reference To Output Step"};
		String[] valInputOutputStepOptions = new String[] {"Reference To Input Step"};
		javax.swing.GroupLayout saveDialogLayout = new javax.swing.GroupLayout(saveDialog.getContentPane());
		saveDialog.getContentPane().setLayout(saveDialogLayout);
		saveDialogLayout.setHorizontalGroup(
				saveDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(saveDialogLayout.createSequentialGroup()
						.addGroup(saveDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(saveDialogLayout.createSequentialGroup()
										.addGap(42, 42, 42)
										.addComponent(msgLabel))
										.addGroup(saveDialogLayout.createSequentialGroup()
												.addGap(113, 113, 113)
												.addComponent(okButton)))
												.addContainerGap(36, Short.MAX_VALUE))
				);
		saveDialogLayout.setVerticalGroup(
				saveDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(saveDialogLayout.createSequentialGroup()
						.addGap(27, 27, 27)
						.addComponent(msgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(okButton)
						.addContainerGap(23, Short.MAX_VALUE))
				);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 901, Short.MAX_VALUE)
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
				);

		addNewTagPanel = new JPanel();
		tabbedPane.addTab("To Add New Tag", null, addNewTagPanel, null);

		inputStepNumLabel_1 = new JLabel();
		inputStepNumLabel_1.setText("Input Step #");
		inputStepNumLabel_1.setName("jLabel1");

		outputStepNumLabel = new JLabel();
		outputStepNumLabel.setText("Output Step #");
		outputStepNumLabel.setName("jLabel1");

		tagTypeLabel_1 = new JLabel();
		tagTypeLabel_1.setText("Tag Type");
		tagTypeLabel_1.setName("jLabel2");

		tagTypeContentLabel_1 = new JLabel();
		tagTypeContentLabel_1.setText("Tag Type Content");
		tagTypeContentLabel_1.setName("jLabel3");

		inputStepNumTextField = new JTextField();
		inputStepNumTextField.setText("stepNumTextField");
		inputStepNumTextField.setName("stepNumTextField");
		inputStepNumTextField.setEditable(false);

		outputStepNumTextField = new JTextField();
		outputStepNumTextField.setText("stepNumTextField");
		outputStepNumTextField.setName("stepNumTextField");
		outputStepNumTextField.setEditable(false);

		tagTypeComboBox = new JComboBox();
		tagTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(tagTypeOptions));
		tagTypeComboBox.setName("tagTypeComboBox"); // NOI18N
		tagTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tagTypeComboBoxActionPerformed(evt);
			}
		});

		tagTypeContentTextField = new JTextField();
		tagTypeContentTextField.setColumns(10);

		tagValueLabel = new JLabel("Tag Value");

		tagValueComboBox = new JComboBox();
		tagValueComboBox.setName("templateComboBox");
		tagValueComboBox.setModel(new javax.swing.DefaultComboBoxModel(tagValueOptions));
		tagValueComboBox.setName("tagValueComboBox"); // NOI18N
		tagValueComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tagValueComboBoxActionPerformed(evt);
			}
		});

		tagValueContentLabel = new JLabel();
		tagValueContentLabel.setText("Tag Value Content");
		tagValueContentLabel.setName("jLabel3");

		tagValueContentTextField = new JTextField();
		tagValueContentTextField.setColumns(10);

		optionalTextField1 = new JTextField();
		optionalTextField1.setColumns(10);
		optionalTextField1.setVisible(false);

		optionalTextField1.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {

			}

			public void keyReleased(KeyEvent keyEvent) {
				optionalTextField1ActionPerformed(keyEvent);
			}

			public void keyTyped(KeyEvent keyEvent) {

			}

		});

		optionalTextField2 = new JTextField();
		optionalTextField2.setColumns(10);
		optionalTextField2.setVisible(false);

		optionalTextField2.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {

			}

			public void keyReleased(KeyEvent keyEvent) {
				optionalTextField12ActionPerformed(keyEvent);
			}

			public void keyTyped(KeyEvent keyEvent) {

			}

		});

		optionalLabel1 = new JLabel();
		optionalLabel1.setText("Input Step #");
		optionalLabel1.setName("jLabel3");
		optionalLabel1.setVisible(false);

		optionalLabel2 = new JLabel();
		optionalLabel2.setText("Output Step #");
		optionalLabel2.setName("outputStepLabel");
		optionalLabel2.setVisible(false);

		addButton_1 = new JButton();
		addButton_1.setText("Add Tag"); // NOI18N
		addButton_1.setName("saveButton"); // NOI18N
		addButton_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addTagButtonActionPerformed(evt);
			}
		});

		clearButton_1 = new JButton();
		clearButton_1.setText("Clear"); // NOI18N
		clearButton_1.setName("clearButton"); // NOI18N
		clearButton_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearButtonActionPerformed(evt);
			}
		});

		cancelButton_1 = new JButton();
		cancelButton_1.setText("Cancel"); // NOI18N
		cancelButton_1.setName("cancelButton"); // NOI18N
		cancelButton_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});


		GroupLayout gl_addNewTagPanel = new GroupLayout(addNewTagPanel);
		gl_addNewTagPanel.setHorizontalGroup(
				gl_addNewTagPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addNewTagPanel.createSequentialGroup()
						.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_addNewTagPanel.createSequentialGroup()
										.addGap(31)
										.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_addNewTagPanel.createSequentialGroup()
														.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.LEADING)
																.addComponent(inputStepNumLabel_1, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
																.addComponent(outputStepNumLabel, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
																.addComponent(tagTypeLabel_1, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(ComponentPlacement.RELATED)
																.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.LEADING)
																		.addGroup(gl_addNewTagPanel.createSequentialGroup()
																				.addComponent(tagTypeComboBox, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
																				.addGap(18)
																				.addComponent(tagValueLabel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
																				.addGap(4)
																				.addComponent(tagValueComboBox, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE))
																				.addComponent(outputStepNumTextField, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
																				.addComponent(inputStepNumTextField, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)))
																				.addGroup(gl_addNewTagPanel.createSequentialGroup()
																						.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.TRAILING)
																								.addComponent(optionalLabel2, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
																								.addGroup(gl_addNewTagPanel.createSequentialGroup()
																										.addComponent(tagTypeContentLabel_1, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(ComponentPlacement.RELATED)
																										.addComponent(tagTypeContentTextField, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
																										.addGap(18)
																										.addComponent(tagValueContentLabel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
																										.addComponent(optionalLabel1, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
																										.addPreferredGap(ComponentPlacement.RELATED)
																										.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.LEADING)
																												.addComponent(optionalTextField1, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
																												.addComponent(tagValueContentTextField, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
																												.addComponent(optionalTextField2, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)))))
																												.addGroup(gl_addNewTagPanel.createSequentialGroup()
																														.addGap(273)
																														.addComponent(addButton_1, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
																														.addGap(18)
																														.addComponent(clearButton_1, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
																														.addGap(18)
																														.addComponent(cancelButton_1, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)))
																														.addContainerGap(129, Short.MAX_VALUE))
				);
		gl_addNewTagPanel.setVerticalGroup(
				gl_addNewTagPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addNewTagPanel.createSequentialGroup()
						.addGap(25)
						.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(inputStepNumLabel_1)
								.addComponent(inputStepNumTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(18)
								.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(outputStepNumLabel)
										.addComponent(outputStepNumTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.BASELINE)
												.addComponent(tagTypeLabel_1)
												.addComponent(tagTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(tagValueLabel)
												.addComponent(tagValueComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(18)
												.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.BASELINE)
														.addComponent(tagTypeContentLabel_1)
														.addComponent(tagTypeContentTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(tagValueContentLabel)
														.addComponent(tagValueContentTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
														.addGap(18)
														.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.TRAILING)
																.addComponent(optionalTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																.addComponent(optionalLabel1))
																.addGap(18)
																.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.BASELINE)
																		.addComponent(optionalTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addComponent(optionalLabel2))
																		.addGap(47)
																		.addGroup(gl_addNewTagPanel.createParallelGroup(Alignment.BASELINE)
																				.addComponent(addButton_1)
																				.addComponent(clearButton_1)
																				.addComponent(cancelButton_1))
																				.addContainerGap(218, Short.MAX_VALUE))
				);
		addNewTagPanel.setLayout(gl_addNewTagPanel);

		useValidationTemplatePanel = new JPanel();
		tabbedPane.addTab("To Use Validation Template", null, useValidationTemplatePanel, null);

		val_StepOptionLabel = new JLabel();
		val_StepOptionLabel.setText("Reference Step Type");
		val_StepOptionLabel.setName("jLabel1");

		val_inputOutputStepComboBox = new JComboBox();
		val_inputOutputStepComboBox.setModel(new javax.swing.DefaultComboBoxModel(valInputOutputStepOptions));
		val_inputOutputStepComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				valInputOutputStepComboBoxActionPerformed(evt);
			}
		});
		val_inputOutputStepComboBox.setName("tagTypeComboBox");

		val_InputStepLabel = new JLabel();
		val_InputStepLabel.setText("Input Step #");
		val_InputStepLabel.setName("jLabel1");

		val_outputStepLabel = new JLabel();
		val_outputStepLabel.setText("Output Step #");
		val_outputStepLabel.setName("jLabel1");
		val_outputStepLabel.setVisible(false);

		val_inputStepTextField = new JTextField();
		val_inputStepTextField.setColumns(10);

		val_inputStepTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {

			}

			public void keyReleased(KeyEvent keyEvent) {
				val_inputStepTextFieldActionPerformed();
			}

			public void keyTyped(KeyEvent keyEvent) {

			}

		});

		val_outputStepTextField = new JTextField();
		val_outputStepTextField.setColumns(10);
		val_outputStepTextField.setVisible(false);

		val_outputStepTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {

			}

			public void keyReleased(KeyEvent keyEvent) {
				val_outputputStepTextFieldActionPerformed(keyEvent);
			}

			public void keyTyped(KeyEvent keyEvent) {

			}

		});

		val_refPlaceHolderLabel = new JLabel();
		val_refPlaceHolderLabel.setText("Reference Place Holder");
		val_refPlaceHolderLabel.setName("jLabel1");

		val_refPlaceHolderTextField = new JTextField();
		val_refPlaceHolderTextField.setColumns(10);
		val_refPlaceHolderTextField.setText("APVAR_REF");
		val_refPlaceHolderTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {

			}

			public void keyReleased(KeyEvent keyEvent) {
				val_refPlaceHolderTextFieldActionPerformed(keyEvent);
			}

			public void keyTyped(KeyEvent keyEvent) {

			}

		});

		scrollPane = new JScrollPane();

		validationTemplateLabel = new JLabel();
		validationTemplateLabel.setText("Validation Template");
		validationTemplateLabel.setName("jLabel1");

		validationTemplateComboBox = new JComboBox();
		validationTemplateComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				validationTemplateComboBoxActionPerformed(evt);
			}
		});
		validationTemplateComboBox.setName("tagTypeComboBox");
		validationTemplateComboBox.setModel(new javax.swing.DefaultComboBoxModel((String[]) commonOverwriteTagMap.keySet().toArray(new String[commonOverwriteTagMap.keySet().size()])));


		JButton btnAddValidationTemplate = new JButton("Add Validation Template");
		btnAddValidationTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addValidationTemplateButtonActionPerformed(evt);
			}
		});

		lblTemplateContents = new JLabel("Template Contents");
		lblTemplateContents.setAlignmentX(Component.CENTER_ALIGNMENT);

		GroupLayout gl_useValidationTemplatePanel = new GroupLayout(useValidationTemplatePanel);
		gl_useValidationTemplatePanel.setHorizontalGroup(
				gl_useValidationTemplatePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
						.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
										.addContainerGap(30, Short.MAX_VALUE)
										.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
														.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.TRAILING, false)
																.addComponent(val_StepOptionLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addComponent(val_InputStepLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addComponent(val_outputStepLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addComponent(val_refPlaceHolderLabel, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
																.addGap(18)
																.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.LEADING)
																		.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.LEADING, false)
																				.addComponent(val_outputStepTextField, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
																				.addComponent(val_refPlaceHolderTextField, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
																				.addComponent(val_inputStepTextField))
																				.addComponent(val_inputOutputStepComboBox, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)))
																				.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
																						.addComponent(validationTemplateLabel, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
																						.addGap(18)
																						.addComponent(validationTemplateComboBox, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)))
																						.addGap(45))
																						.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
																								.addGap(117)
																								.addComponent(btnAddValidationTemplate, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(ComponentPlacement.RELATED)))
																								.addGap(4)
																								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE)
																								.addGap(70))
																								.addGroup(Alignment.TRAILING, gl_useValidationTemplatePanel.createSequentialGroup()
																										.addContainerGap(584, Short.MAX_VALUE)
																										.addComponent(lblTemplateContents, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
																										.addGap(211))
				);
		gl_useValidationTemplatePanel.setVerticalGroup(
				gl_useValidationTemplatePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblTemplateContents)
						.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
										.addGap(8)
										.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
												.addComponent(validationTemplateLabel)
												.addComponent(validationTemplateComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(18)
												.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
														.addComponent(val_inputOutputStepComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(val_StepOptionLabel))
														.addGap(18)
														.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
																.addComponent(val_InputStepLabel)
																.addComponent(val_inputStepTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																.addGap(21)
																.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
																		.addComponent(val_outputStepLabel)
																		.addComponent(val_outputStepTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																		.addGap(18)
																		.addGroup(gl_useValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
																				.addComponent(val_refPlaceHolderLabel)
																				.addComponent(val_refPlaceHolderTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																				.addGap(33)
																				.addComponent(btnAddValidationTemplate))
																				.addGroup(gl_useValidationTemplatePanel.createSequentialGroup()
																						.addPreferredGap(ComponentPlacement.RELATED)
																						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)))
																						.addContainerGap(246, Short.MAX_VALUE))
				);

		validateTagTable = new JTable();
		validateTagTable.setModel(validationTableModel);
		validateTagTable.getColumnModel().getColumn(0).setHeaderValue("Output Tag"); // NOI18N
		validateTagTable.getColumnModel().getColumn(1).setHeaderValue("Expected Value"); // NOI18N

		scrollPane.setViewportView(validateTagTable);
		useValidationTemplatePanel.setLayout(gl_useValidationTemplatePanel);

		addValidationTemplatePanel = new JPanel();
		tabbedPane.addTab("To Create New Validation Template", null, addValidationTemplatePanel, null);

		newTemplateNameLabel = new JLabel("New Template Name");

		newTemplateNameTextField = new JTextField();
		newTemplateNameTextField.setColumns(10);

		lblTagToAdd = new JLabel("Tag Type Content To Add ");

		newValidationTemplateTagTextField = new JTextField();
		newValidationTemplateTagTextField.setColumns(10);

		JButton newValidationTemplateTagButton = new JButton("Add Tag ");
		newValidationTemplateTagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newValidationTemplateTagButtonActionPerformed(evt);
			}
		});

		scrollPane_1 = new JScrollPane();

		JLabel lblNewLabel = new JLabel("Template Contents");

		addNewValidationTemplateButton = new JButton("Add New Template");
		addNewValidationTemplateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addNewValidationTemplateButtionActionPerformed();
			}
		});

		deleteTagFromTemplateButton = new JButton("Remove Selected Tag(s)");
		deleteTagFromTemplateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deleteTagFromTemplateButtonActionPerformed();
			}
		});

		templateTagTypeLabel = new JLabel();
		templateTagTypeLabel.setText("Tag Type To Add");
		templateTagTypeLabel.setName("jLabel2");

		templateTagTypeComboBox = new JComboBox();
		templateTagTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				templateTagTypeComboBoxActionPerformed(evt);
			}
		});
		templateTagTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(validationTagTypeOptions));
		templateTagTypeComboBox.setName("tagTypeComboBox");

		GroupLayout gl_addValidationTemplatePanel = new GroupLayout(addValidationTemplatePanel);
		gl_addValidationTemplatePanel.setHorizontalGroup(
				gl_addValidationTemplatePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_addValidationTemplatePanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_addValidationTemplatePanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_addValidationTemplatePanel.createSequentialGroup()
										.addGroup(gl_addValidationTemplatePanel.createParallelGroup(Alignment.LEADING, false)
												.addComponent(lblTagToAdd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(templateTagTypeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(newTemplateNameLabel, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
												.addGap(18)
												.addGroup(gl_addValidationTemplatePanel.createParallelGroup(Alignment.LEADING, false)
														.addComponent(templateTagTypeComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(newValidationTemplateTagTextField)
														.addComponent(addNewValidationTemplateButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(newTemplateNameTextField, GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(newValidationTemplateTagButton)
														.addGap(57)
														.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE))
														.addComponent(deleteTagFromTemplateButton))
														.addContainerGap(101, Short.MAX_VALUE))
														.addGroup(gl_addValidationTemplatePanel.createSequentialGroup()
																.addContainerGap(583, Short.MAX_VALUE)
																.addComponent(lblNewLabel)
																.addGap(222))
				);
		gl_addValidationTemplatePanel.setVerticalGroup(
				gl_addValidationTemplatePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addValidationTemplatePanel.createSequentialGroup()
						.addGroup(gl_addValidationTemplatePanel.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_addValidationTemplatePanel.createSequentialGroup()
										.addContainerGap()
										.addComponent(lblNewLabel)
										.addGap(11)
										.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_addValidationTemplatePanel.createSequentialGroup()
												.addGap(43)
												.addGroup(gl_addValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
														.addComponent(newTemplateNameLabel)
														.addComponent(newTemplateNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
														.addGap(18)
														.addGroup(gl_addValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
																.addComponent(templateTagTypeLabel)
																.addComponent(templateTagTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																.addGap(18)
																.addGroup(gl_addValidationTemplatePanel.createParallelGroup(Alignment.BASELINE)
																		.addComponent(lblTagToAdd)
																		.addComponent(newValidationTemplateTagTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addComponent(newValidationTemplateTagButton))
																		.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(addNewValidationTemplateButton)))
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(deleteTagFromTemplateButton)
																		.addContainerGap(301, Short.MAX_VALUE))
				);

		newTemplateContentTable = new JTable();
		newTemplateContentTable.setRowSelectionAllowed(true);
		newTemplateContentTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		newTemplateContentTable.setModel(validtionTamplateTableModel);
		newTemplateContentTable.getColumnModel().getColumn(0).setHeaderValue("Output Tag"); // NOI18N
		newTemplateContentTable.getColumnModel().getColumn(1).setHeaderValue("Expected Value"); // NOI18N

		scrollPane_1.setViewportView(newTemplateContentTable);
		addValidationTemplatePanel.setLayout(gl_addValidationTemplatePanel);

		repeatingGroupPanel = new JPanel();
		// for further improvements of the Repeating Group view in SmartTag
		tabbedPane.addTab("Repeating Group", null, repeatingGroupPanel, null);

		lblRepeatingGroup = new JLabel("Repeating Group in message:");

		repeatingGroupComboBox = new JComboBox();
		repeatingGroupComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				RepeatingGroupComboBoxActionPerformed(evt);
			}
		});

		scrollPane_2 = new JScrollPane();



		GroupLayout gl_repeatingGroupPanel = new GroupLayout(repeatingGroupPanel);
		gl_repeatingGroupPanel.setHorizontalGroup(
				gl_repeatingGroupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_repeatingGroupPanel.createSequentialGroup()
						.addGap(31)
						.addGroup(gl_repeatingGroupPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_repeatingGroupPanel.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(repeatingGroupComboBox, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblRepeatingGroup, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
										.addContainerGap(524, Short.MAX_VALUE))
				);
		gl_repeatingGroupPanel.setVerticalGroup(
				gl_repeatingGroupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_repeatingGroupPanel.createSequentialGroup()
						.addGap(29)
						.addComponent(lblRepeatingGroup)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(repeatingGroupComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(276, Short.MAX_VALUE))
				);

		repeatingGroupTagsTable = new JTable();
		repeatingGroupTagsTable.setRowSelectionAllowed(true);
		repeatingGroupTagsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		repeatingGroupTagsTable.setModel(repeatingGroupTagTableModel);
		repeatingGroupTagsTable.getColumnModel().getColumn(0).setHeaderValue("Output Tag"); // NOI18N
		repeatingGroupTagsTable.getColumnModel().getColumn(1).setHeaderValue("Expected Value"); // NOI18N

		scrollPane_2.setViewportView(repeatingGroupTagsTable);
		repeatingGroupPanel.setLayout(gl_repeatingGroupPanel);
		getContentPane().setLayout(layout);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	protected void RepeatingGroupComboBoxActionPerformed(ActionEvent evt) {
		String selectedRepeatingGroup = (String) repeatingGroupComboBox.getSelectedItem();
		repeatingGroupTagTableModel.setGroupNum(selectedRepeatingGroup);
		repeatingGroupTagTableModel.getData().clear();
		repeatingGroupTagTableModel.setTagListAsData(this.repeatingGroups.get(selectedRepeatingGroup).getTags());		
	}

	protected void templateTagTypeComboBoxActionPerformed(ActionEvent evt) {

		//tagTypeOptions  = { "Other", "ClientID", "Side", "OrderPrice", "OrderQuanity", "FarPrice", "NearPrice", "Reference To Input Step", "Reference To Output Step", "ExpireTime", "OrderID", "Price Of This(Last) Fill", "Qty On This(Last) Fill"  };

		String tagType = (String) templateTagTypeComboBox.getSelectedItem();

		if(tagType.equals("Other")){

			newValidationTemplateTagTextField.setText("");

		}else if(tagType.equals("ClientID")){

			newValidationTemplateTagTextField.setText("10270");

		}else if(tagType.equals("Side")){

			newValidationTemplateTagTextField.setText("54");

		}else if(tagType.equals("OrderPrice")){

			newValidationTemplateTagTextField.setText("44");

		}else if(tagType.equals("OrderQuanity")){

			newValidationTemplateTagTextField.setText("38");

		}else if(tagType.equals("FarPrice")){

			newValidationTemplateTagTextField.setText("10626");

		}else if(tagType.equals("NearPrice")){

			newValidationTemplateTagTextField.setText("10625");

		}else if(tagType.equals("NearPrice")){

			newValidationTemplateTagTextField.setText("10625");

		}else if(tagType.equals("ExpireTime")){

			newValidationTemplateTagTextField.setText("126");			

		}else if(tagType.equals("OrderID")){

			newValidationTemplateTagTextField.setText("37");

		}else if(tagType.equals("Price Of This(Last) Fill")){

			newValidationTemplateTagTextField.setText("31");

		}else if(tagType.equals("Qty On This(Last) Fill")){

			newValidationTemplateTagTextField.setText("32");

		}


	}

	protected void resetSmartTagValidationPane(){
		validationTableModel.setData(new ArrayList<LFTag>());
		val_inputOutputStepComboBox.setSelectedItem("");
		val_refPlaceHolderTextField.setText("APVAR_REF");
	}

	protected void validationTemplateComboBoxActionPerformed(ActionEvent evt) {
		resetSmartTagValidationPane();
		String selectedTemplateName = (String) validationTemplateComboBox.getSelectedItem();
		//		
		//		ArrayList<Tag> tagsInTemplate = ApplicationContext.getTestRunPanel().templateToTagMap.get(selectedTemplateName);	
		//		if(tagsInTemplate != null){
		//			for(Tag t : tagsInTemplate){
		//				ApplicationContext.getTestcaseHelper().addValidationTag((LFTag) t);
		//			}
		//		}
		boolean toEnableInput = false;
		if(selectedTemplateName.startsWith("<Smart Tag> ")){
			toEnableInput = true;

		}
		val_inputOutputStepComboBox.setEnabled(toEnableInput);
		val_inputStepTextField.setEnabled(toEnableInput);
		val_outputStepTextField.setEnabled(toEnableInput);
		val_refPlaceHolderTextField.setEnabled(toEnableInput);

		List<LFCommonOverwriteTag> listTag = commonOverwriteTagMap.get(validationTemplateComboBox.getSelectedItem());
		if (listTag != null ) {

			validationTableModel.getData().clear();
			validationTableModel.setListAsData(listTag);
			if(toEnableInput){
				LFTag tag = new LFTag("@APVAR_REF", "@IP[]");
				validationTableModel.add(tag);
			}
		} else {
			validationTableModel.getData().clear();
			validationTableModel.setListAsData(new ArrayList<LFCommonOverwriteTag>());
		}
	}

	protected void okButtonActionPerformed(ActionEvent evt) {
		saveDialog.setVisible(false);
	}

	protected void addNewValidationTemplateButtionActionPerformed() {
		boolean valid = validateNewValidationTemplateData();

		if(valid){

			String templateName = "<Smart Tag> " + newTemplateNameTextField.getText();
			ArrayList<LFCommonOverwriteTag> tagList = new ArrayList<LFCommonOverwriteTag>();
			//System.out.println("here");
			String tagName;
			String tagValue;
			for(int row = 0; row < validtionTamplateTableModel.getRowCount(); row++){
				tagName = (String) validtionTamplateTableModel.getValueAt(row, 0);
				tagValue = (String) validtionTamplateTableModel.getValueAt(row, 1);
				LFCommonOverwriteTag tag = new LFCommonOverwriteTag(ApplicationContext.getTestcaseHelper().getTestcase().getAppName(), templateName, tagName, tagValue);
				DBUtil.getInstance().saveToDB(tag);
			}

			//			ApplicationContext.getTestRunPanel().templateToTagMap.put(templateName, tagList);
			//			validationTemplateComboBox.setModel(new javax.swing.DefaultComboBoxModel(ApplicationContext.getTestRunPanel().templateToTagMap.keySet().toArray()));
			//			validationTemplateComboBox.setSelectedIndex(0);
			DBUtil.getInstance().getCom().loadAllCommonOverwriteTagFromDB();
			commonOverwriteTagMap = DBUtil.getInstance().getCom().getCommonOverwriteTagMap();
			validationTemplateComboBox.setModel(new javax.swing.DefaultComboBoxModel((String[]) commonOverwriteTagMap.keySet().toArray(new String[commonOverwriteTagMap.keySet().size()])));
			resetNewValidationTemplatePane();
			JOptionPane.showMessageDialog(AutoPilotAppl.getApplication().getMainFrame(), "New SmartTag Validation Template Successfully Created!");


		}else{
			msgLabel.setText("Required Fields are not Populated");
			AutoPilotAppl.getApplication().show(saveDialog);
		}

	}

	protected void resetNewValidationTemplatePane(){
		validtionTamplateTableModel.setData(new ArrayList<LFTag>());
		newTemplateNameTextField.setText("");
		newValidationTemplateTagTextField.setText("");
		templateTagTypeComboBox.setSelectedItem("Other");
	}

	protected void deleteTagFromTemplateButtonActionPerformed() {
		if(newTemplateContentTable.getSelectedRow() != -1){
			int[] selectedRowIdxes = newTemplateContentTable.getSelectedRows();

			for(int idx = selectedRowIdxes.length-1; idx >= 0; idx--){
				ApplicationContext.getTestcaseHelper().removeValidationTemplateTag(newTemplateContentTable.convertRowIndexToModel(selectedRowIdxes[idx]));
			}
		}


	}

	protected void newValidationTemplateTagButtonActionPerformed(ActionEvent evt) {

		String tagValue = newValidationTemplateTagTextField.getText();
		if(tagValue.length() > 0){
			LFTag tag = new LFTag(tagValue, "@APVAR_REF.getTag(" + tagValue + ")");
			ApplicationContext.getTestcaseHelper().addNewValidationTemplateTag(tag);
		}
		templateTagTypeComboBox.setSelectedItem("Other");
	}

	protected void addValidationTemplateButtonActionPerformed(ActionEvent evt) {

		boolean valid = validateValidationTemplateData();
		if(valid){

			String tagName;
			String tagValue;

			if(isInputStep){

				//System.out.println("here");
				for(int row = 0; row < validationTableModel.getRowCount(); row++){
					tagName = (String) validationTableModel.getValueAt(row, 0);
					tagValue = (String) validationTableModel.getValueAt(row, 1);
					LFTag tag = new LFTag(tagName, tagValue);
					ApplicationContext.getTestcaseHelper().addInputStepTag(tag);
				}

			}else{
				//System.out.println("there");
				for(int row = 0; row < validationTableModel.getRowCount(); row++){
					tagName = (String) validationTableModel.getValueAt(row, 0);
					tagValue = (String) validationTableModel.getValueAt(row, 1);
					LFOutputTag tag = new LFOutputTag(tagName, tagValue);
					ApplicationContext.getTestcaseHelper().addOutputStepTag(tag);
				}

			}

			this.dispose();
			//((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showMainPanel();
			//ApplicationContext.getTestcaseHelper().setTestCaseChangeStatus(true);
			ApplicationContext.getTestcaseHelper().setTestCaseChangesSavedStatus(false);
		}else{
			msgLabel.setText("Required Fields are not Populated");
			AutoPilotAppl.getApplication().show(saveDialog);
		}


	}





	protected void val_outputputStepTextFieldActionPerformed(KeyEvent keyEvent) {

		String outputStep = val_outputStepTextField.getText();
		String tagContent = (String) this.validationTableModel.getValueAt(0, 1);
		int i1 = tagContent.lastIndexOf("[");
		int i2 = tagContent.lastIndexOf("]");
		String tagContent1 = tagContent.substring(0, i1+1);
		String tagContent2 = tagContent.substring(i2, tagContent.length());
		this.validationTableModel.setValueAt(tagContent1 + outputStep + tagContent2, 0, 1);

	}

	protected void val_inputStepTextFieldActionPerformed() {

		String inputStep = val_inputStepTextField.getText();
		String tagContent = (String) this.validationTableModel.getValueAt(0, 1);
		int i1 = tagContent.indexOf("[");
		int i2 = tagContent.indexOf("]");
		String tagContent1 = tagContent.substring(0, i1+1);
		String tagContent2 = tagContent.substring(i2, tagContent.length());
		this.validationTableModel.setValueAt(tagContent1 + inputStep + tagContent2, 0, 1);

	}

	protected void val_refPlaceHolderTextFieldActionPerformed(KeyEvent keyEvent) {
		//this.validationTableModel.getValueAt(0, 0);
		String newPlaceHolder = val_refPlaceHolderTextField.getText();
		this.validationTableModel.setValueAt("@" + newPlaceHolder , 0, 0);

		for(int row = 1; row<this.validationTableModel.getRowCount(); row++){
			String oldContent = (String) this.validationTableModel.getValueAt(row, 1);
			int idx = oldContent.indexOf(".");
			String oldContent2 = oldContent.substring(idx);
			this.validationTableModel.setValueAt("@" + newPlaceHolder + oldContent2 , row, 1);
		}

	}

	protected void valInputOutputStepComboBoxActionPerformed(ActionEvent evt) {

		//tagTypeOptions  = { "Other", "ClientID", "Side", "OrderPrice", "OrderQuanity", "FarPrice", "NearPrice", "Reference To Input Step", "Reference To Output Step", "ExpireTime", "OrderID", "Price Of This(Last) Fill", "Qty On This(Last) Fill"  };

		String selectedOption = (String)val_inputOutputStepComboBox.getSelectedItem();
		//System.out.println(selectedOption);

		if(selectedOption.equals("Reference To Input Step") ){

			val_outputStepLabel.setVisible(false);
			val_outputStepTextField.setVisible(false);
			val_outputStepTextField.setText("");
			val_inputStepTextField.setText("");

			//updates ValidationTagTable
			this.validationTableModel.setValueAt("@IP[]", 0, 1);

		}else if(selectedOption.equals("Reference To Output Step")){

			val_outputStepLabel.setVisible(true);
			val_outputStepTextField.setVisible(true);
			val_outputStepTextField.setText("");

			val_inputStepTextField.setText("");

			//updates ValidationTagTable
			this.validationTableModel.setValueAt("@OP[][]", 0, 1);

		}



	}

	private void optionalTextField12ActionPerformed(KeyEvent evt) {

		String tagType = (String)tagTypeComboBox.getSelectedItem();

		if(tagType.equals("Reference To Output Step")){

			String outputStep = optionalTextField2.getText();
			String tagContent = tagValueContentTextField.getText();
			int i1 = tagContent.lastIndexOf("[");
			int i2 = tagContent.lastIndexOf("]");
			String tagContent1 = tagContent.substring(0, i1+1);
			String tagContent2 = tagContent.substring(i2, tagContent.length());
			this.tagValueContentTextField.setText(tagContent1 + outputStep + tagContent2);	

		}else{

			String refPlaceHolder = optionalTextField2.getText();
			String tagContent = tagValueContentTextField.getText();
			int i1 = tagContent.indexOf(".");
			System.out.println(i1);
			String tagContent1 = tagContent.substring(i1, tagContent.length());
			System.out.println(tagContent1);
			this.tagValueContentTextField.setText(refPlaceHolder + tagContent1);

		}

	}

	private void optionalTextField1ActionPerformed(KeyEvent evt) {

		String tagValue = (String) tagValueComboBox.getSelectedItem();

		if(tagValue.equals("Future Time")){

			String extraTime = optionalTextField1.getText();
			this.tagValueContentTextField.setText("@TIMEPLUS+" + extraTime);

		}else if(tagValue.equals("Get Tag Value From Referenced Step")){

			String theOtherTag = optionalTextField1.getText();
			String tagContent = tagValueContentTextField.getText();
			int i1 = tagContent.indexOf("(");
			int i2 = tagContent.indexOf(")");
			String tagContent1 = tagContent.substring(0, i1+1);
			String tagContent2 = tagContent.substring(i2, tagContent.length());
			this.tagValueContentTextField.setText(tagContent1 + theOtherTag + tagContent2);

		}else{

			String inputStep = optionalTextField1.getText();
			String tagContent = tagValueContentTextField.getText();
			int i1 = tagContent.indexOf("[");
			int i2 = tagContent.indexOf("]");
			String tagContent1 = tagContent.substring(0, i1+1);
			String tagContent2 = tagContent.substring(i2, tagContent.length());
			this.tagValueContentTextField.setText(tagContent1 + inputStep + tagContent2);
		}
	}

	private void tagValueComboBoxActionPerformed(ActionEvent evt) {

		//tagValueOptions = { "Other", "Remove Tag from Msg"};
		String tagValue = (String) tagValueComboBox.getSelectedItem();

		if(tagValue.equals("Other")){

			resetTagValueFields();

		}else if(tagValue.equals("Remove Tag from Msg")){

			resetTagValueFields();
			tagValueContentTextField.setText("@REMOVE");

		}else if(tagValue.equals("Buy")){

			tagValueContentTextField.setText("1");

		}else if(tagValue.equals("Sell")){

			tagValueContentTextField.setText("2");

		}else if(tagValue.equals("Sell short")){

			tagValueContentTextField.setText("5");

		}else if(tagValue.equals("Sell short exempt")){

			tagValueContentTextField.setText("6");

		}else if(tagValue.equals("Current Time")){

			tagValueContentTextField.setText("@TIMEPLUS");
			optionalTextField1.setText("");
			optionalLabel1.setVisible(false);
			optionalTextField1.setVisible(false);
			tagValueContentTextField.setEditable(false);

		}else if(tagValue.equals("Future Time")){

			tagValueContentTextField.setText("@TIMEPLUS+");

			optionalLabel1.setText("Time (ms) from current time");
			optionalTextField1.setText("");
			optionalLabel1.setVisible(true);
			optionalTextField1.setVisible(true);
			tagValueContentTextField.setEditable(false);

		}else if(tagValue.equals("Get Tag Value From Referenced Step")){

			optionalLabel1.setText("Tag to get");
			optionalTextField1.setText(tagTypeContentTextField.getText());
			tagValueContentTextField.setText("@APVAR_REF.getTag(" + optionalTextField1.getText() + ")");

			optionalLabel2.setText("Reference Placeholder");
			optionalTextField2.setText("@APVAR_REF");

			optionalLabel1.setVisible(true);
			optionalTextField1.setVisible(true);
			optionalLabel2.setVisible(true);
			optionalTextField2.setVisible(true);
			tagValueContentTextField.setEditable(false);
		}

	}

	private void addTagButtonActionPerformed(java.awt.event.ActionEvent evt) {

		boolean valid = validateNewTagData();

		if(valid){

			if(isInputStep){
				LFTag tag = new LFTag(this.tagTypeContentTextField.getText(), this.tagValueContentTextField.getText());
				ApplicationContext.getTestcaseHelper().addInputStepTag(tag);
			}else{
				LFOutputTag tag = new LFOutputTag(this.tagTypeContentTextField.getText(), this.tagValueContentTextField.getText());

				ApplicationContext.getTestcaseHelper().addOutputStepTag(tag);
			}

			this.dispose();
			//((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showMainPanel();
			//			ApplicationContext.getTestcaseHelper().setTestCaseChangeStatus(true);
			ApplicationContext.getTestcaseHelper().setTestCaseChangesSavedStatus(false);
		}else{
			msgLabel.setText("Required Fields are not Populated");
			AutoPilotAppl.getApplication().show(saveDialog);
		}

	}

	private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed

		tagTypeComboBox.setSelectedIndex(0);
		tagTypeContentTextField.setText("");

		resetTagValueFields();

	}//GEN-LAST:event_clearButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		this.dispose();
	}//GEN-LAST:event_cancelButtonActionPerformed

	private void tagTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {

		//tagTypeOptions  = { "Other", "ClientID", "Side", "OrderPrice", "OrderQuanity", "FarPrice", "NearPrice", "Reference To Input Step", "Reference To Output Step", "ExpireTime", "OrderID", "Price Of This(Last) Fill", "Qty On This(Last) Fill"  };

		String tagType = (String)tagTypeComboBox.getSelectedItem();

		resetTagValueFields();

		updateTagValueOptions(new String[] { "Other", "Get Tag Value From Referenced Step", "Remove Tag from Msg"});

		if(tagType.equals("Other")){

			tagTypeContentTextField.setText("");

		}else if(tagType.equals("ClientID")){

			tagTypeContentTextField.setText("10270");

		}else if(tagType.equals("Side")){

			this.tagTypeContentTextField.setText("54");

			//			tagValueOptions = new String[] { "Buy", "Sell", "Sell short", "Sell short exempt"};
			//			tagValueComboBox.setModel(new javax.swing.DefaultComboBoxModel(tagValueOptions));
			//			tagValueComboBox.setSelectedIndex(0);

			updateTagValueOptions(new String[] { "Buy", "Sell", "Sell short", "Sell short exempt"});

			tagValueContentTextField.setText("1");

		}else if(tagType.equals("OrderPrice")){

			tagTypeContentTextField.setText("44");

		}else if(tagType.equals("OrderQuanity")){

			tagTypeContentTextField.setText("38");

		}else if(tagType.equals("FarPrice")){

			tagTypeContentTextField.setText("10626");

		}else if(tagType.equals("NearPrice")){

			tagTypeContentTextField.setText("10625");

		}else if(tagType.equals("NearPrice")){

			tagTypeContentTextField.setText("10625");

		}else if(tagType.equals("Reference To Input Step") ){

			tagTypeContentTextField.setText("@APVAR_REF");

			tagValueContentTextField.setText("@IP[]");

			optionalLabel1.setText("Input Step #");
			optionalTextField1.setText("");
			optionalLabel1.setVisible(true);
			optionalTextField1.setVisible(true);
			tagValueContentTextField.setEditable(false);
			tagValueComboBox.setEnabled(false);

		}else if(tagType.equals("Reference To Output Step")){

			tagTypeContentTextField.setText("@APVAR_REF");
			tagValueContentTextField.setText("@OP[][]");

			optionalLabel1.setText("Input Step #");
			optionalLabel2.setText("Output Step #");
			optionalTextField1.setText("");
			optionalTextField2.setText("");
			optionalLabel1.setVisible(true);
			optionalTextField1.setVisible(true);
			optionalLabel2.setVisible(true);
			optionalTextField2.setVisible(true);
			tagValueContentTextField.setEditable(false);
			tagValueComboBox.setEnabled(false);

		}else if(tagType.equals("ExpireTime")){

			tagTypeContentTextField.setText("126");			

			//			tagValueOptions = new String[] { "Current Time", "Future Time"};
			//			tagValueComboBox.setModel(new javax.swing.DefaultComboBoxModel(tagValueOptions));
			//			tagValueComboBox.setSelectedIndex(0);

			updateTagValueOptions(new String[] { "Current Time", "Future Time"});

			tagValueContentTextField.setText("@TIMEPLUS");

		}else if(tagType.equals("OrderID")){

			tagTypeContentTextField.setText("37");

		}else if(tagType.equals("Price Of This(Last) Fill")){

			tagTypeContentTextField.setText("31");

		}else if(tagType.equals("Qty On This(Last) Fill")){

			tagTypeContentTextField.setText("32");

		}
		//tagValueComboBox.setSelectedIndex(0);
	}

	private void updateTagValueOptions(String[] optionsList){

		updateTagValueOptions(optionsList, true);
	}

	private void updateTagValueOptions(String[] optionsList, boolean toResetChoice){

		tagValueComboBox.setModel(new javax.swing.DefaultComboBoxModel(optionsList));

		if(toResetChoice){
			tagValueComboBox.setSelectedIndex(0);
		}

	}

	private void resetTagValueFields(){

		tagValueComboBox.setSelectedIndex(0);

		optionalLabel1.setVisible(false);
		optionalTextField1.setVisible(false);
		optionalLabel2.setVisible(false);
		optionalTextField2.setVisible(false);

		tagValueContentTextField.setEditable(true);
		tagValueComboBox.setEnabled(true);

		tagValueContentTextField.setText("");
		optionalTextField1.setText("");
		optionalTextField2.setText("");

	}

	public void populateForm(LFTestInputSteps inputStep){		

		this.inputStep= inputStep;
		this.isInputStep = true;
		this.repeatingGroupTagTableModel.setInputStep(true);
		inputStepNumTextField.setText(String.valueOf(inputStep.getActionSequence()));	
		this.outputStepNumLabel.setVisible(false);
		this.outputStepNumTextField.setVisible(false);
		this.loadRepeatingGroupInMessage(isInputStep);
		repeatingGroupPanel.setVisible(true);

	}

	public void populateForm(LFOutputMsg outputStep){		

		this.outputStep = outputStep;
		this.isInputStep = false;
		this.repeatingGroupTagTableModel.setInputStep(false);
		inputStepNumTextField.setText(String.valueOf(outputStep.getActionSequence()));	
		val_inputStepTextField.setText(String.valueOf(outputStep.getActionSequence()));
		this.loadRepeatingGroupInMessage(isInputStep);
		//val_inputStepTextFieldActionPerformed(); //update template table with default inputs

		this.outputStepNumLabel.setVisible(true);
		this.outputStepNumTextField.setVisible(true);
		outputStepNumTextField.setText(String.valueOf(outputStep.getOutputMsgID()));
		repeatingGroupPanel.setVisible(false);
	}

	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub

	}


	private boolean validateNewTagData(){

		boolean valid = true;

		if(tagTypeContentTextField.getText().length() == 0 || tagValueContentTextField.getText().length() == 0){
			valid = false;
		}else{

			String tagType = (String)tagTypeComboBox.getSelectedItem();

			if(tagType.equals(tagType.equals("Reference To Input Step"))){

				if(optionalTextField1.getText().length() == 0){
					valid = false;
				}

			}else if(tagType.equals(tagType.equals("Reference To Output Step"))){

				if(optionalTextField1.getText().length() == 0 || optionalTextField1.getText().length() == 0){
					valid = false;
				}
			}else if(tagType.equals(tagType.equals("Future Time"))){

				if(optionalTextField1.getText().length() == 0){
					valid = false;
				}
			}else if(tagType.equals(tagType.equals("Get Tag Value From Referenced Step"))){

				if(optionalTextField1.getText().length() == 0){
					valid = false;
				}
			}
		}

		return valid;
	}

	private boolean validateValidationTemplateData(){

		if(val_inputStepTextField.getText().length() == 0 || val_refPlaceHolderTextField.getText().length() == 0){

			return false;

		}

		String tagType = (String)val_inputOutputStepComboBox.getSelectedItem();

		if(tagType.equals(tagType.equals("Reference To Output Step")) && val_outputStepTextField.getText().length() == 0){

			return false;

		}

		return true;

	}

	private boolean validateNewValidationTemplateData(){

		if(newTemplateNameTextField.getText().length() == 0 || testCaseHelper.getValidationTemplateTableModel().getRowCount() == 0){

			return false;

		}
		return true;

	}
	
	private int getRepeatingGroupNum(String tagId){
		int groupNum = 1;
		for( int i=0; i<tagId.length(); i++ ) {
		    if( tagId.charAt(i) == '#' ) {
		    	groupNum++;
		    } 
		}
		return groupNum;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				SmartTagDialog dialog = new SmartTagDialog(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}
	
	/**
	 * Internal Repeating Group class used by SmartTagDialog
	 * @author yh95657
	 *
	 */
	private class RepeatingGroup{
		
		List<Tag> tags;
		
		public RepeatingGroup(){
			tags = new ArrayList<Tag>();
		}
		
		public List<Tag> getTags(){
			return this.tags;
		}
		
		public void addTag(String tagId, String tagValue){
			this.tags.add(new Tag(tagId, tagValue));
		}
		
	}
}
