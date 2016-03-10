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

import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class GetUserTask extends AsyncTask<Integer, Void, HHUser> {
	private static final String TAG = "GetUserTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "users/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface GetUserTaskCallback {
		void returnUser(HHUser user);
	}

	private GetUserTaskCallback callbackTo;
	private long id;

	public GetUserTask(GetUserTaskCallback callbackTo, long id) {
		this.callbackTo = callbackTo;
		this.id = id;
	}

	@Override
	protected HHUser doInBackground(Integer... params) {
		Log.d(TAG, "Fetching User from " + VM_SERVER_ADDRESS + id);
		try {
			URL url = new URL(VM_SERVER_ADDRESS + id);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				return new Gson().fromJson(streamString, HHUser.class);
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
	protected void onPostExecute(HHUser result) {
		callbackTo.returnUser(result);	// sends results back
	}

}
