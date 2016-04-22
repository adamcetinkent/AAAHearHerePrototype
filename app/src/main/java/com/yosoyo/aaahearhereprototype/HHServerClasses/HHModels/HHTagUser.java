package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHTagUserNested;

/**
 * Created by adam on 10/03/16.
 *
 * Contains a {@link HHTag} and its associated {@link HHUser}.
 */
public class HHTagUser implements Parcelable {

	private final HHTag tag;
	private final HHUser user;

	public HHTagUser(HHTagUserNested nested){
		this.tag = new HHTag(nested);
		this.user = nested.getUser();
	}

	public HHTagUser(Cursor cursor, String userIDColumnIndex){
		this.tag = new HHTag(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHTag getTag() {
		return tag;
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
		tag.writeToParcel(dest,flags);
		user.writeToParcel(dest, flags);
	}

	public static final Parcelable.Creator<HHTagUser> CREATOR = new Parcelable.Creator<HHTagUser>(){

		@Override
		public HHTagUser createFromParcel(Parcel source) {
			return new HHTagUser(source);
		}

		@Override
		public HHTagUser[] newArray(int size) {
			return new HHTagUser[size];
		}

	};

	private HHTagUser(Parcel in){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			tag = in.readTypedObject(HHTag.CREATOR);
			user = in.readTypedObject(HHUser.CREATOR);
		} else {
			// TODO
			tag = null;
			user = null;
		}
	}

}
