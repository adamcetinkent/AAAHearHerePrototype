package yosoyo.aaahearhereprototype.TestServerClasses.Tasks;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yosoyo.aaahearhereprototype.DownloadImageTask;
import yosoyo.aaahearhereprototype.SpotifyAPIRequestTrack;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.TestComment;
import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class WebHelper {

	public static final String TAG = "Web Helper";
	//public static final String SERVER_IP = "http://10.0.1.79:3000";
	public static final String SERVER_IP = "http://192.168.0.63:3000";

	public static Map<String, Bitmap> spotifyAlbumArt = new HashMap<>();
	public static Map<String, Bitmap> facebookProfilePictures = new HashMap<>();

	public interface GetAllWebPostsCallback {
		void returnAllWebPosts(List<TestPostFullProcess> webPostsToProcess);
	}

	public static void getAllWebPosts(final GetAllWebPostsCallback callback){
		new TestGetPostsTask(new TestGetPostsTask.TestGetPostsTaskCallback() {
			@Override
			public void returnTestPosts(List<TestPostFullProcess> testPosts) {
				callback.returnAllWebPosts(testPosts);
			}
		}).execute();
	}

	public interface GetWebPostCallback {
		void returnWebPost(TestPostFullProcess webPostToProcess);
	}

	public static void getWebPost(long post_id, final GetWebPostCallback callback){
		new TestGetPostTask(post_id, new TestGetPostTask.TestGetPostTaskCallback(){
			@Override
			public void returnTestPost(TestPostFullProcess testPosts) {
				callback.returnWebPost(testPosts);
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

	public static void getSpotifyAlbumArt(final CachedSpotifyTrack track, final GetSpotifyAlbumArtCallback callback){
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
		void returnPostedComment(TestComment returnedComment);
	}

	public static void postComment(final TestComment comment, final PostCommentCallback callback){
		new TestCreateCommentTask(
			comment,
			new TestCreateCommentTask.TestCreateCommentTaskCallback() {
				  @Override
				  public void returnResultCreateComment(Boolean success, TestComment testComment) {
					  callback.returnPostedComment(testComment);
				  }
			  }).execute();
	}

	public interface PostLikeCallback{
		void returnPostedLike(TestLike returnedLike);
	}

	public static void postLike(final TestLike like, final PostLikeCallback callback){
		new TestCreateLikeTask(
			like,
			new TestCreateLikeTask.TestCreateLikeTaskCallback() {
				@Override
				public void returnResultCreateLike(Boolean success, TestLike testLike) {
					callback.returnPostedLike(testLike);
				}
			}).execute();
	}

	public interface DeleteLikeCallback{
		void returnDeletedLike(boolean success);
	}

	public static void deleteLike(final TestLike like, final DeleteLikeCallback callback){
		new TestDeleteLikeTask(
			like,
			new TestDeleteLikeTask.TestDeleteLikeTaskCallback() {
				@Override
				public void returnResultDeleteLike(Boolean success) {
					callback.returnDeletedLike(success);
				}
			}).execute();
	}
}
