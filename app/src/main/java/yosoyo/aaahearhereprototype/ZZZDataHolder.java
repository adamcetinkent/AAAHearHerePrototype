package yosoyo.aaahearhereprototype;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.CachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.ORMCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.ORMTestPostUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestGetPostUsersTask;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostUser;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostUserTrack;

/**
 * Created by adam on 26/02/16.
 */
public class ZZZDataHolder
	implements TestGetPostUsersTask.TestGetPostsTaskCallback,
	ORMTestPostUser.InsertDBTestPostUserTask.InsertDBTestPostUserCallback,
	ORMCachedSpotifyTrack.InsertCachedSpotifyTrackTask.InsertCachedSpotifyTrackTaskCallback,
	SpotifyAPIRequestTrack.SpotifyAPIRequestTrackCallback {

	public static final String TAG = "ZZZDataHolder";

	public static List<TestPostUser> testPostUsers;
	public static List<CachedSpotifyTrack> cachedSpotifyTracks;
	public static List<TestPostUserTrack> testPostUserTracks;

	private Context context;
	private GetPostUsersCallback getPostUsersCallback;
	private InsertPostCallback insertPostCallback;
	private InsertCachedSpotifyTrackCallback insertCachedSpotifyTrackCallback;
	private int numInsertingTestPostUser;

	public interface GetPostUsersCallback {
		void returnOnePost(TestPostUserTrack testPostUser);
		void returnAllPosts(List<TestPostUser> testPostUsers);
	}

	public interface InsertPostCallback{
		void returnInsertPost(long id, TestPostUser testPostUser);
	}

	public interface InsertCachedSpotifyTrackCallback{
		void returnInsertCachedSpotifyTrackCallback(long id, CachedSpotifyTrack cachedSpotifyTrack);
	}

	public void setContext(Context context){
		this.context = context;
	}

	public void addTestPostUserTrack(TestPostUserTrack testPostUserTrack, InsertPostCallback insertPostCallback, InsertCachedSpotifyTrackCallback insertCachedSpotifyTrackCallback){
		this.insertPostCallback = insertPostCallback;
		this.insertCachedSpotifyTrackCallback = insertCachedSpotifyTrackCallback;
		ORMCachedSpotifyTrack.insertCachedSpotifyTrack(context, testPostUserTrack.getCachedSpotifyTrack(), this);
		ORMTestPostUser.insertPost(context, testPostUserTrack.getTestPostUser(), this);
	}

	@Override
	public void returnSpotifyTrack(SpotifyTrack spotifyTrack, int position, TestPostUser testPostUser) {
		ORMCachedSpotifyTrack.insertSpotifyTrack(context, spotifyTrack, this);
		TestPostUserTrack testPostUserTrack = new TestPostUserTrack(testPostUser, new CachedSpotifyTrack(spotifyTrack));
		testPostUserTracks.add(testPostUserTrack);
		getPostUsersCallback.returnOnePost(testPostUserTrack);
	}

	public void createTestPostUserTracks(){
		if (testPostUsers == null || cachedSpotifyTracks == null)
			return;

		testPostUserTracks = new ArrayList<>();
		boolean trackFound;
		for (TestPostUser testPostUser : testPostUsers){
			trackFound = false;
			for (CachedSpotifyTrack cachedSpotifyTrack : cachedSpotifyTracks){
				if (testPostUser.getTestPost().getTrack().equals(cachedSpotifyTrack.getTrackID())){
					testPostUserTracks.add(new TestPostUserTrack(testPostUser, cachedSpotifyTrack));
					trackFound = true;
				}
			}
			if (trackFound == false){
				new SpotifyAPIRequestTrack(this, testPostUser).execute(testPostUser.getTestPost().getTrack());
			}
		}
	}

	public void getAllPosts(Context context, GetPostUsersCallback callback){
		this.getPostUsersCallback = callback;
		TestGetPostUsersTask testGetPostUsersTask = new TestGetPostUsersTask(this);
		testGetPostUsersTask.execute();
	}

	@Override
	public void returnTestPostUsers(TestPostUser[] returnedTestPostUsers) {
		//this.testPostUsers = testPostUsers;
		if (returnedTestPostUsers != null) {
			for (TestPostUser testPostUser : returnedTestPostUsers) {
				if (addTestPostUser(testPostUser)) {
					ORMTestPostUser.insertPost(context, testPostUser, this);
					numInsertingTestPostUser++;
				}
			}
		} else {
			Log.e(TAG, "No posts found!");
		}
		if (numInsertingTestPostUser <= 0){
			getPostUsersCallback.returnAllPosts(testPostUsers);
		}
	}

	private boolean addTestPostUser(TestPostUser testPostUser){
		for (TestPostUser postUser : testPostUsers){
			if (postUser.getTestPost().getId() == testPostUser.getTestPost().getId())
				return false;
		}
		return true;
	}

	@Override
	public void returnInsertedPostUserID(Long postID, int position, TestPostUser testPostUser) {
		testPostUsers.add(testPostUser);
		numInsertingTestPostUser--;
		if (numInsertingTestPostUser <= 0 && getPostUsersCallback != null){
			getPostUsersCallback.returnAllPosts(testPostUsers);
		} else if (getPostUsersCallback == null){
			numInsertingTestPostUser = 0;
		}
		if (insertPostCallback != null)
			insertPostCallback.returnInsertPost(postID, testPostUser);
	}

	@Override
	public void returnInsertCachedSpotifyTrack(Long trackID, int position, CachedSpotifyTrack cachedSpotifyTrack) {
		cachedSpotifyTracks.add(cachedSpotifyTrack);
		if (insertCachedSpotifyTrackCallback != null)
			insertCachedSpotifyTrackCallback.returnInsertCachedSpotifyTrackCallback(trackID, cachedSpotifyTrack);
	}

}
