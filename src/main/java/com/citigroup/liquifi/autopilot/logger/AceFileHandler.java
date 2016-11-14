package com.citigroup.liquifi.autopilot.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;
import com.citigroup.get.quantum.log.QFileHandler;
import com.citigroup.liquifi.util.Util;

/**
 * 
 * @author Nirmalya Chattopadhyay
 * @version 1.0
 * 
 */
public class AceFileHandler extends QFileHandler {

	public AceFileHandler() throws IOException{
		super();
		System.out.println("Creating AceFileHandler");
	}

	public File generate(String pattern, int generation, int unique)
			throws IOException {


		File file = super.generate(pattern, generation, unique);
		LogManager manager = LogManager.getLogManager();
		String prefix = manager.getProperty(getClass().getName() + ".logfileprefix");
		String enrichedFileName = enrichPath(file.getParent());
		String suffix = file.getAbsoluteFile().getName();
		String logFileName = prefix + "_" + suffix;
		file  = new File( enrichedFileName + System.getProperty("file.separator") + logFileName);
		return file;

	} 
	public String enrichPath(String path){
		return Util.enrichPath(path);

	}
}
