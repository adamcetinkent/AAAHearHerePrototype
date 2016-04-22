package com.yosoyo.aaahearhereprototype.ZZZInterface;

import android.content.Context;
import android.text.Editable;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by adam on 11/03/16.
 *
 * An extension of {@link InstantAutoCompleteTextView} that provides a dropdown of potential users
 * to be tagged, which only allows atomic manipulation of tags.
 */
public class TaggableEditText extends InstantAutoCompleteTextView {
	private static final String TAG = "TaggableEditText";

	private boolean listenerBlock = false;
	private boolean isTagging = false;
	private CharSequence suffix;
	private CharSequence prefix;
	private int tagStart;
	private int tagSuffixLength;

	public TaggableEditText(Context context) {
		super(context);
	}

	public TaggableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TaggableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TaggableEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public boolean isListenerBlocked() {
		return listenerBlock;
	}

	public void setListenerBlock(boolean listenerBlock) {
		this.listenerBlock = listenerBlock;
	}

	@Override
	public boolean enoughToFilter() {
		return isTagging;
	}

	public boolean isTagging() {
		return isTagging;
	}

	public void setIsTagging(boolean isTagging) {
		this.isTagging = isTagging;
		tagStart = -1;
		tagSuffixLength = -1;
	}

	public CharSequence getSuffix() {
		return suffix;
	}

	public void setSuffix(CharSequence suffix) {
		this.suffix = suffix;
		tagSuffixLength = suffix.length();

	}

	public CharSequence getPrefix() {
		return prefix;
	}

	public void setPrefix(CharSequence prefix) {
		this.prefix = prefix;
		tagStart = prefix.length();
	}

	public int getTagStart() {
		return tagStart;
	}

	public int getTagSuffixLength() {
		return tagSuffixLength;
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);
		Editable text = getText();
		ClickableSpan[] spans = text.getSpans(0, text.length(), ClickableSpan.class);
		if (spans.length > 0) {
			for (ClickableSpan span : spans){
				int spanStart = text.getSpanStart(span);
				int spanEnd = text.getSpanEnd(span);

				if ((selStart < spanStart && selEnd > spanEnd)
					|| (selStart > spanEnd && selEnd > spanEnd)
					|| (selStart < spanStart && selEnd < spanStart))
					continue;

				if (selStart == selEnd) {
					if (selStart > spanStart && selEnd < spanEnd) {
						if ((selStart - spanStart) < (spanEnd - selStart)) {
							setSelection(spanEnd);
						} else {
							setSelection(spanStart);
						}
					}
					continue;
				}

				int minStart = Math.min(spanStart, selStart);
				int maxEnd = Math.max(spanEnd, selEnd);
				int spanCutoffStart = spanStart + (spanEnd - spanStart)/3;
				int spanCutoffEnd = spanStart + 2 * (spanEnd - spanStart)/3;
				if (selStart > spanCutoffStart && selStart > spanStart){
					setSelection(spanEnd, maxEnd);
					Log.d(TAG, "spanEnd, maxEnd");
				} else if (selEnd < spanCutoffEnd && selEnd > spanStart){
					setSelection(minStart, spanStart);
					Log.d(TAG, "minStart, spanStart");
				} else {
					setSelection(minStart, maxEnd);
					Log.d(TAG, "minStart, maxEnd");
				}
			}
		}
	}
}
