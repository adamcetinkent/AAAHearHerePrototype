package yosoyo.aaahearhereprototype;

import android.annotation.SuppressLint;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.gson.Gson;

/**
 * Created by adam on 07/03/16.
 */
@SuppressLint("ParcelCreator")
public class AddressResultReceiver extends ResultReceiver {

	public interface AddressResultReceiverCallback{
		void returnAddress(Address address);
	}

	private AddressResultReceiverCallback callback;

	public AddressResultReceiver(Handler handler, AddressResultReceiverCallback callback){
		super(handler);
		this.callback = callback;
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData){
		Address address = new Gson().fromJson(resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY), Address.class);

		callback.returnAddress(address);
	}
}
