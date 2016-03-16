package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yosoyo.aaahearhereprototype.DownloadImageTask;
import yosoyo.aaahearhereprototype.HHServerClasses.HHCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.HHServerClasses.HHComment;
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
	//public static final String SERVER_IP = "http://192.168.0.63:3000";
	//public static final String SERVER_IP = "http://10.72.100.185:3000";

	private static final Map<String, Bitmap> spotifyAlbumArt = new HashMap<>();
	private static final Map<String, Bitmap> facebookProfilePictures = new HashMap<>();

	public interface GetAllWebPostsCallback {
		void returnAllWebPosts(List<HHPostFullProcess> webPostsToProcess);
	}

	public static void getAllWebPosts(final GetAllWebPostsCallback callback){
		new GetPostsTask(new GetPostsTask.GetPostsTaskCallback() {
			@Override
			public void returnPosts(List<HHPostFullProcess> postsToProcess) {
				callback.returnAllWebPosts(postsToProcess);
			}
		}).execute();
	}

	public static void getUserWebPosts(long userID, final GetAllWebPostsCallback callback){
		new GetPostsUserTask(
			userID,
			new GetPostsUserTask.GetPostsUserTaskCallback() {
				@Override
				public void returnPosts(List<HHPostFullProcess> postsToProcess) {
					callback.returnAllWebPosts(postsToProcess);
				}
			}).execute();
	}

	public interface GetWebPostCallback {
		void returnWebPost(HHPostFullProcess webPostToProcess);
	}

	public static void getWebPost(long post_id, final GetWebPostCallback callback){
		new GetPostTask(post_id, new GetPostTask.GetPostTaskCallback(){
			@Override
			public void returnPost(HHPostFullProcess post) {
				callback.returnWebPost(post);
			}
		}).execute();
	}

	public interface GetWebPostsAtLocationCallback {
		void returnWebPostsAtLocation(List<HHPostFull> posts);
	}

	public static void getWebPostsAtLocation(Location location, long userID, final GetWebPostsAtLocationCallback callback){
		new GetPostsAtLocationTask(location, userID, new GetPostsAtLocationTask.GetPostsAtLocationTaskCallback(){
			@Override
			public void returnPostsAtLocation(List<HHPostFull> posts){
				callback.returnWebPostsAtLocation(posts);
			}
		}).execute();
	}

	public interface PostPostCallback {
		void returnPostedPost(boolean success, HHPostFullProcess webPostToProcess);
	}

	public static void postPost(HHPostTagsArray post, final PostPostCallback callback){
		new CreatePostTask(post, new CreatePostTask.CreatePostTaskCallback() {
			@Override
			public void returnResultCreatePost(Boolean success, HHPostFullProcess postToProcess) {
				callback.returnPostedPost(success, postToProcess);
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
		void returnPostedComment(HHComment returnedComment);
	}

	public static void postComment(final HHComment comment, final PostCommentCallback callback){
		new CreateCommentTask(
			comment,
			new CreateCommentTask.CreateCommentTaskCallback() {
				  @Override
				  public void returnResultCreateComment(Boolean success, HHComment comment) {
					  callback.returnPostedComment(comment);
				  }
			  }).execute();
	}

	public interface PostLikeCallback{
		void returnPostedLike(HHLike returnedLike);
	}

	public static void postLike(final HHLike like, final PostLikeCallback callback){
		new CreateLikeTask(
			like,
			new CreateLikeTask.CreateLikeTaskCallback() {
				@Override
				public void returnResultCreateLike(Boolean success, HHLike like) {
					callback.returnPostedLike(like);
				}
			}).execute();
	}

	public interface DeleteLikeCallback{
		void returnDeletedLike(boolean success);
	}

	public static void deleteLike(final HHLike like, final DeleteLikeCallback callback){
		new DeleteLikeTask(
			like,
			new DeleteLikeTask.DeleteLikeTaskCallback() {
				@Override
				public void returnResultDeleteLike(Boolean success) {
					callback.returnDeletedLike(success);
				}
			}).execute();
	}
}
