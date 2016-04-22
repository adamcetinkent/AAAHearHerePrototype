package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMTag;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHTagUserNested;

import java.sql.Timestamp;

/**
 * Created by adam on 10/03/16.
 *
 * A Tag belongs to a {@link HHPost} and points to a {@link HHUser}.
 */
public class HHTag extends HHLike implements Parcelable {

	public HHTag(long post_id, long user_id) {
		super(post_id, user_id);
	}

	public HHTag(HHTagUserNested nested){
		super(
			nested.getID(),
			nested.getPostID(),
			nested.getUserID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
	}

	public HHTag(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMTag.ID())),
			cursor.getLong(cursor.getColumnIndex(ORMTag.POST_ID())),
			cursor.getLong(cursor.getColumnIndex(ORMTag.USER_ID())),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMTag.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMTag.UPDATED_AT())))
		);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/*@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}*/

	public static final Parcelable.Creator<HHTag> CREATOR = new Parcelable.Creator<HHTag>(){

		@Override
		public HHTag createFromParcel(Parcel source) {
			return new HHTag(source);
		}

		@Override
		public HHTag[] newArray(int size) {
			return new HHTag[size];
		}

	};

	private HHTag(Parcel in){
		super(in);
	}

}
