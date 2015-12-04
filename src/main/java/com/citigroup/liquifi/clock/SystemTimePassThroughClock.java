package com.citigroup.liquifi.clock;

/**
 * This clock behaves as though no adjustments of time were possible, intended for Production/QA/UAT.
 * Listeners will never be notified of a clock adjustment because this class doesn't invoke {@link ClockAdjustmentListener#onClockAdjustment()}.
 * @author Trampas Kirk
 */
public class SystemTimePassThroughClock extends ListenerNotifyingClock {

    /**
     * Moves the clock time forward
     * @param millisecondsToAdd how much to move forward (this adds the time to the current clock time w/o any conversion).
     */
    @Override
    public void addMilliseconds(long millisecondsToAdd) {
        throw new UnsupportedOperationException("SystemTimePassThroughClock cannot be adjusted.");
    }

    /**
     * Sets the time to the time given by <code>time</code> in this format:
     * </br>HH:MM:SS.mmm
     * @param time the time to set the clock to
     */
    @Override
    public void setTimeWithinCurrentDay(String time) {
        throw new UnsupportedOperationException("SystemTimePassThroughClock cannot be adjusted.");
    }

    /**
     * Returns the current time in milliseconds.
     * Passes through {@link System#currentTimeMillis()} without altering the value.
     * @return the "clock-reported" current time in milliseconds
     */
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
