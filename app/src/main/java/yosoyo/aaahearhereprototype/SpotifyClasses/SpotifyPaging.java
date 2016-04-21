package yosoyo.aaahearhereprototype.SpotifyClasses;

/**
 * Created by Adam Kent on 09/02/2016.
 *
 * The Spotify specification for a "Paging", which is what is contained in a SpotifyAPIResponse,
 * and contains an array of items of type T
 */
@SuppressWarnings("unused")
public class SpotifyPaging<T> {

	private String href;
	private T items[];
	private int limit;
	private String next;
	private int offset;
	private String previous;
	private int total;

	public T[] getItems() {
		return items;
	}
}
