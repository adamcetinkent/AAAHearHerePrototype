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
import java.util.Collections;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.ZZZUtility;

/**
 * Created by adam on 18/02/16.
 */
class SearchUsersTask extends AsyncTask<Void, Void, List<HHUser>> {
	private static final String TAG = "SearchUsersTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/users/for/";
	private static final String VM_SERVER_SUFFIX = "/search/";

	public interface Callback {
		void returnSearchUsers(List<HHUser> foundUsers);
	}

	private final Callback callbackTo;
	private final String query;

	public SearchUsersTask(final String query, final Callback callbackTo) {
		this.query = query;
		this.callbackTo = callbackTo;
	}

	// Construct Spotify API URL from input string
	private static URL makeUserQuery(String query){
		try {
			return new URL(VM_SERVER_ADDRESS + HHUser.getCurrentUserID() + VM_SERVER_SUFFIX + query.replace(" ","%20"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected List<HHUser> doInBackground(Void... params) {
		Log.d(TAG, "Fetching Users from " + VM_SERVER_ADDRESS + HHUser.getCurrentUserID() + VM_SERVER_SUFFIX + query);
		try {
			URL url = makeUserQuery(query);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHUser[] users = new Gson().fromJson(streamString, HHUser[].class);
				List<HHUser> listUsers = new ArrayList<>();
				Collections.addAll(listUsers, users);
				return listUsers;
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
	protected void onPostExecute(List<HHUser> foundUsers) {
		callbackTo.returnSearchUsers(foundUsers);
	}

}
