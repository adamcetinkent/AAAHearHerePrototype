package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by adam on 01/03/16.
 *
 * The Spotify specification for an Album
 */
public class SpotifyAlbum {

	String album_type;
	SpotifyArtistSimple[] artists;
	String[] available_markets;
	SpotifyCopyright[] copyrights;
	SpotifyExternalID externalIDs;
	SpotifyExternalURL externalURLs;
	String[] genres;
	String href;
	String id;
	SpotifyImage[] images;
	String name;
	int popularity;
	String release_date;
	String release_date_precision;
	SpotifyPaging<SpotifyTrackSimple> tracks;
	String type;
	String uri;

	public SpotifyImage getImages(int index) {
		if (index < images.length) {
			return images[index];
		} else {
			return null;
		}
	}

	public String getName(){
		return name;
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

	public String getArtistID(){
		if (artists == null)
			return null;
		return artists[0].id;
	}

	public String getID(){
		return id;
	}

}
