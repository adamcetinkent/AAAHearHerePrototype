package yosoyo.aaahearhereprototype;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Adam Kent on 08/02/2016.
 */
public class HTTPThread extends AsyncTask<String, Void, String> {

	public interface AsyncResponse {
		void processFinish(String output);
	}

	public AsyncResponse delegate = null;
	public String tag = "HTTPThread";

	public HTTPThread(AsyncResponse delegate){
		Log.d(tag, "CREATED");
		this.delegate = delegate;
	}

	@Override
	protected void onPreExecute(){
		Log.d(tag, "onPreExecute");
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	@Override
	protected String doInBackground(String... strings) {
		Log.d(tag, "BEGIN URL STUFF");
		try {
			URL url = new URL("https://api.spotify.com/v1/search?q=leon%20bridges&type=artist");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = convertStreamToString(in);
				Log.d("STREAM:", streamString);
				return streamString;
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
	protected void onPostExecute(String result) {
		Log.d(tag, "onPostExecute");
		delegate.processFinish(result);
	}
}
