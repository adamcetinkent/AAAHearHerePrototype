package com.yosoyo.aaahearhereprototype.ZZZInterface;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by adam on 08/03/16.
 *
 * An extension of {@link AutoCompleteTextView} with no filtering limit.
 */
public class InstantAutoCompleteTextView extends AutoCompleteTextView {

	public InstantAutoCompleteTextView(Context context) {
		super(context);
	}

	public InstantAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InstantAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	InstantAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public boolean enoughToFilter() {
		return true;
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
								  Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if (focused) {
			performFiltering(getText(), 0);
		}
	}

}
