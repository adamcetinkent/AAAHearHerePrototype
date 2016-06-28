package com.yosoyo.aaahearhereprototype.ZZZInterface;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Created by adam on 28/06/16.
 */
public class MutableRadioGroup extends RadioGroup {

	boolean muted = false;

	public MutableRadioGroup(Context context) {
		super(context);
	}

	public MutableRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isMuted() {
		return muted;
	}

	public void mute(boolean muted) {
		this.muted = muted;
	}

}
