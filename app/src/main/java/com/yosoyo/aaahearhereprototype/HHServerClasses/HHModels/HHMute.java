package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

/**
 * Created by adam on 20/06/16.
 *
 * A Mute belongs to a {@link HHUser} and has a {@link HHPost}.
 */
public class HHMute extends HHBase implements Parcelable {

	private final long post_id;
	private final long user_id;

	public HHMute(long post_id, long user_id){
		super();
		this.post_id = post_id;
		this.user_id = user_id;
	}

	protected HHMute(long id, long post_id, long user_id, Timestamp created_at, Timestamp updated_at){
		super(
			id,
			created_at,
			updated_at
		);
		this.post_id = post_id;
		this.user_id = user_id;
	}

	/*public HHMute(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMLike.ID())),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMLike.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMLike.UPDATED_AT())))
		);
		this.post_id = cursor.getLong(cursor.getColumnIndex(ORMLike.POST_ID()));
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMLike.USER_ID()));
	}*/

	public long getPostID() {
		return post_id;
	}

	public long getUserID() {
		return user_id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(post_id);
		dest.writeLong(user_id);
	}

	public static final Creator<HHMute> CREATOR = new Creator<HHMute>(){

		@Override
		public HHMute createFromParcel(Parcel source) {
			return new HHMute(source);
		}

		@Override
		public HHMute[] newArray(int size) {
			return new HHMute[size];
		}

	};

	protected HHMute(Parcel in){
		super(in);
		post_id = in.readLong();
		user_id = in.readLong();
	}

}
