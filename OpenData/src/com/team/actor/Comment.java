package com.team.actor;

public class Comment {

	private String stringComment;
	private int commentId;
	private String timestamp;
	private String username;
	private String userFbId;
	private int ratingNumber; 
	private int ratingId;
	private int userId;
	
	
	public Comment(int commentId, String stringComment, int userId, String timestamp)
	{
		this.stringComment = stringComment;
		this.commentId = commentId;
		this.timestamp = timestamp;
		this.userId = userId;
	}
	
	public String getUserFbId() {
		return userFbId;
	}

	public void setUserFbId(String userFbId) {
		this.userFbId = userFbId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getRatingId() {
		return ratingId;
	}

	public void setRatingId(int ratingId) {
		this.ratingId = ratingId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getRatingNumber() {
		return ratingNumber;
	}
	public String getStringComment() {
		return stringComment;
	}

	public void setStringComment(String stringComment) {
		this.stringComment = stringComment;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public void setRatingNumber(int ratingNumber) {
		this.ratingNumber = ratingNumber;
	}
}
