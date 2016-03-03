package yosoyo.aaahearhereprototype;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Adam Kent on 10/02/2016.
 *
 * Custom Adapter for ListView to show artwork (imageView), title and description (textViews).
 * imgArtwork is downloaded from URLs stored in artistImages.
 */
public class SearchResultsCustomListAdapter extends ArrayAdapter /*implements DownloadImageTask.DownloadImageTaskCallback*/ {
	private static final String TAG = "SearchResultsCustomListAdapter";

	private final Activity context;
	private final String[] artistNames;		// Displayed in artistName TextView
	private final String[] artistImages;	// URLs to download for imageView
	private final String[] artistDescs;		// Displayed in artistDesc TextView
	private Bitmap[] artistBitmaps;			// Storage for downloaded bitmaps

	public SearchResultsCustomListAdapter(Activity context, String[] artistNames, String[] artistImages, String[] artistDescs){
		super(context, R.layout.list_row_track, artistNames);

		this.context = context;
		this.artistNames = artistNames;
		this.artistImages = artistImages;
		this.artistDescs = artistDescs;
		this.artistBitmaps = new Bitmap[artistImages.length];
	}

	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_row_track, null, true);

		// Get views into which info will be put
		TextView txtArtistName = (TextView) rowView.findViewById(R.id.artistname);
		ImageView imgArtwork = (ImageView) rowView.findViewById(R.id.artwork);
		TextView txtArtistDesc = (TextView) rowView.findViewById(R.id.artistdesc);


		txtArtistName.setText(artistNames[position]); // Set artistName TextView

		// Set artwork imgView bitmap
		if (artistImages[position] != null) {
			/*if (artistBitmaps[position] == null) { // need to download image
				new DownloadImageTask(imgArtwork, this, position).execute(artistImages[position]);
			} else {
				imgArtwork.setImageBitmap(artistBitmaps[position]); // get from storage
			}*/
		}
		if (artistDescs[position] != null)
			txtArtistDesc.setText(artistDescs[position]); // Set artistDesc TextView

		return rowView;
	}


	/*@Override
	public void returnDownloadedImage(Bitmap result, int position, Marker marker) {
		artistBitmaps[position] = result; // store downloaded bitmap
	}*/
}
