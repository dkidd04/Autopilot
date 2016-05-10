package com.citigroup.liquifi.autopilot.test;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.citigroup.liquifi.util.SymFiiUtil;

public class TestFII {
	
	SymFiiUtil symFiiUtil;
	String defaultFii = "1234567";
	@Before
    public void setUp() {
		Resource resource = new ClassPathResource("springConfig.xml");
		BeanFactory factory = new XmlBeanFactory(resource);
		symFiiUtil = (SymFiiUtil) factory.getBean("symFiiUtil");
    }

	@Test
    public void testMadeUpSymbol() {
		assertEquals(defaultFii, symFiiUtil.getFiiStr("AZZZZ.L"));
    }

	@Test
    public void testIndexSymbol() {
		assertEquals("32062291", symFiiUtil.getFiiStr(".SXOP"));
    }
	
	@Test
    public void testSymbol() {
		assertEquals("59198795", symFiiUtil.getFiiStr("VOD.L"));
    }
	
	@Test
    public void testEmptySymbol() {
		assertEquals(defaultFii, symFiiUtil.getFiiStr(""));
    }
}
