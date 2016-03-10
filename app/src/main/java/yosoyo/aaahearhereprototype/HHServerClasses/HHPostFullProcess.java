package yosoyo.aaahearhereprototype.HHServerClasses;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHPostFullProcess extends HHPostFull {

	private boolean postProcessed = false;
	private boolean commentsProcessed = false;
	private boolean likesProcessed = false;
	private boolean usersProcessed = false;
	private boolean trackProcessed = false;

	public HHPostFullProcess(HHPostFullNested nested) {
		super(nested);
	}

	public boolean isPostProcessed() {
		return postProcessed;
	}

	public boolean isCommentsProcessed() {
		return commentsProcessed;
	}

	public boolean isLikesProcessed() {
		return likesProcessed;
	}

	public boolean isUsersProcessed() {
		return usersProcessed;
	}

	public boolean isTrackProcessed() {
		return trackProcessed;
	}

	public void setPostProcessed(boolean postProcessed) {
		this.postProcessed = postProcessed;
	}

	public void setCommentsProcessed(boolean commentsProcessed) {
		this.commentsProcessed = commentsProcessed;
	}

	public void setLikesProcessed(boolean likesProcessed) {
		this.likesProcessed = likesProcessed;
	}

	public void setUsersProcessed(boolean usersProcessed) {
		this.usersProcessed = usersProcessed;
	}

	public void setTrackProcessed(boolean trackProcessed) {
		this.trackProcessed = trackProcessed;
	}

}
