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

import yosoyo.aaahearhereprototype.TestServerClasses.TestLike;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestCreateLikeTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "TestCreateLikeTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/likes/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface TestCreateLikeTaskCallback {
		void returnResultCreateLike(Boolean success, TestLike testLike);
	}

	private TestCreateLikeTaskCallback callbackTo;
	private TestLike testLike;
	private TestLike testLikeReturned;

	public TestCreateLikeTask(TestLike testLike, TestCreateLikeTaskCallback callbackTo) {
		this.callbackTo = callbackTo;
		this.testLike = testLike;
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
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(testLike, TestLike.class);
				String jsonplus = "{\"like\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					testLikeReturned = new Gson().fromJson(inString, TestLike.class);

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
		callbackTo.returnResultCreateLike(result, testLikeReturned);	// sends results back
	}

}
