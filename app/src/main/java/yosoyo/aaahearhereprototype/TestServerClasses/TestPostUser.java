package yosoyo.aaahearhereprototype.TestServerClasses;

import android.database.Cursor;

/**
 * Created by adam on 24/02/16.
 */
public class TestPostUser {

	TestPost post;
	TestUser user;

	public TestPostUser(TestPostUserNested testPostUserNested){
		post = new TestPost(testPostUserNested);
		user = testPostUserNested.user;
	}

	public TestPost getTestPost() {
		return post;
	}

	public TestUser getTestUser() {
		return user;
	}

	public TestPostUser(Cursor cursor){
		post = new TestPost(cursor);
		user = new TestUser(cursor);
	}

}
