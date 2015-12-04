package com.citigroup.liquifi.clock;

/**
 * @author Trampas Kirk
 */
public interface ClockAdjustmentListener {

    /**
     * Provides a method to handle <code>Clock</code> adjustments for interested <code>Object</code>s.
     */
    public void onClockAdjustment();
}
