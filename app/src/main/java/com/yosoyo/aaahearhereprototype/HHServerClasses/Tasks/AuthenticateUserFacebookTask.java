package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUserFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHUserFullNested;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by adam on 18/02/16.
 *
 * Sends an access token to the Facebook API. The response is sent back via the {@link Callback} interface.
 */
public class AuthenticateUserFacebookTask extends AsyncTask<Void, Void, Integer> {
	private static final String TAG = "FBAuthenticateUser";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/auth/";

	public interface Callback {
		void returnAuthenticationResult(Integer result, HHUserFullProcess user, String HHAuthToken);
	}

	private final Callback callbackTo;
	private final AccessToken accessToken;
	private HHUserFullProcess user;
	private String HHAuthToken;

	public AuthenticateUserFacebookTask(AccessToken accessToken, Callback callbackTo) {
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

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String httpResponseStream = ZZZUtility.convertStreamToString(in);

				if (httpResult == HttpURLConnection.HTTP_OK){

					Log.d(TAG, httpResponseStream);
					user = new HHUserFullProcess(new Gson().fromJson(httpResponseStream, HHUserFullNested.class));
					HHAuthToken = urlConnection.getHeaderField("Authorization");
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
		callbackTo.returnAuthenticationResult(result, user, HHAuthToken);	// sends results back
	}

}
