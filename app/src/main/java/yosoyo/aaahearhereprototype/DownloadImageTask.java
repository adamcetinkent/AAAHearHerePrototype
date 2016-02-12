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
 *
 * Downloads a bitmap from a URL and sets imageView to display it.
 * callbackTo function receives resulting bitmap for storage.
 */
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private static final String TAG = "DownloadImageTask";

	private DownloadImageTaskCallback callbackTo = null;
	private int position;
	private ImageView imageView;

	// Interface for classes wanting to incorporate this class to download bitmaps asynchronously
	public interface DownloadImageTaskCallback {
		void processFinish(Bitmap result, int position);
	}

	public DownloadImageTask(ImageView imageView, DownloadImageTaskCallback callbackTo, int position) {
		this.callbackTo = callbackTo;
		this.position = position;
		this.imageView = imageView;
	}

	@Override
	// The actual process which downloads the bitmap;
	protected Bitmap doInBackground(String... urls) {
		Log.d(TAG, "Fetching image from " + urls[0]);
		try {
			URL url = new URL(urls[0]);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				Bitmap bitmap;
				BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
				bitmapFactoryOptions.inSampleSize = 1;

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				bitmap = BitmapFactory.decodeStream(in, null, bitmapFactoryOptions);
				if (bitmap != null)
					return bitmap;
				else
					Log.e(TAG, "Empty bitmap!");
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

	// Fires once doInBackground is completed
	protected void onPostExecute(Bitmap result) {
		imageView.setImageBitmap(result); // immediately update imageView
		callbackTo.processFinish(result, position); // send result back for storage
	}
}