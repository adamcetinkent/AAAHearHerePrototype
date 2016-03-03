package yosoyo.aaahearhereprototype.TestServerClasses;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestCommentUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class TestCommentUser {

	TestComment comment;
	TestUser user;

	public TestCommentUser(TestCommentUserNested nested){
		this.comment = new TestComment(nested);
		this.user = nested.getUser();
	}

	public TestCommentUser(Cursor cursor){
		this.comment = new TestComment(cursor);
		this.user = new TestUser(cursor);
	}

	public TestComment getComment() {
		return comment;
	}

	public TestUser getUser() {
		return user;
	}
}
