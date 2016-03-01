package yosoyo.aaahearhereprototype.TestServerClasses;

import android.database.Cursor;

import com.facebook.Profile;

/**
 * Created by adam on 18/02/16.
 */
public class TestUser {

	long id;
	String first_name;
	String last_name;
	String email;
	String img_url;
	String fb_user_id;
	String created_at;
	String updated_at;

	public long getID(){
		return id;
	}

	public String getFirstName() {
		return first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public String getImgUrl(){
		return img_url;
	}

	public String  getFBUserID(){
		return fb_user_id;
	}

	public TestUser(Cursor cursor){
		this.id = cursor.getLong(cursor.getColumnIndex(ORMTestPostUser.COLUMN_USER_ID_NAME));;
		this.first_name = cursor.getString(
			cursor.getColumnIndex(ORMTestPostUser.COLUMN_USER_FIRST_NAME_NAME));
		this.last_name = cursor.getString(cursor.getColumnIndex(ORMTestPostUser.COLUMN_USER_LAST_NAME_NAME));
		this.img_url = cursor.getString(cursor.getColumnIndex(ORMTestPostUser.COLUMN_USER_IMG_NAME));
		this.fb_user_id = cursor.getString(cursor.getColumnIndex(ORMTestPostUser.COLUMN_FB_USER_ID_NAME));
	}

	public TestUser(Profile profile){
		this.first_name = profile.getFirstName();
		this.last_name = profile.getLastName();
		this.img_url = profile.getProfilePictureUri(200, 200).toString();
		this.fb_user_id = profile.getId();
	}

}
