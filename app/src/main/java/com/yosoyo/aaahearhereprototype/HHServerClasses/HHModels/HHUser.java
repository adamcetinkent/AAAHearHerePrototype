package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.facebook.Profile;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFullNested;
import com.yosoyo.aaahearhereprototype.R;

import java.sql.Timestamp;

/**
 * Created by adam on 18/02/16.
 *
 * A User is one of the two most fundamental classes of Hear Here, along with {@link HHPost}.
 * It describes a user of the app, along with their privacy settings.
 */
public class HHUser extends HHBase implements Parcelable {

	private final String first_name;
	private final String last_name;
	private final String fb_user_id;
	private String email;
	private String bio;
	private String url;
	private int auto_accept;
	private int profile_privacy;
	private int search_privacy;

	public HHUser(HHUserFullNested nested){
		super(
			nested.getID(),
			nested.getCreatedAt(),
			nested.getUpdatedAt()
		);
		this.first_name = 		nested.getFirstName();
		this.last_name = 		nested.getLastName();
		this.fb_user_id = 		nested.getFBUserID();
		this.email = 			nested.getEmail();
		this.bio = 				nested.getBio();
		this.url = 				nested.getURL();
		this.auto_accept = 		nested.getAutoAccept();
		this.profile_privacy =	nested.getProfilePrivacy();
		this.search_privacy = 	nested.getSearchPrivacy();
	}

	public HHUser(Cursor cursor, String idColumnIndex){
		super(
			cursor.getLong(cursor.getColumnIndex(idColumnIndex)),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMUser.CREATED_AT()))),
			Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ORMUser.UPDATED_AT())))
		);
		this.first_name = 		cursor.getString(cursor.getColumnIndex(ORMUser.FIRST_NAME()));
		this.last_name = 		cursor.getString(cursor.getColumnIndex(ORMUser.LAST_NAME()));
		this.email = 			cursor.getString(cursor.getColumnIndex(ORMUser.EMAIL()));
		this.fb_user_id = 		cursor.getString(cursor.getColumnIndex(ORMUser.FB_USER_ID()));
		this.bio = 				cursor.getString(cursor.getColumnIndex(ORMUser.BIO()));
		this.url = 				cursor.getString(cursor.getColumnIndex(ORMUser.URL()));
		this.auto_accept = 		cursor.getInt(cursor.getColumnIndex(ORMUser.AUTO_ACCEPT()));
		this.profile_privacy =	cursor.getInt(cursor.getColumnIndex(ORMUser.PROFILE_PRIVACY()));
		this.search_privacy = 	cursor.getInt(cursor.getColumnIndex(ORMUser.SEARCH_PRIVACY()));
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

	public String getURL() {
		return url;
	}

	public int getAutoAccept() {
		return auto_accept;
	}

	public int getProfilePrivacy() {
		return profile_privacy;
	}

	public int getSearchPrivacy() {
		return search_privacy;
	}

	public void setProfilePrivacy(int profilePrivacy) {
		this.profile_privacy = profilePrivacy;
	}

	public void setSearchPrivacy(int searchPrivacy) {
		this.search_privacy = searchPrivacy;
	}

	public void setAutoAccept(int autoAccept) {
		this.auto_accept = autoAccept;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HHUser user = (HHUser) o;

		return id == user.id;

	}

	public CharSequence toCharSequence(Context context){
		String name = getName();
		SpannableString spannable = new SpannableString(String.format(" %s ", name));
		int length = spannable.length();
		if (length > 2){
			spannable.setSpan(
				new HHUserSpan(context, this, null),
				0,
				length,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
			);
		}
		return spannable;
	}

	private static HHUserFull currentUser;
	private static long currentUserUpdated;
	private static String authorisationToken;
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
	public static void setAuthorisationToken(String authorisationToken){
		HHUser.authorisationToken = authorisationToken;
	}
	public static String getAuthorisationToken(){
		return HHUser.authorisationToken;
	}

	private static Bitmap profilePicture;
	public static void setProfilePicture(Bitmap bitmap){
		profilePicture = bitmap;
	}
	public static Bitmap getProfilePicture(){
		return profilePicture;
	}

	public static void setCurrentUserProfilePrivacy(int profilePrivacy){
		currentUser.getUser().setProfilePrivacy(profilePrivacy);
	}

	public static void setCurrentUserSearchPrivacy(int searchPrivacy){
		currentUser.getUser().setSearchPrivacy(searchPrivacy);
	}

	public static void setCurrentUserAutoAccept(int autoAccept){
		currentUser.getUser().setAutoAccept(autoAccept);
	}

	public static boolean userIsFriend(long testID){
		return userIsFriend(getCurrentUser(), testID);
	}

	public static boolean userIsFollowed(long testID){
		return userIsFollowed(getCurrentUser(), testID);
	}

	public static boolean userFollowsMe(long testID){
		return userFollowsMe(getCurrentUser(), testID);
	}

	public static boolean userIsRequested(long testID){
		return userIsRequested(getCurrentUser(), testID);
	}

	public static boolean userRequestedMe(long testID){
		return userRequestedMe(getCurrentUser(), testID);
	}

	public static boolean userIsFriend(HHUser testUser){
		return userIsFriend(getCurrentUser(), testUser.getID());
	}

	public static boolean userIsFollowed(HHUser testUser){
		return userIsFollowed(getCurrentUser(), testUser.getID());
	}

	public static boolean userFollowsMe(HHUser testUser){
		return userFollowsMe(getCurrentUser(), testUser.getID());
	}

	public static boolean userIsRequested(HHUser testUser){
		return userIsRequested(getCurrentUser(), testUser.getID());
	}

	public static boolean userRequestedMe(HHUser testUser){
		return userRequestedMe(getCurrentUser(), testUser.getID());
	}

	public static boolean userIsFriend(HHUserFull user, HHUser testUser){
		return userIsFriend(user, testUser.getID());
	}

	public static boolean userIsFollowed(HHUserFull user, HHUser testUser){
		return userIsFollowed(user, testUser.getID());
	}

	public static boolean userFollowsMe(HHUserFull user, HHUser testUser){
		return userFollowsMe(user, testUser.getID());
	}

	public static boolean userIsRequested(HHUserFull user, HHUser testUser){
		return userIsRequested(user, testUser.getID());
	}

	public static boolean userRequestedMe(HHUserFull user, HHUser testUser){
		return userRequestedMe(user, testUser.getID());
	}

	public static boolean userIsFriend(HHUserFull user, long testID){
		for (HHFriendshipUser follow : user.getFriendships()){
			if (follow.getUser().getID() == testID){
				return true;
			}
		}
		return false;
	}

	public static boolean userIsFollowed(HHUserFull user, long testID){
		for (HHFollowUser follow : user.getFollowOuts()){
			if (follow.getUser().getID() == testID){
				return true;
			}
		}
		return false;
	}

	public static boolean userFollowsMe(HHUserFull user, long testID){
		for (HHFollowUser follow : user.getFollowIns()){
			if (follow.getUser().getID() == testID){
				return true;
			}
		}
		return false;
	}

	public static boolean userIsRequested(HHUserFull user, long testID){
		for (HHFollowRequestUser follow : user.getFollowOutRequests()){
			if (follow.getUser().getID() == testID){
				return true;
			}
		}
		return false;
	}

	public static boolean userRequestedMe(HHUserFull user, long testID){
		for (HHFollowRequestUser follow : user.getFollowInRequests()){
			if (follow.getUser().getID() == testID){
				return true;
			}
		}
		return false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(	first_name);
		dest.writeString(	last_name);
		dest.writeString(	fb_user_id);
		dest.writeString(	email);
		dest.writeString(	bio);
		dest.writeString(	url);
		dest.writeInt(		auto_accept);
		dest.writeInt(		profile_privacy);
		dest.writeInt(		search_privacy);
	}

	public static final Parcelable.Creator<HHUser> CREATOR = new Parcelable.Creator<HHUser>(){

		@Override
		public HHUser createFromParcel(Parcel source) {
			return new HHUser(source);
		}

		@Override
		public HHUser[] newArray(int size) {
			return new HHUser[size];
		}

	};

	private HHUser(Parcel in){
		super(in);
		first_name = 		in.readString();
		last_name = 		in.readString();
		fb_user_id = 		in.readString();
		email = 			in.readString();
		bio = 				in.readString();
		url = 				in.readString();
		auto_accept = 		in.readInt();
		profile_privacy = 	in.readInt();
		search_privacy = 	in.readInt();
	}

	public static class HHUserSpan extends ClickableSpan {
		private final HHUser user;
		private final Context context;
		private final HHUserSpanClickCallback clickCallback;

		public interface HHUserSpanClickCallback{
			void onClickSpan(HHUser hhUser);
		}

		public HHUserSpan(Context context, HHUser user, HHUserSpanClickCallback clickCallback){
			super();
			this.context = context;
			this.clickCallback = clickCallback;
			this.user = user;
		}

		public HHUser getUser(){
			return user;
		}

		@Override
		public void updateDrawState(TextPaint ds){
			ds.setColor(ContextCompat.getColor(context, R.color.adam_theme_black));
			ds.setFakeBoldText(true);
			ds.bgColor = ContextCompat.getColor(context, R.color.adam_theme_lightest);
		}

		@Override
		public void onClick(View view){
			clickCallback.onClickSpan(user);
		}

		@Override
		public String toString(){
			return String.format(" %s ", user.getName());
		}

	}

}
