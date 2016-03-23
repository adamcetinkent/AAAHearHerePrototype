package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFullNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHUserFullProcess extends HHUserFull {

	private boolean userProcessed = false;
	private boolean friendshipsProcessed = false;
	private boolean followsProcessed = false;
	private boolean followRequestsProcessed = false;

	public HHUserFullProcess(HHUserFullNested nested){
		super(nested);
	}

	public boolean isProcessed(){
		return userProcessed && friendshipsProcessed && followsProcessed && followRequestsProcessed;
	}

	public void setUserProcessed(boolean userProcessed) {
		this.userProcessed = userProcessed;
	}

	public void setFriendshipsProcessed(boolean friendshipsProcessed) {
		this.friendshipsProcessed = friendshipsProcessed;
	}

	public void setFollowsProcessed(boolean followsProcessed) {
		this.followsProcessed = followsProcessed;
	}

	public void setFollowRequestsProcessed(boolean followRequestsProcessed) {
		this.followRequestsProcessed = followRequestsProcessed;
	}
}
