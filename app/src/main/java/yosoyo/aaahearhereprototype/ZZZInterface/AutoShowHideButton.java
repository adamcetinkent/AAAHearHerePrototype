package yosoyo.aaahearhereprototype.ZZZInterface;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * Created by adam on 07/04/16.
 */
public class AutoShowHideButton extends ToggleButton {

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public AutoShowHideButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public AutoShowHideButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AutoShowHideButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoShowHideButton(Context context) {
		super(context);
	}

	private boolean isListening = true;
	public void setListening(boolean listening){
		this.isListening = listening;
	}
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean getListening(){
		return isListening;
	}

	private boolean autoListening = false;
	public void setAutoListening(boolean autoListening){
		this.autoListening = autoListening;
	}
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean getAutoListening(){
		return autoListening;
	}

	private boolean manualOverride = false;
	public void setManualOverride(boolean manualOverride){
		this.manualOverride = manualOverride;
	}
	public boolean getManualOverride(){
		return manualOverride;
	}

}
