package yosoyo.aaahearhereprototype;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyPagingArtist;

/**
 * Created by Adam Kent on 08/02/2016.
 */
public class AsyncArtist extends AsyncTask<String, Void, SpotifyArtist[]> {

	public interface AsyncResponse {
		//void processFinish(String output);
		void processFinish(SpotifyArtist[] output);
	}

	public AsyncResponse delegate = null;
	public String tag = "HTTPThread";

	public AsyncArtist(AsyncResponse delegate){
		Log.d(tag, "CREATED");
		this.delegate = delegate;
	}

	@Override
	protected void onPreExecute(){
		Log.d(tag, "onPreExecute");
	}

	static String convertStreamToString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	@Override
	protected SpotifyArtist[] doInBackground(String... strings) {
		Log.d(tag, "BEGIN URL STUFF");
		try {
			URL url = new URL("https://api.spotify.com/v1/search?q=leon%20bridges&type=artist");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = convertStreamToString(in);
				Log.d("STREAM:", streamString);
				//return streamString;
				final Gson gson = new Gson();
				String testString = gson.toJson(new SpotifyPagingArtist());
				String skipBeginning = streamString.substring(16);
				String skipEnd = skipBeginning.substring(0, skipBeginning.length()-1).trim();
				SpotifyPagingArtist paging = gson.fromJson(skipEnd, SpotifyPagingArtist.class);
				//SpotifyArtist spotifyArtist = paging.items[0];
				return paging.items;
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(SpotifyArtist[] result) {
		Log.d(tag, "onPostExecute");
		delegate.processFinish(result);
	}
}
