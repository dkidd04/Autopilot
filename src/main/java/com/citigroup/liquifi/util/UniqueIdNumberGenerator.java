package com.citigroup.liquifi.util;

public class UniqueIdNumberGenerator implements UniqueId {
	//static HashMap<Integer, Long> save = new HashMap<Integer, Long>();
	static long current = System.currentTimeMillis();

	public String generate(int length) {
		return String.valueOf(current++);
	}
}
