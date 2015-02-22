package com.team.actor;

public class User {

	String fbId;
	String user_id;
	
	public User(String user_id, String fbId)
	{
		this.fbId = fbId;
		this.user_id = user_id;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
