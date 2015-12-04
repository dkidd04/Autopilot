package com.citigroup.liquifi.clock;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.citigroup.liquifi.autopilot.controller.AdminMsgNotifier;
import com.citigroup.liquifi.autopilot.controller.TestCaseController;
import com.citigroup.liquifi.autopilot.logger.AceLogger;

/**
 * An adjustable Clock for testing/development.
 * @author Trampas Kirk
 */
public class AdjustableClock extends ListenerNotifyingClock {

	/**
	 * The clock offset in milliseconds.
	 */
	private long adjustment = 0;

	/**
	 * The previous clock offseet in milliseconds.
	 */
	private long previousAdjustment = 0;

	/**
	 * Gets the clock offset.
	 * @return the clock offset
	 */
	@Override
	public long getAdjustment() {
		return adjustment;
	}

	/**
	 * Setter for <code>adjustment</code>.
	 * @param adjustment the new adjustment
	 */
	public void setAdjustment(long adjustment) {
		this.adjustment = adjustment;
	}

	/**
	 * Gets the previous clock offset.
	 * @return the previous clock offset
	 */
	@Override
	public long getPreviousAdjustment() {
		return previousAdjustment;
	}

	/**
	 * Sets the previous adjustment.
	 * @param adjustment
	 */
	public void setPreviousAdjustment(long adjustment) {
		this.previousAdjustment = adjustment;
	}

	/**
	 * Returns the current time in milliseconds, including the net result of all previous adjustments.
	 * @return the current time in milliseconds, including the net result of all previous adjustments
	 */
	@Override
	public long currentTimeMillis() {
		return getAdjustment() + System.currentTimeMillis();
	}

	/**
	 * Sets the time to the time given by <code>time</code> in this format:
	 * </br>HH:MM:SS.mmm
	 * Roughly validates input in two ways:
	 * <ol>
	 * <li>The input time must match the Regex Pattern: "\d\d:\d\d:\d\d.\d\d\d". For example
	 * 14:18:25.203 would be 18 minutes after 2PM with 25.203 seconds elapsed.</li>
	 * </ol>
	 * @param time the time to set the clock to
	 */
	@Override
	public void setTimeWithinCurrentDay(String time) {
		if (StringUtils.isNotBlank(time) && !time.trim().matches("\\d\\d:\\d\\d:\\d\\d(.\\d\\d\\d)?")) {
			throw new IllegalArgumentException("Time to set clock to must be in the form HH:MM:SS.mmm. HH=24h, MM=Zero prefixed minutes, SS=Zero prefixed seconds, mmm=milliseconds");
		}

		long offset = 0;
		if (StringUtils.isNotBlank(time)) {
			long newTime = getTimeToSetInMillis(time);
			if (newTime == currentTimeMillis()) {
				return;
			}
			offset = newTime - System.currentTimeMillis();
			setPreviousAdjustment(getAdjustment());
		} else {
			setPreviousAdjustment(0);
		}

		setAdjustment(offset);
		clockAdjusted();
	}

	//private static AceLogger logger = AceLogger.getLogger(TestCaseController.class.getSimpleName());
	
	/**
	 * Converts the time of day given into a long, using today's date.
	 * @param time the time today to get milliseconds for 
	 * @return the time specified in milliseconds
	 */
	protected long getTimeToSetInMillis(String time) {
		String[] timePieces = time.trim().split("\\D");

		// a very hacky way of setting the correct timezone (can't default to local timezone as AP can be run in India, whilst TT is run on local NY time)
		Calendar calendar = AdminMsgNotifier.INSTANCE.getTimezone() == null ? Calendar.getInstance() : Calendar.getInstance(TimeZone.getTimeZone(AdminMsgNotifier.INSTANCE.getTimezone())); // set to local time of TT (set timezone before setting new time)
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timePieces[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
		calendar.set(Calendar.SECOND, Integer.parseInt(timePieces[2]));
		if (timePieces.length == 4) {
			calendar.set(Calendar.MILLISECOND, Integer.parseInt(timePieces[3]));
		} else {
			calendar.set(Calendar.MILLISECOND, 0);
		}

		//logger.info("Time:"+System.currentTimeMillis()+" Time2:"+calendar.getTimeInMillis());
		//logger.info("Day of the year:"+calendar.get(Calendar.DAY_OF_YEAR));
		//logger.info("Calendar:"+calendar);

		long newTime = calendar.getTimeInMillis();
		return newTime;
	}

	/**
	 * Adds millisecondsToAdd milliseconds to the clock.
	 * Negative numbers are supported, but not recommended.
	 * Zero results in nothing being added, and listeners <em>not</em> being notified.
	 * @param millisecondsToAdd how much to add (this adds the time to the current clock time w/o any conversion).
	 */
	@Override
	public void addMilliseconds(long millisecondsToAdd) {
		if (millisecondsToAdd == 0) {
			return;
		}
		adjustment += millisecondsToAdd;
		clockAdjusted();
	}

	@Override
	public Date adjust(Date time) {
		time.setTime(adjust(time.getTime()));
		return time;
	}

	@Override
	public long adjust(long time) {
		return time + getAdjustment();
	}

	@Override
	public String toString() {
		return new Timestamp(this.currentTimeMillis()).toString();
	}
}
