package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHLikeUserNested;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by adam on 18/02/16.
 *
 * Posts a new {@link HHLike} to the server
 */
class PostLikeTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "PostLikeTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/likes/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnPostLike(Boolean success, HHLike like);
	}

	private final String authToken;
	private final Callback callbackTo;
	private final HHLike like;
	private HHLike likeReturned;

	public PostLikeTask(final String authToken, final HHLike like, final Callback callbackTo) {
		this.authToken = authToken;
		this.callbackTo = callbackTo;
		this.like = like;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Posting like to " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestProperty("Authorization", "Token token="+ authToken);
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(like, HHLike.class);
				String jsonplus = "{\"like\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					likeReturned = new Gson().fromJson(inString, HHLikeUserNested.class);

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
		callbackTo.returnPostLike(result, likeReturned);	// sends results back
	}

}
