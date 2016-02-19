package yosoyo.aaahearhereprototype.TestServerClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by adam on 18/02/16.
 */
public class TestCreatePostTask extends AsyncTask<TestPost, Void, Boolean> {
	private static final String TAG = "TestCreatePostTask";
	private static final String VM_SERVER_ADDRESS = "http://10.0.1.79:3000/posts/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface TestCreatePostTaskCallback {
		void processFinish(Boolean success);
	}

	private TestCreatePostTaskCallback callbackTo;
	private TestPost testPost;

	public TestCreatePostTask(TestCreatePostTaskCallback callbackTo, TestPost testPost) {
		this.callbackTo = callbackTo;
		this.testPost = testPost;
	}

	@Override
	protected Boolean doInBackground(TestPost... params) {
		Log.d(TAG, "Posting test post to " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(testPost, TestPost.class);
				String jsonplus = "{\"post\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){
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
		callbackTo.processFinish(result);	// sends results back
	}

}
