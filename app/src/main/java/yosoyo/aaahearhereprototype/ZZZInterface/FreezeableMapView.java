package yosoyo.aaahearhereprototype.ZZZInterface;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Created by adam on 12/04/16.
 */
public class FreezeableMapView extends MapView {
	private boolean frozen;

	public FreezeableMapView(Context context) {
		super(context);
	}

	public FreezeableMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FreezeableMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FreezeableMapView(Context context, GoogleMapOptions options) {
		super(context, options);
	}

	@Override
	protected void onDraw(Canvas canvas){
		if (!frozen)
			super.onDraw(canvas);
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}
