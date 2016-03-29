package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

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
	private String bio;
	private int auto_accept;
	private int privacy;

	public HHUser(HHUserFullNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
			 );
		this.first_name = nested.getFirstName();
		this.last_name = nested.getLastName();
		this.fb_user_id = nested.getFBUserID();
		this.email = nested.getEmail();
		this.bio = nested.getBio();
		this.auto_accept = nested.getAutoAccept();
		this.privacy = nested.getPrivacy();
	}

	public HHUser(Cursor cursor){
		super(
			cursor.getLong(cursor.getColumnIndex(ORMUser.COLUMN_ID_NAME)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_CREATED_AT_NAME))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_UPDATED_AT_NAME)))
			 );
		this.first_name = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_FIRST_NAME_NAME));
		this.last_name = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_LAST_NAME_NAME));
		this.email = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_EMAIL_NAME));
		this.fb_user_id = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_FB_USER_ID_NAME));
		this.bio = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_BIO_NAME));
		this.auto_accept = cursor.getInt(cursor.getColumnIndex(ORMUser.COLUMN_AUTO_ACCEPT_NAME));
		this.privacy = cursor.getInt(cursor.getColumnIndex(ORMUser.COLUMN_PRIVACY_NAME));
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

	public String getBio() {
		return bio;
	}

	public int getAutoAccept() {
		return auto_accept;
	}

	public int getPrivacy() {
		return privacy;
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
	private static long currentUserUpdated;
	public static HHUserFull getCurrentUser(){
		return currentUser;
	}
	public static long getCurrentUserID(){
		return currentUser.getUser().getID();
	}
	public static void setCurrentUser(HHUserFull user){
		currentUser = user;
		currentUserUpdated = System.currentTimeMillis();
	}
	public static long getCurrentUserUpdated(){
		return currentUserUpdated;
	}

	private static Bitmap profilePicture;
	public static void setProfilePicture(Bitmap bitmap){
		profilePicture = bitmap;
	}
	public static Bitmap getProfilePicture(){
		return profilePicture;
	}

	public static boolean friendIsFollowed(HHUserFull user, HHUser friend){
		for (HHFollowUser follow : user.getFollowOuts()){
			if (follow.getUser().equals(friend)){
				return true;
			}
		}
		return false;
	}

	public static boolean friendFollowsMe(HHUserFull user, HHUser friend){
		for (HHFollowUser follow : user.getFollowIns()){
			if (follow.getUser().equals(friend)){
				return true;
			}
		}
		return false;
	}

	public static boolean friendIsRequested(HHUserFull user, HHUser friend){
		for (HHFollowRequestUser follow : user.getFollowOutRequests()){
			if (follow.getUser().equals(friend)){
				return true;
			}
		}
		return false;
	}

	public static boolean friendRequestedMe(HHUserFull user, HHUser friend){
		for (HHFollowRequestUser follow : user.getFollowInRequests()){
			if (follow.getUser().equals(friend)){
				return true;
			}
		}
		return false;
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
