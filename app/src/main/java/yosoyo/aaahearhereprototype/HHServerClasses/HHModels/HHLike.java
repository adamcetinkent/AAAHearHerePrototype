package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMComment;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHLikeUserNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHLike extends HHBase implements Parcelable {

	private final long post_id;
	private final long user_id;

	public HHLike(long post_id, long user_id){
		super();
		this.post_id = post_id;
		this.user_id = user_id;
	}

	protected HHLike(long id, long post_id, long user_id, Timestamp created_at, Timestamp updated_at){
		super(
			id,
			created_at,
			updated_at
			 );
		this.post_id = post_id;
		this.user_id = user_id;
	}

	public HHLike(HHLikeUserNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
			 );
		this.post_id = nested.getPostID();
		this.user_id = nested.getUserID();
	}

	public HHLike(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMComment.COLUMN_UPDATED_AT_NAME)))
			 );
		this.post_id = cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_POST_ID_NAME));
		this.user_id = cursor.getLong(cursor.getColumnIndex(ORMComment.COLUMN_USER_ID_NAME));
	}

	public long getPostID() {
		return post_id;
	}

	public long getUserID() {
		return user_id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(post_id);
		dest.writeLong(user_id);
	}

	public static final Parcelable.Creator<HHLike> CREATOR = new Parcelable.Creator<HHLike>(){

		@Override
		public HHLike createFromParcel(Parcel source) {
			return new HHLike(source);
		}

		@Override
		public HHLike[] newArray(int size) {
			return new HHLike[size];
		}

	};

	protected HHLike(Parcel in){
		super(in);
		post_id = in.readLong();
		user_id = in.readLong();
	}

}
