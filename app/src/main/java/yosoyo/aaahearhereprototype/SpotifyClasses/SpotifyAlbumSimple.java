package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 11/02/2016.
 *
 * The Spotify specification for an simplified Album
 */
public class SpotifyAlbumSimple {

	String album_type;
	String[] available_markets;
	SpotifyExternalURL external_urls;
	String href;
	String id;
	SpotifyImage[] images;
	String name;
	String type;
	String uri;

	public SpotifyImage[] getImages() {
		return images;
	}

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

	@Override
	public String toString(){
		return name;
	}

}
