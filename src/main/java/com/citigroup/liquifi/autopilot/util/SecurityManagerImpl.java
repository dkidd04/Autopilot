package com.citigroup.liquifi.autopilot.util;

import java.util.LinkedHashSet;
import java.util.Set;

public class SecurityManagerImpl implements SercurityManager {
	private final String _suffix;
	private final int _min_len;
	private final int _suffix_len;
	private final char _prefix;
	
	public SecurityManagerImpl(final int len, final String suffix, final char prefix) {
		this._min_len = len;
		this._suffix = suffix;
		this._suffix_len = suffix.length();
		this._prefix = prefix;
	}
	
	public SecurityManagerImpl(final String suffix) {
		this(0, suffix, '\0');
	}

	public String localCode(final String security) {
		int start = 0, len = security.length();
		while (start < len && security.charAt(start) == this._prefix) start++;
		String local = security.substring(start, len - this._suffix_len);
		return local;
	}
	
	public String ricCode(final String security) {
		final int len = security.length();
		if (this._min_len > len) {
			final char[] newChar = new char[this._min_len + this._suffix_len];
			final int prefix = this._min_len - len;
			for (int i = 0; i < prefix; i++)
				newChar[i] = this._prefix;
			
			for (int i = 0; i < len; i++)
				newChar[prefix + i] = security.charAt(i);
			
			for (int i = 0; i < this._suffix_len; i++)
				newChar[this._min_len + i] = this._suffix.charAt(i);
			return new String(newChar);
		}
		return security + this._suffix;
	}
	
	@Override
	public Set<String> getAlternateName(String security) {
		Set<String> set = new LinkedHashSet<String>(1);
		if (security.endsWith(this._suffix))
			set.add(localCode(security));
		else
			set.add(ricCode(security));
		return set;
	}

}
