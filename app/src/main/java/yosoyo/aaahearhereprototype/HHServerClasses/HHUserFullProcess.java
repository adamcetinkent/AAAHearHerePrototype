package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFriendshipsNested;

/**
 * Created by adam on 10/03/16.
 */
public class HHUserFullProcess extends HHUserFull {

	private boolean userProcessed = false;
	private boolean friendshipsProcessed = false;

	public HHUserFullProcess(HHUserFriendshipsNested nested){
		super(nested);
	}

	public boolean isUserProcessed() {
		return userProcessed;
	}

	public void setUserProcessed(boolean userProcessed) {
		this.userProcessed = userProcessed;
	}

	public boolean isFriendshipsProcessed() {
		return friendshipsProcessed;
	}

	public void setFriendshipsProcessed(boolean friendshipsProcessed) {
		this.friendshipsProcessed = friendshipsProcessed;
	}
}
