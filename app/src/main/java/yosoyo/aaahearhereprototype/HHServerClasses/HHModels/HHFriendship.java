package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMFriendship;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFriendshipUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFriendship extends HHBase {

	private final long user_id;
	private final long friend_user_id;

	public HHFriendship(HHFriendshipUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
			 );
		this.user_id = nested.getUserID();
		this.friend_user_id = nested.getFriendUserID();
	}

	public HHFriendship(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMFriendship.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFriendship.COLUMN_UPDATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFriendship.COLUMN_CREATED_AT_NAME)))
		);
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMFriendship.COLUMN_USER_ID_NAME));
		this.friend_user_id =  cursor.getLong(cursor.getColumnIndex(ORMFriendship.COLUMN_FRIEND_USER_ID_NAME));
	}

	public long getUserID() {
		return user_id;
	}

	public long getFriendUserID() {
		return friend_user_id;
	}
}
