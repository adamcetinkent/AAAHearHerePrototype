package yosoyo.aaahearhereprototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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

}
