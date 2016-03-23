package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMComment;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHCommentUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHComment extends HHLike {

	private final String message;

	public HHComment(long post_id, long user_id, String message){
		super(post_id, user_id);
		this.message = message;
	}

	public HHComment(HHCommentUserNested nested){
		super(
			nested.getID(),
			nested.getPostID(),
			nested.getUserID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.message = nested.getMessage();
	}

	public HHComment(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_ID_NAME)),
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_POST_ID_NAME)),
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_USER_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_UPDATED_AT_NAME)))
		);
		this.message = cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_MESSAGE_NAME));
	}

	public String getMessage() {
		return message;
	}

}
