package com.yosoyo.aaahearhereprototype.SpotifyClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public List<T> getItemsList(){
		return new ArrayList<>(Arrays.asList(items));
	}

	public int getTotal() {
		return total;
	}
}
