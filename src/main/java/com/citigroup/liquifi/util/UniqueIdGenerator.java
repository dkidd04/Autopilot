package com.citigroup.liquifi.util;
import java.util.UUID;

public class UniqueIdGenerator implements UniqueId {
	public String generate(int length) {
		return UUID.randomUUID().toString().replace("-", "").substring(0,length);
	}
}
