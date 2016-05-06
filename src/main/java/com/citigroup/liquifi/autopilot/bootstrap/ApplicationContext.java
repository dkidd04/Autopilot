package com.citigroup.liquifi.autopilot.bootstrap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.citigroup.liquifi.autopilot.gui.InputCommonOverwriteTagPanel;
import com.citigroup.liquifi.autopilot.gui.InputStepPanel;
import com.citigroup.liquifi.autopilot.gui.InputTemplatePanel;
import com.citigroup.liquifi.autopilot.gui.InputTestCasePanel;
import com.citigroup.liquifi.autopilot.gui.MainPanel;
import com.citigroup.liquifi.autopilot.gui.MessagePanel;
import com.citigroup.liquifi.autopilot.gui.OutputStepPanel;
import com.citigroup.liquifi.autopilot.gui.PreferenceDialog;
import com.citigroup.liquifi.autopilot.gui.TestResultPanel;
import com.citigroup.liquifi.autopilot.gui.TestRunPanel;
import com.citigroup.liquifi.autopilot.gui.model.CommonTagTableModel;
import com.citigroup.liquifi.autopilot.gui.model.InputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.MessageTableModel;
import com.citigroup.liquifi.autopilot.gui.model.OutputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.OutputValidationModel;
import com.citigroup.liquifi.autopilot.gui.model.PropertyTableModel;
import com.citigroup.liquifi.autopilot.gui.model.RepeatingGroupTableModel;
import com.citigroup.liquifi.autopilot.gui.model.SecurityTableModel;
import com.citigroup.liquifi.autopilot.gui.model.TagTableModel;
import com.citigroup.liquifi.autopilot.gui.model.TopicManagerTableModel;
import com.citigroup.liquifi.autopilot.helper.AutoPilotConfiguration;
import com.citigroup.liquifi.autopilot.helper.AutoPilotBenchmarkConfiguration;
import com.citigroup.liquifi.autopilot.helper.TestCaseHelper;
import com.citigroup.liquifi.autopilot.message.FIXFactory;
import com.citigroup.liquifi.autopilot.message.XMLFactory;
import com.citigroup.liquifi.autopilot.messaging.AutoPilotBrokerInfoFactory;
import com.citigroup.liquifi.autopilot.messaging.AutoPilotSocketConnectionFactory;
import com.citigroup.liquifi.autopilot.util.PlaceHolders;
import com.citigroup.liquifi.clock.Clock;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.SymFiiUtil;

public class ApplicationContext {
	private static XmlBeanFactory factory;
	private static InputTemplatePanel templatePanel;
	private static InputCommonOverwriteTagPanel inputCommonOverwriteTagPanel;	
	private static InputStepPanel inputStepPanel;
	private static OutputStepPanel outputStepPanel;
	private static InputTestCasePanel testcasePanel;
	private static TestRunPanel testRunPanel;
	private static MessagePanel messagePanel;
	private static MainPanel mainPanel;
	private static TestResultPanel resultPanel;
	private static PreferenceDialog prefDialog;
	private static OutputStepTableModel outputStepTableModel;
	private static InputStepTableModel inputStepTableModel;
	private static TagTableModel<LFTag> inputTagTableModel;
	private static TagTableModel<LFOutputTag> outputTagTableModel;
	private static TagTableModel<LFTag> validationTagTableModel;
	private static TopicManagerTableModel topicManagerTableModel;
	private static TagTableModel<LFTag> validationTemplateTableModel;
	private static RepeatingGroupTableModel<Tag> repeatingGroupTagTableMode;
	private static TestCaseHelper testcaseHelper;
	private static AutoPilotConfiguration config;
	private static AutoPilotBenchmarkConfiguration benchmarkConfig;
	private static XMLFactory xmlFactory;
	private static SecurityTableModel securityTableModel;
	private static PropertyTableModel systemTableModel;
	private static MessageTableModel messageTableModel;
	private static OutputValidationModel outputValidationModel;
	private static FIXFactory fixFactory;
	private static PlaceHolders placeHolder;
	private static AutoPilotBrokerInfoFactory brokerFactory;
    private static AutoPilotSocketConnectionFactory socketFactory;
    private static Clock clock;
    private static SymFiiUtil symFiiUtil;
	private static CommonTagTableModel commonTagTableModel;
	
	public static void init() throws Exception{
		String confighome = System.getProperty("config.home");
		String application = System.getProperty("application");
		String region = System.getProperty("region");
		String envirionment = System.getProperty("env");
		String common = System.getProperty("common","common");
		String strSpringFile = confighome + "/" + application+"/" + region+"/" + common + "/" + "spring.xml";
		Resource res  = new FileSystemResource (strSpringFile);
		factory = new XmlBeanFactory(res);
		
		// Use XmlBeanDefinitionReader to read several config files. In this case
		// it is being used to read the broker config file, which is specific for
		// each environment.
		//AutoPilotEnvLoader props = AutoPilotEnvLoader.getInstance();
		//String file = props.getConfighome() + "/" + props.getRegion() +"/" + props.getEnvirionment() + "/broker.xml";
		String fileBrokerXml = confighome + "/" + application+"/" + region+"/" + envirionment + "/" + "broker.xml";
		Resource res2  = new FileSystemResource(fileBrokerXml);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		reader.loadBeanDefinitions(res2);
				
		PropertyPlaceholderConfigurer cfg = (PropertyPlaceholderConfigurer) factory.getBean("propertyConfigurer");
		cfg.postProcessBeanFactory(factory);
	}

	public static InputTemplatePanel getTemplatePanel() {
		if(templatePanel == null)
			templatePanel = (InputTemplatePanel) factory.getBean("templatePanel");
		return templatePanel;
	}

	public static InputCommonOverwriteTagPanel getInputCommonOverwriteTagPanel() {
		if(inputCommonOverwriteTagPanel == null)
			inputCommonOverwriteTagPanel = (InputCommonOverwriteTagPanel) factory.getBean("inputCommonOverwriteTagPanel");
		return inputCommonOverwriteTagPanel;
	}
	
	public static InputStepPanel getInputStepPanel() {
		if(inputStepPanel == null)
			inputStepPanel = (InputStepPanel) factory.getBean("inputStepPanel");
		return inputStepPanel;
	}
	

	public static OutputStepPanel getOutputStepPanel() {
		if(outputStepPanel == null)
			outputStepPanel = (OutputStepPanel) factory.getBean("outputStepPanel");
		return outputStepPanel;
	}


	public static InputTestCasePanel getTestcasePanel() {
		if(testcasePanel == null)
			testcasePanel = (InputTestCasePanel) factory.getBean("testcasePanel");
		return testcasePanel;
	}



	public static TestRunPanel getTestRunPanel() {
		if(testRunPanel == null)
			testRunPanel = (TestRunPanel) factory.getBean("testRunPanel");
		return testRunPanel;
	}
	

	public static OutputStepTableModel getOutputStepTableModel() {
		if (outputStepTableModel == null)
			outputStepTableModel = (OutputStepTableModel) factory.getBean("outputStepTableModel");
		return outputStepTableModel;
	}


	public static TestCaseHelper getTestcaseHelper() {
		if (testcaseHelper == null)
			testcaseHelper = (TestCaseHelper) factory.getBean("testcaseHelper");
		return testcaseHelper;
	}


	public static InputStepTableModel getInputStepTableModel() {
		if (inputStepTableModel == null)
			inputStepTableModel = (InputStepTableModel) factory.getBean("inputStepTableModel");
		return inputStepTableModel;
	}


	@SuppressWarnings("unchecked")
	public static TagTableModel<LFTag> getInputTagTableModel() {
		if(inputTagTableModel == null)
			inputTagTableModel = (TagTableModel<LFTag>) factory.getBean("inputTagTableModel");
		return inputTagTableModel;
	}

	@SuppressWarnings("unchecked")
	public static TagTableModel<LFOutputTag> getOutputTagTableModel() {
		if(outputTagTableModel == null)
			outputTagTableModel = (TagTableModel<LFOutputTag>) factory.getBean("outputTagTableModel");
		return outputTagTableModel;
	}
	
	@SuppressWarnings("unchecked")
	public static TagTableModel<LFTag> getValidationTagTableModel() {
		if(validationTagTableModel == null)
			validationTagTableModel = (TagTableModel<LFTag>) factory.getBean("validationTagTableModel");
		return validationTagTableModel;
	}
	
	@SuppressWarnings("unchecked")
	public static TagTableModel<LFTag> getValidationTemplateTableModel() {
		if(validationTemplateTableModel == null)
			validationTemplateTableModel = (TagTableModel<LFTag>) factory.getBean("validationTemplateTableModel");
		return validationTemplateTableModel;
	}
	
	public static RepeatingGroupTableModel<Tag> getRepeatingGroupTagTableModel() {
		if(repeatingGroupTagTableMode == null){
			repeatingGroupTagTableMode = (RepeatingGroupTableModel<Tag>) factory.getBean("repeatingGroupTagTableModel");
		}
		return repeatingGroupTagTableMode;
	}

	public static CommonTagTableModel getCommonTagTableModel() {
		if(commonTagTableModel == null)
			commonTagTableModel = (CommonTagTableModel) factory.getBean("commonTagTableModel");
		return commonTagTableModel;
	}
	

	public static AutoPilotConfiguration getConfig() {
		if(config == null)
			config = (AutoPilotConfiguration) factory.getBean("autoPilotConfig");
		return config;
	}

	
	public static AutoPilotBenchmarkConfiguration getBenchmarkConfig() {
		if(benchmarkConfig == null)
			benchmarkConfig = (AutoPilotBenchmarkConfiguration) factory.getBean("autoPilotBenchmarkConfig");
		return benchmarkConfig;
	}

	public static MessagePanel getMessagePanel() {
		if(messagePanel == null){
			messagePanel = (MessagePanel) factory.getBean("messagePanel");
		}
		return messagePanel;
	}


	public static MainPanel getMainPanel() {
		if(mainPanel == null){
			mainPanel = (MainPanel) factory.getBean("mainPanel");
		}
		return mainPanel;
	}


	public static XMLFactory getXmlFactory() {
		if(xmlFactory == null){
			xmlFactory = (XMLFactory) factory.getBean("xmlFactory");
		}
		return xmlFactory;
	}


	public static PreferenceDialog getPrefDialog() {
		if(prefDialog == null){
			prefDialog = (PreferenceDialog) factory.getBean("prefDialog");
		}
		return prefDialog;
	}


	public static SecurityTableModel getSecurityTableModel() {
		if(securityTableModel == null){
			securityTableModel = (SecurityTableModel) factory.getBean("securityTableModel");
		}
		return securityTableModel;
	}


	public static PropertyTableModel getSystemTableModel() {
		if(systemTableModel == null)
			systemTableModel = (PropertyTableModel) factory.getBean("systemTableModel");
		return systemTableModel;
	}


	public static TestResultPanel getResultPanel() {
		if(resultPanel == null)
			resultPanel = (TestResultPanel) factory.getBean("resultPanel");
		return resultPanel;
	}


	public static MessageTableModel getMessageTableModel() {
		if (messageTableModel == null)
			messageTableModel = (MessageTableModel) factory.getBean("messageTableModel");
		return messageTableModel;
	}
	
	public static OutputValidationModel getOutputValidationModel() {
		if (outputValidationModel == null)
			outputValidationModel = (OutputValidationModel) factory.getBean("outputValidationModel");
		return outputValidationModel;
	}
	
	public static TopicManagerTableModel getTopicManagerTableModel(){
    	if(topicManagerTableModel == null){
    		topicManagerTableModel = (TopicManagerTableModel) factory.getBean("topicManagerTableModel");
		}
		return topicManagerTableModel;
    }
	
	
	public static AutoPilotBrokerInfoFactory getBrokerFactory(){
		if(brokerFactory == null){
			brokerFactory = (AutoPilotBrokerInfoFactory) factory.getBean("brokerFactory");
		}
		return brokerFactory;
	}
	
	public static FIXFactory getFIXFactory(){
		if(fixFactory == null){
			fixFactory = (FIXFactory) factory.getBean("fixFactory");
		}
		return fixFactory;
	}
	
	public static PlaceHolders getPlaceHolders(){
		if(placeHolder == null){
			placeHolder = (PlaceHolders) factory.getBean("placeHolder");
		}
		return placeHolder;
	}
	
    public static AutoPilotSocketConnectionFactory getSocketFactory(){
		if(socketFactory == null && factory.containsBean("socketFactory")){
			socketFactory = (AutoPilotSocketConnectionFactory) factory.getBean("socketFactory");
		}
		return socketFactory;
	}
    
    public static Clock getClock() {
    	if (clock == null) {
    		clock = (Clock) factory.getBean("clock");
    	}
    	return clock;
    }

	public static SymFiiUtil getSymFiiUtil() {
		if (symFiiUtil == null) {
			Resource resource = new ClassPathResource("springConfig.xml");
			BeanFactory factory = new XmlBeanFactory(resource);
			symFiiUtil = (SymFiiUtil) factory.getBean("symFiiUtil");
		}
		return symFiiUtil;
	}

}
