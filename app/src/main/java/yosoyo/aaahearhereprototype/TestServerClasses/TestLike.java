package yosoyo.aaahearhereprototype.TestServerClasses;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.TestServerClasses.Database.ORMTestComment;
import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestLikeUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class TestLike {

	long id;
	long post_id;
	long user_id;
	Timestamp updated_at;
	Timestamp created_at;

	public TestLike(){}

	public TestLike(long post_id, long user_id){
		this.post_id = post_id;
		this.user_id = user_id;
	}

	public TestLike(TestLikeUserNested nested){
		this.id = nested.getID();
		this.post_id = nested.getPostID();
		this.user_id = nested.getUserID();
		this.created_at = nested.getCreatedAt();
		this.updated_at = nested.getUpdatedAt();
	}

	public TestLike(Cursor cursor){
		this.id = cursor.getLong(cursor.getColumnIndex(ORMTestComment.COLUMN_ID_NAME));;
		this.post_id = cursor.getLong(cursor.getColumnIndex(ORMTestComment.COLUMN_POST_ID_NAME));
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMTestComment.COLUMN_USER_ID_NAME));
		this.updated_at = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMTestComment.COLUMN_UPDATED_AT_NAME)));
		this.created_at = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMTestComment.COLUMN_CREATED_AT_NAME)));
	}

	public long getID() {
		return id;
	}

	public long getPostID() {
		return post_id;
	}

	public long getUserID() {
		return user_id;
	}

	public Timestamp getUpdatedAt() {
		return updated_at;
	}

	public Timestamp getCreatedAt() {
		return created_at;
	}
}
