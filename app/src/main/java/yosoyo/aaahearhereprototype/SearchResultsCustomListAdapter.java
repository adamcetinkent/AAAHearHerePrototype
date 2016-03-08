package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import yosoyo.aaahearhereprototype.TestServerClasses.Tasks.WebHelper;

/**
 * Created by Adam Kent on 10/02/2016.
 *
 * Custom Adapter for ListView to show artwork (imageView), title and description (textViews).
 * imgArtwork is downloaded from URLs stored in images.
 */
public class SearchResultsCustomListAdapter extends ArrayAdapter{
	private static final String TAG = "SearchResultsCustomListAdapter";

	private final Activity context;
	private final String[] ids;				// underlying IDs
	private final String[] titles;			// Displayed in artistName TextView
	private final String[] images;			// URLs to download for imageView
	private final String[] descriptions;	// Displayed in artistDesc TextView

	public SearchResultsCustomListAdapter(Activity context, String[] ids, String[] titles, String[] images, String[] descriptions){
		super(context, R.layout.list_row_track, titles);

		this.context = context;
		this.ids = ids;
		this.titles = titles;
		this.images = images;
		this.descriptions = descriptions;
	}

	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_row_track, null, true);

		// Get views into which info will be put
		TextView txtArtistName = (TextView) rowView.findViewById(R.id.artistname);
		final ImageView imgArtwork = (ImageView) rowView.findViewById(R.id.artwork);
		TextView txtArtistDesc = (TextView) rowView.findViewById(R.id.artistdesc);


		txtArtistName.setText(titles[position]); // Set artistName TextView

		// Set artwork imgView bitmap
		WebHelper.getSpotifyAlbumArt(
			ids[position],
			images[position],
			new WebHelper.GetSpotifyAlbumArtCallback() {
				@Override
				public void returnSpotifyAlbumArt(Bitmap bitmap) {
				  imgArtwork.setImageBitmap(bitmap);
				}
			});
		if (descriptions[position] != null)
			txtArtistDesc.setText(descriptions[position]); // Set artistDesc TextView

		return rowView;
	}

}
