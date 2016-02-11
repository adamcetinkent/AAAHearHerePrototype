package yosoyo.aaahearhereprototype;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAPIResponse;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyImage;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

public class SearchResultsActivity extends AppCompatActivity implements SpotifyAPIRequest.SpotifyAPIRequestCallback {
	private static final String tag = "SearchResultsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			showResults(query);
		}
	}

	private void showResults(String query) {
		Log.d(tag, "Query: " + query);
		SpotifyAPIRequest spotifyAPIRequest = new SpotifyAPIRequest(this, "track");
		spotifyAPIRequest.execute(query);
	}

	public void processFinish(SpotifyAPIResponse result){
		SpotifyTrack[] trackResults = result.getTracks().getItems();
		Log.d(tag, "JSON search results:\n" + trackResults);
		if (trackResults == null){
			return;
		}
		String trackNameList[] = new String[trackResults.length];
		String trackImageList[] = new String[trackResults.length];
		String trackDescList[] = new String[trackResults.length];
		for (int i = 0; i < trackResults.length; i++){
			trackNameList[i] = trackResults[i].getName();
			SpotifyImage image = trackResults[i].getImages(0);
			if (image != null)
				trackImageList[i] = image.getUrl();
			trackDescList[i] = trackResults[i].getArtistNameAlbumName();
		}

		SearchResultsCustomListAdapter adapter = new SearchResultsCustomListAdapter(this, trackNameList, trackImageList, trackDescList);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView txtTrackName = (TextView) view.findViewById(R.id.artistname);
				String trackName = txtTrackName.getText().toString();
				TextView txtTrackDesc = (TextView) view.findViewById(R.id.artistdesc);
				String trackDesc = txtTrackDesc.getText().toString();
				Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
				intent.putExtra("trackName", trackName);
				intent.putExtra("trackDesc", trackDesc);
				startActivity(intent);
			}
		});

	}

}
