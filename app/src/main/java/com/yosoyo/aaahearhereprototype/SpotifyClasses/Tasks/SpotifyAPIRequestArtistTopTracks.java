package com.yosoyo.aaahearhereprototype.SpotifyClasses.Tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Adam Kent on 08/02/2016.
 *
 * Asynchronously performs API request from Spotify for an artist.
 * Response is returned to processFinish function of specified Callback.
 */
@SuppressWarnings("unused")
public class SpotifyAPIRequestArtistTopTracks extends AsyncTask<Void, Void, List<SpotifyTrack>> {
	private static final String TAG = "SpotifyAPIRequestArtistTopTracks";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface Callback {
		void returnGetSpotifyArtistTopTracks(List<SpotifyTrack> spotifyTracks);
	}

	private static final String urlSpotifySearch = "https://api.spotify.com/v1/artists/";

	private final String artistID;
	private final String country;
	private final Callback callback;

	public SpotifyAPIRequestArtistTopTracks(String artistID, String country, Callback callback){
		this.artistID = artistID;
		this.country = country;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute(){

	}

	@Override
	// The actual process which makes the HTTP request
	protected List<SpotifyTrack> doInBackground(Void... params) {
		String urlString = ""; // TURNS OUT THIS REQUIRES Spotify OAUTH...
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				SpotifyTrack[] spotifyTracks = new Gson().fromJson(streamString, SpotifyTrack[].class);
				return new ArrayList<>(Arrays.asList(spotifyTracks));
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
	protected void onPostExecute(List<SpotifyTrack> spotifyTracks) {
		callback.returnGetSpotifyArtistTopTracks(spotifyTracks);	// sends results back
	}

}
