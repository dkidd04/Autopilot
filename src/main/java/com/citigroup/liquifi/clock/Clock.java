package com.citigroup.liquifi.clock;

import java.util.Calendar;
import java.util.Date;

/**
 * A clock class that allows us to "modify time" for development testing.
 * By default, {@link SystemTimePassThroughClock} is used in Production, and {@link AdjustableClock} in Development.
 * Values are set in Production.properties and Development.properties, respectively through the {@link EnvLoader} at startup time
 * using property 
 * @see https://gcateng-nj1p.nam.nsroot.net:6550/jira/browse/35318-772
 * @author Trampas Kirk
 */
public interface Clock {

	/**
     * Gets the clock offset.
     * @return the clock offset
     */
    public long getAdjustment();

    /**
     * Gets the previous clock offset.
     * @return the previous clock offset
     */
    public long getPreviousAdjustment();
	
    /**
     * Returns the current time in milliseconds.
     * @return the current time in milliseconds
     */
    public long currentTimeMillis();
    
    /**
     * Returns the current time as a GMT Calendar.
     * @return the current time as a GMT Calendar
     */
    public Calendar currentTimeGMT();

    /**
     * Adds millisecondsToAdd milliseconds to the clock.
     * Negative numbers are supported, but not generally recommended.
     * @param millisecondsToAdd how much to add (this adds the time to the current clock time w/o any conversion).
     */
    public void addMilliseconds(long millisecondsToAdd);

    /**
     * Sets the time to the time given by <code>time</code> in this format:
     * </br>HH:MM:SS.mmm
     * @param time the time to set the clock to
     */
    public void setTimeWithinCurrentDay(String time);

    /**
     * Whenever the clock is adjusted, clock change listeners are notified
     * Adds a listener to this clock, for change events.
     * @param listener the listener to add.
     */
    public void addClockAdjustedListener(ClockAdjustmentListener listener);

    /**
     * Whenever the clock is adjusted, clock change listeners are notified
     * Removes a listener from this clock, for change events.
     * @param listener the listener to add.
     */
    public void removeClockAdjustedListener(ClockAdjustmentListener listener);

	Calendar currentTimeLocal();

	public Date adjust(Date time);

	long adjust(long time);
}

