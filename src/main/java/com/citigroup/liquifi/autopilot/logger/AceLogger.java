package com.citigroup.liquifi.autopilot.logger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
 

/**
 * @author Rafid Wahab
 * @date Oct 26, 2006
 */
public class AceLogger {
 
	private static Level loggingLevel = null;
	private Logger logger = null;
	private static Map<String, AceLogger> loggerMap = 
		                                         new HashMap<String,AceLogger>();
	
	
	private AceLogger(String className) {		 
		logger = Logger.getLogger(className);	
	}
	
	public static synchronized AceLogger getLogger(String className){
		AceLogger aceLogger = loggerMap.get(className);
		if(aceLogger == null){
			aceLogger = new AceLogger(className);
			loggerMap.put(className,aceLogger);
			if(loggingLevel == null){				
				loggingLevel = Level.INFO;	
			}			
		}
		return aceLogger;
	}
	
	public void finest(String finest){
		if(loggingLevel.intValue() <= Level.FINEST.intValue()){
			logger.finest(finest);
		}
	}
	
	public void finer(String finer){
		if(loggingLevel.intValue() <= Level.FINER.intValue()){
			logger.finer(finer);
		}
	}
	
	public void fine(String fine){
		if(loggingLevel.intValue() <= Level.FINE.intValue()){
			logger.fine(fine);
		}
	}
	
	public void config(String config){
		if(loggingLevel.intValue() <= Level.CONFIG.intValue()){
			logger.config(config);
		}
	}
	
	public void info(String info){
		if(loggingLevel.intValue() <= Level.INFO.intValue()){
			logger.info(info);
		}
	}
	
	public void warning(String warning){
		if(loggingLevel.intValue() <= Level.WARNING.intValue()){
			logger.warning(warning);
		}
	}
	
	public void severe(String severe){
		if(loggingLevel.intValue() <= Level.WARNING.intValue()){
			logger.severe(severe);
		}
	}

	public static Level getLoggingLevel() {
		return loggingLevel;
	}

	public static void setLoggingLevel(Level loggingLevel) {
		AceLogger.loggingLevel = loggingLevel;
	}
	
	
	
	
	

}
