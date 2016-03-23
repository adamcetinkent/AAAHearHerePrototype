package yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;

import yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMCachedSpotifyTrack;
import yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * Created by adam on 23/02/16.
 */
public class HHCachedSpotifyTrack {

	private final String trackID;
	private final String name;
	private final String artist;
	private final String album;
	private final String image_url;
	private final String preview_url;

	public HHCachedSpotifyTrack(SpotifyTrack spotifyTrack){
		this.trackID = spotifyTrack.getID();
		this.name = spotifyTrack.getName();
		this.artist = spotifyTrack.getArtistName();
		this.album = spotifyTrack.getAlbumName();
		this.image_url = spotifyTrack.getImages(0).getUrl();
		this.preview_url = spotifyTrack.getPreviewUrl();
	}

	public HHCachedSpotifyTrack(Cursor cursor){
		this.trackID = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.COLUMN_TRACK_ID_NAME));
		this.name = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.COLUMN_NAME_NAME));
		this.artist = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.COLUMN_ARTIST_NAME));
		this.album = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.COLUMN_ALBUM_NAME));
		this.image_url = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.COLUMN_IMAGE_URL_NAME));
		this.preview_url = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.COLUMN_PREVIEW_URL_NAME));
	}

	public String getTrackID() {
		return trackID;
	}

	public String getName() {
		return name;
	}

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}

	public String getImageUrl() {
		return image_url;
	}

	public String getPreviewUrl() {
		return preview_url;
	}
}
