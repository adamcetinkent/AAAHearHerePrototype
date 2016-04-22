package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowRequestUserNested;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedRequestUserNested;

import java.sql.Timestamp;

/**
 * Created by adam on 10/03/16.
 *
 * A FollowRequest belongs to two {@link HHUser}s - the requester and the requested.
 */
public class HHFollowRequest extends HHBase implements Parcelable {

	private final long user_id;
	private final long requested_user_id;

	public HHFollowRequest(long user_id, long requested_user_id) {
		super();
		this.user_id = user_id;
		this.requested_user_id = requested_user_id;
	}

	public HHFollowRequest(HHFollowRequestUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.user_id = nested.getUserID();
		this.requested_user_id = nested.getRequestedUserID();
	}

	public HHFollowRequest(HHFollowedRequestUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.user_id = nested.getUserID();
		this.requested_user_id = nested.getRequestedUserID();
	}


	public HHFollowRequest(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMFollowRequest.ID())),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollowRequest.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollowRequest.UPDATED_AT())))
		);
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMFollowRequest.USER_ID()));
		this.requested_user_id =  cursor.getLong(cursor.getColumnIndex(ORMFollowRequest.REQUESTED_USER_ID()));
	}

	public long getUserID() {
		return user_id;
	}

	public long getRequestedUserID() {
		return requested_user_id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(user_id);
		dest.writeLong(requested_user_id);
	}

	public static final Parcelable.Creator<HHFollowRequest> CREATOR = new Parcelable.Creator<HHFollowRequest>(){

		@Override
		public HHFollowRequest createFromParcel(Parcel source) {
			return new HHFollowRequest(source);
		}

		@Override
		public HHFollowRequest[] newArray(int size) {
			return new HHFollowRequest[size];
		}

	};

	private HHFollowRequest(Parcel in){
		super(in);
		user_id = in.readLong();
		requested_user_id = in.readLong();
	}

}
