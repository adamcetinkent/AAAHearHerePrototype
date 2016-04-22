package com.yosoyo.aaahearhereprototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Adam Kent on 10/02/2016.
 *
 * A dump for utility functions.
 */
public class ZZZUtility {
	private static final String TAG = "ZZZUtility";

	public static String convertStreamToString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private static final SimpleDateFormat fullDate = new SimpleDateFormat("dd MMMM yyyy", Locale.UK);
	private static final SimpleDateFormat halfDate = new SimpleDateFormat("dd MMMM", Locale.UK);
	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
	private static final long YEAR_MILLIS = 365 * DAY_MILLIS;

	public static String formatDynamicDate(Timestamp timestamp){

		long time = timestamp.getTime();
		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - time;

		if (elapsed <=0)
			return "the future?";

		if (elapsed > YEAR_MILLIS){
			return fullDate.format(time);
		} else if (elapsed > 2 * DAY_MILLIS){
			return halfDate.format(time);
		} else if (elapsed > DAY_MILLIS){
			return "Yesterday";
		} else if (elapsed > 2 * HOUR_MILLIS){
			return (elapsed / HOUR_MILLIS) + " hours ago";
		} else if (elapsed > 50 * MINUTE_MILLIS) {
			return "an hour ago";
		} else if (elapsed > 2 * MINUTE_MILLIS) {
			return (elapsed / MINUTE_MILLIS) + " minutes ago";
		} else if (elapsed > MINUTE_MILLIS){
			return "a minute ago";
		} else {
			return "just now";
		}

	}

	public static String truncatedAddress(String inString, int maxLength){
		if (inString.length() < maxLength)
			return inString;

		maxLength = maxLength - 3;
		boolean spaceSplit = false;
		String[] parts = inString.split(",");
		if (parts[0].length()+1 > maxLength){
			parts = inString.split(" ");
			spaceSplit = true;
			if (parts[0].length()+1 > maxLength)
				return inString.substring(0, maxLength) + "...";
		}
		StringBuilder sb = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++){
			if (sb.length() + parts[i].length() > maxLength)
				return sb.append("...").toString();
			sb.append(spaceSplit ? " " : ",").append(parts[i]);
		}
		return sb.toString();

	}

	public static byte[] convertBitmapToByteArray(Bitmap bitmap){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] convertImageViewToByteArray(ImageView imageView){
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		return convertBitmapToByteArray(bitmap);
	}

	public static Bitmap convertByteArrayToBitmap(byte[] bytes){
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	public static <T> List<T> mergeLists(List<T> oldList, List<T> newList){
		//TreeSet setBoth = new TreeSet(newList);
		HashSet<T> setBoth = new HashSet<>(newList);
		setBoth.addAll(oldList);
		oldList.clear();
		oldList.addAll(setBoth);
		return oldList;
	}

	public static <T> List<T> updateList(List<T> oldList, T newItem){
		for (int i = 0; i < oldList.size(); i++){
			if (oldList.get(i).equals(newItem)){
				oldList.set(i, newItem);
				return oldList;
			}
		}
		oldList.add(newItem);
		return oldList;
	}


	public static <T> Boolean addItemToList(List<T> oldList, T newItem){
		for (T testItem : oldList)
			if (testItem.equals(newItem))
				return false;
		oldList.add(newItem);
		return true;
	}

	public static String getLatLng(Location location){
		if (location == null)
			return null;
		return location.getLatitude() + " " + location.getLongitude();
	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value){
		for (Map.Entry<T,E> entry: map.entrySet()){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				if (Objects.equals(value, entry.getValue())){
					return entry.getKey();
				}
			} else {
				if (value.equals(entry.getValue())){
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public static ColorFilter screen(int c){
		return new LightingColorFilter(0xFFFFFFFF -c, c);
	}

	public static final ColorFilter greyOut = screen(Color.LTGRAY);

}
