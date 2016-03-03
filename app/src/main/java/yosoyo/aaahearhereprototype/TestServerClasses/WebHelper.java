package yosoyo.aaahearhereprototype.TestServerClasses;

import java.util.List;

import yosoyo.aaahearhereprototype.SpotifyAPIRequestTrack;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TestGetPostsTask;

/**
 * Created by adam on 02/03/16.
 */
public class WebHelper {

	public static final String TAG = "Web Helper";

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

}
