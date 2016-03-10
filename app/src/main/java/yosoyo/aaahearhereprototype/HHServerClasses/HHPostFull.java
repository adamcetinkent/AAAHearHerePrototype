package yosoyo.aaahearhereprototype.HHServerClasses;

import android.database.Cursor;

import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;

/**
 * Created by adam on 02/03/16.
 */
public class HHPostFull implements Comparable {

	HHPost post;
	HHUser user;
	HHCachedSpotifyTrack track;
	List<HHCommentUser> comments;
	List<HHLikeUser> likes;
	List<HHTagUser> tags;

	public HHPostFull(HHPostFullProcess that){
		this.post = that.post;
		this.user = that.user;
		this.comments = that.comments;
		this.track = that.track;
		this.likes = that.likes;
		this.tags = that.tags;
	}

	public HHPostFull(HHPostFullNested nested){
		this.post = new HHPost(nested);
		this.user = nested.getUser();
		this.comments = nested.getCommentsList();
		this.likes = nested.getLikesList();
		this.tags = nested.getTagsList();
	}

	public HHPostFull(Cursor cursor){
		this.post = new HHPost(cursor);
		this.user = new HHUser(cursor);
		this.track = new HHCachedSpotifyTrack(cursor);
	}

	public HHPost getPost() {
		return post;
	}

	public HHUser getUser() {
		return user;
	}

	public List<HHCommentUser> getComments() {
		return comments;
	}

	public List<HHLikeUser> getLikes() {
		return likes;
	}

	public List<HHTagUser> getTags() {
		return tags;
	}

	public HHCachedSpotifyTrack getTrack() {
		return track;
	}

	public void setTrack(HHCachedSpotifyTrack cachedSpotifyTrack){
		this.track = cachedSpotifyTrack;
	}

	public void setComments(List<HHCommentUser> comments) {
		this.comments = comments;
	}

	public void setLikes(List<HHLikeUser> likes) {
		this.likes = likes;
	}

	public void setTags(List<HHTagUser> tags) {
		this.tags = tags;
	}

	@Override
	public int compareTo(Object that) {
		if (!(that instanceof HHPostFull))
			return -1;

		return ((HHPostFull) that).getPost().getCreatedAt().compareTo(
			this.getPost().getCreatedAt());
	}

	@Override
	public boolean equals(Object o){
		HHPostFull that = (HHPostFull) o;
		return (this.post.id == that.post.id);
	}

	@Override
	public int hashCode() {
		return (int) post.id;
	}

}
