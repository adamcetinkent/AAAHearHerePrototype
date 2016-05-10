package com.yosoyo.aaahearhereprototype.ZZZInterface;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ExpandableListView;

/**
 * Created by adam on 09/05/16.
 */
public class OverscrollExpandableListView extends ExpandableListView {
	private static final String TAG = "OverscrollExpandableListView";

	private static final int MAX_Y_OVERSCROLL_DISTANCE = 1;
	private Context context;
	private int maxYOverscrollDistance;
	private boolean isOverscrolling;
	private onOverScrollListener onOverScrollListener;
	private boolean released;

	public interface onOverScrollListener{
		void onOverScroll();
		void onRelease();
	}

	public OverscrollExpandableListView(Context context) {
		super(context);
		this.context = context;
		initialiseOverscroll();
	}

	public OverscrollExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initialiseOverscroll();
	}

	public OverscrollExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		initialiseOverscroll();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public OverscrollExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.context = context;
		initialiseOverscroll();
	}

	private void initialiseOverscroll(){
		final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		final float density = metrics.density;

		maxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
	}

	public void setOnOverScrollListener(OverscrollExpandableListView.onOverScrollListener onOverScrollListener) {
		this.onOverScrollListener = onOverScrollListener;
	}

	/*public void allowOverscroll(boolean allow){
		this.allowOverscroll = allow;
	}*/

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		int maxY;
		Log.d(TAG, "overscrolling: " + deltaY + " " + scrollY);
		if (deltaY < 0 || scrollY < 0) {
			if (!isOverscrolling && deltaY != 0 && isTouchEvent) {
				isOverscrolling = true;
				released = false;
				Log.d(TAG, "CAPTURED: " + deltaY + " " + scrollY);
				if (onOverScrollListener != null){
					onOverScrollListener.onOverScroll();
				}
			}
			maxY = maxYOverscrollDistance;
		} else {
			maxY = maxOverScrollY;
		}

		if (isOverscrolling && scrollY == 0 && deltaY == 0){
			isOverscrolling = false;
		} else {
		}
		if (!isOverscrolling && !isTouchEvent && onOverScrollListener != null){
			released = true;
			Log.d(TAG, "RELEASED: " + deltaY + " " + scrollY);
			onOverScrollListener.onRelease();
		} else {
			/*if (isOverscrolling)
				Log.d(TAG, "overscrolling: " + deltaY + " " + scrollY);*/
			if (isTouchEvent)
				Log.d(TAG, "TOUCH: " + deltaY + " " + scrollY);
		}

		return super.overScrollBy(deltaX, deltaY,
								  scrollX, scrollY,
								  scrollRangeX, scrollRangeY,
								  maxOverScrollX, maxY,
								  isTouchEvent);
	}

	/*@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		Log.d(TAG, "SCROLL: " + l + " " + t + " " + oldl + " " + oldt);
		if (t == 0 && !released){
			Log.d(TAG, "SCROLL RELEASED!");
			overScrollBy(0, 0, 0, 0, 0, 0, 0, 0, false);
		}
	}*/

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!released && ev.getAction() == MotionEvent.ACTION_UP){
			released = true;
			onOverScrollListener.onRelease();
			overScrollBy(0, 0, 0, 0, 0, 0, 0, 0, false);
		}
		return super.onTouchEvent(ev);
	}


}
