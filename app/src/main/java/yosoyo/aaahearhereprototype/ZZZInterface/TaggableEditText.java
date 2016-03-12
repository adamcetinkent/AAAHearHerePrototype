package yosoyo.aaahearhereprototype.ZZZInterface;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;

/**
 * Created by adam on 11/03/16.
 */
public class TaggableEditText extends InstantAutoCompleteTextView {
	private static final String TAG = "TaggableEditText";

	boolean listenerBlock = false;
	boolean showSuggestions = false;
	boolean isTagging = false;
	CharSequence suffix;
	CharSequence prefix;
	int tagStart;
	int tagSuffixLength;
	List<TextTag> tags = new ArrayList<>();
	List<Object> blocks = new ArrayList<>();

	private class TextTag{
		private HHUser user;
		boolean fullName;
		private int start;

		public TextTag(HHUser user, boolean fullName, int start) {
			this.user = user;
			this.fullName = fullName;
			this.start = start;
		}
	}

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
		return showSuggestions;
	}

	public void showSuggestions(boolean show){
		showSuggestions = show;
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

	public int addTag(HHUser user, boolean fullName, int position){
		tags.add(new TextTag(user, fullName, position));
		Log.d(TAG, "added new Tag");
		return tags.size();
	}

}
