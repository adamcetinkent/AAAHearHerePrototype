package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowUser implements Parcelable {

	private final HHFollow follow;
	private final HHUser user;

	public HHFollowUser(HHFollowUserNested nested){
		this.follow = new HHFollow(nested);
		this.user = nested.getUser();
	}

	public HHFollowUser(HHFollowedUserNested nested){
		this.follow = new HHFollow(nested);
		this.user = nested.getUser();
	}

	public HHFollowUser(Cursor cursor, String userIDColumnIndex){
		this.follow = new HHFollow(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHFollow getFollow() {
		return follow;
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
		follow.writeToParcel(dest,flags);
		user.writeToParcel(dest, flags);
	}

	public static final Parcelable.Creator<HHFollowUser> CREATOR = new Parcelable.Creator<HHFollowUser>(){

		@Override
		public HHFollowUser createFromParcel(Parcel source) {
			return new HHFollowUser(source);
		}

		@Override
		public HHFollowUser[] newArray(int size) {
			return new HHFollowUser[size];
		}

	};

	private HHFollowUser(Parcel in){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			follow = in.readTypedObject(HHFollow.CREATOR);
			user = in.readTypedObject(HHUser.CREATOR);
		} else {
			// TODO
			follow = null;
			user = null;
		}
	}

}
