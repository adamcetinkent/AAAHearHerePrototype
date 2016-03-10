package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMComment;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHLikeUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHLike {

	long id;
	long post_id;
	long user_id;
	Timestamp created_at;
	Timestamp updated_at;

	public HHLike(long post_id, long user_id){
		this.post_id = post_id;
		this.user_id = user_id;
	}

	protected HHLike(long id, long post_id, long user_id, Timestamp created_at, Timestamp updated_at){
		this.id = id;
		this.post_id = post_id;
		this.user_id = user_id;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}

	public HHLike(HHLikeUserNested nested){
		this.id = nested.getID();
		this.post_id = nested.getPostID();
		this.user_id = nested.getUserID();
		this.created_at = nested.getCreatedAt();
		this.updated_at = nested.getUpdatedAt();
	}

	public HHLike(Cursor cursor){
		this.id = cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_ID_NAME));;
		this.post_id = cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_POST_ID_NAME));
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_USER_ID_NAME));
		this.created_at = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_CREATED_AT_NAME)));
		this.updated_at = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_UPDATED_AT_NAME)));
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

	public Timestamp getCreatedAt() {
		return created_at;
	}

	public Timestamp getUpdatedAt() {
		return updated_at;
	}
}
