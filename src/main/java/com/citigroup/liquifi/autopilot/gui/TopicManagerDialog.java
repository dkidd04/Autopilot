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
import com.citigroup.liquifi.autopilot.gui.model.TopicManagerTableModel;
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
import java.awt.Color;

public class TopicManagerDialog extends JDialog{
	private JTable table;
	private TopicManagerTableModel tableModel = ApplicationContext.getTopicManagerTableModel();
	//TestCaseHelper testcaseHelper = ApplicationContext.getTestcaseHelper();
	LFTestCase testcase = ApplicationContext.getTestcaseHelper().getTestcase();
	
	/** Creates new form PreferenceDialog */
	public TopicManagerDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		getContentPane().setBackground(Color.WHITE);
		initComponents();
			
	}
	
	void initComponents() {
		setTitle("Topic Manager");
		setIconImage(Toolkit.getDefaultToolkit().getImage(TopicManagerDialog.class.getResource("/com/citigroup/liquifi/autopilot/gui/resources/icons/insert_link.png")));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setName("jScrollPane3");
		
		JLabel lblCurrentLinkingPairs = new JLabel("Broker Topics");
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblCurrentLinkingPairs))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(8)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(187)
							.addComponent(btnOk)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblCurrentLinkingPairs)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnOk)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		
		
		table = new JTable();
		table.setModel(tableModel);
		scrollPane.setViewportView(table);
		
		
		getContentPane().setLayout(groupLayout);
	}

	protected void okButtonActionPerformed(ActionEvent evt) {
		this.dispose();
	}

	public void populateForm() {
		this.tableModel.init();
	}
}
