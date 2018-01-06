package com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAPIResponse;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyToken;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Adam Kent on 08/02/2016.
 *
 * Asynchronously performs API request from Spotify.
 * Response is returned to processFinish function of specified Callback.
 */
@SuppressWarnings("unused")
public class SpotifyAPIRequestSearch extends AsyncTask<Void, Void, SpotifyAPIResponse> {
	private static final String TAG = "SpotifyAPIRequestSearch";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface Callback {
		void returnSpotifySearchResults(SpotifyAPIResponse output);
	}

	private static final String URL_TEMPLATE = "https://api.spotify.com/v1/search?q=%1$s&type=%2$s&offset=%3$d";
	public static final String SEARCH_TYPE_TRACK = "track";
	public static final String SEARCH_TYPE_ARTIST = "artist";
	public static final String SEARCH_TYPE_ALBUM = "album";

	private final Callback callbackTo;
	private final String query;
	private final String searchType;
	private final int offset;

	public SpotifyAPIRequestSearch(String query, String searchType, int offset, Callback callbackTo){
		this.query = query;
		this.callbackTo = callbackTo;
		this.searchType = searchType;
		this.offset = offset;
	}

	@Override
	// The actual process which makes the HTTP request
	protected SpotifyAPIResponse doInBackground(Void... params) {
		String urlString = String.format(Locale.ENGLISH,
										 URL_TEMPLATE,
										 ZZZUtility.formatURL(query),
										 searchType,
										 offset);
		Log.d(TAG, "Searching Spotify: "+urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setRequestProperty("Authorization", SpotifyToken.getAuthorisation());
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
		callbackTo.returnSpotifySearchResults(result);	// sends results back
	}

}
