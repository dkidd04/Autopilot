package com.citigroup.liquifi.autopilot.message.xstream;

public class AdminMarketData {
	private double bid;
	private double offer;

	protected AdminMarketData() {	// For XStream initialization via reflection in Java 7
	}

	public AdminMarketData(double bid, double offer ){
		this.bid = bid;
		this.offer = offer;
	}
	
	public double getBid() {
		return bid;
	}
	
	public void setBid(double bid) {
		this.bid = bid;
	}
	
	public double getOffer() {
		return offer;
	}
	
	public void setOffer(double offer) {
		this.offer = offer;
	}
	
}
