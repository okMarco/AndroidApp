package com.hochan.tumlodr.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.Window;

import com.android.vending.billing.IInAppBillingService;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseDrawerActivity;
import com.hochan.tumlodr.ui.component.listener.BubbleOnTouchListener;
import com.hochan.tumlodr.ui.fragment.SettingFragment;
import com.hochan.tumlodr.util.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.os.Build.VERSION_CODES.M;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isMEIZU;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isMIUI;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isOPPO;

public class SettingActivity extends BaseDrawerActivity {

	IInAppBillingService mService;
	private ServiceConnection mServiceConn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mServiceConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name,
			                               IBinder service) {
				mService = IInAppBillingService.Stub.asInterface(service);

				Observable.create(new ObservableOnSubscribe<Bundle>() {
					@Override
					public void subscribe(ObservableEmitter<Bundle> e) throws Exception {
						ArrayList<String> skuList = new ArrayList<>();
						skuList.add("premiumUpgrade");
						skuList.add("gas");
						Bundle querySkus = new Bundle();
						querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
						Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
						e.onNext(skuDetails);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
						.subscribe(new BaseObserver<Bundle>() {
							@Override
							public void onNext(Bundle skuDetails) {
								int response = skuDetails.getInt("RESPONSE_CODE");
								if (response == 0) {
									ArrayList<String> responseList
											= skuDetails.getStringArrayList("DETAILS_LIST");

									for (String thisResponse : responseList) {
										JSONObject object;
										try {
											object = new JSONObject(thisResponse);
											String sku = object.getString("productId");
											String price = object.getString("price");
											if ("tumlodr_pro".equals(sku)) {

											}
										} catch (JSONException e) {
											e.printStackTrace();
										}

									}
								}
							}
						});

			}
		};

		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected String getTitleString() {
		return getString(R.string.activity_title_setting);
	}

	@Override
	public Fragment getContentFragment(int id) {
		SettingFragment settingFragment = (SettingFragment) getSupportFragmentManager().findFragmentByTag("setting_fragment");
		if (settingFragment == null) {
			settingFragment = new SettingFragment();
		}
		return settingFragment;
	}

	@Override
	public void onToolbarClick() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mService != null) {
			unbindService(mServiceConn);
		}
	}

}
