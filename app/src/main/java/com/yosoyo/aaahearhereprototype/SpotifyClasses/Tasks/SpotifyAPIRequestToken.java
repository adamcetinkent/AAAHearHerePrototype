package com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyToken;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTokenRaw;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Adam Kent on 08/02/2016.
 *
 * Asynchronously performs API request from Spotify for a track.
 * Response is returned to processFinish function of specified Callback.
 */
@SuppressWarnings("unused")
public class SpotifyAPIRequestToken extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "SpotifyAPIRequestToken";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface Callback {
		void returnSpotifyToken(boolean success, SpotifyToken token);
	}

	private static final String urlSpotifyToken = "https://accounts.spotify.com/api/token?grant_type=client_credentials";

	private final Callback callback;
	private final Context context;
	private static SpotifyToken token;

	public SpotifyAPIRequestToken(Callback callback, Context context){
		this.callback = callback;
		this.context = context;
	}

	@Override
	protected void onPreExecute(){

	}

	@Override
	// The actual process which makes the HTTP request
	protected Boolean doInBackground(Void... params) {
		try {
			URL url = new URL(urlSpotifyToken);
			if (url == null)
				return null;
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {

				String encoded = "Basic " + Base64.encodeToString((
					context.getString(R.string.SpotifyClientID) +
						":" +
						context.getString(R.string.SpotifyClientSecret)).getBytes(), Base64.NO_WRAP);

				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("Authorization", encoded);
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Accept", "application/json");

				int httpResult = urlConnection.getResponseCode();

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				token = new SpotifyToken(new Gson().fromJson(streamString, SpotifyTokenRaw.class));
				return token != null;
			} finally {
				urlConnection.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(Boolean result) {
		callback.returnSpotifyToken(result, token);	// sends results back
	}

}
