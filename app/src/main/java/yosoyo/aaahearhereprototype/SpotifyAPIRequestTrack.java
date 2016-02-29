package yosoyo.aaahearhereprototype;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;
import yosoyo.aaahearhereprototype.TestServerClasses.TestPostUser;

/**
 * Created by Adam Kent on 08/02/2016.
 *
 * Asynchronously performs API request from Spotify.
 * Response is returned to processFinish function of specified SpotifyAPIRequestCallback.
 */
public class SpotifyAPIRequestTrack extends AsyncTask<String, Void, SpotifyTrack> {
	private static final String TAG = "SpotifyAPIRequestTrack";

	// Interface for classes wanting to incorporate this class to make Spotify API Requests
	public interface SpotifyAPIRequestTrackCallback {
		void returnSpotifyTrack(SpotifyTrack spotifyTrack, int position, TestPostUser testPostUser);
	}

	private static final String urlSpotifySearch = "https://api.spotify.com/v1/tracks/";
	//private static final String urlSpotifyType = "&type=";

	private SpotifyAPIRequestTrackCallback callbackTo = null;
	private int position;
	private TestPostUser testPostUser;

	public SpotifyAPIRequestTrack(SpotifyAPIRequestTrackCallback callbackTo, int position){
		this.callbackTo = callbackTo;
		this.position = position;
	}

	public SpotifyAPIRequestTrack(SpotifyAPIRequestTrackCallback callbackTo, TestPostUser testPostUser){
		this.callbackTo = callbackTo;
		this.testPostUser = testPostUser;
		this.position = -1;
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
	protected SpotifyTrack doInBackground(String... spotifyTracks) {
		try {
			URL url = makeSpotifyQuery(spotifyTracks[0]);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
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
		callbackTo.returnSpotifyTrack(result, position, testPostUser);	// sends results back
	}

}
