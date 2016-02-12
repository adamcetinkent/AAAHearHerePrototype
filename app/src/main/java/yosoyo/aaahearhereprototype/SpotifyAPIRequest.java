package yosoyo.aaahearhereprototype;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAPIResponse;

/**
 * Created by Adam Kent on 08/02/2016.
 *
 * Asynchronously performs API request from Spotify.
 * Response is returned to processFinish function of specified SpotifyAPIRequestCallback.
 */
public class SpotifyAPIRequest extends AsyncTask<String, Void, SpotifyAPIResponse> {
	private static final String TAG = "SpotifyAPIRequest";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface SpotifyAPIRequestCallback {
		void processFinish(SpotifyAPIResponse output);
	}

	private static final String urlSpotifySearch = "https://api.spotify.com/v1/search?q=";
	private static final String urlSpotifyType = "&type=";

	private SpotifyAPIRequestCallback callbackTo = null;
	private String searchType;

	public SpotifyAPIRequest(SpotifyAPIRequestCallback callbackTo, String searchType){
		this.callbackTo = callbackTo;
		this.searchType = searchType;
	}

	// Construct Spotify API URL from input string
	private static URL makeSpotifyQuery(String query, String type){
		try {
			return new URL(urlSpotifySearch + query.replace(" ","%20") + urlSpotifyType + type);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPreExecute(){

	}

	@Override
	// The actual process which makes the HTTP request
	protected SpotifyAPIResponse doInBackground(String... strings) {
		try {
			URL url = makeSpotifyQuery(strings[0], this.searchType);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				return new Gson().fromJson(streamString, SpotifyAPIResponse.class);
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
	protected void onPostExecute(SpotifyAPIResponse result) {
		callbackTo.processFinish(result);	// sends results back
	}

}
