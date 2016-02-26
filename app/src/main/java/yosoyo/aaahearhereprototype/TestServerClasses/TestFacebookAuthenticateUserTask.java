package yosoyo.aaahearhereprototype.TestServerClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class TestFacebookAuthenticateUserTask extends AsyncTask<Void, Void, Integer> {
	private static final String TAG = "FBAuthenticateUser";
	public static final String VM_SERVER_ADDRESS = "http://10.0.1.79:3000/auth/";
	//private static final String VM_SERVER_ADDRESS = "http://10.72.150.66:3000/users/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface TestFacebookAuthenticateUserTaskCallback {
		void returnAuthenticationResult(Integer result);
	}

	private TestFacebookAuthenticateUserTaskCallback callbackTo;
	private AccessToken accessToken;

	public TestFacebookAuthenticateUserTask(TestFacebookAuthenticateUserTaskCallback callbackTo, AccessToken accessToken) {
		this.callbackTo = callbackTo;
		this.accessToken = accessToken;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		Log.d(TAG, "Authenticating user from " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(accessToken, AccessToken.class);

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(json);
				out.close();

				int httpResult = urlConnection.getResponseCode();

				String httpResponse = urlConnection.getResponseMessage();

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String httpResponseStream = ZZZUtility.convertStreamToString(in);

				if (httpResult == HttpURLConnection.HTTP_OK){

					Log.d(TAG, httpResponseStream);
					return httpResult;

				} else if (httpResult == HttpURLConnection.HTTP_ACCEPTED) {

					return httpResult;

				} else {
					Log.e(TAG, "HTTP ERROR! " + httpResult);
				}
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return HttpURLConnection.HTTP_NOT_ACCEPTABLE;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(Integer result) {
		callbackTo.returnAuthenticationResult(result);	// sends results back
	}

}
