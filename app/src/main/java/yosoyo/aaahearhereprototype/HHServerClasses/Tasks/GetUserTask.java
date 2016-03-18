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

import yosoyo.aaahearhereprototype.HHServerClasses.HHUserFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFullNested;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class GetUserTask extends AsyncTask<Integer, Void, Boolean> {
	private static final String TAG = "GetUserTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "users/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface Callback {
		void returnUser(boolean success, HHUserFullProcess user);
	}

	private final Callback callbackTo;
	private final long id;
	private HHUserFullProcess user;

	public GetUserTask(long id, Callback callbackTo) {
		this.id = id;
		this.callbackTo = callbackTo;
	}

	@Override
	protected Boolean doInBackground(Integer... params) {
		Log.d(TAG, "Fetching User from " + VM_SERVER_ADDRESS + id);
		try {
			URL url = new URL(VM_SERVER_ADDRESS + id);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				int httpResult = urlConnection.getResponseCode();

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String httpResponseStream = ZZZUtility.convertStreamToString(in);

				if (httpResult == HttpURLConnection.HTTP_OK) {

					Log.d(TAG, httpResponseStream);
					user = new HHUserFullProcess(
						new Gson().fromJson(httpResponseStream, HHUserFullNested.class));
					return true;
				} else {
					Log.e(TAG, "HTTP ERROR! " + httpResult);
				}

				return false;
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(Boolean result) {
		callbackTo.returnUser(result, user);	// sends results back
	}

}
