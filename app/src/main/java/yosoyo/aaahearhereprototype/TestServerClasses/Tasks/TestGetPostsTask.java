package yosoyo.aaahearhereprototype.TestServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestPostUserCommentsNested;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestGetPostsTask extends AsyncTask<Void, Void, List<TestPostFullProcess>> {
	private static final String TAG = "TestGetPostsTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface TestGetPostsTaskCallback {
		void returnTestPosts(List<TestPostFullProcess> testPosts);
	}

	private TestGetPostsTaskCallback callbackTo;
	//private long id;

	public TestGetPostsTask(TestGetPostsTaskCallback callbackTo/*, long id*/) {
		this.callbackTo = callbackTo;
		//this.id = id;
	}

	@Override
	protected List<TestPostFullProcess> doInBackground(Void... params) {
		Log.d(TAG, "Fetching posts from " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				TestPostUserCommentsNested[] testPostsNested = new Gson().fromJson(streamString, TestPostUserCommentsNested[].class);
				List<TestPostFullProcess> testPosts = new ArrayList<>(testPostsNested.length);
				for (int i = 0; i < testPostsNested.length; i++){
					testPosts.add(new TestPostFullProcess(testPostsNested[i]));
				}
				return testPosts;
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(List<TestPostFullProcess> result) {
		callbackTo.returnTestPosts(result);	// sends results back
	}

}
