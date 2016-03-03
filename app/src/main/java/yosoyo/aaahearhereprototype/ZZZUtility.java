package yosoyo.aaahearhereprototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public static <T> List<T> mergeLists (List<T> oldList, List<T> newList){
		Set setBoth = new HashSet(newList);
		setBoth.addAll(oldList);
		oldList.clear();
		oldList.addAll(setBoth);
		return oldList;
	}

	public static <T> Boolean mergeLists (List<T> oldList, T newItem){
		//if (!oldList.contains(newItem))
		//	oldList.add(newItem);
		for (T testItem : oldList)
			if (testItem.equals(newItem))
				return false;
		oldList.add(newItem);
		return true;
	}

}
