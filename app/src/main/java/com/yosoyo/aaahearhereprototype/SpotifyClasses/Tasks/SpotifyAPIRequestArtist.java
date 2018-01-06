package com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyToken;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Adam Kent on 08/02/2016.
 *
 * Asynchronously performs API request from Spotify for an artist.
 * Response is returned to processFinish function of specified Callback.
 */
@SuppressWarnings("unused")
public class SpotifyAPIRequestArtist extends AsyncTask<Void, Void, SpotifyArtist> {
	private static final String TAG = "SpotifyAPIRequestArtist";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface Callback {
		void returnSpotifyArtist(SpotifyArtist spotifyArtist);
	}

	private static final String urlSpotifySearch = "https://api.spotify.com/v1/artists/";

	private final Callback callback;
	private final String artistID;

	public SpotifyAPIRequestArtist(String artistID, Callback callback){
		this.callback = callback;
		this.artistID = artistID;
	}

	// Construct Spotify API URL from input string
	private static URL makeSpotifyQuery(String spotifyArtist){
		try {
			return new URL(urlSpotifySearch + spotifyArtist);
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
	protected SpotifyArtist doInBackground(Void... params) {
		try {
			URL url = makeSpotifyQuery(artistID);
			if (url == null)
				return null;
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setRequestProperty("Authorization", SpotifyToken.getAuthorisation());
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				return new Gson().fromJson(streamString, SpotifyArtist.class);
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
	protected void onPostExecute(SpotifyArtist result) {
		callback.returnSpotifyArtist(result);	// sends results back
	}

}
