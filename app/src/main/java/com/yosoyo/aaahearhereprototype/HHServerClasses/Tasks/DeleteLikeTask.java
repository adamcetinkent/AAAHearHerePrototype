package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHLike;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by adam on 18/02/16.
 *
 * Sends a {@link HHLike} to the server to be deleted
 */
class DeleteLikeTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "DeleteLikeTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/likes/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnDeleteLike(Boolean success);
	}

	private final Callback callbackTo;
	private final HHLike like;

	public DeleteLikeTask(HHLike like, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.like = like;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Deleting Like to " + VM_SERVER_ADDRESS + like.getID());
		try {
			URL url = new URL(VM_SERVER_ADDRESS + like.getID());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("DELETE");

				/*String json = new Gson().toJson(like, HHLike.class);
				String jsonplus = "{\"like\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();*/

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
		callbackTo.returnDeleteLike(result);	// sends results back
	}

}
