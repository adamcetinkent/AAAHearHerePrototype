package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMFriendship;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFriendshipUserNested;

/**
 * Created by adam on 10/03/16.
 *
 * A Friendship belongs to two {@link HHUser}s - the user and his friend.
 */
public class HHFriendship extends HHBase implements Parcelable {

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
			cursor.getLong(cursor.getColumnIndex(ORMFriendship.ID())),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFriendship.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFriendship.UPDATED_AT())))
		);
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMFriendship.USER_ID()));
		this.friend_user_id =  cursor.getLong(cursor.getColumnIndex(ORMFriendship.FRIEND_USER_ID()));
	}

	public long getUserID() {
		return user_id;
	}

	public long getFriendUserID() {
		return friend_user_id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(user_id);
		dest.writeLong(friend_user_id);
	}

	public static final Parcelable.Creator<HHFriendship> CREATOR = new Parcelable.Creator<HHFriendship>(){

		@Override
		public HHFriendship createFromParcel(Parcel source) {
			return new HHFriendship(source);
		}

		@Override
		public HHFriendship[] newArray(int size) {
			return new HHFriendship[size];
		}

	};

	private HHFriendship(Parcel in){
		super(in);
		user_id = in.readLong();
		friend_user_id = in.readLong();
	}

}
