package com.yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 06/01/2018.
 *
 * The Spotify specification for an Token
 */
public class SpotifyToken {

	private static SpotifyToken spotifyToken;

	private String access_token;
	private String token_type;
	private int expires_in;

	public static void setSpotifyToken(SpotifyToken token){ spotifyToken = token; }

	public static SpotifyToken getSpotifyToken(){ return spotifyToken; }

	public static String getAuthorisation() { return "Bearer " + spotifyToken.access_token; }

}
