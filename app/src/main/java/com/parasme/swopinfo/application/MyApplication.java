package com.parasme.swopinfo.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.onesignal.OneSignal;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.notification.MyNotificationOpenedHandler;
import com.parasme.swopinfo.notification.MyNotificationReceivedHandler;
import com.squareup.picasso.Picasso;


public class MyApplication extends Application {

	private static MyApplication instance;
	public static Context appContext;

	@Override
	public void attachBaseContext(Context base) {
		super.onCreate();
		instance = this;
		appContext = this;
		super.attachBaseContext(base);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
//		if(getSharedPreferences("swopinfo", Context.MODE_PRIVATE).getString(AppConstants.PREF_PLAYER_ID, "").equals("")){
			initOneSignal(this);
//		}
		initImageLoader(getApplicationContext());

	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	public static void initOneSignal(Context context) {
		Log.e("One Signal", "initOneSignal: " );
		OneSignal.startInit(context)
				.setNotificationOpenedHandler(new MyNotificationOpenedHandler())
				.setNotificationReceivedHandler(new MyNotificationReceivedHandler())
				.init();
	}

	public static boolean isConnectingToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)

		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}

	public static synchronized MyApplication getInstance() {
		return instance;
	}

	public static void alertDialog(final Activity activity, final String message, final String title) {
		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
		adb.setMessage(message);
		adb.setTitle(title);
		adb.setCancelable(false);
		adb.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (message.contains("Verify"))
				{
					//activity.startActivity(new Intent(activity,LoginActivity_.class));
					activity.finish();
				}
				else if(title.equalsIgnoreCase("Update Success")){
					dialog.dismiss();
					activity.getFragmentManager().popBackStack();
				}
				else if(title.equalsIgnoreCase("share"))
					activity.finish();
				else
					dialog.dismiss();
			}
		});

		adb.show();
	}

	public static void toggleSoftKeyBoard(Activity activity, boolean hide) {
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = activity.getCurrentFocus();
		if (view == null) {
			return;
		}
		if (hide) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} else {
			inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}

}