package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;
import android.graphics.Bitmap;

import com.facebook.Profile;

import java.sql.Timestamp;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMUser;

/**
 * Created by adam on 18/02/16.
 */
public class HHUser {

	long id;
	String first_name;
	String last_name;
	String email;
	String fb_user_id;
	Timestamp created_at;
	Timestamp updated_at;

	public long getID(){
		return id;
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

	public Timestamp getCreatedAt() {
		return created_at;
	}

	public Timestamp getUpdatedAt() {
		return updated_at;
	}

	public HHUser(Cursor cursor){
		this.id = cursor.getLong(cursor.getColumnIndex(ORMUser.COLUMN_ID_NAME));;
		this.first_name = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_FIRST_NAME_NAME));
		this.last_name = cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_LAST_NAME_NAME));
		this.fb_user_id = cursor.getString(
			cursor.getColumnIndex(ORMUser.COLUMN_FB_USER_ID_NAME));
		this.updated_at = Timestamp.valueOf(
			cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_UPDATED_AT_NAME)));
		this.created_at = Timestamp.valueOf(
			cursor.getString(cursor.getColumnIndex(ORMUser.COLUMN_CREATED_AT_NAME)));
	}

	public HHUser(Profile profile){
		this.first_name = profile.getFirstName();
		this.last_name = profile.getLastName();
		//this.img_url = profile.getProfilePictureUri(200, 200).toString();
		this.fb_user_id = profile.getId();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HHUser user = (HHUser) o;

		return id == user.id;

	}

	static HHUser currentUser;
	public static HHUser getCurrentUser(){
		return currentUser;
	}
	public static void setCurrentUser(HHUser user){
		currentUser = user;
	}

	static Bitmap profilePicture;
	public static void setProfilePicture(Bitmap bitmap){
		profilePicture = bitmap;
	}
	public static Bitmap getProfilePicture(){
		return profilePicture;
	}

}
