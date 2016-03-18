package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.facebook.Profile;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFullNested;

/**
 * Created by adam on 18/02/16.
 */
public class HHUser extends HHBase {

	private final String first_name;
	private final String last_name;
	private final String fb_user_id;
	private String email;

	public HHUser(HHUserFullNested nested){
		super(
			nested.getID(),
			nested.getUpdatedAt(),
			nested.getCreatedAt()
			 );
		this.first_name = nested.getFirstName();
		this.last_name = nested.getLastName();
		this.fb_user_id = nested.getFBUserID();
		this.email = nested.getEmail();
	}

	public HHUser(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMUser.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_UPDATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_CREATED_AT_NAME)))
			 );
		this.first_name = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_FIRST_NAME_NAME));
		this.last_name = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_LAST_NAME_NAME));
		this.email = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_EMAIL_NAME));
		this.fb_user_id = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_FB_USER_ID_NAME));
	}

	public HHUser(Profile profile){
		this.first_name = profile.getFirstName();
		this.last_name = profile.getLastName();
		this.fb_user_id = profile.getId();
	}

	public String getFirstName() {
		return first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public String getName(){
		return first_name + " " + last_name;
	}

	public String getEmail() {
		return email;
	}

	public String  getFBUserID(){
		return fb_user_id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HHUser user = (HHUser) o;

		return id == user.id;

	}

	public CharSequence toCharSequence(){
		String name = getName();
		SpannableString spannable = new SpannableString(name);
		int length = spannable.length();
		if (length > 0){
			spannable.setSpan(
				new HHUserSpan(this, null),
				0,
				length,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
			);
		}
		return spannable;
	}

	private static HHUserFull currentUser;
	public static HHUserFull getCurrentUser(){
		return currentUser;
	}
	public static long getCurrentUserID(){
		return currentUser.getUser().getID();
	}
	public static void setCurrentUser(HHUserFull user){
		currentUser = user;
	}

	private static Bitmap profilePicture;
	public static void setProfilePicture(Bitmap bitmap){
		profilePicture = bitmap;
	}
	public static Bitmap getProfilePicture(){
		return profilePicture;
	}

	public static class HHUserSpan extends ClickableSpan {
		private final HHUser user;
		private final HHUserSpanClickCallback clickCallback;

		public interface HHUserSpanClickCallback{
			void onClickSpan(HHUser hhUser);
		}

		public HHUserSpan(HHUser user, HHUserSpanClickCallback clickCallback){
			super();
			this.clickCallback = clickCallback;
			this.user = user;
		}

		public HHUser getUser(){
			return user;
		}

		@Override
		public void updateDrawState(TextPaint ds){
			ds.setColor(Color.BLACK);
			ds.setFakeBoldText(true);
			ds.bgColor = 0xFFDDDDDD;
		}

		@Override
		public void onClick(View view){
			clickCallback.onClickSpan(user);
		}

		@Override
		public String toString(){
			return user.getName();
		}

	}

}
