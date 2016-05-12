package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

/**
 * Created by adam on 11/05/16.
 */
public class HHNotification extends HHBase implements Parcelable {

	public static final int NOTIFICATION_TYPE_NEW_POST = 0;
	public static final int NOTIFICATION_TYPE_LIKE_POST = 1;
	public static final int NOTIFICATION_TYPE_NEW_COMMENT = 2;
	public static final int NOTIFICATION_TYPE_NEW_FOLLOW = 3;

	private long user_id;
	private Timestamp read_at;
	private int notification_type;
	private long notification_link;

	public long getUserID() {
		return user_id;
	}

	public Timestamp getReadAt() {
		return read_at;
	}

	public int getNotificationType() {
		return notification_type;
	}

	public long getNotificationLink() {
		return notification_link;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(user_id);
		if (read_at != null)
			dest.writeLong(read_at.getTime());
		else
			dest.writeLong(-1);
		dest.writeInt(notification_type);
		dest.writeLong(notification_link);
	}

	public static final Parcelable.Creator<HHNotification> CREATOR = new Parcelable.Creator<HHNotification>(){

		@Override
		public HHNotification createFromParcel(Parcel source) {
			return new HHNotification(source);
		}

		@Override
		public HHNotification[] newArray(int size) {
			return new HHNotification[size];
		}

	};

	protected HHNotification(Parcel in){
		super(in);
		user_id = in.readLong();
		long read_at_time = in.readLong();
		if (read_at_time == -1)
			read_at = null;
		else
			read_at = new Timestamp(read_at_time);
		notification_type = in.readInt();
		notification_link = in.readLong();
	}

}
