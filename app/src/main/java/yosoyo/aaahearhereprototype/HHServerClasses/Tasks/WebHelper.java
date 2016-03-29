package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yosoyo.aaahearhereprototype.DownloadImageTask;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostTagsArray;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestTrack;

/**
 * Created by adam on 02/03/16.
 */
public class WebHelper {

	public static final String TAG = "Web Helper";
	//public static final String SERVER_IP = "http://10.0.1.79:3000";		// MSHAW
	public static final String SERVER_IP = "http://192.168.5.59:3000";		// KAS
	//public static final String SERVER_IP = "http://192.168.1.183:3000";
	//public static final String SERVER_IP = "http://192.168.0.63:3000";
	//public static final String SERVER_IP = "http://10.72.100.185:3000";

	private static final Map<String, Bitmap> spotifyAlbumArt = new HashMap<>();
	private static final Map<String, Bitmap> facebookProfilePictures = new HashMap<>();

	public interface GetAllPostsCallback {
		void returnGetAllPosts(List<HHPostFullProcess> webPostsToProcess);
	}

	public static void getAllPosts(final GetAllPostsCallback callback){
		new GetPostsTask(new GetPostsTask.Callback() {
			@Override
			public void returnPosts(List<HHPostFullProcess> postsToProcess) {
				callback.returnGetAllPosts(postsToProcess);
			}
		}).execute();
	}

	public static void getUserPosts(long userID, final GetAllPostsCallback callback){
		new GetPostsUserTask(
			userID,
			new GetPostsUserTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFullProcess> postsToProcess) {
					callback.returnGetAllPosts(postsToProcess);
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

	public static void getUserFollowersInCount(final long user_id, final GetUserFollowersInCountCallback callback){
		new GetUserFollowersInCountTask(user_id, new GetUserFollowersInCountTask.Callback(){
			@Override
			public void returnUserFollowersInCount(int followersInCount) {
				callback.returnGetUserFollowersInCount(followersInCount);
			}
		}).execute();
	}

	public interface GetUserFollowersOutCountCallback {
		void returnGetUserFollowersOutCount(int followersOutCount);
	}

	public static void getUserFollowersOutCount(final long user_id, final GetUserFollowersOutCountCallback callback){
		new GetUserFollowersOutCountTask(user_id, new GetUserFollowersOutCountTask.Callback(){
			@Override
			public void returnUserFollowersOutCount(int followersOutCount) {
				callback.returnGetUserFollowersOutCount(followersOutCount);
			}
		}).execute();
	}


	public interface GetUserPrivacyCallback {
		void returnGetUserPrivacy(boolean userPrivacy);
	}

	public static void getUserPrivacy(final long user_id, final GetUserPrivacyCallback callback){
		new GetUserPrivacyTask(user_id, new GetUserPrivacyTask.Callback(){
			@Override
			public void returnUserPrivacy(boolean userPrivacy) {
				callback.returnGetUserPrivacy(userPrivacy);
			}
		}).execute();
	}

	public interface GetPostsAtLocationCallback {
		void returnGetPostsAtLocation(List<HHPostFull> posts);
	}

	public static void getPostsAtLocation(Location location, long userID, final GetPostsAtLocationCallback callback){
		new GetPostsAtLocationTask(location, userID, new GetPostsAtLocationTask.Callback(){
			@Override
			public void returnPostsAtLocation(List<HHPostFull> posts){
				callback.returnGetPostsAtLocation(posts);
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
		new SpotifyAPIRequestTrack(trackID,
								   new SpotifyAPIRequestTrack.SpotifyAPIRequestTrackCallback() {
			@Override
			public void returnSpotifyTrack(SpotifyTrack spotifyTrack) {
				callback.returnSpotifyTrack(spotifyTrack);
			}
		}).execute();
	}

	public interface GetSpotifyAlbumArtCallback {
		void returnSpotifyAlbumArt(Bitmap bitmap);
	}

	public static void getSpotifyAlbumArt(final String trackID, final String imageURL, final GetSpotifyAlbumArtCallback callback){
		if (spotifyAlbumArt.containsKey(trackID))
			callback.returnSpotifyAlbumArt(spotifyAlbumArt.get(trackID));
		else
			new DownloadImageTask(new DownloadImageTask.DownloadImageTaskCallback() {
				@Override
				public void returnDownloadedImage(Bitmap result) {
					spotifyAlbumArt.put(trackID, result);
					callback.returnSpotifyAlbumArt(result);
				}
			}).execute(imageURL);
	}

	public static void getSpotifyAlbumArt(final HHCachedSpotifyTrack track, final GetSpotifyAlbumArtCallback callback){
		if (spotifyAlbumArt.containsKey(track.getTrackID()))
			callback.returnSpotifyAlbumArt(spotifyAlbumArt.get(track.getTrackID()));
		else
			new DownloadImageTask(new DownloadImageTask.DownloadImageTaskCallback() {
				@Override
				public void returnDownloadedImage(Bitmap result) {
					spotifyAlbumArt.put(track.getTrackID(), result);
					callback.returnSpotifyAlbumArt(result);
				}
			}).execute(track.getImageUrl());
	}

	public interface GetFacebookProfilePictureCallback {
		void returnFacebookProfilePicture(Bitmap bitmap);
	}

	public static void getFacebookProfilePicture(final String fb_user_id, final GetFacebookProfilePictureCallback callback){
		if (facebookProfilePictures.containsKey(fb_user_id))
			callback.returnFacebookProfilePicture(facebookProfilePictures.get(fb_user_id));
		else
			new DownloadImageTask(new DownloadImageTask.DownloadImageTaskCallback() {
				@Override
				public void returnDownloadedImage(Bitmap result) {
					facebookProfilePictures.put(fb_user_id, result);
					callback.returnFacebookProfilePicture(result);
				}
			}).execute(DownloadImageTask.FACEBOOK_PROFILE_PHOTO + fb_user_id + DownloadImageTask.FACEBOOK_PROFILE_PHOTO_NORMAL);
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
		void returnAcceptFollowRequest(boolean success);
	}

	public static void acceptFollowRequest(final HHFollowRequestUser followRequest, final AcceptFollowRequestCallback callback){
		new PostAcceptFollowRequestTask(
			followRequest,
			new PostAcceptFollowRequestTask.Callback() {
				@Override
				public void returnPostAcceptFollowRequest(Boolean success) {
					callback.returnAcceptFollowRequest(success);
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

	public static void getUser(final long userID, final GetUserCallback callback){
		new GetUserTask(
			userID,
			new GetUserTask.Callback() {
				@Override
				public void returnGetUser(boolean success, HHUserFullProcess user) {
					callback.returnGetUser(new HHUserFull(user));
				}
			}).execute();
	}

	public interface SearchUsersCallback{
		void returnSearchUsers(List<HHUser> foundUsers);
	}

	public static void searchUsers(final String query, final SearchUsersCallback callback){
		new SearchUsersTask(
			query,
			new SearchUsersTask.Callback() {
				@Override
				public void returnSearchUsers(List<HHUser> foundUsers) {
					callback.returnSearchUsers(foundUsers);
				}
			}).execute();
	}

}
