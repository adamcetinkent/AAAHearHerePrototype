package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMComment;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHLikeUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHLike extends HHBase {

	long post_id;
	long user_id;

	public HHLike(long post_id, long user_id){
		super();
		this.post_id = post_id;
		this.user_id = user_id;
	}

	protected HHLike(long id, long post_id, long user_id, Timestamp created_at, Timestamp updated_at){
		super(
			id,
			created_at,
			updated_at
			 );
		this.post_id = post_id;
		this.user_id = user_id;
	}

	public HHLike(HHLikeUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
			 );
		this.post_id = nested.getPostID();
		this.user_id = nested.getUserID();
	}

	public HHLike(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_UPDATED_AT_NAME)))
			 );
		this.post_id = cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_POST_ID_NAME));
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_USER_ID_NAME));
	}

	public long getPostID() {
		return post_id;
	}

	public long getUserID() {
		return user_id;
	}
}
