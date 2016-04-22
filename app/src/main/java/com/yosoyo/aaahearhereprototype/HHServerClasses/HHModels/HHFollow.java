package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMFollow;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowUserNested;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedUserNested;

import java.sql.Timestamp;

/**
 * Created by adam on 10/03/16.
 *
 * A Follow belongs to two {@link HHUser}s - the follower and the followed.
 */
public class HHFollow extends HHBase implements Parcelable {

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
			cursor.getLong(cursor.getColumnIndex(ORMFollow.ID())),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollow.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollow.UPDATED_AT())))
		);
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMFollow.USER_ID()));
		this.followed_user_id =  cursor.getLong(cursor.getColumnIndex(ORMFollow.FOLLOWED_USER_ID()));
	}

	public long getUserID() {
		return user_id;
	}

	public long getFollowedUserID() {
		return followed_user_id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(user_id);
		dest.writeLong(followed_user_id);
	}

	public static final Parcelable.Creator<HHFollow> CREATOR = new Parcelable.Creator<HHFollow>(){

		@Override
		public HHFollow createFromParcel(Parcel source) {
			return new HHFollow(source);
		}

		@Override
		public HHFollow[] newArray(int size) {
			return new HHFollow[size];
		}

	};

	private HHFollow(Parcel in){
		super(in);
		user_id = in.readLong();
		followed_user_id = in.readLong();
	}

}
