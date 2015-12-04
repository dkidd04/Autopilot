/*
 * AutoPilot.java
 */

package com.citigroup.liquifi;
 
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import com.citigroup.liquifi.autopilot.gui.AutoPilotApplView;


/**
 * The main class of the application.
 */
public class AutoPilotAppl extends SingleFrameApplication {
	

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new AutoPilotApplView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of AutoPilotAppl
     */
    public static AutoPilotAppl getApplication() {
        return Application.getInstance(AutoPilotAppl.class);
    }
}
