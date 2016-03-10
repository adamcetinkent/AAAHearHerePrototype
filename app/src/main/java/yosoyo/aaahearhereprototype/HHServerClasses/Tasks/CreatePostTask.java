package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import yosoyo.aaahearhereprototype.HHServerClasses.HHPost;
import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFull;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
public class CreatePostTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "CreatePostTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface CreatePostTaskCallback {
		void returnResultCreatePost(Boolean success, HHPostFull postReturned);
	}

	private CreatePostTaskCallback callbackTo;
	private HHPost post;
	private HHPostFullNested postReturned;

	public CreatePostTask(CreatePostTaskCallback callbackTo, HHPost post) {
		this.callbackTo = callbackTo;
		this.post = post;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "Posting Post to " + VM_SERVER_ADDRESS);
		try {
			URL url = new URL(VM_SERVER_ADDRESS);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				String json = new Gson().toJson(post, HHPost.class);
				String jsonplus = "{\"post\": "+json+"}";

				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				out.write(jsonplus);
				out.close();

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					postReturned = new Gson().fromJson(inString, HHPostFullNested.class);

					return true;
				} else {
					Log.e(TAG, "HTTP ERROR! " + httpResult);
				}

				return true;
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(Boolean result) {
		callbackTo.returnResultCreatePost(result, new HHPostFull(postReturned));	// sends results back
	}

}