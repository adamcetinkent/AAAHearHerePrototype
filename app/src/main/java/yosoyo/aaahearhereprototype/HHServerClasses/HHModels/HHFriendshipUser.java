package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFriendshipUserNested;

/**
 * Created by adam on 10/03/16.
 *
 * Contains a {@link HHFriendship} and its associated {@link HHUser}.
 * When "inwards", the user is the friend; when "outwards", the user is the user.
 */
public class HHFriendshipUser implements Parcelable {

	private final HHFriendship friendship;
	private final HHUser user;

	public HHFriendshipUser(HHFriendshipUserNested nested){
		this.friendship = new HHFriendship(nested);
		this.user = nested.getUser();
	}

	public HHFriendshipUser(Cursor cursor, String userIDColumnIndex){
		this.friendship = new HHFriendship(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHFriendship getFriendship() {
		return friendship;
	}

	public HHUser getUser() {
		return user;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		friendship.writeToParcel(dest,flags);
		user.writeToParcel(dest, flags);
	}

	public static final Parcelable.Creator<HHFriendshipUser> CREATOR = new Parcelable.Creator<HHFriendshipUser>(){

		@Override
		public HHFriendshipUser createFromParcel(Parcel source) {
			return new HHFriendshipUser(source);
		}

		@Override
		public HHFriendshipUser[] newArray(int size) {
			return new HHFriendshipUser[size];
		}

	};

	private HHFriendshipUser(Parcel in){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			friendship = in.readTypedObject(HHFriendship.CREATOR);
			user = in.readTypedObject(HHUser.CREATOR);
		} else {
			// TODO
			friendship = null;
			user = null;
		}
	}

}
