package com.intrepidusgroup.passwordmanager1;

public class SafeEntry {
	private String passwordName;
	private String passwordValue;
	public SafeEntry (String n, String v) {
		setPasswordName(n);
		setPasswordValue(v);
		
	}
	public String getPasswordName() {
		return passwordName;
	}
	public void setPasswordName(String passwordName) {
		this.passwordName = passwordName;
	}
	public String getPasswordValue() {
		return passwordValue;
	}
	public void setPasswordValue(String passwordValue) {
		this.passwordValue = passwordValue;
	}
}
