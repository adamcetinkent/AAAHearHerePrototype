package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHLikeUserNested;

/**
 * Created by adam on 02/03/16.
 *
 * Contains a {@link HHLike} and its associated {@link HHUser}.
 */
public class HHLikeUser implements Parcelable {

	private final HHLike like;
	private final HHUser user;

	public HHLikeUser(HHLikeUserNested nested){
		this.like = new HHLike(nested);
		this.user = nested.getUser();
	}

	public HHLikeUser(Cursor cursor, String userIDColumnIndex){
		this.like = new HHLike(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHLike getLike() {
		return like;
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
		like.writeToParcel(dest,flags);
		user.writeToParcel(dest, flags);
	}

	public static final Parcelable.Creator<HHLikeUser> CREATOR = new Parcelable.Creator<HHLikeUser>(){

		@Override
		public HHLikeUser createFromParcel(Parcel source) {
			return new HHLikeUser(source);
		}

		@Override
		public HHLikeUser[] newArray(int size) {
			return new HHLikeUser[size];
		}

	};

	private HHLikeUser(Parcel in){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			like = in.readTypedObject(HHLike.CREATOR);
			user = in.readTypedObject(HHUser.CREATOR);
		} else {
			// TODO
			like = null;
			user = null;
		}
	}

}
