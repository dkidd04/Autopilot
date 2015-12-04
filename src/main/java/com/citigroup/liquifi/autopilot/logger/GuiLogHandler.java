package com.citigroup.liquifi.autopilot.logger ;

import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;


import com.citigroup.get.quantum.log.SimpleFormatter;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;

public class GuiLogHandler extends Handler
{
	  //the singleton instance
	private static GuiLogHandler handler = null;

	public GuiLogHandler()
	{
	    configure();
	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publish(LogRecord record) {
	    String message = null;
	    //check if the record is loggable
	    if (!isLoggable(record))
	      return;
	    try {
	      message = getFormatter().format(record);
	    } catch (Exception e) {
	      reportError(null, e, ErrorManager.FORMAT_FAILURE);
	    }

	    try {
	    	String previousText = ApplicationContext.getResultPanel().getLogEditorPane().getText();   
	    	ApplicationContext.getResultPanel().getLogEditorPane().setText(previousText + message);
	    } catch (Exception ex) {
	      reportError(null, ex, ErrorManager.WRITE_FAILURE);
	    }

	}
	  /**
	   * The getInstance method returns the singleton instance of the
	   * WindowHandler object It is synchronized to prevent two threads trying to
	   * create an instance simultaneously. @ return WindowHandler object
	   */

	  public static synchronized GuiLogHandler getInstance() {

	    if (handler == null) {
	      handler = new GuiLogHandler();
	    }
	    return handler;
	  }
	
	private void configure() {
	    LogManager manager = LogManager.getLogManager();
	    String className = this.getClass().getName();
	    String level = manager.getProperty(className + ".level");
	    String filter = manager.getProperty(className + ".filter");
	    String formatter = manager.getProperty(className + ".formatter");

	    //accessing super class methods to set the parameters
	    setLevel(level != null ? Level.parse(level) : Level.INFO);
	    setFilter(makeFilter(filter));
	    setFormatter(makeFormatter(formatter));

	}
	  /**
	   * private method constructing a Filter object with the filter name.
	   * 
	   * @param filterName
	   *            the name of the filter
	   * @return the Filter object
	   */
	  private Filter makeFilter(String filterName) {
	    Class<?> c = null;
	    Filter f = null;
	    try {
	      c = Class.forName(filterName);
	      f = (Filter) c.newInstance();
	    } catch (Exception e) {
	      System.out.println("There was a problem to load the filter class: "
	          + filterName);
	    }
	    return f;
	  }
	  
	  /**
	   * private method creating a Formatter object with the formatter name. If no
	   * name is specified, it returns a SimpleFormatter object
	   * 
	   * @param formatterName
	   *            the name of the formatter
	   * @return Formatter object
	   */
	  private Formatter makeFormatter(String formatterName) {
	    Class<?> c = null;
	    Formatter f = null;

	    try {
	      c = Class.forName(formatterName);
	      f = (Formatter) c.newInstance();
	    } catch (Exception e) {
	      f = new SimpleFormatter();
	    }
	    return f;
	  }
}
