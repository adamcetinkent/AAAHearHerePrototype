package yosoyo.aaahearhereprototype.TestServerClasses.Tasks;

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
public class TestAuthenticateUserTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "TestAuthenticateUser";
	public static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/auth/";

	// Interface for classes wanting to incorporate this class to download user info asynchronously
	public interface TestAuthenticateUserTaskCallback {
		void returnAuthenticationResult(boolean result);
	}

	private TestAuthenticateUserTaskCallback callbackTo;
	private String tokenID;

	public TestAuthenticateUserTask(TestAuthenticateUserTaskCallback callbackTo, String tokenID) {
		this.callbackTo = callbackTo;
		this.tokenID = tokenID;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
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

				String json = new Gson().toJson(tokenID, String.class);

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(json);
				out.close();

				int httpResult = urlConnection.getResponseCode();

				if (httpResult == HttpURLConnection.HTTP_OK){

					return true;

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

		return false;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(Boolean result) {
		callbackTo.returnAuthenticationResult(result);	// sends results back
	}

}
