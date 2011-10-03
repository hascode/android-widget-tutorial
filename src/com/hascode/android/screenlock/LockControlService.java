package com.hascode.android.screenlock;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.RemoteViews;

public class LockControlService extends Service {
	public static final String EXTRA_LOCK_ACTIVATED = "com.hascode.android.lockwidget.activated";
	public static final String ACTION_LOCK = "com.hascode.android.lockwidget.lock";
	public static final String ACTION_UNLOCK = "com.hascode.android.lockwidget.unlock";
	public static final String ACTION_STATUS = "com.hascode.android.lockwidget.status";

	private static final int DEFAULT_TIMEOUT_MILLIS = 6000;
	private static final String APP_TAG = "com.hascode.android.lockwidget";

	@Override
	public void onStart(Intent intent, int startId) {
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		// fetch widgets to be updated
		int[] widgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		if (widgetIds.length > 0) {
			// fetching timeout, setting status
			for (int widgetId : widgetIds) {
				RemoteViews remoteViews = new RemoteViews(getPackageName(),
						R.layout.main);
				if (intent.hasExtra(EXTRA_LOCK_ACTIVATED)) {
					Log.d(APP_TAG, "intent has extra enableLock");
					if (intent.getBooleanExtra(EXTRA_LOCK_ACTIVATED, true)) {
						lockOn();
					} else {
						lockOff();
					}
				} else {
					Log.d(APP_TAG, "intent has no extra enableLock");
				}
				printStatus(getApplicationContext(), remoteViews);
				widgetManager.updateAppWidget(widgetId, remoteViews);
			}
			stopSelf();
		}
		super.onStart(intent, startId);
	}

	private void lockOff() {
		Log.d(APP_TAG, "lock settings from intent: enable=true");
		Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, -1);
	}

	private void lockOn() {
		Log.d(APP_TAG, "lock settings from intent: enable=false");
		Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, DEFAULT_TIMEOUT_MILLIS);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void printStatus(final Context context,
			final RemoteViews remoteViews) {
		int screenTimeoutMillis = 0;
		CharSequence status = getText(R.string.configError);
		try {
			screenTimeoutMillis = android.provider.Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			Log.e(APP_TAG, "reading settings failed");
		}

		if (-1 == screenTimeoutMillis) {
			status = getText(R.string.screenIsUnlocked);
		} else {
			status = getText(R.string.screenIsLocked);
		}
		remoteViews.setTextViewText(R.id.tvTime, status);
	}
}
