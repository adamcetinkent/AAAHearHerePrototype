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
		for (int i = 0; i < genres.length; i++){
			sb.append(genres[i]);
		}
		return sb.toString();
	}

	@Override
	public String toString(){
		return name;
	}

}
