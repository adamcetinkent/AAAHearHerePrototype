package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 11/02/2016.
 *
 * The Spotify specification for an simplified Album
 */
@SuppressWarnings("unused")
class SpotifyAlbumSimple {

	private String album_type;
	private String[] available_markets;
	private SpotifyExternalURL external_urls;
	private String href;
	private String id;
	private SpotifyImage[] images;
	private String name;
	private String type;
	private String uri;

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
