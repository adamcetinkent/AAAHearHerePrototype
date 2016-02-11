package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 11/02/2016.
 *
 * The Spotify specification for a simplified Artist.
 */
public class SpotifyArtistSimple {

	SpotifyExternalURL external_urls;
	String href;
	String id;
	String name;
	String type;
	String uri;

	public String getName() {
		return name;
	}
}
