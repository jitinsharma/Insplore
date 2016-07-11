package io.github.jitinsharma.insplore.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static io.github.jitinsharma.insplore.data.InContract.PoiEntry;

/**
 * Created by jitin on 06/07/16.
 */
public class InProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    static final int POI = 101;
    private InDBHelper inDBHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, InContract.PATH_POI, POI);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        inDBHelper = new InDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case POI:
                cursor = inDBHelper.getReadableDatabase().query(
                        PoiEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case POI:
                return PoiEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Invalid uri" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = inDBHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case POI:
                Long _id = sqLiteDatabase.insert(PoiEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = PoiEntry.buildPoiUri(_id);
                }
                else{
                    throw new SQLException("unable to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = inDBHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match){
            case POI:
                rowsDeleted = sqLiteDatabase.delete(PoiEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = inDBHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case POI:
                rowsUpdated = sqLiteDatabase.update(PoiEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
