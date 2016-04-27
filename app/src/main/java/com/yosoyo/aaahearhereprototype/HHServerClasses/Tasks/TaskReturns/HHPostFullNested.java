package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import android.annotation.SuppressLint;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCommentUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLikeUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPost;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHTagUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 02/03/16.
 *
 * A {@link HHPost} with nested arrays of {@link HHCommentUserNested}s, {@link HHLikeUserNested}s
 * and {@link HHTagUserNested}s.
 *
 * Only used when parsed from JSON.
 */
@SuppressWarnings({"MismatchedReadAndWriteOfArray", "unused"})
@SuppressLint("ParcelCreator")
public class HHPostFullNested extends HHPost {

	private HHUser user;
	private HHCommentUserNested[] comments;
	private HHLikeUserNested[] likes;
	private HHTagUserNested[] tags;

	public HHUser getUser(){
		return user;
	}

	public List<HHCommentUser> getCommentsList(){
		if (comments == null){
			return new ArrayList<>();
		}
		List<HHCommentUser> commentUsers = new ArrayList<>(comments.length);
		for (HHCommentUserNested commentNested : comments){
			HHCommentUser commentUser = new HHCommentUser(commentNested);
			commentUsers.add(commentUser);
		}
		return commentUsers;
	}

	public List<HHLikeUser> getLikesList(){
		if (likes == null){
			return new ArrayList<>();
		}
		List<HHLikeUser> likeUsers = new ArrayList<>(likes.length);
		for (HHLikeUserNested likeUserNested : likes){
			HHLikeUser likeUser = new HHLikeUser(likeUserNested);
			likeUsers.add(likeUser);
		}
		return likeUsers;
	}

	public List<HHTagUser> getTagsList(){
		if (tags == null){
			return new ArrayList<>();
		}
		List<HHTagUser> tagUsers = new ArrayList<>(tags.length);
		for (HHTagUserNested tagUserNested : tags){
			HHTagUser tagUser = new HHTagUser(tagUserNested);
			tagUsers.add(tagUser);
		}
		return tagUsers;
	}

}
