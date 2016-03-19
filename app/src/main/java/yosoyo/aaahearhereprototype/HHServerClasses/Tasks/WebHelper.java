package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yosoyo.aaahearhereprototype.DownloadImageTask;
import yosoyo.aaahearhereprototype.HHServerClasses.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHLike;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostTagsArray;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.SpotifyClasses.Tasks.SpotifyAPIRequestTrack;

/**
 * Created by adam on 02/03/16.
 */
public class WebHelper {

	public static final String TAG = "Web Helper";
	public static final String SERVER_IP = "http://10.0.1.79:3000";
	//public static final String SERVER_IP = "http://192.168.1.183:3000";
	//public static final String SERVER_IP = "http://192.168.0.63:3000";
	//public static final String SERVER_IP = "http://10.72.100.185:3000";

	private static final Map<String, Bitmap> spotifyAlbumArt = new HashMap<>();
	private static final Map<String, Bitmap> facebookProfilePictures = new HashMap<>();

	public interface GetAllWebPostsCallback {
		void returnGetAllWebPosts(List<HHPostFullProcess> webPostsToProcess);
	}

	public static void getAllWebPosts(final GetAllWebPostsCallback callback){
		new GetPostsTask(new GetPostsTask.Callback() {
			@Override
			public void returnPosts(List<HHPostFullProcess> postsToProcess) {
				callback.returnGetAllWebPosts(postsToProcess);
			}
		}).execute();
	}

	public static void getUserWebPosts(long userID, final GetAllWebPostsCallback callback){
		new GetPostsUserTask(
			userID,
			new GetPostsUserTask.Callback() {
				@Override
				public void returnPosts(List<HHPostFullProcess> postsToProcess) {
					callback.returnGetAllWebPosts(postsToProcess);
				}
			}).execute();
	}

	public interface GetWebPostCallback {
		void returnGetWebPost(HHPostFullProcess webPostToProcess);
	}

	public static void getWebPost(long post_id, final GetWebPostCallback callback){
		new GetPostTask(post_id, new GetPostTask.Callback(){
			@Override
			public void returnPost(HHPostFullProcess post) {
				callback.returnGetWebPost(post);
			}
		}).execute();
	}

	public interface GetWebPostsAtLocationCallback {
		void returnGetWebPostsAtLocation(List<HHPostFull> posts);
	}

	public static void getWebPostsAtLocation(Location location, long userID, final GetWebPostsAtLocationCallback callback){
		new GetPostsAtLocationTask(location, userID, new GetPostsAtLocationTask.Callback(){
			@Override
			public void returnPostsAtLocation(List<HHPostFull> posts){
				callback.returnGetWebPostsAtLocation(posts);
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

}
