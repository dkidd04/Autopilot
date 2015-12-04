package com.citigroup.liquifi.autopilot.logger;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class AceFormatter extends Formatter {
	private SimpleDateFormat sdf;

	public AceFormatter() {
		sdf = null;
		sdf = new SimpleDateFormat("HH:mm:ss:SSS");
	}

	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(record.getThreadID()).append("|");
		sb.append(sdf.format(Long.valueOf(record.getMillis()))).append("|");
		sb.append(record.getLevel()).append("|");

		if (record.getLoggerName() != null) {
			sb.append(record.getLoggerName()).append("|");
		}
		if (record.getMessage() != null) {
			sb.append(record.getMessage()).append("|");
		}
		if (record.getThrown() != null) {
			Throwable throwable = record.getThrown();
			sb.append("Throwable: ").append(throwable).append("|");
			append(throwable, sb);
		}
		sb.append("\n");

		return sb.toString();
	}

	private void append(Throwable throwable, StringBuilder sb) {
		if (throwable == null || sb == null)
			return;
		StackTraceElement[] stackTrace = throwable.getStackTrace();
		if (stackTrace != null && stackTrace.length > 0) {
			for (int i = 0; i < stackTrace.length; i++) {
				sb.append("at ").append(stackTrace[i].toString()).append("|");
			}
		}

		Throwable cause = throwable.getCause();
		if (cause != null) {
			sb.append("Caused by: ").append(cause).append("|");
			append(cause, sb);
		}
	}

}
