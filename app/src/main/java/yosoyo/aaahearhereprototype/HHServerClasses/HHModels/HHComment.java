package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMComment;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHCommentUserNested;

/**
 * Created by adam on 02/03/16.
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
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_ID_NAME)),
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_POST_ID_NAME)),
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_USER_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_UPDATED_AT_NAME)))
		);
		this.message = cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_MESSAGE_NAME));
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
