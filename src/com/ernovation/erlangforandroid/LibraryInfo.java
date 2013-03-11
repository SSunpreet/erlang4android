package com.ernovation.erlangforandroid;

import java.io.File;
import java.util.Iterator;

import org.json.JSONObject;

public class LibraryInfo {

	private long iId;
	private String iName;
	private String iVersion;
	private String iSummary;
	private String iKeywords;
	private String iAbstract;
	private Repository iRepository;
	private String iAuthor;
	private String iCategory;
	private String iDepends;
	private String iHome;
	private String iURL;
	private String iDate;
	private String iPackager;
	private int iState = 0;
	
	public final static int INSTALLED = 1;
	public final static int TO_INSTALL = 2;
	public final static int DEPENDENCY = 4;
	public final static int TO_REMOVE = 8;
	
	
	public static LibraryInfo fromJSON(JSONObject aObj) {
		LibraryInfo l = new LibraryInfo();
		Iterator i = aObj.keys();
		while (i.hasNext()) {
			String key = (String) i.next();
			try {
				if ("name".equals(key))
					l.setName(aObj.getString("name"));
				else if ("abstract".equals(key))

					l.setAbstract(aObj.getString("abstract"));
				else if ("author".equals(key))
					l.setAuthor(aObj.getString("author"));
				else if ("category".equals(key))
					l.setCategory(aObj.getString("category"));
				else if ("date".equals(key))
					l.setDate(aObj.getString("date"));
				else if ("depends".equals(key))
					l.setDepends(aObj.getString("depends"));
				else if ("home".equals(key))
					l.setHome(aObj.getString("home"));
				else if ("keywords".equals(key))
					l.setKeywords(aObj.getString("keywords"));
				else if ("summary".equals(key))
					l.setSummary(aObj.getString("summary"));
				else if ("url".equals(key))
					l.setURL(aObj.getString("url"));
				else if ("packager".equals(key))
					l.setPackager(aObj.getString("packager"));
			} catch (Exception e) {

			}
		}
		return l;
	}

	public boolean isInstalled() {
		File ext = ErlangMain.CONTEXT.getFilesDir();
		File f = new File(ext.getAbsolutePath()+"/erlang/lib/"+iName+"-"+iVersion);
		//System.out.println("Checking "+f.getAbsolutePath()+": "+(f.exists()&& f.isDirectory()));
		return f.exists() && f.isDirectory();
	}
	
	public String getPackager() {
		return iPackager;
	}

	public void setPackager(String aPackager) {
		this.iPackager = aPackager;
	}

	public String getAuthor() {
		return iAuthor;
	}

	public void setAuthor(String aAuthor) {
		this.iAuthor = aAuthor;
	}

	public String getCategory() {
		return iCategory;
	}

	public void setCategory(String aCategory) {
		this.iCategory = aCategory;
	}

	public String getDepends() {
		return iDepends;
	}

	public void setDepends(String aDepends) {
		this.iDepends = aDepends;
	}

	public String getHome() {
		return iHome;
	}

	public void setHome(String aHome) {
		this.iHome = aHome;
	}

	public String getURL() {
		return iURL;
	}

	public void setURL(String aURL) {
		this.iURL = aURL;
	}

	public String getDate() {
		return iDate;
	}

	public void setDate(String aDate) {
		this.iDate = aDate;
	}

	public String getName() {
		return iName;
	}

	public void setName(String aName) {
		this.iName = aName;
	}

	public String getVersion() {
		return iVersion;
	}

	public void setVersion(String aVersion) {
		this.iVersion = aVersion;
	}

	public String getSummary() {
		return iSummary;
	}

	public void setSummary(String aSummary) {
		this.iSummary = aSummary;
	}

	public String getKeywords() {
		return iKeywords;
	}

	public void setKeywords(String aKeywords) {
		this.iKeywords = aKeywords;
	}

	public String getAbstract() {
		return iAbstract;
	}

	public void setAbstract(String aAbstract) {
		this.iAbstract = aAbstract;
	}

	public Repository getRepository() {
		return iRepository;
	}

	public void setRepository(Repository aRepository) {
		this.iRepository = aRepository;
	}

	public long getId() {
		return iId;
	}

	public void setId(long aId) {
		this.iId = aId;
	}

	public int getState() {
		return iState;
	}

	public void clearState() {
		iState = 0;
	}

	public boolean hasState(int aState) {
		return (iState & aState) != 0;
	}
	
	public void setState(int aState) {
		iState |= aState;
	}
	
	public void resetState(int aState) {
		iState = iState & ~aState;
	}

}
