package com.citigroup.liquifi.util;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;
import java.io.IOException;


public class ClassPathLoader {
	 
	private static final Class<?>[] parameters = new Class[]{URL.class};
	 
	public static void addToClasspath(String s) throws IOException {
		File f = new File(s);
		if (!f.exists()) {
			throw new java.io.FileNotFoundException(f.getAbsolutePath());
		}
		addFile(f);
	}//end method
	 
	@SuppressWarnings("deprecation")
	public static void addFile(File f) throws IOException {
		addURL(f.toURL());
	}//end method
	 
	 
	public static void addURL(URL u) throws IOException {
		
		System.out.println("u|"+ u);
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;
	 
		try {
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });
			
			
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}//end try catch
			
	}//end method

	public static void main (String args[]) throws Exception {
		System.out.println("classpath before  " + System.getProperty("java.class.path"));
		//addFile("jnlp.jar");
		//addFile("file:/C:/_platform_c/perforce/AEE/DEV/LiqFiTool/src/main/java/jnlp.jar");
		//addFile("jnlp.jar");
		addToClasspath("C:/_2007_P1/_Mission/12.ActMsiGateway/ReplayAceRecord");
		addToClasspath("C:/_2007_P1/_Mission/12.ActMsiGateway");

		Class.forName("javax.jnlp.FileContents");
		System.out.println("classpath nachher " + System.getProperty("java.class.path"));
	}
	
	}//end class