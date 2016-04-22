package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.Profile;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFullNested;

/**
 * Created by adam on 10/03/16.
 *
 * Contains a {@link HHUser}, and lists of its associated {@link HHFriendshipUser}s,
 * inward and outward {@link HHFollowUser}s, and inward and outward {@link HHFollowRequestUser}s.
 */
public class HHUserFull implements Parcelable {

	private final HHUser user;
	private List<HHFriendshipUser> friendships;
	private List<HHFollowUser> followOuts;
	private List<HHFollowUser> followIns;
	private List<HHFollowRequestUser> followOutRequests;
	private List<HHFollowRequestUser> followInRequests;

	public HHUserFull(HHUserFullProcess process){
		this.user = process.getUser();
		this.friendships = process.getFriendships();
		this.followOuts = process.getFollowOuts();
		this.followIns = process.getFollowIns();
		this.followOutRequests = process.getFollowOutRequests();
		this.followInRequests = process.getFollowInRequests();
	}

	HHUserFull(HHUserFullNested nested){
		this.user = new HHUser(nested);
		this.friendships = nested.getFriendshipsList();
		this.followOuts = nested.getFollowsList();
		this.followIns = nested.getFollowedsList();
		this.followOutRequests = nested.getFollowRequestsList();
		this.followInRequests = nested.getFollowedRequestsList();
	}

	public HHUserFull(Profile profile){
		user = new HHUser(profile);
		friendships = new ArrayList<>();
		followOuts = new ArrayList<>();
		followIns = new ArrayList<>();
		followOutRequests = new ArrayList<>();
		followInRequests = new ArrayList<>();
	}

	public HHUserFull(Cursor cursor, String idColumnIndex){
		this.user = new HHUser(cursor, idColumnIndex);
	}

	public HHUser getUser() {
		return user;
	}

	public List<HHFriendshipUser> getFriendships() {
		return friendships;
	}

	public List<HHFollowUser> getFollowOuts() {
		return followOuts;
	}

	public List<HHFollowUser> getFollowIns() {
		return followIns;
	}

	public List<HHFollowRequestUser> getFollowOutRequests() {
		return followOutRequests;
	}

	public List<HHFollowRequestUser> getFollowInRequests() {
		return followInRequests;
	}

	public void setFriendships(List<HHFriendshipUser> friendships) {
		this.friendships = friendships;
	}

	public void setFollowOuts(List<HHFollowUser> followOuts) {
		this.followOuts = followOuts;
	}

	public void setFollowIns(List<HHFollowUser> followIns) {
		this.followIns = followIns;
	}

	public void setFollowOutRequests(List<HHFollowRequestUser> followOutRequests) {
		this.followOutRequests = followOutRequests;
	}

	public void setFollowInRequests(List<HHFollowRequestUser> followInRequests) {
		this.followInRequests = followInRequests;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(user, flags);
		dest.writeTypedList(friendships);
		dest.writeTypedList(followOuts);
		dest.writeTypedList(followIns);
		dest.writeTypedList(followOutRequests);
		dest.writeTypedList(followInRequests);
	}

	private final static Parcelable.Creator<HHUserFull> CREATOR = new Parcelable.Creator<HHUserFull>(){

		@Override
		public HHUserFull createFromParcel(Parcel source) {
			return new HHUserFull(source);
		}

		@Override
		public HHUserFull[] newArray(int size) {
			return new HHUserFull[size];
		}

	};

	private HHUserFull(Parcel in){
		user = in.readParcelable(HHUser.class.getClassLoader());
		in.readTypedList(friendships, HHFriendshipUser.CREATOR);
		in.readTypedList(followOuts, HHFollowUser.CREATOR);
		in.readTypedList(followIns, HHFollowUser.CREATOR);
		in.readTypedList(followOutRequests, HHFollowRequestUser.CREATOR);
		in.readTypedList(followInRequests, HHFollowRequestUser.CREATOR);
	}

}
