package com.citigroup.liquifi.autopilot.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.citigroup.liquifi.autopilot.gui.SmartTagPanel;
import com.citigroup.liquifi.util.Util;

public class SmartTagView extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	String configpath;
	JFrame frame;
	
	JPanel centerpane= new JPanel();
	JPanel bottompane= new JPanel();
	//String[] applications ;
		
	
	
	public SmartTagView () {
		super(new BorderLayout());
		//this.configpath= configpath;

		centerpane.setLayout(new BorderLayout());
		centerpane.setBorder(BorderFactory.createEtchedBorder());
		centerpane.add(new SmartTagPanel(),BorderLayout.CENTER);


		bottompane.setLayout(new BoxLayout(bottompane, BoxLayout.Y_AXIS));
		bottompane.setBorder(BorderFactory.createEtchedBorder());

		
		this.add(centerpane,BorderLayout.CENTER);
		this.add(bottompane, BorderLayout.SOUTH);
		createAndShowGUI();
	}
	
	
	
	private void createAndShowGUI()
	{

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Smart Tag");
		Dimension preferredFrameSize= new Dimension(840, 340);
		frame.setPreferredSize(preferredFrameSize);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.setResizable(true);
		this.setOpaque(true);
		frame.setContentPane(this);
		frame.pack();
		Util.setAtScreenCenter(frame);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}
	
	public static void main(String args[]) {
		new SmartTagView();
	}



	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	
}