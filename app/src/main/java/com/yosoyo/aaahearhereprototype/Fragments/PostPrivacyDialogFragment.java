package com.yosoyo.aaahearhereprototype.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yosoyo.aaahearhereprototype.R;

/**
 * Created by adam on 01/06/16.
 */
public class PostPrivacyDialogFragment extends DialogFragment {

	private static final String TAG = PostPrivacyDialogFragment.class.getSimpleName();

	private int privacy;
	private RadioGroup btnGroup;
	Callback callback;

	public PostPrivacyDialogFragment(){}

	interface Callback {
		void setPrivacy(int privacy);
	}

	public static PostPrivacyDialogFragment newInstance(int privacy, Callback callback){
		PostPrivacyDialogFragment postPrivacyDialogFragment = new PostPrivacyDialogFragment();
		postPrivacyDialogFragment.privacy = privacy;
		postPrivacyDialogFragment.callback = callback;
		return postPrivacyDialogFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View view = inflater
			.inflate(R.layout.dialog_fragment_post_privacy, container, false);

		final TextView txtDesc = (TextView) view.findViewById(R.id.fragment_post_privacy_txtDesc);
		final String[] descs = getResources().getStringArray(R.array.post_privacy_descs);

		btnGroup = (RadioGroup) view.findViewById(R.id.fragment_post_privacy_btnGroup);

		((RadioButton) btnGroup.getChildAt(privacy)).setChecked(true);
		txtDesc.setText(descs[privacy]);

		btnGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				privacy = group.indexOfChild(view.findViewById(checkedId));
				callback.setPrivacy(privacy);
				txtDesc.setText(descs[privacy]);
			}
		});

		//txtDesc.setWidth(btnGroup.getWidth());

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}
}