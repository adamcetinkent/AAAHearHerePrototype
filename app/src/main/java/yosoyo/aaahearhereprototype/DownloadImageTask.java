package yosoyo.aaahearhereprototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Adam Kent on 10/02/2016.
 */
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	public String tag = "DownloadImageTask";
	public AsyncResponse callbackTo = null;
	public int position;
	ImageView imageView;
	//Bitmap bitmapStore;

	public interface AsyncResponse {
		void processFinish(Bitmap result, int position);
	}

	public DownloadImageTask(ImageView imageView, /*Bitmap bitmapStore*/AsyncResponse callbackTo, int position) {
		this.callbackTo = callbackTo;
		this.position = position;
		this.imageView = imageView;
		//this.bitmapStore = bitmapStore;
	}

	protected Bitmap doInBackground(String... urls) {
		Log.d(tag, "Fetching image from " + urls[0]);
		try {
			URL url = new URL(urls[0]);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				Bitmap bitmap = null;
				BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
				bitmapFactoryOptions.inSampleSize = 1;

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				//InputStream in = new BufferedInputStream((InputStream) urlConnection.getContent());
				//InputStream in = (InputStream) urlConnection.getContent();
				//InputStream in = (InputStream) new URL(urls[0]).getContent();
				bitmap = BitmapFactory.decodeStream(in, null, bitmapFactoryOptions);
				if (bitmap != null)
					return bitmap;
				else
					Log.e(tag, "Empty bitmap!");
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


	protected void onPostExecute(Bitmap result) {
		imageView.setImageBitmap(result);
		//bitmapStore = result;
		//imageView = result;
		callbackTo.processFinish(result, position);
	}
}