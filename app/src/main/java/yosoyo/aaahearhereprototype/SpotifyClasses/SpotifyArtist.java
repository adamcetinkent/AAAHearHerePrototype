package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 09/02/2016.
 *
 * The Spotify specification for an Artist.
 */
@SuppressWarnings({"MismatchedReadAndWriteOfArray", "unused"})
public class SpotifyArtist {

	private SpotifyExternalURL external_urls;
	private SpotifyFollowers followers;
	private String genres[];
	private String href;
	private String id;
	private SpotifyImage images[];
	private String name;
	private int popularity;
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

	public String getID(){
		return id;
	}

	public String getGenres(){
		StringBuilder sb = new StringBuilder();
		for (String genre : genres) {
			sb.append(genre);
		}
		return sb.toString();
	}

	public String getName(){
		return name;
	}

	@Override
	public String toString(){
		return name;
	}

}
