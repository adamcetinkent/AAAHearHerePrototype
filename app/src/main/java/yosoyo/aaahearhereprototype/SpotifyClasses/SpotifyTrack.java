package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 11/02/2016.
 *
 * The Spotify specification for a Track.
 */
public class SpotifyTrack {

	SpotifyAlbumSimple album;
	SpotifyArtistSimple[] artists;
	String[] available_markets;
	int disc_number;
	int duration_ms;
	boolean explicit;
	SpotifyExternalID external_ids;
	SpotifyExternalURL external_urls;
	String href;
	String id;
	boolean is_playable;
	SpotifyTrackLink linked_from;
	String name;
	int popularity;
	String preview_url;
	int track_number;
	String type;
	String uri;

	public SpotifyImage[] getImages() {
		if (album != null)
			return album.getImages();
		else
			return null;
	}

	public SpotifyImage getImages(int index) {
		SpotifyImage[] images = getImages();
		if (images == null)
			return null;
		if (index < images.length) {
			return images[index];
		} else {
			return null;
		}
	}

	public String getID(){
		return id;
	}

	public String getName(){
		return name;
	}

	public SpotifyArtistSimple[] getArtists() {
		return artists;
	}

	public String getArtistName(){
		if (artists == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < artists.length; i++){
			sb.append(artists[i].getName());
			if (i < artists.length - 1){
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	public String getAlbumName(){
		if (album == null)
			return null;
		return album.getName();
	}

	public String getArtistNameAlbumName(){
		if (artists == null || album == null)
			return null;
		return  getArtistName() + " - " + getAlbumName();
	}

	public String getPreviewUrl() {
		return preview_url;
	}

	@Override
	public String toString(){
		return name;
	}

}
