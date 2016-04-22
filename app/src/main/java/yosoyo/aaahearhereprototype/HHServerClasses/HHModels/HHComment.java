package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMComment;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHCommentUserNested;

/**
 * Created by adam on 02/03/16.
 *
 * A Comment belongs to a {@link HHPost} and a {@link HHUser}, and contains a message.
 */
public class HHComment extends HHLike implements Parcelable {

	private final String message;

	public HHComment(long post_id, long user_id, String message){
		super(post_id, user_id);
		this.message = message;
	}

	public HHComment(HHCommentUserNested nested){
		super(
			nested.getID(),
			nested.getPostID(),
			nested.getUserID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.message = nested.getMessage();
	}

	public HHComment(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMComment.ID())),
			cursor.getLong(cursor.getColumnIndex(ORMComment.POST_ID())),
			cursor.getLong(cursor.getColumnIndex(ORMComment.USER_ID())),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.UPDATED_AT())))
		);
		this.message = cursor.getString(cursor.getColumnIndex(ORMComment.MESSAGE()));
	}

	public String getMessage() {
		return message;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(message);
	}

	public static final Parcelable.Creator<HHComment> CREATOR = new Parcelable.Creator<HHComment>(){

		@Override
		public HHComment createFromParcel(Parcel source) {
			return new HHComment(source);
		}

		@Override
		public HHComment[] newArray(int size) {
			return new HHComment[size];
		}

	};

	private HHComment(Parcel in){
		super(in);
		message = in.readString();
	}

}
