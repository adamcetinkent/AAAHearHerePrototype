package yosoyo.aaahearhereprototype.TestServerClasses;

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestPostUserCommentsNested;

/**
 * Created by adam on 02/03/16.
 */
public class TestPostFullProcess extends TestPostFull {

	private boolean postProcessed = false;
	private boolean commentsProcessed = false;
	private boolean usersProcessed = false;
	private boolean trackProcessed = false;

	public TestPostFullProcess(TestPostUserCommentsNested nested) {
		super(nested);
	}

	public boolean isPostProcessed() {
		return postProcessed;
	}

	public boolean isCommentsProcessed() {
		return commentsProcessed;
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

	public void setUsersProcessed(boolean usersProcessed) {
		this.usersProcessed = usersProcessed;
	}

	public void setTrackProcessed(boolean trackProcessed) {
		this.trackProcessed = trackProcessed;
	}



}
