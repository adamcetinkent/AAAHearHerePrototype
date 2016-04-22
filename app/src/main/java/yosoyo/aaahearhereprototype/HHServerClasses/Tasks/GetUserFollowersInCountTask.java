package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

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
 *
 * Requests from the server the number of followers of the given user
 */
class GetUserFollowersInCountTask extends AsyncTask<Void, Void, Integer> {
	private static final String TAG = "GetUserFollowersInCount";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/follows/count/in/";

	public interface Callback {
		void returnUserFollowersInCount(int postCount);
	}

	private final long userID;
	private final Callback callbackTo;

	public GetUserFollowersInCountTask(long userID, Callback callbackTo) {
		this.userID = userID;
		this.callbackTo = callbackTo;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		Log.d(TAG, "Fetching Followers In Count by " + VM_SERVER_ADDRESS + userID);
		try {
			URL url = new URL(VM_SERVER_ADDRESS + userID);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				Integer postCount = new Gson().fromJson(streamString, Integer.class);
				return postCount;
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
	protected void onPostExecute(Integer result) {
		callbackTo.returnUserFollowersInCount(result);	// sends results back
	}

}
