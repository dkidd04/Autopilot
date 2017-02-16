package com.citigroup.liquifi.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.citigroup.get.util.date.DateUtil;

public class Util {
	private static Logger logger = Logger.getLogger("RaceConditon"); 
	private static int sequenceID = 0x0000;
	
	public synchronized static String generateSequence() {
		return Long.toHexString(System.currentTimeMillis()) + Integer.toHexString(sequenceID++); 
	}
	
	public static String getStackTrace(Throwable e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static String enrichPath(String path) {
		String hostAlias= System.getProperty("hostName","#");
		String hostName = getHostName();
		path = path.replaceAll(hostAlias, hostName);
		return path;
	}
	
	public static String getHostName(){
		String hostNameProp = System.getProperty("hostname"); //
		if (hostNameProp == null){
			try {
				InetAddress addr = InetAddress.getLocalHost();
				hostNameProp = addr.getHostName();

			} catch (UnknownHostException e) {
				logger.info(Util.getStackTrace(e));
			}
		}
		return hostNameProp;
	}
	
	public static String getTestIDSequencer(boolean isJulian){
		String sequencer = null;
		long julianDate = isJulian ? (long) (DateUtil.convertDateToJulianDate(new Date())*1000000)
				: System.currentTimeMillis() * 2;
		sequencer = String.valueOf(julianDate);
		logger.info("New TestID: " + sequencer);
		return sequencer;
	}
	
	public static String getLabelIDSequencer(){
		String sequencer = null;
		long julianDate = (long) (DateUtil.convertDateToJulianDate(new Date())*1000000);
		sequencer = String.valueOf(julianDate);
		logger.info("New LabelID: " + sequencer);
		return sequencer;
	}

	public static void setAtScreenCenter(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int midXPos = (int)screenSize.getWidth() / 2;
        int midYPos = (int)screenSize.getHeight() / 2;
        
        // show splash screen
        component.setBounds(midXPos - component.getWidth()/2,
                			midYPos - component.getHeight()/2,
                			component.getWidth(), 
                			component.getHeight());      
	}	
	
    public static <T> List<T> asSortedList(Collection<T> c, Comparator<T> comparator) {
    	List<T> list = new ArrayList<T>(c);
    	Collections.sort(list, comparator);
    	return list;
    }
}
