package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHCommentUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHCommentUser implements Parcelable {

	private final HHComment comment;
	private final HHUser user;

	public HHCommentUser(HHCommentUserNested nested){
		this.comment = new HHComment(nested);
		this.user = nested.getUser();
	}

	public HHCommentUser(Cursor cursor, String userIDColumnIndex){
		this.comment = new HHComment(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHComment getComment() {
		return comment;
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
		comment.writeToParcel(dest,flags);
		user.writeToParcel(dest, flags);
	}

	public static final Parcelable.Creator<HHCommentUser> CREATOR = new Parcelable.Creator<HHCommentUser>(){

		@Override
		public HHCommentUser createFromParcel(Parcel source) {
			return new HHCommentUser(source);
		}

		@Override
		public HHCommentUser[] newArray(int size) {
			return new HHCommentUser[size];
		}

	};

	private HHCommentUser(Parcel in){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			comment = in.readTypedObject(HHComment.CREATOR);
			user = in.readTypedObject(HHUser.CREATOR);
		} else {
			// TODO
			comment = null;
			user = null;
		}
	}

}
