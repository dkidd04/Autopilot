package com.citigroup.liquifi.util;
import java.util.Properties;

/**
 * This class gets the FII for the symbol passed. 
 * In case of made up symbols the default value 1234567 would be passed.
 * @author KS18260
 *
 */
public class SymFiiUtil {
	Properties properties;
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public String getFiiStr(String symbol){
		String fii = properties.getProperty(symbol);
		if(fii == null || "".equals(fii)){
			return "1234567";
		}else{
			return fii;
		}
	}
}
