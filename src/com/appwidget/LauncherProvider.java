package com.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.jeffen.note.NoteEditActivity;
import com.jeffen.note.NoteListActivity;
import com.jeffen.note.R;

public class LauncherProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			Intent intent = new Intent(context, NoteListActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.appwidget_launcher);
			views.setOnClickPendingIntent(R.id.ibApp, pendingIntent);
			
			// go to search note list
			Intent intentSearch = new Intent(context, NoteListActivity.class);
			intentSearch.putExtra("IS_SEARCH", true);
			PendingIntent pendingIntentSearch = PendingIntent.getActivity(
					context, 0, intentSearch, 0);
			views.setOnClickPendingIntent(R.id.ibSearch, pendingIntentSearch);

			// go to alarm note list
			Intent intentAlarm = new Intent(context, NoteListActivity.class);
			intentAlarm.putExtra("IS_ALARM", true);
			PendingIntent pendingIntentAlarm = PendingIntent.getActivity(
					context, 0, intentAlarm, 0);
			views.setOnClickPendingIntent(R.id.ibAlarm, pendingIntentAlarm);

			// go to star note list
			Intent intentStar = new Intent(context, NoteListActivity.class);
			intentStar.putExtra("IS_STAR", true);
			PendingIntent pendingIntentStar = PendingIntent.getActivity(
					context, 0, intentStar, 0);
			views.setOnClickPendingIntent(R.id.ibStar, pendingIntentStar);

			// go to note edit activity
			Intent intentPlus = new Intent(context, NoteEditActivity.class);
			PendingIntent pendingIntentPlus = PendingIntent.getActivity(
					context, 0, intentPlus, 0);
			views.setOnClickPendingIntent(R.id.ibPlus, pendingIntentPlus);

			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}