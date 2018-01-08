package com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
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
 * Asynchronously performs API request from Spotify for a track.
 * Response is returned to processFinish function of specified Callback.
 */
public class SpotifyAPIRequestTrack extends AsyncTask<Void, Void, SpotifyTrack> {
	private static final String TAG = "SpotifyAPIRequestTrack";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface Callback {
		void returnSpotifyTrack(SpotifyTrack spotifyTrack);
	}

	private static final String urlSpotifySearch = "https://api.spotify.com/v1/tracks/";

	private final Callback callback;
	private final String trackID;
	private final String authorisation;

	public SpotifyAPIRequestTrack(String trackID, String authorisation, Callback callback){
		this.callback = callback;
		this.authorisation = authorisation;
		this.trackID = trackID;
	}

	// Construct Spotify API URL from input string
	private static URL makeSpotifyQuery(String spotifyTrack){
		try {
			return new URL(urlSpotifySearch + spotifyTrack);
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
	protected SpotifyTrack doInBackground(Void... params) {
		try {
			URL url = makeSpotifyQuery(trackID);
			if (url == null)
				return null;
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setRequestProperty("Authorization", authorisation);

				int httpResult = urlConnection.getResponseCode();

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				return new Gson().fromJson(streamString, SpotifyTrack.class);
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
	protected void onPostExecute(SpotifyTrack result) {
		callback.returnSpotifyTrack(result);	// sends results back
	}

}
