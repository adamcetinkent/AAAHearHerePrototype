package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowRequestUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedRequestUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowRequestUser implements Parcelable {

	private final HHFollowRequest followRequest;
	private final HHUser user;

	public HHFollowRequestUser(HHFollowRequestUserNested nested){
		this.followRequest = new HHFollowRequest(nested);
		this.user = nested.getUser();
	}

	public HHFollowRequestUser(HHFollowedRequestUserNested nested){
		this.followRequest = new HHFollowRequest(nested);
		this.user = nested.getUser();
	}

	public HHFollowRequestUser(Cursor cursor, String userIDColumnIndex){
		this.followRequest = new HHFollowRequest(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHFollowRequest getFollowRequest() {
		return followRequest;
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
		followRequest.writeToParcel(dest,flags);
		user.writeToParcel(dest, flags);
	}

	public static final Parcelable.Creator<HHFollowRequestUser> CREATOR = new Parcelable.Creator<HHFollowRequestUser>(){

		@Override
		public HHFollowRequestUser createFromParcel(Parcel source) {
			return new HHFollowRequestUser(source);
		}

		@Override
		public HHFollowRequestUser[] newArray(int size) {
			return new HHFollowRequestUser[size];
		}

	};

	private HHFollowRequestUser(Parcel in){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			followRequest = in.readTypedObject(HHFollowRequest.CREATOR);
			user = in.readTypedObject(HHUser.CREATOR);
		} else {
			// TODO
			followRequest = null;
			user = null;
		}
	}

}
