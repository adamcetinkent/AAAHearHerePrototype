package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;

/**
 * Created by adam on 18/02/16.
 *
 * Posts the acceptance of a {@link HHFollowRequestUser} to the server
 */
class PostAcceptFollowRequestTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "AcceptFollowRequestTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/follows/accept/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnPostAcceptFollowRequest(Boolean success);
	}

	private final Callback callbackTo;
	private final HHFollowRequestUser followRequest;

	public PostAcceptFollowRequestTask(HHFollowRequestUser followRequest, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.followRequest = followRequest;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Posting Follow Request to " + VM_SERVER_ADDRESS + followRequest.getFollowRequest().getID());
		try {
			URL url = new URL(VM_SERVER_ADDRESS + followRequest.getFollowRequest().getID());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){
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
		callbackTo.returnPostAcceptFollowRequest(result);	// sends results back
	}

}
