package io.github.jitinsharma.insplore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static io.github.jitinsharma.insplore.data.InContract.PoiEntry;

/**
 * Created by jitin on 06/07/16.
 */
public class InDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION  = 1;
    static final String DATABASE_NAME = "poi.db";

    public InDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_POI_TABLE = "CREATE TABLE " + PoiEntry.TABLE_NAME + " (" +
                PoiEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PoiEntry.COLUMN_POI_TITLE + " TEXT NOT NULL, " +
                PoiEntry.COLUMN_POI_DESC + " TEXT NOT NULL, " +
                PoiEntry.COLUMN_POI_IMAGE + " BLOB, " +
                PoiEntry.COLUMN_POI_LAT + " TEXT NOT NULL, " +
                PoiEntry.COLUMN_POI_LONG + " TEXT NOT NULL, " +
                PoiEntry.COLUMN_GEO_ID + " TEXT NOT NULL, " +
                PoiEntry.COLUMN_POI_WIKI_LINK + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_POI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + PoiEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
