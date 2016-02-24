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
public class TestGetUserTask extends AsyncTask<Integer, Void, TestUser> {
	private static final String TAG = "TestGetUserTask";
	private static final String VM_SERVER_ADDRESS = "http://10.0.1.79:3000/users/";
	//private static final String VM_SERVER_ADDRESS = "http://10.72.150.66:3000/users/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface TestGetUserTaskCallback {
		void returnTestUser(TestUser testUser);
	}

	private TestGetUserTaskCallback callbackTo;
	private long id;

	public TestGetUserTask(TestGetUserTaskCallback callbackTo, long id) {
		this.callbackTo = callbackTo;
		this.id = id;
	}

	@Override
	protected TestUser doInBackground(Integer... params) {
		Log.d(TAG, "Fetching user from " + VM_SERVER_ADDRESS + id);
		try {
			URL url = new URL(VM_SERVER_ADDRESS + id);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				return new Gson().fromJson(streamString, TestUser.class);
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
	protected void onPostExecute(TestUser result) {
		callbackTo.returnTestUser(result);	// sends results back
	}

}
