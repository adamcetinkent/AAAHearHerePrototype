package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 09/02/2016.
 */
public class SpotifyArtist {

	SpotifyExternalURL external_urls;
	SpotifyFollowers followers;
	String genres[];
	String href;
	String id;
	SpotifyImage images[];
	String name;
	int popularity;
	String type;
	String uri;

	@Override
	public String toString(){
		return name;
	}

}
