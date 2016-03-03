package yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

/**
 * Created by adam on 02/03/16.
 */
public class TestCommentUserNested {

	long id;
	long post_id;
	long user_id;
	String message;
	Timestamp updated_at;
	Timestamp created_at;
	TestUser user;

	public long getID() {
		return id;
	}

	public long getPostID() {
		return post_id;
	}

	public long getUserID() {
		return user_id;
	}

	public String getMessage() {
		return message;
	}

	public TestUser getUser(){
		return user;
	}

	public Timestamp getUpdatedAt() {
		return updated_at;
	}

	public Timestamp getCreatedAt() {
		return created_at;
	}
}
