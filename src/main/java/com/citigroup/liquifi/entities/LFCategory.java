package com.citigroup.liquifi.entities;

import java.io.Serializable;

public class LFCategory implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	private String category;


	public LFCategory(){
		
	}
	
	public LFCategory(String categoryName){
		this.category = categoryName;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public int hashCode(){
		return this.category.hashCode();
	}

	@Override
	public boolean equals(Object obj){
		
		if(obj == null) return false;
		
		if(obj == this) return true;
		
		
		if(!(obj instanceof LFCategory)){
			return false;
		}
		
		return this.getCategory().equals(((LFCategory) obj).getCategory());
	}
}

