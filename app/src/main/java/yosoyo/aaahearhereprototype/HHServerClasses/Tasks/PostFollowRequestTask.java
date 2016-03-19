package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequest;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowRequestUserNested;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowUserNested;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
class PostFollowRequestTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "PostFollowRequestTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/follows/request/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnPostFollowRequest(Boolean success, HHFollowRequestUser followRequest);
		void returnPostFollowRequestAccepted(Boolean success, HHFollowUser follow);
	}

	private final Callback callbackTo;
	private final HHFollowRequest followRequest;
	private HHFollowRequestUser followRequestReturned;
	private HHFollowUser followReturned;

	public PostFollowRequestTask(HHFollowRequest followRequest, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.followRequest = followRequest;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Posting Follow Request to " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(followRequest, HHFollowRequest.class);
				String jsonplus = "{\"follow_request\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					followRequestReturned = new HHFollowRequestUser(new Gson().fromJson(inString, HHFollowRequestUserNested.class));
					if (followRequestReturned.getFollowRequest().getRequestedUserID() <= 0){
						followReturned = new HHFollowUser(new Gson().fromJson(inString,HHFollowUserNested.class));
						if (followReturned != null) {
							return true;
						} else {
							return false;
						}
					}

					return true;

				} else {
					Log.e(TAG, "HTTP ERROR! " + httpResult);
				}

				return true;
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
		if (followReturned != null){
			callbackTo.returnPostFollowRequestAccepted(result, followReturned);
		} else {
			callbackTo.returnPostFollowRequest(result, followRequestReturned);    // sends results back
		}
	}

}
