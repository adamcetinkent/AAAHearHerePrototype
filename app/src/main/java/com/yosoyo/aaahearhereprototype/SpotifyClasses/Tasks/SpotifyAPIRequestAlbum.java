package com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
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
 * Asynchronously performs API request from Spotify for an album.
 * Response is returned to processFinish function of specified Callback.
 */
@SuppressWarnings("unused")
public class SpotifyAPIRequestAlbum extends AsyncTask<Void, Void, SpotifyAlbum> {
	private static final String TAG = "SpotifyAPIRequestAlbum";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface Callback {
		void returnSpotifyAlbum(SpotifyAlbum spotifyAlbum);
	}

	private static final String urlSpotifySearch = "https://api.spotify.com/v1/albums/";

	private final Callback callback;
	private final String albumID;

	public SpotifyAPIRequestAlbum(String albumID, Callback callback){
		this.callback = callback;
		this.albumID = albumID;
	}

	// Construct Spotify API URL from input string
	private static URL makeSpotifyQuery(String spotifyAlbum){
		try {
			return new URL(urlSpotifySearch + spotifyAlbum);
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
	protected SpotifyAlbum doInBackground(Void... params) {
		try {
			URL url = makeSpotifyQuery(albumID);
			if (url == null)
				return null;
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setRequestProperty("Authorization", SpotifyToken.getAuthorisation());
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				return new Gson().fromJson(streamString, SpotifyAlbum.class);
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
	protected void onPostExecute(SpotifyAlbum result) {
		callback.returnSpotifyAlbum(result);	// sends results back
	}

}
