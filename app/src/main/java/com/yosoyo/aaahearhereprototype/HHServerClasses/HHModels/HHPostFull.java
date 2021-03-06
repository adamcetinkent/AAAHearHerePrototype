package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 02/03/16.
 *
 * Contains a {@link HHPost}, its associated {@link HHUser}, {@link HHCachedSpotifyTrack} and lists
 * of its {@link HHCommentUser}s, {@link HHLikeUser}s and {@link HHTagUser}s.
 */
public class HHPostFull implements Comparable, Parcelable {

	private final HHPost post;
	private final HHUser user;
	private HHCachedSpotifyTrack track;
	private List<HHCommentUser> comments;
	private List<HHLikeUser> likes;
	private List<HHTagUser> tags;
	private HHMute mute;

	public HHPostFull(HHPostFullProcess that){
		this.post = that.getPost();
		this.user = that.getUser();
		this.comments = that.getComments();
		this.track = that.getTrack();
		this.likes = that.getLikes();
		this.tags = that.getTags();
		this.mute = that.getMute();
	}

	public HHPostFull(HHPostFullNested nested){
		this.post = new HHPost(nested);
		this.user = nested.getUser();
		this.comments = nested.getCommentsList();
		this.likes = nested.getLikesList();
		this.tags = nested.getTagsList();
		this.mute = nested.getMute();
	}

	public HHPostFull(HHPostFullNested nested, HHUser user){
		this.post = new HHPost(nested);
		this.user = user;
		this.comments = null;
		this.likes = null;
		this.tags = null;
		this.mute = null; //TODO: ?
	}

	public HHPostFull(Cursor cursor, String userIDColumnIndex){
		this.post = new HHPost(cursor);
		this.user = new HHUser(cursor, userIDColumnIndex);
		this.track = new HHCachedSpotifyTrack(cursor);
		this.mute = null; //TODO: ?
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

	public HHMute getMute() {
		return mute;
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

	public void setMute(HHMute mute) {
		this.mute = mute;
	}

	@Override
	public int compareTo(@NonNull Object that) {
		if (!(that instanceof HHPostFull))
			return -1;

		return ((HHPostFull) that).getPost().getCreatedAt().compareTo(
			this.getPost().getCreatedAt());
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object o){
		HHPostFull that = (HHPostFull) o;
		return (this.post.id == that.post.id);
	}

	@Override
	public int hashCode() {
		return (int) post.id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(post, flags);
		dest.writeParcelable(user, flags);
		dest.writeParcelable(track, flags);
		if (comments == null)
			comments = new ArrayList<>();
		dest.writeTypedList(comments);
		if (likes == null)
			likes = new ArrayList<>();
		dest.writeTypedList(likes);
		if (tags == null)
			tags = new ArrayList<>();
		dest.writeTypedList(tags);
		dest.writeParcelable(mute, flags);
	}

	public final static Parcelable.Creator<HHPostFull> CREATOR = new Parcelable.Creator<HHPostFull>(){

		@Override
		public HHPostFull createFromParcel(Parcel source) {
			return new HHPostFull(source);
		}

		@Override
		public HHPostFull[] newArray(int size) {
			return new HHPostFull[size];
		}

	};

	private HHPostFull(Parcel in){
		post = in.readParcelable(HHPost.class.getClassLoader());
		user = in.readParcelable(HHUser.class.getClassLoader());
		track = in.readParcelable(HHCachedSpotifyTrack.class.getClassLoader());
		if (comments == null)
			comments = new ArrayList<>();
		in.readTypedList(comments, HHCommentUser.CREATOR);
		if (likes == null)
			likes = new ArrayList<>();
		in.readTypedList(likes, HHLikeUser.CREATOR);
		if (tags == null)
			tags = new ArrayList<>();
		in.readTypedList(tags, HHTagUser.CREATOR);
		mute = in.readParcelable(HHMute.class.getClassLoader());
	}

}
