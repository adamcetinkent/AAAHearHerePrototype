package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 10/02/2016.
 *
 * This is returned by a SpotifyAPIRequest. The type of search request sent to Spotify determines
 * which fields will be filled.
 */
public class SpotifyAPIResponse {

	SpotifyPaging<SpotifyArtist> artists;
	SpotifyPaging<SpotifyTrack> tracks;
	SpotifyPaging<SpotifyAlbum> albums;

	public SpotifyPaging<SpotifyArtist> getArtists() {
		return artists;
	}

	public SpotifyPaging<SpotifyTrack> getTracks() {
		return tracks;
	}

	public SpotifyPaging<SpotifyAlbum> getAlbums(){
		return albums;
	}

}
