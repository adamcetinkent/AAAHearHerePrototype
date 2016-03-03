package yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.TestCommentUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;

/**
 * Created by adam on 02/03/16.
 */
public class TestPostUserCommentsNested {

	long id;
	long user_id;
	String track;
	double lat;
	double lon;
	String message;
	Timestamp updated_at;
	Timestamp created_at;
	TestUser user;
	TestCommentUserNested[] comments;

	public long getID() {
		return id;
	}

	public long getUserID() {
		return user_id;
	}

	public String getTrack() {
		return track;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getMessage() {
		return message;
	}

	public Timestamp getUpdatedAt() {
		return updated_at;
	}

	public Timestamp getCreatedAt() {
		return created_at;
	}

	public TestUser getUser(){
		return user;
	}

	public List<TestCommentUser> getCommentsList(){
		List<TestCommentUser> testCommentUsers = new ArrayList<>(comments.length);
		for (TestCommentUserNested commentNested : comments){
			TestCommentUser testCommentUser = new TestCommentUser(commentNested);
			testCommentUsers.add(testCommentUser);
		}
		return testCommentUsers;
	}

}
