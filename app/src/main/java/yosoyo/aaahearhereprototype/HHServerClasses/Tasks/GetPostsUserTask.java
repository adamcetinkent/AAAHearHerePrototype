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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
class GetPostsUserTask extends AsyncTask<Void, Void, List<HHPostFullProcess>> {
	private static final String TAG = "GetPostsTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/by/%1$d/for/%2$d/";
	private static final String VM_SERVER_ADDRESS_BEFORE = WebHelper.SERVER_IP + "/posts/by/%1$d/for/%2$d/before/%3$s";

	public interface Callback {
		void returnPosts(List<HHPostFullProcess> postsToProcess);
	}

	private final long userID;
	private final Timestamp beforeTime;
	private final Callback callbackTo;

	public GetPostsUserTask(final long userID,
							final Timestamp beforeTime,
							final Callback callbackTo) {
		this.userID = userID;
		this.callbackTo = callbackTo;
		this.beforeTime = beforeTime;
	}

	@Override
	protected List<HHPostFullProcess> doInBackground(Void... params) {
		String urlString;
		if (beforeTime != null) {
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS_BEFORE,
									  userID,
									  HHUser.getCurrentUserID(),
									  beforeTime.toString());
			urlString = urlString.replace(" ", "%20");
		} else {
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS,
									  userID,
									  HHUser.getCurrentUserID());
		}
		Log.d(TAG, "Fetching Posts from " + urlString);
		try {
			URL url = new URL(urlString);
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
