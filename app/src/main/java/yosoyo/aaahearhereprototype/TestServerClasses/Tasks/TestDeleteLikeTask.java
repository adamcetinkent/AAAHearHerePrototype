package yosoyo.aaahearhereprototype.TestServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;

/**
 * Created by adam on 18/02/16.
 */
public class TestDeleteLikeTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "TestDeleteLikeTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/likes/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface TestDeleteLikeTaskCallback {
		void returnResultDeleteLike(Boolean success);
	}

	private TestDeleteLikeTaskCallback callbackTo;
	private TestLike testLike;

	public TestDeleteLikeTask(TestLike testLike, TestDeleteLikeTaskCallback callbackTo) {
		this.callbackTo = callbackTo;
		this.testLike = testLike;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Deleting like to " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS + testLike.getID());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("DELETE");

				/*String json = new Gson().toJson(testLike, TestLike.class);
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
		callbackTo.returnResultDeleteLike(result);	// sends results back
	}

}
