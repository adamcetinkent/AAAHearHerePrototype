package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 09/02/2016.
 *
 * The Spotify specification for a "Paging", which is what is contained in a SpotifyAPIResponse,
 * and contains an array of items of type T
 */
public class SpotifyPaging<T> {

	String href;
	T items[];
	int limit;
	String next;
	int offset;
	String previous;
	int total;

	public T[] getItems() {
		return items;
	}
}
