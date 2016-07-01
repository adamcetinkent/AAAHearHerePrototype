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
	public static final int NOTIFICATION_TYPE_NEW_FOLLOW_REQUEST = 4;

	private int notification_type;
	private long for_user_id;
	//private String by_fb_user_id;
	private long by_user_id;
	private long post_id;
	private String notification_text;
	private Timestamp read_at;
	private Timestamp sent_at;
	private HHUser by_user;
	private HHUser for_user;
	private HHPost post;
	private boolean newlyRead;

	public long getForUserID(){
		return for_user_id;
	}

	/*public String getByFacebookUserID() {
		return by_fb_user_id;
	}*/

	public long getByUserID() {
		return by_user_id;
	}

	public long getPostID() {
		return post_id;
	}

	public String getNotificationText() {
		return notification_text;
	}

	public Timestamp getReadAt() {
		return read_at;
	}

	public Timestamp getSentAt() {
		return sent_at;
	}

	public int getNotificationType() {
		return notification_type;
	}

	public HHUser getByUser() {
		return by_user;
	}

	public HHPost getPost() {
		return post;
	}

	public boolean isNewlyRead() {
		return newlyRead;
	}

	public void setNewlyRead(boolean newlyRead) {
		this.newlyRead = newlyRead;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(notification_type);
		dest.writeLong(for_user_id);
		dest.writeLong(by_user_id);
		dest.writeLong(post_id);
		dest.writeString(notification_text);
		if (read_at != null)
			dest.writeLong(read_at.getTime());
		else
			dest.writeLong(-1);
		if (sent_at != null)
			dest.writeLong(sent_at.getTime());
		else
			dest.writeLong(-1);
		dest.writeParcelable(by_user, flags);
		dest.writeParcelable(for_user, flags);
		dest.writeParcelable(post, flags);
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
		notification_type = in.readInt();
		for_user_id = in.readLong();
		by_user_id = in.readLong();
		post_id = in.readLong();
		notification_text = in.readString();
		long read_at_time = in.readLong();
		if (read_at_time == -1)
			read_at = null;
		else
			read_at = new Timestamp(read_at_time);
		long sent_at_time = in.readLong();
		if (sent_at_time == -1)
			sent_at = null;
		else
			sent_at = new Timestamp(sent_at_time);
		by_user = in.readParcelable(HHUser.class.getClassLoader());
		for_user = in.readParcelable(HHUser.class.getClassLoader());
		post = in.readParcelable(HHPost.class.getClassLoader());
	}

}
