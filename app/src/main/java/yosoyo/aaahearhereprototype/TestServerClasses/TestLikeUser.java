package yosoyo.aaahearhereprototype.TestServerClasses;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestLikeUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class TestLikeUser {

	TestLike like;
	TestUser user;

	public TestLikeUser(TestLikeUserNested nested){
		this.like = new TestLike(nested);
		this.user = nested.getUser();
	}

	public TestLikeUser(Cursor cursor){
		this.like = new TestLike(cursor);
		this.user = new TestUser(cursor);
	}

	public TestLike getLike() {
		return like;
	}

	public TestUser getUser() {
		return user;
	}
}
