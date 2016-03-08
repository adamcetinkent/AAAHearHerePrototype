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

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestPostUserCommentsNested;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFullProcess;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestGetPostTask extends AsyncTask<Void, Void, TestPostFullProcess> {
	private static final String TAG = "TestGetPostsTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface TestGetPostTaskCallback {
		void returnTestPost(TestPostFullProcess testPost);
	}

	private TestGetPostTaskCallback callbackTo;
	private long post_id;

	public TestGetPostTask(long post_id, TestGetPostTaskCallback callbackTo) {
		this.callbackTo = callbackTo;
		this.post_id = post_id;
	}

	@Override
	protected TestPostFullProcess doInBackground(Void... params) {
		Log.d(TAG, "Fetching post from " + VM_SERVER_ADDRESS + post_id);
		try {
			URL url = new URL(VM_SERVER_ADDRESS + post_id);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				TestPostUserCommentsNested testPostNested = new Gson().fromJson(streamString, TestPostUserCommentsNested.class);
				TestPostFullProcess testPost = new TestPostFullProcess(testPostNested);
				return testPost;
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
	protected void onPostExecute(TestPostFullProcess result) {
		callbackTo.returnTestPost(result);	// sends results back
	}

}
