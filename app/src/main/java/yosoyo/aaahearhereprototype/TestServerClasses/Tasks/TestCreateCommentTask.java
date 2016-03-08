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

import yosoyo.aaahearhereprototype.TestServerClasses.TestComment;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestCreateCommentTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "TestCreateCommentTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/comments/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface TestCreateCommentTaskCallback {
		void returnResultCreateComment(Boolean success, TestComment testComment);
	}

	private TestCreateCommentTaskCallback callbackTo;
	private TestComment testComment;
	private TestComment testCommentReturned;

	public TestCreateCommentTask(TestComment testComment, TestCreateCommentTaskCallback callbackTo) {
		this.callbackTo = callbackTo;
		this.testComment = testComment;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Posting comment to " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(testComment, TestComment.class);
				String jsonplus = "{\"comment\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					testCommentReturned = new Gson().fromJson(inString, TestComment.class);

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
		callbackTo.returnResultCreateComment(result, testCommentReturned);	// sends results back
	}

}