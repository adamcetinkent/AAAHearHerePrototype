package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by adam on 18/02/16.
 *
 * Requests the posts from the server that fall within the given bounds
 */
class GetPostsWithinBoundsTask extends AsyncTask<Void, Void, List<HHPostFullProcess>> {
	private static final String TAG = "GetPostsWithinBounds";
	//private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/for/%1$d/within/%2$.6f/%3$.6f/%4$.6f/%5$.6f";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/for/%1$d/within/%2$.6f/%3$.6f/%4$.6f/%5$.6f/%6$s";

	public interface Callback {
		void returnPostsWithinBounds(List<HHPostFullProcess> posts);
	}

	private final LatLngBounds bounds;
	private final long userID;
	private final Long[] excludeIDs;
	private final Callback callbackTo;

	public GetPostsWithinBoundsTask(final LatLngBounds bounds,
									final long userID,
									final Long[] excludeIDs,
									final Callback callbackTo) {
		this.bounds = bounds;
		this.userID = userID;
		this.excludeIDs = excludeIDs;
		this.callbackTo = callbackTo;
	}

	@Override
	protected List<HHPostFullProcess> doInBackground(Void... params) {
		String urlString;
		urlString = String.format(Locale.ENGLISH,
								  VM_SERVER_ADDRESS,
								  userID,
								  bounds.southwest.latitude,
								  bounds.northeast.latitude,
								  bounds.southwest.longitude,
								  bounds.northeast.longitude,
								  ZZZUtility.formatURL(excludeIDs));
		Log.d(TAG, "Fetching Posts from " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Token token="+ HHUser.getAuthorisationToken());
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHPostFullNested[] posts = new Gson().fromJson(streamString,HHPostFullNested[].class);
				List<HHPostFullProcess> postsFull = new ArrayList<>();
				for (HHPostFullNested post : posts){
					postsFull.add(new HHPostFullProcess(post));
				}
				return postsFull;
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
		callbackTo.returnPostsWithinBounds(result);	// sends results back
	}

}
