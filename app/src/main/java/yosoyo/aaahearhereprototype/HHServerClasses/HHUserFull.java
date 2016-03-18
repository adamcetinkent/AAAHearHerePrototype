package yosoyo.aaahearhereprototype.HHServerClasses;

import com.facebook.Profile;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFullNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHUserFull {

	private final HHUser user;
	private final List<HHFriendshipUser> friendships;
	private final List<HHFollowUser> followOuts;
	private final List<HHFollowUser> followIns;
	private final List<HHFollowRequestUser> followOutRequests;
	private final List<HHFollowRequestUser> followInRequests;

	public HHUserFull(HHUserFullProcess process){
		this.user = process.getUser();
		this.friendships = process.getFriendships();
		this.followOuts = process.getFollowOuts();
		this.followIns = process.getFollowIns();
		this.followOutRequests = process.getFollowOutRequests();
		this.followInRequests = process.getFollowInRequests();
	}

	HHUserFull(HHUserFullNested nested){
		this.user = new HHUser(nested);
		this.friendships = nested.getFriendshipsList();
		this.followOuts = nested.getFollowsList();
		this.followIns = nested.getFollowedsList();
		this.followOutRequests = nested.getFollowRequestsList();
		this.followInRequests = nested.getFollowedRequestsList();
	}

	public HHUserFull(Profile profile){
		user = new HHUser(profile);
		friendships = new ArrayList<>();
		followOuts = new ArrayList<>();
		followIns = new ArrayList<>();
		followOutRequests = new ArrayList<>();
		followInRequests = new ArrayList<>();
	}

	public HHUser getUser() {
		return user;
	}

	public List<HHFriendshipUser> getFriendships() {
		return friendships;
	}

	public List<HHFollowUser> getFollowOuts() {
		return followOuts;
	}

	public List<HHFollowUser> getFollowIns() {
		return followIns;
	}

	public List<HHFollowRequestUser> getFollowOutRequests() {
		return followOutRequests;
	}

	public List<HHFollowRequestUser> getFollowInRequests() {
		return followInRequests;
	}

}
