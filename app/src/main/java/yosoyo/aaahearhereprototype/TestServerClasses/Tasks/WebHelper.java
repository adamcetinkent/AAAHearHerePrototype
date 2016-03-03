package yosoyo.aaahearhereprototype.TestServerClasses.Tasks;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yosoyo.aaahearhereprototype.DownloadImageTask;
import yosoyo.aaahearhereprototype.SpotifyAPIRequestTrack;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;

/**
 * Created by adam on 02/03/16.
 */
public class WebHelper {

	public static final String TAG = "Web Helper";
	public static final String SERVER_IP = "http://10.0.1.79:3000";

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
			}).execute(DownloadImageTask.FACEBOOK_PROFILE_PHOTO + fb_user_id + DownloadImageTask.FACEBOOK_PROFILE_PHOTO_SMALL);
	}

}
