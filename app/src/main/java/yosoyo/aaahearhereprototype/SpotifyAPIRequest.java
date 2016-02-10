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
	private static final String tag = "SpotifyAPIRequest";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface SpotifyAPIRequestCallback {
		void processFinish(SpotifyAPIResponse output);
	}

	private SpotifyAPIRequestCallback callbackTo = null;
	private String searchType;

	public SpotifyAPIRequest(SpotifyAPIRequestCallback callbackTo, String searchType){
		this.callbackTo = callbackTo;
		this.searchType = searchType;
	}

	@Override
	protected void onPreExecute(){

	}

	// TODO: Move somewhere more useful
	static String convertStreamToString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	// TODO: Expand and move somewhere more useful
	static URL urlify(String query, String type){
		try {
			return new URL("https://api.spotify.com/v1/search?q=" + query.replace(" ","%20") + "&type="+type);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	// The actual process which makes the HTTP request
	protected SpotifyAPIResponse doInBackground(String... strings) {
		try {
			URL url = urlify(strings[0], this.searchType);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = convertStreamToString(in);
				SpotifyAPIResponse results = new Gson().fromJson(streamString, SpotifyAPIResponse.class);
				return results;
			} finally {
				urlConnection.disconnect();
			}
		} catch (IOException e) {
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
