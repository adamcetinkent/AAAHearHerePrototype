package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMFollow;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollow extends HHBase {

	private final long user_id;
	private final long followed_user_id;

	public HHFollow(HHFollowUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.user_id = nested.getUserID();
		this.followed_user_id = nested.getFollowedUserID();
	}

	public HHFollow(HHFollowedUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.user_id = nested.getUserID();
		this.followed_user_id = nested.getFollowedUserID();
	}

	public HHFollow(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMFollow.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollow.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollow.COLUMN_UPDATED_AT_NAME)))
		);
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMFollow.COLUMN_USER_ID_NAME));
		this.followed_user_id =  cursor.getLong(cursor.getColumnIndex(ORMFollow.COLUMN_FOLLOWED_USER_ID_NAME));
	}

	public long getUserID() {
		return user_id;
	}

	public long getFollowedUserID() {
		return followed_user_id;
	}
}
