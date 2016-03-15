package yosoyo.aaahearhereprototype.HHServerClasses;

import com.facebook.Profile;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFriendshipsNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHUserFull {

	private final HHUser user;
	private final List<HHFriendshipUser> friendships;

	public HHUserFull(HHUserFullProcess process){
		this.user = process.getUser();
		this.friendships = process.getFriendships();
	}

	HHUserFull(HHUserFriendshipsNested nested){
		this.user = new HHUser(nested);
		this.friendships = nested.getFriendshipsList();
	}

	public HHUserFull(Profile profile){
		user = new HHUser(profile);
		friendships = new ArrayList<>();
	}

	public HHUser getUser() {
		return user;
	}

	public List<HHFriendshipUser> getFriendships() {
		return friendships;
	}
}
