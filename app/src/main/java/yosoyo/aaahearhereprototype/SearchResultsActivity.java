package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAPIResponse;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyAlbum;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyArtist;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyImage;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

public class SearchResultsActivity extends Activity implements SpotifyAPIRequest.SpotifyAPIRequestCallback {
	private static final String TAG = "SearchResultsActivity";

	public static final String TRACK_JSON = "trackJson";
	public static final String ARTIST_JSON = "artistJson";
	public static final String ALBUM_JSON = "albumJson";
	public static final String BMP_JSON = "bitmapJson";

	public static final String QUERY_ARTIST = "queryArtist";
	public static final String QUERY_ALBUM = "queryAlbum";

	public static final int REQUEST_CODE_TRACK = 270027;
	public static final int REQUEST_CODE_ARTIST = 270028;
	public static final int REQUEST_CODE_ALBUM = 270029;
	public static final int REQUEST_CODE_TRACK_ARTIST = 270030;
	public static final int REQUEST_CODE_TRACK_ALBUM = 270031;
	public static final int REQUEST_CODE_TRACK_ARTIST_ALBUM = 270032;
	//public static final int REQUEST_CODE_ARTIST_ALBUM = 270033;
	public static final int REQUEST_CODE_ALBUM_ARTIST = 270034;

	private SpotifyTrack[] trackResults;
	private SpotifyArtist[] artistResults;
	private SpotifyAlbum[] albumResults;
	private int requestCode;

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

		requestCode = intent.getIntExtra(HolderActivity.REQUEST_CODE, -1);

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			switch (requestCode){
				case REQUEST_CODE_TRACK_ARTIST:{
					String artistName = intent.getStringExtra(QUERY_ARTIST);
					query = "track:"+query+"%20artist:%22"+artistName+"%22";
					break;
				}
				case REQUEST_CODE_TRACK_ALBUM:{
					String albumName = intent.getStringExtra(QUERY_ALBUM);
					query = "track:"+query+"%20album:%22"+albumName+"%22";
					break;
				}
				case REQUEST_CODE_TRACK_ARTIST_ALBUM:{
					String artistName = intent.getStringExtra(QUERY_ARTIST);
					String albumName = intent.getStringExtra(QUERY_ALBUM);
					query = "track:"+query+"%20artist:%22"+artistName+"%22"+"%20album:%22"+albumName+"%22";
					break;
				}
				/*case REQUEST_CODE_ARTIST_ALBUM:{
					String albumName = intent.getStringExtra(QUERY_ALBUM);
					query = "artist:"+query+"%20album:%22"+albumName+"%22";
					break;
				}*/
				case REQUEST_CODE_ALBUM_ARTIST:{
					String artistName = intent.getStringExtra(QUERY_ARTIST);
					query = "album:"+query+"%20artist:%22"+artistName+"%22";
					break;
				}
			}

			showResults(query);
		}
	}

	private void showResults(String query) {
		Log.d(TAG, "Query: " + query);
		SpotifyAPIRequest spotifyAPIRequest = null;
		switch (requestCode){
			case REQUEST_CODE_TRACK_ARTIST_ALBUM:
			case REQUEST_CODE_TRACK_ARTIST:
			case REQUEST_CODE_TRACK:{
				spotifyAPIRequest = new SpotifyAPIRequest(this, "track");
				break;
			}
			//case REQUEST_CODE_ARTIST_ALBUM:
			case REQUEST_CODE_ARTIST:{
				spotifyAPIRequest = new SpotifyAPIRequest(this, "artist");
				break;
			}
			case REQUEST_CODE_ALBUM_ARTIST:
			case REQUEST_CODE_ALBUM:{
				spotifyAPIRequest = new SpotifyAPIRequest(this, "album");
				break;
			}
		}
		if (spotifyAPIRequest == null)
			return;
		else
			spotifyAPIRequest.execute(query);
	}

	@Override
	public void returnSpotifySearchResults(final SpotifyAPIResponse result) {

		switch (requestCode) {
			case REQUEST_CODE_TRACK_ARTIST:
			case REQUEST_CODE_TRACK_ARTIST_ALBUM:
			case REQUEST_CODE_TRACK: {
				processTracks(result);
				break;
			}
			//case REQUEST_CODE_ARTIST_ALBUM:
			case REQUEST_CODE_ARTIST: {
				processArtists(result);
				break;
			}
			case REQUEST_CODE_ALBUM_ARTIST:
			case REQUEST_CODE_ALBUM: {
				processAlbums(result);
				break;
			}

		}
	}

	private void processTracks(final SpotifyAPIResponse result){
		trackResults = result.getTracks().getItems();
		Log.d(TAG, "JSON search results:\n" + trackResults);
		if (trackResults == null){
			return;
		}
		String trackIDList[] = new String[trackResults.length];
		String trackNameList[] = new String[trackResults.length];
		String trackImageList[] = new String[trackResults.length];
		String trackDescList[] = new String[trackResults.length];
		for (int i = 0; i < trackResults.length; i++){
			trackIDList[i] = trackResults[i].getID();
			trackNameList[i] = trackResults[i].getName();
			SpotifyImage image = trackResults[i].getImages(0);
			if (image != null)
				trackImageList[i] = image.getUrl();
			trackDescList[i] = trackResults[i].getArtistNameAlbumName();
		}

		SearchResultsCustomListAdapter adapter = new SearchResultsCustomListAdapter(this, trackIDList, trackNameList, trackImageList, trackDescList);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SpotifyTrack track = trackResults[position];

				Intent resultIntent = new Intent();
				resultIntent.putExtra(TRACK_JSON, new Gson().toJson(track, SpotifyTrack.class));

				//byte[] bytes = ZZZUtility.convertImageViewToByteArray((ImageView) view.findViewById(R.id.artwork));
				//resultIntent.putExtra(BMP_JSON, bytes);

				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});

	}

	private void processArtists(SpotifyAPIResponse result){
		artistResults = result.getArtists().getItems();
		Log.d(TAG, "JSON search results:\n" + artistResults);
		if (artistResults == null){
			return;
		}
		String artistIDList[] = new String[artistResults.length];
		String artistNameList[] = new String[artistResults.length];
		String artistImageList[] = new String[artistResults.length];
		String artistDescList[] = new String[artistResults.length];
		for (int i = 0; i < artistResults.length; i++){
			artistIDList[i] = artistResults[i].getID();
			artistNameList[i] = artistResults[i].getName();
			SpotifyImage image = artistResults[i].getImages(0);
			if (image != null)
				artistImageList[i] = image.getUrl();
			artistDescList[i] = artistResults[i].getGenres();
		}

		SearchResultsCustomListAdapter adapter = new SearchResultsCustomListAdapter(this, artistIDList, artistNameList, artistImageList, artistDescList);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SpotifyArtist artist = artistResults[position];

				Intent resultIntent = new Intent();
				resultIntent.putExtra(ARTIST_JSON, new Gson().toJson(artist, SpotifyArtist.class));

				//byte[] bytes = ZZZUtility.convertImageViewToByteArray((ImageView) view.findViewById(R.id.artwork));
				//resultIntent.putExtra(BMP_JSON, bytes);

				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});
	}

	private void processAlbums(SpotifyAPIResponse result){
		albumResults = result.getAlbums().getItems();
		Log.d(TAG, "JSON search results:\n" + albumResults);
		if (albumResults == null){
			return;
		}
		String albumIDList[] = new String[albumResults.length];
		String albumNameList[] = new String[albumResults.length];
		String albumImageList[] = new String[albumResults.length];
		String albumDescList[] = new String[albumResults.length];
		for (int i = 0; i < albumResults.length; i++){
			albumIDList[i] = albumResults[i].getID();
			albumNameList[i] = albumResults[i].getName();
			SpotifyImage image = albumResults[i].getImages(0);
			if (image != null)
				albumImageList[i] = image.getUrl();
			albumDescList[i] = albumResults[i].getArtistName();
		}

		SearchResultsCustomListAdapter adapter = new SearchResultsCustomListAdapter(this, albumIDList, albumNameList, albumImageList, albumDescList);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SpotifyAlbum album = albumResults[position];

				Intent resultIntent = new Intent();
				resultIntent.putExtra(ALBUM_JSON, new Gson().toJson(album, SpotifyAlbum.class));

				//byte[] bytes = ZZZUtility.convertImageViewToByteArray((ImageView) view.findViewById(R.id.artwork));
				//resultIntent.putExtra(BMP_JSON, bytes);

				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});
	}

}
