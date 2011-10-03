package com.hascode.android.screenlock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class LockControlWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// fetching our remote views
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);

		// unlock intent
		final Intent unlockIntent = createIntent(context, appWidgetIds);
		unlockIntent.putExtra(LockControlService.EXTRA_LOCK_ACTIVATED, false);
		unlockIntent.setAction(LockControlService.ACTION_UNLOCK);
		final PendingIntent pendingUnlockIntent = createPendingIntent(context,
				unlockIntent);

		// lock intent
		final Intent lockIntent = createIntent(context, appWidgetIds);
		lockIntent.putExtra(LockControlService.EXTRA_LOCK_ACTIVATED, true);
		unlockIntent.setAction(LockControlService.ACTION_LOCK);
		final PendingIntent pendingLockIntent = createPendingIntent(context,
				lockIntent);

		// status intent
		final Intent statusIntent = createIntent(context, appWidgetIds);
		statusIntent.setAction(LockControlService.ACTION_LOCK);
		final PendingIntent pendingStatusIntent = createPendingIntent(context,
				statusIntent);

		// bind click events to the pending intents
		remoteViews.setOnClickPendingIntent(R.id.txtOn, pendingLockIntent);
		remoteViews.setOnClickPendingIntent(R.id.txtOff, pendingUnlockIntent);
		remoteViews.setOnClickPendingIntent(R.id.widget_root,
				pendingStatusIntent);

		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		context.startService(statusIntent);
	}

	private Intent createIntent(Context context, int[] appWidgetIds) {
		Intent updateIntent = new Intent(context.getApplicationContext(),
				LockControlService.class);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetIds);
		return updateIntent;
	}

	private PendingIntent createPendingIntent(Context context,
			Intent updateIntent) {
		PendingIntent pendingIntent = PendingIntent.getService(
				context.getApplicationContext(), 0, updateIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

}
