package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowRequestUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedRequestUserNested;

/**
 * Created by adam on 10/03/16.
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
			cursor.getLong(cursor.getColumnIndex(ORMFollowRequest.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollowRequest.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMFollowRequest.COLUMN_UPDATED_AT_NAME)))
		);
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMFollowRequest.COLUMN_USER_ID_NAME));
		this.requested_user_id =  cursor.getLong(cursor.getColumnIndex(ORMFollowRequest.COLUMN_REQUESTED_USER_ID_NAME));
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
