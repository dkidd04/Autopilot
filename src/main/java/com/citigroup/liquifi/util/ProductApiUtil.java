package com.citigroup.liquifi.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.citi.get.common.data.IDataManager;
import com.citi.get.common.data.factory.DataManagerFactory;
import com.citi.productapi.info.intf.IProduct;

public class ProductApiUtil {
	static IDataManager dm = null;
	Map<String, String> configMap;

	public Map<String, String> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<String, String> configMap) {
		this.configMap = configMap;
	}
	
	private IProduct testLookup(String lookupKey,Map<String, Object> lookupParms) {
		IProduct product = null;
		try {
		if(dm == null){
			configMap.put("ConfigPath", new ClassPathResource("DataAPIConfig_"+configMap.get("ENV")+".xml").getURL().toString().replace("file:/", ""));
			configMap.put("TransportPath", new ClassPathResource("Transport_"+configMap.get("Region")+".xml").getURL().toString().replace("file:/", ""));
			
			dm = DataManagerFactory.createDataManager(configMap);
		}
			product = dm.lookup(IProduct.class, lookupKey, lookupParms);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return product;
	}
	
	
	public String getFiiStr(String symbol){
		long startTime = System.currentTimeMillis();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("RIC", symbol);
		IProduct product = testLookup("lookup_ric", params);
		if (product!=null){
			System.out.println("Fii:"+product.getFII());
			long endTime = System.currentTimeMillis();
			System.out.println("Time Taken : " + ((endTime - startTime) / 1000));
			return product.getFII();
		}else{
			return "1234567";
		}
	}
	
}
