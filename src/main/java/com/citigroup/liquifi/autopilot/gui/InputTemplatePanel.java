/*
 * InputTemplatePanel.java
 *
 * Created on July 30, 2008, 3:37 PM
 */

package com.citigroup.liquifi.autopilot.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.citigroup.liquifi.AutoPilotAppl;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.gui.model.CommonTagTableModel;
import com.citigroup.liquifi.entities.LFCommonOverwriteTag;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.util.AutoPilotConstants;
import com.citigroup.liquifi.util.DBUtil;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 *
 * @author  zy63334
 */
public class InputTemplatePanel extends javax.swing.JPanel implements TableModelListener{
	private static final long serialVersionUID = 1L;
	/** Creates new form InputTemplatePanel */
	public InputTemplatePanel() {
		
		commonTagTableModel = ApplicationContext.getCommonTagTableModel();
		
		initDBData();
		initComponents();
		setEditable(false);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveDialog = new javax.swing.JDialog();
        msgLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        AppNameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        templateNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        msgTypeComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgTemplateTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        templateComboBox = new javax.swing.JComboBox();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        isInputComboBox = new javax.swing.JComboBox();
        commonTagListComboBox = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        commonTagTable = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();

        saveDialog.setName("saveDialog"); // NOI18N

        msgLabel.setText("Save Successful. Entry added to Database.");
        msgLabel.setName("msgLabel"); // NOI18N

        okButton.setText("OK");
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

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

        setName("Form"); // NOI18N

        jLabel1.setText("AppName");
        jLabel1.setName("jLabel1"); // NOI18N

        AppNameTextField.setName("AppNameTextField"); // NOI18N

        jLabel2.setText("Template name");
        jLabel2.setName("jLabel2"); // NOI18N

        templateNameTextField.setName("templateNameTextField"); // NOI18N

        jLabel3.setText("Msg Type");
        jLabel3.setName("jLabel3"); // NOI18N

		msgTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { AutoPilotConstants.MSG_TYPE_FIXMSG, AutoPilotConstants.MSG_TYPE_XML, AutoPilotConstants.MSG_TYPE_CONTROL, "JSON", AutoPilotConstants.MSG_TYPE_CONFIG}));
        msgTypeComboBox.setName("msgTypeComboBox"); // NOI18N

        jLabel4.setText("Message Template");
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        msgTemplateTextArea.setColumns(20);
        msgTemplateTextArea.setLineWrap(true);
        msgTemplateTextArea.setRows(5);
        msgTemplateTextArea.setName("msgTemplateTextArea"); // NOI18N
        jScrollPane1.setViewportView(msgTemplateTextArea);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setName("descriptionTextArea"); // NOI18N
        jScrollPane2.setViewportView(descriptionTextArea);

        jLabel5.setText("Description");
        jLabel5.setName("jLabel5"); // NOI18N

        saveButton.setText("Save");
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        clearButton.setText("Clear");
        clearButton.setName("clearButton"); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        jLabel6.setText("Template");
        jLabel6.setName("jLabel6"); // NOI18N

        templateComboBox.setModel(new javax.swing.DefaultComboBoxModel((String[]) templateMap.keySet().toArray(new String[templateMap.keySet().size()])));
        templateComboBox.setName("templateComboBox"); // NOI18N
        templateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                templateComboBoxActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.setName("editButton"); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        deleteButton.setText("Delete");
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	deleteButtonActionPerformed(evt);
            }
        });
        createButton.setText("Create");
        createButton.setName("deleteButton"); // NOI18N
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	createButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("IsInput");
        jLabel7.setName("jLabel7"); // NOI18N

        isInputComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Y", "N" }));
        isInputComboBox.setName("isInputComboBox"); // NOI18N

        commonTagListComboBox.setModel(new javax.swing.DefaultComboBoxModel((String[]) commonOverwriteTagMap.keySet().toArray(new String[commonOverwriteTagMap.keySet().size()])));
        commonTagListComboBox.addItem(AutoPilotConstants.ComboBoxEmptyItem);
        commonTagListComboBox.setName("commonTagListComboBox"); // NOI18N
        commonTagListComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commonTagListComboBoxActionPerformed(evt);
            }
        });
        commonTagListComboBox.setSelectedItem(AutoPilotConstants.ComboBoxEmptyItem);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        commonTagTable.setModel(commonTagTableModel);
        commonTagTable.setName("commonTagTable"); // NOI18N
        commonTagTable.getModel().addTableModelListener(this);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel> (commonTagTable.getModel());
        commonTagTable.setRowSorter(sorter);
        jScrollPane3.setViewportView(commonTagTable);

        jLabel8.setText("CommonTagListName");
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
        			.addGap(59)
        			.addComponent(templateComboBox, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
        			.addGap(18)
        			.addComponent(editButton)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(deleteButton)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(createButton)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(saveButton)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(clearButton)
        			.addContainerGap(281, Short.MAX_VALUE))
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE)
        			.addContainerGap())
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jLabel1)
        			.addContainerGap(740, Short.MAX_VALUE))
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jLabel2)
        			.addContainerGap(713, Short.MAX_VALUE))
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createParallelGroup(Alignment.LEADING)
        					.addGroup(layout.createSequentialGroup()
        						.addComponent(jLabel5)
        						.addPreferredGap(ComponentPlacement.RELATED, 291, Short.MAX_VALUE))
        					.addGroup(layout.createSequentialGroup()
        						.addGroup(layout.createParallelGroup(Alignment.LEADING)
        							.addComponent(jLabel7)
        							.addComponent(jLabel4))
        						.addGap(42)
        						.addGroup(layout.createParallelGroup(Alignment.LEADING)
        							.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
        							.addComponent(isInputComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        							.addComponent(templateNameTextField, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
        							.addComponent(AppNameTextField, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
        							.addComponent(msgTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        							.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(jLabel3)
        					.addPreferredGap(ComponentPlacement.RELATED, 348, Short.MAX_VALUE)))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(jLabel8, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
        					.addGap(28)
        					.addComponent(commonTagListComboBox, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE))
        				.addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE))
        			.addGap(116))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(20)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel6)
        				.addComponent(templateComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(editButton)
        				.addComponent(deleteButton)
        				.addComponent(createButton)
        				.addComponent(saveButton)
        				.addComponent(clearButton))
        			.addGap(26)
        			.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel1)
        				.addComponent(AppNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addGap(30)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jLabel2)
        				.addComponent(templateNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addGap(28)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel3)
        				.addComponent(msgTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addGap(18)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jLabel7)
        				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        					.addComponent(isInputComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addComponent(commonTagListComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addComponent(jLabel8)))
        			.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
        				.addGroup(layout.createSequentialGroup()
        					.addGap(14)
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(jLabel4)
        						.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
        					.addGap(51)
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(jLabel5)
        						.addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(18)
        					.addComponent(jScrollPane3, 0, 0, Short.MAX_VALUE)))
        			.addGap(230))
        );
        this.setLayout(layout);
    }// </editor-fold>//GEN-END:initComponents


	public void initDBData(){
		templateMap = DBUtil.getInstance().getTem().getAllTemplateMap();
		commonOverwriteTagMap = DBUtil.getInstance().getCom().getCommonOverwriteTagMap();

	}

	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try{

			boolean valid = validate(AppNameTextField.getText(), templateNameTextField.getText());

			if (valid){
				LFTemplate template = new LFTemplate();
				template.setAppName(AppNameTextField.getText());
				template.setTemplateName(templateNameTextField.getText());
				template.setMsgTemplate(msgTemplateTextArea.getText());
				template.setDescription(descriptionTextArea.getText());
				template.setMsgType((String)msgTypeComboBox.getSelectedItem());
				template.setIsInput(((String)isInputComboBox.getSelectedItem()).charAt(0));
				
				String strListName = (String)commonTagListComboBox.getSelectedItem();
				template.setCommonOverwriteTagListName(strListName);
				/*
				if (strListName!=null && strListName.trim().length()> 0) {
					template.setCommonOverwriteTagListName(strListName);
				}				
				*/
				
				if(templateMap.containsKey(templateNameTextField.getText())){
					DBUtil.getInstance().getTem().updateTemplate(template);
					msgLabel.setText("Template updated in Database successfully!");
					AutoPilotAppl.getApplication().show(saveDialog);
				}else{
					DBUtil.getInstance().getTem().saveTemplate(template);
					msgLabel.setText("New Template created and saved to Database successfully!");
					AutoPilotAppl.getApplication().show(saveDialog);
				}

				

				clear();
				refreshFromDB();
			}else{
				msgLabel.setText("Required Fields are not Populated");
				AutoPilotAppl.getApplication().show(saveDialog);

			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

	}

	private boolean validate(String appName, String templateName) {
		if(appName == null || appName.equals("") || templateName == null || templateName.equals("")){
			return false;
		}
		return true;
	}

	public  void clear() {
		clearButtonActionPerformed(null);
	}

	private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
		AppNameTextField.setText("");
		templateNameTextField.setText("");
		msgTemplateTextArea.setText("");
		descriptionTextArea.setText("");


	}//GEN-LAST:event_clearButtonActionPerformed

private void commonTagListComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commonTagListComboBoxActionPerformed
// TODO add your handling code here:
	List<LFCommonOverwriteTag> listTag = commonOverwriteTagMap.get(commonTagListComboBox.getSelectedItem());
	if (listTag != null ) {
		//set the list of CommonTag to the tableModel of the Tag grid
		Set <LFCommonOverwriteTag> setTag = new HashSet<LFCommonOverwriteTag> ();
		for (LFCommonOverwriteTag tagLocal: listTag) {
			setTag.add(tagLocal);
		}
		if(setTag != null){
			commonTagTableModel.getData().clear();
			commonTagTableModel.setData(setTag);
		}
	}
	else {
		commonTagTableModel.getData().clear();
		commonTagTableModel.setData(new LinkedHashSet<LFCommonOverwriteTag>());
	}
}//GEN-LAST:event_commonTagListComboBoxActionPerformed

	private void templateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		setEditable(false);
		LFTemplate template = templateMap.get(templateComboBox.getSelectedItem());
		AppNameTextField.setText(template.getAppName());
		msgTypeComboBox.setSelectedItem((String)template.getMsgType()); 
		isInputComboBox.setSelectedItem((String) String.valueOf(template.getIsInput()));

		templateNameTextField.setText(template.getTemplateName());
		msgTemplateTextArea.setText(template.getMsgTemplate());
		descriptionTextArea.setText(template.getDescription());
		
		String strName = template.getCommonOverwriteTagListName();
		if (strName!=null && strName.trim().length()>0 ) {
			commonTagListComboBox.setSelectedItem(strName);	
		}
		else {
			commonTagListComboBox.setSelectedItem(AutoPilotConstants.ComboBoxEmptyItem);
		}
		commonTagListComboBoxActionPerformed(evt);
		

	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		saveDialog.setVisible(false);
	}

	private void refreshFromDB(){
		DBUtil.getInstance().getTem().loadAllTemplateFromDB();
		templateMap = DBUtil.getInstance().getTem().getAllTemplateMap();
		ApplicationContext.getFIXFactory().setTemplate(templateMap);
		templateComboBox.setModel(new javax.swing.DefaultComboBoxModel((String[]) templateMap.keySet().toArray(new String[templateMap.keySet().size()])));
		ApplicationContext.getInputStepPanel().resetTemplate();
		ApplicationContext.getOutputStepPanel().resetTemplate();
	}
	
	public void resetCommonTags() {
		commonTagListComboBox.setModel(new javax.swing.DefaultComboBoxModel((String[]) commonOverwriteTagMap.keySet().toArray(new String[commonOverwriteTagMap.keySet().size()])));
		commonTagListComboBox.addItem(AutoPilotConstants.ComboBoxEmptyItem);
	}

	private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setEditable(true);
		LFTemplate template = templateMap.get(templateComboBox.getSelectedItem());
		AppNameTextField.setText(template.getAppName());
		msgTypeComboBox.setSelectedItem((String)template.getMsgType()); 
		templateNameTextField.setText(template.getTemplateName());
		msgTemplateTextArea.setText(template.getMsgTemplate());
		descriptionTextArea.setText(template.getDescription());
	}
	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		LFTemplate template = templateMap.get(templateComboBox.getSelectedItem());
		
		try{
			// delete only when no test case is using the template
			if (DBUtil.getInstance().getIsm().getTestIDListSizeForATemplate(template.getTemplateName()) == 0 && DBUtil.getInstance().getOsm().getTestIDListSizeForATemplate(template.getTemplateName())== 0)
			{
				DBUtil.getInstance().getTem().deleteTemplate(template);
				msgLabel.setText("Template deleted successfully!");
				AutoPilotAppl.getApplication().show(saveDialog);
			}
			else
			{
				msgLabel.setText("Template Not Deleted. TestCase using the template");
				AutoPilotAppl.getApplication().show(saveDialog);
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		clear();
		refreshFromDB();
		
		
	}
	private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		try{
			setEditable(true);
			AppNameTextField.setText("");
			templateNameTextField.setText("");
			msgTemplateTextArea.setText("");
			descriptionTextArea.setText("");


			}catch(Exception e){
			System.out.println(e.getMessage());
		}

		
		
	}
	
	private void setEditable(boolean editable){
		if (editable == false){
			AppNameTextField.setEditable(false);
			templateNameTextField.setEditable(false);
			msgTemplateTextArea.setEditable(false);
			descriptionTextArea.setEditable(false);
			msgTypeComboBox.setEditable(false);
			saveButton.setVisible(false);
			clearButton.setVisible(false);
		}else{
			AppNameTextField.setEditable(true);
			templateNameTextField.setEditable(true);
			msgTemplateTextArea.setEditable(true);
			descriptionTextArea.setEditable(true);
			msgTypeComboBox.setEditable(true);
			saveButton.setVisible(true);
			clearButton.setVisible(true);
		}
	}

	public void tableChanged(TableModelEvent tablemodelevent) {
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AppNameTextField;
    private javax.swing.JButton clearButton;
    private javax.swing.JComboBox commonTagListComboBox;
    private javax.swing.JTable commonTagTable;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton editButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton createButton;
    private javax.swing.JComboBox isInputComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel msgLabel;
    private javax.swing.JTextArea msgTemplateTextArea;
    private javax.swing.JComboBox msgTypeComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JDialog saveDialog;
    private javax.swing.JComboBox templateComboBox;
    private javax.swing.JTextField templateNameTextField;
    // End of variables declaration//GEN-END:variables

	private Map<String, LFTemplate> templateMap = new HashMap<String, LFTemplate>();
	private Map <String, List<LFCommonOverwriteTag> > commonOverwriteTagMap = new TreeMap<String, List<LFCommonOverwriteTag>>();
	private CommonTagTableModel commonTagTableModel;

}
