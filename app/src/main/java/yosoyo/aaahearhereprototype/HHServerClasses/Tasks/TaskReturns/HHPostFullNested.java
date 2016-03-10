package yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHCommentUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLikeUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPost;
import yosoyo.aaahearhereprototype.HHServerClasses.HHTagUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 02/03/16.
 */
public class HHPostFullNested extends HHPost {

	HHUser user;
	HHCommentUserNested[] comments;
	HHLikeUserNested[] likes;
	HHTagUserNested[] tags;

	public HHUser getUser(){
		return user;
	}

	public List<HHCommentUser> getCommentsList(){
		List<HHCommentUser> commentUsers = new ArrayList<>(comments.length);
		for (HHCommentUserNested commentNested : comments){
			HHCommentUser commentUser = new HHCommentUser(commentNested);
			commentUsers.add(commentUser);
		}
		return commentUsers;
	}

	public List<HHLikeUser> getLikesList(){
		List<HHLikeUser> likeUsers = new ArrayList<>(likes.length);
		for (HHLikeUserNested likeUserNested : likes){
			HHLikeUser likeUser = new HHLikeUser(likeUserNested);
			likeUsers.add(likeUser);
		}
		return likeUsers;
	}

	public List<HHTagUser> getTagsList(){
		List<HHTagUser> tagUsers = new ArrayList<>(tags.length);
		for (HHTagUserNested tagUserNested : tags){
			HHTagUser tagUser = new HHTagUser(tagUserNested);
			tagUsers.add(tagUser);
		}
		return tagUsers;
	}

}
