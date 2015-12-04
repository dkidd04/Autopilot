package com.citigroup.liquifi.entities;

import java.io.Serializable;
import java.util.List;

public interface Step<X extends Tag> extends Serializable, Cloneable {
	public int getActionSequence();
	public String getCommonTags();
	public String getMessage();
	public String getMsgType();
	public String getTemplate();
	public List<X> getTags();
}
