package com.citigroup.liquifi.autopilot.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.citi.get.common.data.IDataManager;
import com.citi.get.common.data.factory.DataManagerFactory;
import com.citi.productapi.info.intf.IProduct;
public class ProductApiTest {
	
	
	static IDataManager dm = null;
	long startTime;
	Map<String, String> configMap;
	@Before
    public void setUp() throws IOException {
		
		configMap = new HashMap<String, String>();
		configMap.put("ClientName", "liquifi");
		configMap.put("Region", "EMEA");
		configMap.put("ENV", "QAPROD");
		configMap.put("ConfigPath", new ClassPathResource("DataAPIConfig_"+configMap.get("ENV")+".xml").getURL().toString().replace("file:/", ""));
		configMap.put("TransportPath", new ClassPathResource("Transport_"+configMap.get("Region")+".xml").getURL().toString().replace("file:/", ""));
		startTime = System.currentTimeMillis();
		dm = DataManagerFactory.createDataManager(configMap);
    }
	
	@Test
	public void testLookupRic(){
		
		String symbol="VOD.L";
		while (true){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("RIC", symbol);
		IProduct product = testLookup("lookup_ric", params);
		if (product!=null){
			long endTime = System.currentTimeMillis();
			System.out.println("Time Taken : " + ((endTime - startTime) / 1000));
			System.out.println("FII: "+product.getFII());
		}else{
			System.out.println("Product does not exist");
		}
		break;

		}
	}

	private static IProduct testLookup(String lookupKey,Map<String, Object> lookupParms) {
		IProduct product = null;
		try {
			product = dm.lookup(IProduct.class, lookupKey, lookupParms);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return product;
	}

}
