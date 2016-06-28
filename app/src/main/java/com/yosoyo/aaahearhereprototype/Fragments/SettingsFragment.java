package com.yosoyo.aaahearhereprototype.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yosoyo.aaahearhereprototype.AsyncDataManager;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.R;
import com.yosoyo.aaahearhereprototype.ZZZInterface.MutableRadioGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

	public static final String TAG = SettingsFragment.class.getSimpleName();

	public SettingsFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_settings, container, false);

		{
			final MutableRadioGroup grpProfilePrivacy = (MutableRadioGroup) view
				.findViewById(R.id.fragment_settings_grpProfilePrivacy);
			final TextView txtProfilePrivacyDesc = (TextView) view
				.findViewById(R.id.fragment_settings_txtProfilePrivacyDesc);
			final String[] profilePrivacyDescs = getResources()
				.getStringArray(R.array.profile_privacy_descs);
			grpProfilePrivacy.mute(true);

			grpProfilePrivacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(final RadioGroup group, int checkedId) {
					final int index = group.indexOfChild(view.findViewById(checkedId));
					txtProfilePrivacyDesc.setText(profilePrivacyDescs[index]);

					if (!((MutableRadioGroup) group).isMuted()) {
						AsyncDataManager.updateUserProfilePrivacy(
							HHUser.getAuthorisationToken(),
							index,
							new AsyncDataManager.UpdateUserProfilePrivacyCallback() {
								@Override
								public void returnUpdateUserProfilePrivacy(boolean success, int profilePrivacy) {
									if (success) {
										Log.d(TAG, "Profile Privacy changed to " + profilePrivacy);
									} else {
										Log.e(TAG, "Error changing Profile Privacy to " + index);
										grpProfilePrivacy.mute(true);
										((RadioButton) group.getChildAt(
											HHUser.getCurrentUser().getUser().getProfilePrivacy())
										).setChecked(true);
										Toast.makeText(getActivity(), "Error changing Profile Privacy",
													   Toast.LENGTH_LONG).show();
										grpProfilePrivacy.mute(false);
									}
								}
							}
						);
					}
				}
			});

			((RadioButton) grpProfilePrivacy.getChildAt(
				HHUser.getCurrentUser().getUser().getProfilePrivacy())
			).setChecked(true);
			grpProfilePrivacy.mute(false);
		}

		{
			final MutableRadioGroup grpSearchPrivacy = (MutableRadioGroup) view
				.findViewById(R.id.fragment_settings_grpSearchPrivacy);
			final TextView txtSearchPrivacyDesc = (TextView) view
				.findViewById(R.id.fragment_settings_txtSearchPrivacyDesc);
			final String[] searchPrivacyDescs = getResources()
				.getStringArray(R.array.search_privacy_descs);
			grpSearchPrivacy.mute(true);

			grpSearchPrivacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(final RadioGroup group, int checkedId) {
					final int index = group.indexOfChild(view.findViewById(checkedId));
					txtSearchPrivacyDesc.setText(searchPrivacyDescs[index]);

					if (!((MutableRadioGroup) group).isMuted()) {
						AsyncDataManager.updateUserSearchPrivacy(
							HHUser.getAuthorisationToken(),
							index,
							new AsyncDataManager.UpdateUserSearchPrivacyCallback() {
								@Override
								public void returnUpdateUserSearchPrivacy(boolean success, int searchPrivacy) {
									if (success) {
										Log.d(TAG, "Search Privacy changed to " + searchPrivacy);
									} else {
										Log.e(TAG, "Error changing Search Privacy to " + index);
										grpSearchPrivacy.mute(true);
										((RadioButton) group.getChildAt(
											HHUser.getCurrentUser().getUser().getSearchPrivacy())
										).setChecked(true);
										Toast.makeText(getActivity(),
													   "Error changing Search Privacy",
													   Toast.LENGTH_LONG).show();
										grpSearchPrivacy.mute(false);
									}
								}
							}
						);
					}
				}
			});

			((RadioButton) grpSearchPrivacy.getChildAt(
				HHUser.getCurrentUser().getUser().getSearchPrivacy())
			).setChecked(true);
			grpSearchPrivacy.mute(false);
		}

		{
			final MutableRadioGroup grpAutoAcceptance = (MutableRadioGroup) view
				.findViewById(R.id.fragment_settings_grpAutoAcceptance);
			final TextView txtAutoAcceptanceDesc = (TextView) view
				.findViewById(R.id.fragment_settings_txtAutoAcceptanceDesc);
			final String[] autoAcceptDescs = getResources()
				.getStringArray(R.array.auto_accept_descs);
			grpAutoAcceptance.mute(true);

			grpAutoAcceptance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(final RadioGroup group, int checkedId) {
					final int index = group.indexOfChild(view.findViewById(checkedId));
					txtAutoAcceptanceDesc.setText(autoAcceptDescs[index]);

					if (!((MutableRadioGroup) group).isMuted()) {
						AsyncDataManager.updateUserAutoAccept(
							HHUser.getAuthorisationToken(),
							index,
							new AsyncDataManager.UpdateUserAutoAcceptCallback() {
								@Override
								public void returnUpdateUserAutoAccept(boolean success, int autoAccept) {
									if (success) {
										Log.d(TAG, "Auto Accept changed to " + autoAccept);
									} else {
										Log.e(TAG, "Error changing Auto Accept to " + index);
										grpAutoAcceptance.mute(true);
										((RadioButton) group.getChildAt(
											HHUser.getCurrentUser().getUser().getAutoAccept())
										).setChecked(true);
										Toast.makeText(getActivity(),
													   "Error changing Follower Acceptance",
													   Toast.LENGTH_LONG).show();
										grpAutoAcceptance.mute(false);
									}
								}
							}
						);
					}
				}
			});

			((RadioButton) grpAutoAcceptance.getChildAt(
				HHUser.getCurrentUser().getUser().getAutoAccept())
			).setChecked(true);
			grpAutoAcceptance.mute(false);
		}

		return view;
	}

}
