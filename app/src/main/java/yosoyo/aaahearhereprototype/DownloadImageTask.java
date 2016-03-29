package yosoyo.aaahearhereprototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

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
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private static final String TAG = "DownloadImageTask";

	public static final String FACEBOOK_PROFILE_PHOTO = "https://graph.facebook.com/";
	public static final String FACEBOOK_PROFILE_PHOTO_SMALL = "/picture?type=small";
	public static final String FACEBOOK_PROFILE_PHOTO_NORMAL = "/picture?type=normal";
	public static final String FACEBOOK_PROFILE_PHOTO_LARGE = "/picture?type=large";

	private DownloadImageTaskCallback callbackTo = null;
	//private int position;
	//private ImageView imageView;
	//private Marker marker;

	// Interface for classes wanting to incorporate this class to download bitmaps asynchronously
	public interface DownloadImageTaskCallback {
		void returnDownloadedImage(Bitmap result/*, int position, Marker marker*/);
	}

	public DownloadImageTask(DownloadImageTaskCallback callbackTo){
		this.callbackTo = callbackTo;
	}

//	public DownloadImageTask(ImageView imageView, DownloadImageTaskCallback callbackTo) {
//		this.callbackTo = callbackTo;
//		this.imageView = imageView;
//	}

	/*public DownloadImageTask(ImageView imageView, DownloadImageTaskCallback callbackTo, int position) {
		this.callbackTo = callbackTo;
		this.position = position;
		this.imageView = imageView;
	}

	public DownloadImageTask(ImageView imageView, DownloadImageTaskCallback callbackTo, Marker marker) {
		this.callbackTo = callbackTo;
		this.imageView = imageView;
		this.marker = marker;
	}*/

	@Override
	// The actual process which downloads the bitmap;
	protected Bitmap doInBackground(String... urls) {
		Log.d(TAG, "Fetching image from " + urls[0]);
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
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
		callbackTo.returnDownloadedImage(result); // send result back for storage
	}
}