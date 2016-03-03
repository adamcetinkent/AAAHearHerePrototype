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

import yosoyo.aaahearhereprototype.TestServerClasses.TestUser;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestCreateUserTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "TestCreatePostTask";
	private static final String VM_SERVER_ADDRESS = "http://10.0.1.79:3000/users/";
	//private static final String VM_SERVER_ADDRESS = "http://10.72.150.66:3000/posts/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface TestCreateUserTaskCallback {
		void returnResultCreateUser(Boolean success, TestUser testUser);
	}

	private TestCreateUserTaskCallback callbackTo;
	private TestUser testUser;
	private TestUser testUserReturned;

	public TestCreateUserTask(TestCreateUserTaskCallback callbackTo, TestUser testUser) {
		this.callbackTo = callbackTo;
		this.testUser = testUser;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Posting test user to " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(testUser, TestUser.class);
				String jsonplus = "{\"user\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					testUserReturned = new Gson().fromJson(inString, TestUser.class);

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
		callbackTo.returnResultCreateUser(result, testUserReturned);	// sends results back
	}

}
