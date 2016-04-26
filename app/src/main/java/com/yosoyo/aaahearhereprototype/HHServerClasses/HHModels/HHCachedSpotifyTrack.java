package com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.yosoyo.aaahearhereprototype.HHServerClasses.Database.ORMCachedSpotifyTrack;
import com.yosoyo.aaahearhereprototype.SpotifyClasses.SpotifyTrack;

/**
 * Created by adam on 23/02/16.
 *
 * A streamlined form of the {@link SpotifyTrack} for caching
 */
public class HHCachedSpotifyTrack implements Parcelable {

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
		this.image_url = spotifyTrack.getImageURL();
		this.preview_url = spotifyTrack.getPreviewURL();
	}

	public HHCachedSpotifyTrack(Cursor cursor){
		this.trackID = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.TRACK_ID()));
		this.name = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.NAME()));
		this.artist = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.ARTIST_NAME()));
		this.album = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.ALBUM_NAME()));
		this.image_url = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.IMAGE_URL()));
		this.preview_url = cursor.getString(cursor.getColumnIndex(ORMCachedSpotifyTrack.PREVIEW_URL()));
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(trackID);
		dest.writeString(name);
		dest.writeString(artist);
		dest.writeString(album);
		dest.writeString(image_url);
		dest.writeString(preview_url);
	}

	public static final Parcelable.Creator<HHCachedSpotifyTrack> CREATOR = new Parcelable.Creator<HHCachedSpotifyTrack>(){

		@Override
		public HHCachedSpotifyTrack createFromParcel(Parcel source) {
			return new HHCachedSpotifyTrack(source);
		}

		@Override
		public HHCachedSpotifyTrack[] newArray(int size) {
			return new HHCachedSpotifyTrack[size];
		}

	};

	private HHCachedSpotifyTrack(Parcel in){
		trackID = in.readString();
		name = in.readString();
		artist = in.readString();
		album = in.readString();
		image_url = in.readString();
		preview_url = in.readString();
	}

}
