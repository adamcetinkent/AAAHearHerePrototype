package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowRequestUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowedRequestUserNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHFollowRequestUser {

	private final HHFollowRequest followRequest;
	private final HHUser user;

	public HHFollowRequestUser(HHFollowRequestUserNested nested){
		this.followRequest = new HHFollowRequest(nested);
		this.user = nested.getUser();
	}

	public HHFollowRequestUser(HHFollowedRequestUserNested nested){
		this.followRequest = new HHFollowRequest(nested);
		this.user = nested.getUser();
	}

	public HHFollowRequestUser(Cursor cursor, String userIDColumnIndex){
		this.followRequest = new HHFollowRequest(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
	}

	public HHFollowRequest getFollowRequest() {
		return followRequest;
	}

	public HHUser getUser() {
		return user;
	}
}
