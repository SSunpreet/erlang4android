package com.ernovation.erlangforandroid;

public class Repository {

	private long iId;
	private String iName;
	private String iURL;
	
	public long getId() {
		return iId;
	}
	public void setId(long aId) {
		this.iId = aId;
	}
	public String getName() {
		return iName;
	}
	public void setName(String aName) {
		this.iName = aName;
	}
	public String getURL() {
		return iURL;
	}
	public void setURL(String aURL) {
		this.iURL = aURL;
	}
	
	
}
