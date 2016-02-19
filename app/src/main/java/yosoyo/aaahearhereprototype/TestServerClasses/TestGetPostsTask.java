package yosoyo.aaahearhereprototype.TestServerClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestGetPostsTask extends AsyncTask<Integer, Void, TestPost[]> {
	private static final String TAG = "TestGetPostsTask";
	private static final String VM_SERVER_ADDRESS = "http://10.0.1.79:3000/posts/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface TestGetPostsTaskCallback {
		void processFinish(TestPost[] testPost);
	}

	private TestGetPostsTaskCallback callbackTo;
	//private long id;

	public TestGetPostsTask(TestGetPostsTaskCallback callbackTo/*, long id*/) {
		this.callbackTo = callbackTo;
		//this.id = id;
	}

	@Override
	protected TestPost[] doInBackground(Integer... params) {
		Log.d(TAG, "Fetching posts from " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				return new Gson().fromJson(streamString, TestPost[].class);
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
	protected void onPostExecute(TestPost[] result) {
		callbackTo.processFinish(result);	// sends results back
	}

}
