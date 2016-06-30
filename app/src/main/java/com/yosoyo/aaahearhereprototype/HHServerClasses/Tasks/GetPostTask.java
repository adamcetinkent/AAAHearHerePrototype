package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by adam on 18/02/16.
 *
 * Requests the post from the server with the given postID.
 */
class GetPostTask extends AsyncTask<Void, Void, HHPostFullProcess> {
	private static final String TAG = "GetPostsTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface Callback {
		void returnPost(HHPostFullProcess post);
	}

	private final Callback callbackTo;
	private final long post_id;

	public GetPostTask(long post_id, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.post_id = post_id;
	}

	@Override
	protected HHPostFullProcess doInBackground(Void... params) {
		Log.d(TAG, "Fetching Post from " + VM_SERVER_ADDRESS + post_id);
		try {
			URL url = new URL(VM_SERVER_ADDRESS + post_id);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Token token="+ HHUser.getAuthorisationToken());
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHPostFullNested postNested = new Gson().fromJson(streamString, HHPostFullNested.class);
				return new HHPostFullProcess(postNested);
			} catch (Exception e){
				return null;
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
	protected void onPostExecute(HHPostFullProcess result) {
		callbackTo.returnPost(result);	// sends results back
	}

}
