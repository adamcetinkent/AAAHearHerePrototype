package yosoyo.aaahearhereprototype.TestServerClasses.Tasks;

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

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.TaskReturns.TestPostUserCommentsNested;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPost;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostFull;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestCreatePostTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "TestCreatePostTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface TestCreatePostTaskCallback {
		void returnResultCreatePost(Boolean success, TestPostFull testPost);
	}

	private TestCreatePostTaskCallback callbackTo;
	private TestPost testPost;
	//private TestPostUser testPostUserReturned;
	private TestPostUserCommentsNested testPostReturned;

	public TestCreatePostTask(TestCreatePostTaskCallback callbackTo, TestPost testPost) {
		this.callbackTo = callbackTo;
		this.testPost = testPost;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
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

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					testPostReturned = new Gson().fromJson(inString, TestPostUserCommentsNested.class);

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
		callbackTo.returnResultCreatePost(result, new TestPostFull(testPostReturned));	// sends results back
	}

}
