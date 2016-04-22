package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.annotation.SuppressLint;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;

/**
 * Created by adam on 02/03/16.
 *
 * Used to "process" a newly downloaded post as its constituents are inserted into the databse.
 * Each boolean is set to true as the database insertions are completed.
 * Once the processing is complete, the underlying {@link HHPostFull} is passed on.
 */
@SuppressLint("ParcelCreator")
public class HHPostFullProcess extends HHPostFull {

	private boolean postProcessed = false;
	private boolean commentsProcessed = false;
	private boolean likesProcessed = false;
	private boolean usersProcessed = false;
	private boolean trackProcessed = false;
	private boolean tagsProcessed = false;

	public HHPostFullProcess(HHPostFullNested nested) {
		super(nested);
	}

	public boolean isProcessed(){
		return postProcessed && commentsProcessed && likesProcessed && usersProcessed && trackProcessed && tagsProcessed;
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

	public void setTagsProcessed(boolean tagsProcessed) {
		this.tagsProcessed = tagsProcessed;
	}

}
