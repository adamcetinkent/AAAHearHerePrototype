package yosoyo.aaahearhereprototype;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by adam on 08/03/16.
 */
public class InstantAutoCompleteTextView extends AutoCompleteTextView {

	public InstantAutoCompleteTextView(Context context) {
		super(context);
	}

	public InstantAutoCompleteTextView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public InstantAutoCompleteTextView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
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
