package com.citigroup.liquifi.clock;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Trampas Kirk
 */
public abstract class ListenerNotifyingClock implements Clock {

    /**
     * Clock adjustment listeners.
     */
    private final List<ClockAdjustmentListener> clockAdjustmentListeners = new CopyOnWriteArrayList<ClockAdjustmentListener>();

    /**
     * Gets the clock offset.
     * @return the clock offset
     */
    @Override
    public long getAdjustment() {
        return 0;
    }

    /**
     * Gets the previous clock offset.
     * @return the previous clock offset
     */
    @Override
    public long getPreviousAdjustment() {
        return 0;
    }

    /**
     * Returns the current time as a GMT Calendar.
     * @return the current time as a GMT Calendar
     */
    @Override
    public Calendar currentTimeGMT() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(currentTimeMillis());
		return cal;
    }
    
    @Override
    public Calendar currentTimeLocal() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentTimeMillis());
		return cal;
    }
    
    /**
     * Gets the <code>ClockAdjustmentListener</code>s.
     * @return the <code>ClockAdjustmentListener</code>s
     */
    public List<ClockAdjustmentListener> getClockAdjustmentListeners() {
        return clockAdjustmentListeners;
    }

    /**
     * Whenever the clock is adjusted, clock change listeners are notified
     * Adds a listener to this clock, for change events.
     * @param listener the listener to add.
     */
    @Override
    public void addClockAdjustedListener(ClockAdjustmentListener listener) {
        getClockAdjustmentListeners().add(listener);
    }

    /**
     * Whenever the clock is adjusted, clock change listeners are notified
     * Removes a listener from this clock, for change events.
     * @param listener the listener to add.
     */
    @Override
    public void removeClockAdjustedListener(ClockAdjustmentListener listener) {
        getClockAdjustmentListeners().remove(listener);
    }

    /**
     * Notifies all <code>ClockAdjustmentListener</code>s that the <code>Clock</code> has been adjusted.
     */
    protected void clockAdjusted() {
        for (ClockAdjustmentListener clockAdjustmentListener : getClockAdjustmentListeners()) {
            clockAdjustmentListener.onClockAdjustment();
        }
    }

    @Override
	public Date adjust(Date time) {
		return time;
	}
    
    @Override
	public long adjust(long time) {
		return time;
	}
}
