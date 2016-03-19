package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowUser;

/**
 * Created by adam on 18/02/16.
 */
class DeleteFollowTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "DeleteFollowTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/follows/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnDeleteFollow(Boolean success);
	}

	private final Callback callbackTo;
	private final HHFollowUser follow;

	public DeleteFollowTask(HHFollowUser follow, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.follow = follow;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Deleting Follow to " + VM_SERVER_ADDRESS + follow.getFollow().getID());
		try {
			URL url = new URL(VM_SERVER_ADDRESS + follow.getFollow().getID());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("DELETE");

				/*String json = new Gson().toJson(follow, HHLike.class);
				String jsonplus = "{\"follow\": "+json+"}";

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
		callbackTo.returnDeleteFollow(result);	// sends results back
	}

}
