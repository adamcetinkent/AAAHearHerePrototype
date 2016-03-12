package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFriendshipUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 10/03/16.
 */
public class HHUserFriendshipsNested extends HHUser {

	private HHFriendshipUserNested[] friendships;

	public HHUserFriendshipsNested(Cursor cursor) {
		super(cursor);
	}

	public List<HHFriendshipUser> getFriendshipsList(){
		List<HHFriendshipUser> friendshipUsers = new ArrayList<>(friendships.length);
		for (HHFriendshipUserNested friendshipUserNested : friendships){
			HHFriendshipUser friendshipUser = new HHFriendshipUser(friendshipUserNested);
			friendshipUsers.add(friendshipUser);
		}
		return friendshipUsers;
	}

}
