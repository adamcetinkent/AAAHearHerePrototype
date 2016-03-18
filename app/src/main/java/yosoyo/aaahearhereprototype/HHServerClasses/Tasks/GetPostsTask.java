package yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
class GetPostsTask extends AsyncTask<Void, Void, List<HHPostFullProcess>> {
	private static final String TAG = "GetPostsTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/for/";

	public interface Callback {
		void returnPosts(List<HHPostFullProcess> postsToProcess);
	}

	private final Callback callbackTo;

	public GetPostsTask(Callback callbackTo) {
		this.callbackTo = callbackTo;
	}

	@Override
	protected List<HHPostFullProcess> doInBackground(Void... params) {
		Log.d(TAG, "Fetching Posts from " + VM_SERVER_ADDRESS + HHUser.getCurrentUserID());
		try {
			URL url = new URL(VM_SERVER_ADDRESS + HHUser.getCurrentUserID());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHPostFullNested[] postsNested = new Gson().fromJson(streamString, HHPostFullNested[].class);
				List<HHPostFullProcess> posts = new ArrayList<>(postsNested.length);
				for (HHPostFullNested postNested : postsNested) {
					posts.add(new HHPostFullProcess(postNested));
				}
				return posts;
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
	// Fires once doInBackground is completed
	protected void onPostExecute(List<HHPostFullProcess> result) {
		callbackTo.returnPosts(result);	// sends results back
	}

}
