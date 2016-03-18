package yosoyo.aaahearhereprototype.Fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import yosoyo.aaahearhereprototype.AsyncDataManager;
import yosoyo.aaahearhereprototype.HHServerClasses.HHFollowRequestUser;
import yosoyo.aaahearhereprototype.HHServerClasses.HHUser;
import yosoyo.aaahearhereprototype.HHServerClasses.Tasks.WebHelper;
import yosoyo.aaahearhereprototype.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFollowFragment extends Fragment {

	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	public RequestFollowFragment() {
		// Required empty public constructor
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_request_follow, container, false);

		recyclerView = (RecyclerView) view.findViewById(R.id.fragment_request_follow_recycler_view);

		layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);

		FollowRequestAdapter.AdapterCallback adapterCallback = new FollowRequestAdapter.AdapterCallback() {
			@Override
			public void requestAccepted(final HHFollowRequestUser followRequest, final int position) {

				HHUser.getCurrentUser().getFollowInRequests().remove(followRequest);
				adapter.notifyItemRemoved(position);

				AsyncDataManager.updateCurrentUser(
					new AsyncDataManager.UpdateCurrentUserCallback() {
						@Override
						public void returnUpdateCurrentUser(boolean success) {
							if (success) {
								getActivity().invalidateOptionsMenu();
							}
						}
					}
				);
			}

			@Override
			public void onUserClick(HHUser user) {

			}
		};

		adapter = new FollowRequestAdapter(
			HHUser.getCurrentUser().getFollowInRequests(),
			adapterCallback);

		recyclerView.setAdapter(adapter);

		return view;
	}

	static class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {
		private final List<HHFollowRequestUser> followRequests;
		private final AdapterCallback adapterCallback;

		interface AdapterCallback {
			void requestAccepted(HHFollowRequestUser followRequest, int position);
			void onUserClick(HHUser user);
		}

		private abstract static class OnClickFollowRequestListener implements View.OnClickListener {

			public HHFollowRequestUser followRequest;

			public void setFollowRequest(HHFollowRequestUser followRequest) {
				this.followRequest = followRequest;
			}

		}

		static class ViewHolder extends RecyclerView.ViewHolder{
			public TextView txtUserName;
			public ImageView imgProfile;
			public ImageView btnAccept;
			public ImageView btnDelete;
			public ProgressBar btnAcceptProgressBar;
			public OnClickFollowRequestListener btnAcceptOnClickListener;
			public int position;
			private final AdapterCallback adapterCallback;

			public ViewHolder(View view, final AdapterCallback adapterCallback){
				super(view);
				this.adapterCallback = adapterCallback;
				txtUserName = (TextView) view.findViewById(R.id.rv_row_follow_request_txtUserName);
				imgProfile = (ImageView) view.findViewById(R.id.rv_row_follow_request_imgProfile);
				btnAccept = (ImageView) view.findViewById(R.id.rv_row_follow_request_btnAccept);
				btnDelete = (ImageView) view.findViewById(R.id.rv_row_follow_request_btnDelete);
				btnAcceptProgressBar = (ProgressBar) view.findViewById(R.id.rv_row_follow_request_btnAccept_progress);
				btnAcceptOnClickListener = new OnClickFollowRequestListener(){
					@Override
					public void onClick(View v) {
						btnAccept.setVisibility(View.GONE);
						btnAcceptProgressBar.setVisibility(View.VISIBLE);
						AsyncDataManager.acceptFollowRequest(
							this.followRequest,
							new AsyncDataManager.AcceptFollowRequestCallback() {
								@Override
								public void returnAcceptFollowRequest(boolean success, HHFollowRequestUser followRequest) {
									if (success){
										adapterCallback.requestAccepted(followRequest, position);
									}
									btnAccept.setVisibility(View.VISIBLE);
									btnAcceptProgressBar.setVisibility(View.GONE);
								}
							});
					}
				};
				btnAccept.setOnClickListener(btnAcceptOnClickListener);
			}
		}

		public FollowRequestAdapter(List<HHFollowRequestUser> followRequests, AdapterCallback adapterCallback){
			this.followRequests = followRequests;
			this.adapterCallback = adapterCallback;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
									  .inflate(R.layout.rv_row_follow_request, parent, false);
			ViewHolder viewHolder = new ViewHolder(view, adapterCallback);

			return viewHolder;
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			HHFollowRequestUser followRequest = followRequests.get(position);
			holder.position = position;
			holder.btnAcceptOnClickListener.setFollowRequest(followRequest);
			holder.txtUserName.setText(followRequest.getUser().getName());

			// get User Image
			WebHelper.getFacebookProfilePicture(
				followRequest.getUser().getFBUserID(),
				new WebHelper.GetFacebookProfilePictureCallback() {
					@Override
					public void returnFacebookProfilePicture(Bitmap bitmap) {
						holder.imgProfile.setImageBitmap(bitmap);
					}
				});
		}

		@Override
		public int getItemCount() {
			return followRequests.size();
		}

	}

}
