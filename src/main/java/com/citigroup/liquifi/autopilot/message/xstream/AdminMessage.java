package com.citigroup.liquifi.autopilot.message.xstream;

import java.util.List;

public class AdminMessage {
	private AdminCommand command;
	private String symbol;
	private String instance;
	private String orderID;
	private String crossID;
	private String clOrdID;
	private String execID;
	private double totalCrossQty;
	private double lastPx;
	private String account;
	private String traderID;
	
	private AdminMarketData marketData;
	private List<Property> properties;

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	protected AdminMessage() {	// For XStream initialization via reflection in Java 7
	}

	public AdminMessage(AdminCommand command){
		this.command = command;
	}
	
	public AdminCommand getCommand() {
		return command;
	}
	public void setCommand(AdminCommand command) {
		this.command = command;
	}
	
	public AdminMarketData getMarketData() {
		return marketData;
	}

	public void setMarketData(AdminMarketData marketData) {
		this.marketData = marketData;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public String getClOrdID() {
		return clOrdID;
	}

	public void setClOrdID(String clOrdID) {
		this.clOrdID = clOrdID;
	}

	public double getTotalCrossQty() {
		return totalCrossQty;
	}

	public void setTotalCrossQty(double totalCrossQty) {
		this.totalCrossQty = totalCrossQty;
	}

	public double getLastPx() {
		return lastPx;
	}

	public void setLastPx(double lastPx) {
		this.lastPx = lastPx;
	}

	public String getCrossID() {
		return crossID;
	}

	public void setCrossID(String crossID) {
		this.crossID = crossID;
	}

	public void setExecID(String execID) {
		this.execID = execID;
	}

	public String getExecID() {
		return execID;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getTraderID() {
		return traderID;
	}
	
	public void setTraderID(String traderID) {
		this.traderID = traderID;
	}

}
