package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMComment;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHTagUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHTag extends HHLike {

	public HHTag(long post_id, long user_id) {
		super(post_id, user_id);
	}

	public HHTag(HHTagUserNested nested){
		super(
			nested.getID(),
			nested.getPostID(),
			nested.getUserID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
			 );
	}

	public HHTag(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_ID_NAME)),
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_POST_ID_NAME)),
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_USER_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_UPDATED_AT_NAME)))
			 );
	}

}
