package io.github.jitinsharma.insplore.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.activities.MainActivity;
import io.github.jitinsharma.insplore.activities.SearchActivity;
import io.github.jitinsharma.insplore.data.InContract;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.storage.TinyDB;
import io.github.jitinsharma.insplore.utilities.Utils;

/**
 * Created by jitin on 17/07/16.
 */
public class InWidgetService extends IntentService{
    public static final String[] POI_COLUMNS = {
            InContract.PoiEntry._ID,
            InContract.PoiEntry.COLUMN_POI_TITLE,
            InContract.PoiEntry.COLUMN_POI_DESC,
            InContract.PoiEntry.COLUMN_POI_IMAGE,
            InContract.PoiEntry.COLUMN_POI_LAT,
            InContract.PoiEntry.COLUMN_POI_LONG,
            InContract.PoiEntry.COLUMN_GEO_ID,
            InContract.PoiEntry.COLUMN_POI_WIKI_LINK
    };
    Cursor cursor;
    TinyDB tinyDB;
    String poiName;
    byte[] imageArray;

    public InWidgetService(){
        super("InWidgetService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, InWidgetProvider.class));
        tinyDB = new TinyDB(this);
        String cityName = tinyDB.getString(Constants.SAVED_CITY);
        cursor = getContentResolver().query(
                InContract.PoiEntry.CONTENT_URI,
                POI_COLUMNS,
                null,
                null,
                null
        );

        if (cursor!=null && cursor.moveToLast()){
            poiName = cursor.getString(cursor.getColumnIndex(InContract.PoiEntry.COLUMN_POI_TITLE));
            imageArray = cursor.getBlob(cursor.getColumnIndex(InContract.PoiEntry.COLUMN_POI_IMAGE));
        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.in_widget);
            views.setTextViewText(R.id.widget_city_name, cityName);
            if (poiName!=null) {
                views.setTextViewText(R.id.widget_poi_title, poiName);
            }
            else{
                views.setTextViewText(R.id.widget_poi_title, getBaseContext().getString(R.string.empty_text));
            }
            if (imageArray!=null) {
                views.setImageViewBitmap(R.id.widget_poi_image, Utils.convertBytesToBitmap(imageArray));
            }

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_city_name, pendingIntent);

            Intent savedPoiIntent = new Intent(this, SearchActivity.class);
            intent.putExtra(Constants.INTENT_TYPE, Constants.SAVED_POI);
            savedPoiIntent.setAction("io.github.jitinsharma.insplore.widget");
            PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, savedPoiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_saved_poi, pendingIntent1);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
