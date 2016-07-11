package io.github.jitinsharma.insplore.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jitin on 06/07/16.
 */
public class InContract {
    public static final String CONTENT_AUTHORITY = "io.github.jitinsharma.insplore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_POI = "poi";

    public static final class PoiEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendEncodedPath(PATH_POI).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POI;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POI;
        public static final String TABLE_NAME = "poi";
        public static final String COLUMN_POI_TITLE = "poi_title";
        public static final String COLUMN_POI_IMAGE = "poi_image";
        public static final String COLUMN_POI_WIKI_LINK = "poi_wiki_link";
        public static final String COLUMN_POI_LAT = "poi_lat";
        public static final String COLUMN_POI_LONG = "poi_long";
        public static final String COLUMN_POI_DESC = "poi_description";
        public static final String COLUMN_GEO_ID = "geoname_id";

        public static Uri buildPoiUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
