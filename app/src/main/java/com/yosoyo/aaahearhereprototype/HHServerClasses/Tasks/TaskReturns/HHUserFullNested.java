package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFriendshipUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 10/03/16.
 *
 * A {@link HHUser} with nested arrays of {@link HHFriendshipUserNested}s, {@link HHFollowUserNested}s,
 * {@link HHFollowedUserNested}s, {@link HHFollowRequestUserNested}s and {@link HHFollowedRequestUserNested}s.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings({"MismatchedReadAndWriteOfArray", "unused"})
@SuppressLint("ParcelCreator")
public class HHUserFullNested extends HHUser {

	private HHFriendshipUserNested[] friendships;
	private HHFollowUserNested[] follows;
	private HHFollowedUserNested[] followeds;
	private HHFollowRequestUserNested[] follow_requests;
	private HHFollowedRequestUserNested[] followed_requests;

	public HHUserFullNested(Cursor cursor, String userIDColumnIndex) {
		super(cursor, userIDColumnIndex);
	}

	public List<HHFriendshipUser> getFriendshipsList(){
		List<HHFriendshipUser> friendshipUsers = new ArrayList<>(friendships.length);
		for (HHFriendshipUserNested friendshipUserNested : friendships){
			HHFriendshipUser friendshipUser = new HHFriendshipUser(friendshipUserNested);
			friendshipUsers.add(friendshipUser);
		}
		return friendshipUsers;
	}

	public List<HHFollowUser> getFollowsList(){
		List<HHFollowUser> followUsers = new ArrayList<>(follows.length);
		for (HHFollowUserNested followUserNested : follows){
			HHFollowUser followUser = new HHFollowUser(followUserNested);
			followUsers.add(followUser);
		}
		return followUsers;
	}

	public List<HHFollowUser> getFollowedsList(){
		List<HHFollowUser> followedUsers = new ArrayList<>(followeds.length);
		for (HHFollowedUserNested followedUserNested : followeds){
			HHFollowUser followedUser = new HHFollowUser(followedUserNested);
			followedUsers.add(followedUser);
		}
		return followedUsers;
	}

	public List<HHFollowRequestUser> getFollowRequestsList(){
		List<HHFollowRequestUser> followUsers = new ArrayList<>(follow_requests.length);
		for (HHFollowRequestUserNested followUserNested : follow_requests){
			HHFollowRequestUser followUser = new HHFollowRequestUser(followUserNested);
			followUsers.add(followUser);
		}
		return followUsers;
	}

	public List<HHFollowRequestUser> getFollowedRequestsList(){
		List<HHFollowRequestUser> followedUsers = new ArrayList<>(followed_requests.length);
		for (HHFollowedRequestUserNested followedUserNested : followed_requests){
			HHFollowRequestUser followedUser = new HHFollowRequestUser(followedUserNested);
			followedUsers.add(followedUser);
		}
		return followedUsers;
	}

}
