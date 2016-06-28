package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.android.gms.maps.model.LatLngBounds;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHMute;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostTagsArray;
import com.yosoyo.aaahearhereprototype.Services.DownloadImageTask;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAPIResponse;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestAlbum;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestArtist;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestArtistTopTracks;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestSearch;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestTrack;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adam on 02/03/16.
 *
 * Handles web operations
 */
public class WebHelper {

	public static final String TAG = "Web Helper";
	public static final String SERVER_IP = "http://94.174.159.110";			// WEB
	//public static final String SERVER_IP = "http://10.0.1.79:3000";		// MSHAW

	private static final Map<String, Bitmap> spotifyAlbumArt = new HashMap<>();
	private static final Map<String, Bitmap> facebookProfilePictures = new HashMap<>();
	private static Activity activity;

	public interface GetAllPostsCallback {
		void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess);
		void warnNoEarlierPosts();
		void warnNoLaterPosts();
	}

	public static void getAllPosts(final Timestamp beforeTime,
								   final Long[] excludeIDs,
								   final GetAllPostsCallback callback){
		new GetPostsTask(
			beforeTime,
			excludeIDs,
			new GetPostsTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFullProcess> postsToProcess) {
					callback.returnGetAllPosts(postsToProcess);
					if (postsToProcess != null) {
						preLoadPostProcessBitmaps(postsToProcess);
						if (postsToProcess.size() <= 0)
							callback.warnNoEarlierPosts();
					}
				}
			}).execute();
	}

	public static void getAllPostsSince(final Timestamp sinceTime,
								   		final Long[] excludeIDs,
								   		final GetAllPostsCallback callback){
		new GetPostsSinceTask(
			sinceTime,
			excludeIDs,
			new GetPostsSinceTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFullProcess> postsToProcess) {
					callback.returnGetAllPosts(postsToProcess);
					if (postsToProcess != null) {
						preLoadPostProcessBitmaps(postsToProcess);
						if (postsToProcess.size() <= 0)
							callback.warnNoLaterPosts();
					}
				}
			}).execute();
	}

	public static void getUserPosts(final long userID,
									final Timestamp beforeTime,
									final Long[] excludeIDs,
									final GetAllPostsCallback callback){
		new GetPostsUserTask(
			userID,
			beforeTime,
			excludeIDs,
			new GetPostsUserTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFullProcess> postsToProcess) {
					callback.returnGetAllPosts(postsToProcess);
					if (postsToProcess != null) {
						preLoadPostProcessBitmaps(postsToProcess);
						if (postsToProcess.size() <= 0)
							callback.warnNoEarlierPosts();
					}
				}
			}).execute();
	}

	public interface GetPostCallback {
		void returnGetPost(HHPostFullProcess webPostToProcess);
	}

	public static void getPost(long post_id, final GetPostCallback callback){
		new GetPostTask(post_id, new GetPostTask.Callback(){
			@Override
			public void returnPost(HHPostFullProcess post) {
				callback.returnGetPost(post);
				preLoadPostProcessBitmaps(post);
			}
		}).execute();
	}

	public interface GetUserPostCountCallback {
		void returnGetUserPostCount(int postCount);
	}

	public static void getUserPostCount(final long user_id, final GetUserPostCountCallback callback){
		new GetUserPostCountTask(user_id, new GetUserPostCountTask.Callback(){
			@Override
			public void returnUserPostCount(int postCount) {
				callback.returnGetUserPostCount(postCount);
			}
		}).execute();
	}

	public interface GetUserFollowersInCountCallback {
		void returnGetUserFollowersInCount(int followersInCount);
	}

	public static void getUserFollowersInCount(final String authToken,
											   final long user_id,
											   final GetUserFollowersInCountCallback callback){
		new GetUserFollowersInCountTask(
			authToken,
			user_id,
			new GetUserFollowersInCountTask.Callback(){
				@Override
				public void returnUserFollowersInCount(int followersInCount) {
					callback.returnGetUserFollowersInCount(followersInCount);
				}
			}).execute();
	}

	public interface GetUserFollowersOutCountCallback {
		void returnGetUserFollowersOutCount(int followersOutCount);
	}

	public static void getUserFollowersOutCount(final String authToken,
												final long user_id,
												final GetUserFollowersOutCountCallback callback){
		new GetUserFollowersOutCountTask(
			authToken,
			user_id,
				new GetUserFollowersOutCountTask.Callback(){
				@Override
				public void returnUserFollowersOutCount(int followersOutCount) {
					callback.returnGetUserFollowersOutCount(followersOutCount);
				}
			}).execute();
	}


	public interface GetUserPrivacyCallback {
		void returnGetUserPrivacy(boolean userPrivacy);
	}

	public static void getUserPrivacy(final String authToken,
									  final long user_id,
									  final GetUserPrivacyCallback callback){
		new GetUserPrivacyTask(
			authToken,
			user_id,
			new GetUserPrivacyTask.Callback(){
				@Override
				public void returnUserPrivacy(boolean userPrivacy) {
					callback.returnGetUserPrivacy(userPrivacy);
				}
			}).execute();
		}

	public interface GetPostsAtLocationCallback {
		void returnGetPostsAtLocation(List<HHNotification> posts);
	}

	public static void getPostsAtLocation(final Location location,
										  final long userID,
										  final String authToken,
										  final GetPostsAtLocationCallback callback){
		new GetPostsAtLocationTask(location, userID, authToken, new GetPostsAtLocationTask.Callback(){
			@Override
			public void returnPostsAtLocation(List<HHNotification> posts){
				callback.returnGetPostsAtLocation(posts);
			}
		}).execute();
	}

	/*public interface GetPostsWithinBoundsCallback {
		void returnGetPostsWithinBounds(List<HHPostFull> posts);
	}*/

	public static void getPostsWithinBounds(final LatLngBounds bounds,
											final long userID,
											final Long[] excludeIDs,
											final GetAllPostsCallback callback){
		new GetPostsWithinBoundsTask(bounds,
									 userID,
									 excludeIDs,
									 new GetPostsWithinBoundsTask.Callback(){
			@Override
			public void returnPostsWithinBounds(List<HHPostFullProcess> postsToProcess){
				callback.returnGetAllPosts(postsToProcess);
				if (postsToProcess != null) {
					preLoadPostProcessBitmaps(postsToProcess);
					if (postsToProcess.size() <= 0)
						callback.warnNoEarlierPosts();
				} else {
					callback.warnNoEarlierPosts();
				}
			}
		}).execute();
	}

	public interface PostPostCallback {
		void returnPostPost(boolean success, HHPostFullProcess webPostToProcess);
	}

	public static void postPost(HHPostTagsArray post, final PostPostCallback callback){
		new PostPostTask(post, new PostPostTask.Callback() {
			@Override
			public void returnPostPost(Boolean success, HHPostFullProcess postToProcess) {
				callback.returnPostPost(success, postToProcess);
			}
		}).execute();
	}

	public interface GetSpotifyTrackCallback {
		void returnSpotifyTrack(SpotifyTrack spotifyTrack);
	}

	public static void getSpotifyTrack(String trackID, final GetSpotifyTrackCallback callback){
		new SpotifyAPIRequestTrack(
			trackID,
			new SpotifyAPIRequestTrack.Callback() {
				@Override
				public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {
					callback.returnSpotifyTrack(spotifyTrack);
					if (spotifyTrack != null) {
						preLoadTrackBitmaps(spotifyTrack);
					}
				}
			}).execute();
	}

	public interface GetSpotifyAlbumCallback {
		void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum);
	}

	public static void getSpotifyAlbum(String albumID, final GetSpotifyAlbumCallback callback){
		new SpotifyAPIRequestAlbum(
			albumID,
			new SpotifyAPIRequestAlbum.Callback() {
				@Override
				public void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum) {
					callback.returnSpotifyAlbum(spotifyAlbum);
				}
			}).execute();
	}

	public interface GetSpotifyArtistCallback {
		void returnSpotifyArtist(SpotifyArtist spotifyArtist);
	}

	public static void getSpotifyArtist(String artistID, final GetSpotifyArtistCallback callback){
		new SpotifyAPIRequestArtist(
			artistID,
			new SpotifyAPIRequestArtist.Callback() {
				@Override
				public void returnSpotifyArtist(SpotifyArtist spotifyArtist) {
					callback.returnSpotifyArtist(spotifyArtist);
				}
			}).execute();
	}

	public interface GetSpotifyAlbumArtCallback {
		void returnSpotifyAlbumArt(Bitmap bitmap);
	}

	public static Bitmap getSpotifyAlbumArt(final String trackID, final String imageURL, final GetSpotifyAlbumArtCallback callback){
		if (spotifyAlbumArt.containsKey(trackID)) {
			Bitmap bitmap = spotifyAlbumArt.get(trackID);
			if (callback != null)
				callback.returnSpotifyAlbumArt(bitmap);
			return bitmap;
		} else {
			new DownloadImageTask(new DownloadImageTask.DownloadImageTaskCallback() {
				@Override
				public void returnDownloadedImage(Bitmap result) {
					spotifyAlbumArt.put(trackID, result);
					if (callback != null)
						callback.returnSpotifyAlbumArt(result);
				}
			}).execute(imageURL);
		}
		return null;
	}

	public static Bitmap getSpotifyAlbumArt(final HHCachedSpotifyTrack track, final GetSpotifyAlbumArtCallback callback){
		if (track == null)
			return null;
		if (spotifyAlbumArt.containsKey(track.getTrackID())) {
			Bitmap bitmap = spotifyAlbumArt.get(track.getTrackID());
			if (callback != null)
				callback.returnSpotifyAlbumArt(bitmap);
			return bitmap;
		} else {
			new DownloadImageTask(new DownloadImageTask.DownloadImageTaskCallback() {
				@Override
				public void returnDownloadedImage(Bitmap result) {
					spotifyAlbumArt.put(track.getTrackID(), result);
					if (callback != null)
						callback.returnSpotifyAlbumArt(result);
				}
			}).execute(track.getImageUrl());
		}
		return null;
	}

	public interface GetFacebookProfilePictureCallback {
		void returnFacebookProfilePicture(Bitmap bitmap);
	}

	public static Bitmap getFacebookProfilePicture(final String fb_user_id, final GetFacebookProfilePictureCallback callback){
		if (facebookProfilePictures.containsKey(fb_user_id)) {
			Bitmap bitmap = facebookProfilePictures.get(fb_user_id);
			if (callback != null)
				callback.returnFacebookProfilePicture(bitmap);
			return bitmap;
		} else {
			new DownloadImageTask(new DownloadImageTask.DownloadImageTaskCallback() {
				@Override
				public void returnDownloadedImage(Bitmap result) {
					facebookProfilePictures.put(fb_user_id, result);
					if (callback != null)
						callback.returnFacebookProfilePicture(result);
				}
			}).execute(
				DownloadImageTask.FACEBOOK_PROFILE_PHOTO + fb_user_id + DownloadImageTask.FACEBOOK_PROFILE_PHOTO_NORMAL);
		}
		return null;
	}

	public interface PostCommentCallback{
		void returnPostComment(HHComment returnedComment);
	}

	public static void postComment(final HHComment comment, final PostCommentCallback callback){
		new PostCommentTask(
			comment,
			new PostCommentTask.Callback() {
				  @Override
				  public void returnPostComment(Boolean success, HHComment comment) {
					  callback.returnPostComment(comment);
				  }
			  }).execute();
	}

	public interface PostLikeCallback{
		void returnPostLike(HHLike returnedLike);
	}

	public static void postLike(final HHLike like, final PostLikeCallback callback){
		new PostLikeTask(
			like,
			new PostLikeTask.Callback() {
				@Override
				public void returnPostLike(Boolean success, HHLike like) {
					callback.returnPostLike(like);
				}
			}).execute();
	}

	public interface DeleteLikeCallback{
		void returnDeleteLike(boolean success);
	}

	public static void deleteLike(final HHLike like, final DeleteLikeCallback callback){
		new DeleteLikeTask(
			like,
			new DeleteLikeTask.Callback() {
				@Override
				public void returnDeleteLike(Boolean success) {
					callback.returnDeleteLike(success);
				}
			}).execute();
	}

	public interface DeleteFollowCallback{
		void returnDeleteFollow(boolean success);
	}

	public static void deleteFollow(final HHFollowUser follow, final DeleteFollowCallback callback){
		new DeleteFollowTask(
			follow,
			new DeleteFollowTask.Callback() {
				@Override
				public void returnDeleteFollow(Boolean success) {
					callback.returnDeleteFollow(success);
				}
			}).execute();
	}

	public interface PostFollowRequestCallback{
		void returnPostFollowRequest(boolean success, HHFollowRequestUser followRequest);
		void returnPostFollowRequestAccepted(boolean success, HHFollowUser follow);
	}

	public static void postFollowRequest(final HHFollowRequest followRequest, final PostFollowRequestCallback callback){
		new PostFollowRequestTask(
			followRequest,
			new PostFollowRequestTask.Callback() {
				@Override
				public void returnPostFollowRequest(Boolean success, HHFollowRequestUser returnedFollowRequest) {
					callback.returnPostFollowRequest(success, returnedFollowRequest);
				}

				@Override
				public void returnPostFollowRequestAccepted(Boolean success, HHFollowUser follow) {
					callback.returnPostFollowRequestAccepted(success, follow);
				}
			}).execute();
	}

	public interface AcceptFollowRequestCallback{
		void returnAcceptFollowRequest(boolean success, HHFollowRequestUser acceptedFollowRequest);
	}

	public static void acceptFollowRequest(final HHFollowRequestUser followRequest,
										   final AcceptFollowRequestCallback callback){
		new PostAcceptFollowRequestTask(
			followRequest,
			new PostAcceptFollowRequestTask.Callback() {
				@Override
				public void returnPostAcceptFollowRequest(boolean success, HHFollowRequestUser acceptedFollowRequest) {
					callback.returnAcceptFollowRequest(success, acceptedFollowRequest);
				}
			}).execute();
	}

	public static void acceptFollowRequest(final String authToken,
										   final HHNotification notification,
										   final AcceptFollowRequestCallback callback){
		new PostAcceptFollowRequestTask(
			authToken,
			notification,
			new PostAcceptFollowRequestTask.Callback() {
				@Override
				public void returnPostAcceptFollowRequest(boolean success, HHFollowRequestUser acceptedFollowRequest) {
					callback.returnAcceptFollowRequest(success, acceptedFollowRequest);
				}
			}).execute();
	}

	public interface DeleteFollowRequestCallback{
		void returnDeleteFollowRequest(boolean success);
	}

	public static void deleteFollowRequest(final HHFollowRequestUser followRequest, final DeleteFollowRequestCallback callback){
		new DeleteFollowRequestTask(
			followRequest,
			new DeleteFollowRequestTask.Callback() {
				@Override
				public void returnDeleteFollowRequest(Boolean success) {
					callback.returnDeleteFollowRequest(success);
				}
			}).execute();
	}

	public interface GetUserCallback{
		void returnGetUser(HHUserFull user);
	}

	public static void getUser(final String authToken,
							   final long userID,
							   final GetUserCallback callback){
		new GetUserTask(
			authToken,
			userID,
			new GetUserTask.Callback() {
				@Override
				public void returnGetUser(boolean success, HHUserFullProcess user) {
					callback.returnGetUser(new HHUserFull(user));
					preLoadUserBitmaps(user);
				}
			}).execute();
	}

	public interface SearchUsersCallback{
		void returnSearchUsers(List<HHUser> foundUsers);
	}

	public static void searchUsers(final String authToken,
								   final String query,
								   final SearchUsersCallback callback){
		new SearchUsersTask(
			authToken,
			query,
			new SearchUsersTask.Callback() {
				@Override
				public void returnSearchUsers(List<HHUser> foundUsers) {
					callback.returnSearchUsers(foundUsers);
					preLoadUserBitmaps(foundUsers);
				}
			}).execute();
	}

	public interface GetNotificationsCallback{
		void returnGetNotifications(List<HHNotification> notifications);
	}

	public static void getNotifications(final String authToken,
										final Timestamp sinceTime,
										final Long[] excludeIDs,
										final GetNotificationsCallback callback){
		new GetNotificationsTask(
			authToken,
			sinceTime,
			excludeIDs,
			new GetNotificationsTask.Callback() {
				@Override
				public void returnGetNotifications(List<HHNotification> notifications) {
					callback.returnGetNotifications(notifications);
				}
			}).execute();
	}

	public interface ReadNotificationCallback {
		void returnReadNotification(HHNotification readNotification);
	}

	public static void readNotification(final String authToken,
										final HHNotification notification,
										final ReadNotificationCallback callback){
		new ReadNotificationTask(
			authToken,
			notification,
			new ReadNotificationTask.Callback(){
				@Override
				public void returnReadNotification(HHNotification readNotification) {
					callback.returnReadNotification(readNotification);
				}
			}
		).execute();
	}

	public interface PostMutePostCallback {
		void returnPostMutePost(boolean success, HHMute returnedMute);
	}

	public static void postMutePost(final String authToken,
									final long postID,
									final PostMutePostCallback callback){
		new PostMutePostTask(
			authToken,
			postID,
			new PostMutePostTask.Callback() {
				@Override
				public void returnPostMutePost(Boolean success, HHMute mute) {
					callback.returnPostMutePost(success, mute);
				}
			}
		).execute();
	}

	public interface DeleteMutePostCallback {
		void returnDeleteMutePost(boolean success, HHMute deletedMute);
	}

	public static void deleteMutePost(final String authToken,
									  final long postID,
									  final DeleteMutePostCallback callback){
		new DeleteMutePostTask(
			authToken,
			postID,
			new DeleteMutePostTask.Callback() {
				@Override
				public void returnDeleteMutePost(Boolean success, HHMute deletedMute) {
					callback.returnDeleteMutePost(success, deletedMute);
				}
			}
		).execute();
	}

	public interface UpdateUserProfilePrivacyCallback {
		void returnUpdateUserProfilePrivacy(boolean success, int profilePrivacy);
	}

	public static void updateUserProfilePrivacy(final String authToken,
												final int profilePrivacy,
												final UpdateUserProfilePrivacyCallback callback){
		new UpdateUserProfilePrivacyTask(
			authToken,
			profilePrivacy,
			new UpdateUserProfilePrivacyTask.Callback(){
				@Override
				public void returnUpdateUserProfilePrivacy(Boolean success, int profilePrivacy) {
					callback.returnUpdateUserProfilePrivacy(success, profilePrivacy);
				}
			}
		).execute();
	}

	public interface UpdateUserSearchPrivacyCallback {
		void returnUpdateUserSearchPrivacy(boolean success, int searchPrivacy);
	}

	public static void updateUserSearchPrivacy(final String authToken,
											   final int searchPrivacy,
											   final UpdateUserSearchPrivacyCallback callback){
		new UpdateUserSearchPrivacyTask(
			authToken,
			searchPrivacy,
			new UpdateUserSearchPrivacyTask.Callback(){
				@Override
				public void returnUpdateUserSearchPrivacy(Boolean success, int searchPrivacy) {
					callback.returnUpdateUserSearchPrivacy(success, searchPrivacy);
				}
			}
		).execute();
	}

	public interface UpdateUserAutoAcceptCallback {
		void returnUpdateUserAutoAccept(boolean success, int autoAccept);
	}

	public static void updateUserAutoAccept(final String authToken,
											final int autoAccept,
											final UpdateUserAutoAcceptCallback callback){
		new UpdateUserAutoAcceptTask(
			authToken,
			autoAccept,
			new UpdateUserAutoAcceptTask.Callback(){
				@Override
				public void returnUpdateUserAutoAccept(Boolean success, int autoAccept) {
					callback.returnUpdateUserAutoAccept(success, autoAccept);
				}
			}
		).execute();
	}

	private static void preLoadPostProcessBitmaps(List<HHPostFullProcess> posts){
		if (posts != null && checkWifi()){
			for (HHPostFullProcess post : posts) {
				preLoadPostProcessBitmaps(post, true);
			}
		}
	}

	private static void preLoadPostProcessBitmaps(HHPostFullProcess post){
		preLoadPostProcessBitmaps(post, false);
	}

	private static void preLoadPostProcessBitmaps(HHPostFullProcess post, boolean shortcut){
		if ((shortcut || checkWifi()) && post != null){
			if (post.getUser() != null)
				getFacebookProfilePicture(post.getUser().getFBUserID(), null);
			if (post.getTrack() != null)
				getSpotifyAlbumArt(post.getTrack(), null);
		}
	}

	public static void preLoadPostBitmaps(List<HHPostFull> posts){
		if (checkWifi()){
			for (HHPostFull post : posts) {
				preLoadPostBitmaps(post, true);
			}
		}
	}

	public static void preLoadPostBitmaps(HHPostFull post){
		preLoadPostBitmaps(post, false);
	}

	private static void preLoadPostBitmaps(HHPostFull post, boolean shortcut){
		if (shortcut || checkWifi()){
			getFacebookProfilePicture(post.getUser().getFBUserID(), null);
			getSpotifyAlbumArt(post.getTrack(), null);
		}
	}

	private static void preLoadUserBitmaps(List<HHUser> users){
		if (checkWifi()){
			for (HHUser user : users) {
				preLoadUserBitmaps(user, true);
			}
		}
	}

	public static void preLoadUserBitmaps(HHUserFull user){
		preLoadUserBitmaps(user.getUser(), false);
	}

	private static void preLoadUserBitmaps(HHUser user){
		preLoadUserBitmaps(user, false);
	}

	private static void preLoadUserBitmaps(HHUser user, boolean shortcut){
		if (shortcut || checkWifi()){
			getFacebookProfilePicture(user.getFBUserID(), null);
		}
	}

	public static void preLoadTrackBitmaps(SpotifyTrack spotifyTrack){
		if (checkWifi()){
			getSpotifyAlbumArt(spotifyTrack.getID(), spotifyTrack.getImageURL(), null);
		}
	}

	public static void preLoadTrackBitmaps(HHCachedSpotifyTrack spotifyTrack){
		if (checkWifi()){
			getSpotifyAlbumArt(spotifyTrack, null);
		}
	}

	public static void setActivity(Activity activity){
		WebHelper.activity = activity;
	}

	private static boolean checkWifi(){
		if (activity == null)
			return false;

		WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()){
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			return !(wifiInfo.getNetworkId() == -1 && wifiInfo
				.getSupplicantState() != SupplicantState.COMPLETED);
		}
		return false;
	}

	public interface SearchSpotifyTracksCallback{
		void returnSearchSpotifyTracks(List<SpotifyTrack> spotifyTracks, int totalTracks);
	}

	public static void searchSpotifyTracks(final String query,
										   final int offset,
										   final SearchSpotifyTracksCallback callback){
		new SpotifyAPIRequestSearch(
			query,
			SpotifyAPIRequestSearch.SEARCH_TYPE_TRACK,
			offset,
			new SpotifyAPIRequestSearch.Callback() {
				@Override
				public void returnSpotifySearchResults(SpotifyAPIResponse output) {
					if (output.getTracks() != null) {
						callback.returnSearchSpotifyTracks(
							output.getTracks().getItemsList(),
							output.getTracks().getTotal());
					} else {
						callback.returnSearchSpotifyTracks(new ArrayList<SpotifyTrack>(), 0);
					}
				}
			}).execute();
	}

	public interface SearchSpotifyArtistsCallback{
		void returnSearchSpotifyArtists(List<SpotifyArtist> spotifyArtists, int totalArtists);
	}

	public static void searchSpotifyArtists(final String query,
											final int offset,
											final SearchSpotifyArtistsCallback callback){
		new SpotifyAPIRequestSearch(
			query,
			SpotifyAPIRequestSearch.SEARCH_TYPE_ARTIST,
			offset,
			new SpotifyAPIRequestSearch.Callback() {
				@Override
				public void returnSpotifySearchResults(SpotifyAPIResponse output) {
					if (output.getArtists() != null) {
						callback.returnSearchSpotifyArtists(
							output.getArtists().getItemsList(),
							output.getArtists().getTotal());
					} else {
						callback.returnSearchSpotifyArtists(new ArrayList<SpotifyArtist>(), 0);
					}
				}
			}).execute();
	}

	public interface SearchSpotifyAlbumsCallback{
		void returnSearchSpotifyAlbums(List<SpotifyAlbum> spotifyAlbums, int totalAlbums);
	}

	public static void searchSpotifyAlbums(final String query,
										   final int offset,
										   final SearchSpotifyAlbumsCallback callback){
		new SpotifyAPIRequestSearch(
			query,
			SpotifyAPIRequestSearch.SEARCH_TYPE_ALBUM,
			offset,
			new SpotifyAPIRequestSearch.Callback() {
				@Override
				public void returnSpotifySearchResults(SpotifyAPIResponse output) {
					if (output.getAlbums() != null) {
						callback.returnSearchSpotifyAlbums(
							output.getAlbums().getItemsList(),
							output.getAlbums().getTotal());
					} else {
						callback.returnSearchSpotifyAlbums(new ArrayList<SpotifyAlbum>(), 0);
					}
				}
			}).execute();
	}

	public interface GetSpotifyArtistTopTracksCallback{
		void returnGetSpotifyArtistTopTracks(List<SpotifyTrack> spotifyTracks);
	}

	public static void getSpotifyArtistTopTracks(final String artistID,
												 final String country,
												 final GetSpotifyArtistTopTracksCallback callback){
		new SpotifyAPIRequestArtistTopTracks(
			artistID,
			country,
			new SpotifyAPIRequestArtistTopTracks.Callback() {
				@Override
				public void returnGetSpotifyArtistTopTracks(List<SpotifyTrack> spotifyTracks) {
					if (spotifyTracks != null) {
						callback.returnGetSpotifyArtistTopTracks(spotifyTracks);
					} else {
						callback.returnGetSpotifyArtistTopTracks(new ArrayList<SpotifyTrack>());
					}
				}
			}).execute();
	}

}
