package com.citigroup.liquifi.autopilot.bootstrap;

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

import com.citigroup.liquifi.AutoPilotAppl;
import com.citigroup.liquifi.autopilot.helper.AutoPilotCommonParser;
import com.citigroup.liquifi.util.Util;

public class AutoPilotGuiStarter extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	String configpath;
	JFrame frame;
	
	JPanel apppane= new JPanel();
	JPanel loginpane= new JPanel();
	JPanel centerpane= new JPanel();
	JPanel bottompane= new JPanel();
	//String[] applications ;
	
	File file = new File(System.getProperty("config.home")+"/AutoPilotCommonEnv.xml");
	AutoPilotCommonParser aParser = new AutoPilotCommonParser(file);
	
	//String[] applications= {"AEE","TOTALTOUCH","CITISMART" };
	JComboBox applicationCmb = new JComboBox(retrieveComboBoxItems(aParser,"ApplictionList")); 
	
	JPanel applicationComboPane= new JPanel();

	//String[] regions= {"US", "EMEA", "HK","JPN"};
	JComboBox regionCmb = new JComboBox(retrieveComboBoxItems(aParser,"RegionList"));
	JPanel regionComboPane= new JPanel();
	
	//String[] envs= {"DEV", "LAB"};
	JComboBox envCmb = new JComboBox(retrieveComboBoxItems(aParser,"EnvList"));
	JPanel envComboPane= new JPanel();
	
	JPanel logPane= new JPanel();
	Logger logger= Logger.getLogger(AutoPilotGuiStarter.class.getSimpleName());
	Integer applicationID;
	JButton invokeBtn= new JButton("Login");
	JButton cancelBtn= new JButton("Cancel");
	JLabel logLbl= new JLabel("                                     ");
	String args [];
	
	public AutoPilotGuiStarter (String args[]) {
		super(new BorderLayout());
		
		String application = System.getProperty("application");
		String region = System.getProperty("region");
		String env = System.getProperty("env");
		
		if(application != null && region != null & env != null) {
			this.login(application, region, env);
			return;
		}
		
		//this.configpath= configpath;
		this.args= args;

		apppane.setLayout(new BoxLayout(apppane, BoxLayout.Y_AXIS));
		//apppane.setBorder(BorderFactory.createEtchedBorder());

		applicationCmb.addActionListener(this);
		applicationComboPane.add(new JLabel("Select Application         "));
		applicationComboPane.add(applicationCmb);
		apppane.add(applicationComboPane);
		
		regionCmb.addActionListener(this);
		regionComboPane.add(new JLabel("Select Region         "));
		regionComboPane.add(regionCmb);
		apppane.add(regionComboPane);

		//envCmb.setEnabled(false);
		//envCmb.addActionListener(this);
		envComboPane.add(new JLabel("Select environment"));
		envComboPane.add(envCmb);//("Select Region     "));
		apppane.add(envComboPane);

		loginpane.setLayout(new BoxLayout(loginpane, BoxLayout.Y_AXIS));
		
		centerpane.setLayout(new BorderLayout());
		centerpane.setBorder(BorderFactory.createEtchedBorder());
		centerpane.add(apppane,BorderLayout.CENTER);

		JPanel buttonPane= new JPanel();
		buttonPane.add(invokeBtn);
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(cancelBtn);
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		invokeBtn.setEnabled(true);
		invokeBtn.addActionListener(this);
		cancelBtn.addActionListener(this);

		bottompane.setLayout(new BoxLayout(bottompane, BoxLayout.Y_AXIS));
		bottompane.setBorder(BorderFactory.createEtchedBorder());
		logPane.add(logLbl);
		bottompane.add(logPane);
		bottompane.add(buttonPane);
		
		this.add(centerpane,BorderLayout.CENTER);
		this.add(bottompane, BorderLayout.SOUTH);
		createAndShowGUI();
	}
	
	public void actionPerformed(ActionEvent event) {
		Object source= event.getSource();
		if(source== applicationCmb )
		{
			System.out.println("applicationCmb");
		}
		else if (source== regionCmb) {
			System.out.println("regionCmb");
		}
		else if (source== envCmb) {
			System.out.println("envCmb");
		}
		else if (source== invokeBtn) {
			System.out.println("invokeBtn");
			login((String)applicationCmb.getSelectedItem(), (String)regionCmb.getSelectedItem(), (String)envCmb.getSelectedItem());
			this.frame.dispose();
		}		
		else
		{
			this.frame.dispose();
			System.exit(0);
		}
	}
	
	private void login(String strApplication, String strRegion, String strEnv) {
		logLbl.setText("<html><B>Loading...GUI Components<B></html>");
		
		try {
			String strConfighome =  System.getProperty("config.home");
			String strCommon =  System.getProperty("common");
			
			System.setProperty("application", strApplication);
			System.setProperty("region", strRegion);
			System.setProperty("env", strEnv);
			System.setProperty("title", "AutoPilot_"+strApplication+"_"+strRegion+"_"+strEnv);
			
			String strClassPathRegion = strConfighome + "/" + strApplication+"/" + strRegion;
			String strClassPathCommon = strClassPathRegion+"/" + strCommon;
			String strClassPathDb = strClassPathCommon + "/" + "db";
			
			AutoPilotBootstrap.loadClassPath(strClassPathRegion);
			AutoPilotBootstrap.loadClassPath(strClassPathCommon);
			AutoPilotBootstrap.loadClassPath(strClassPathDb);
			logger.info("CLASSPATH|"+strClassPathRegion+";"+strClassPathCommon+";"+strClassPathDb+";");
			
			String strClassPathCustomizedValidation = aParser.getProperty("CustomizedValidationClasspath");
			if (strClassPathCustomizedValidation != null && strClassPathCustomizedValidation.trim().length()>0 ) {
				try  {
					AutoPilotBootstrap.loadClassPath(strClassPathCustomizedValidation);
					logger.info("CUSTVALIDATIONCLASSPATH|"+strClassPathCustomizedValidation);
				}
				catch (Exception ex) {
					logger.warning("CUSTVALIDATIONCLASSPATH|Cannot not classpath:"+ strClassPathCustomizedValidation);
					ex.printStackTrace();						
				}
			}
			
			
			AutoPilotBootstrap.initSpring();
			AutoPilotBootstrap.initDB();	
			
			logger.info("Initialize User Interface ... ");
			AutoPilotAppl.launch(AutoPilotAppl.class, args);   
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logLbl.setText("<html><B>Cannot launch AutoPilot. Pls check the log.<B></html>");
			//this.frame.dispose();
		}						
	}
	
	private void createAndShowGUI()
	{

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("AutoPilot GUI");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension frameSize= new Dimension(330, 250);
		frame.setMinimumSize(frameSize);
		frame.setPreferredSize(frameSize);
		frame.setMaximumSize(frameSize);
		this.setOpaque(true);
		frame.setContentPane(this);
		frame.pack();
		Util.setAtScreenCenter(frame);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}
	
	public static void main(String args[]) {
		new AutoPilotGuiStarter(args);
	}
	
	private String [] retrieveComboBoxItems(AutoPilotCommonParser localParser, String strPropertyName) {
		ArrayList<String> applicationList = localParser.getCommonVariables(strPropertyName);
		String [] strArray = new String [applicationList.size()];
		for(int j=0; j<applicationList.size(); j++){
			strArray[j] = applicationList.get(j);
		}                                 

		return strArray;
	}
	
}
