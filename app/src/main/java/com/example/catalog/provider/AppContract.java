package com.example.catalog.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract to communicate with {@link com.example.catalog.provider.AppContentProvider}
 */
public class AppContract {
    /** The authority for app contents. */
    public static final String CONTENT_AUTHORITY = "com.example.catalog.provider";
    /** Base URI to access provider's content. */
    protected static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /** Base content type. */
    protected static final String BASE_CONTENT_TYPE = "vnd.catalog.app.dir/vnd.catalog.";
    /** Base item Content type. */
    protected static final String BASE_CONTENT_ITEM_TYPE = "vnd.catalog.app.item/vnd.catalog.";

    /** Catalog columns. */
    interface ItemColumns {
        /** Item name. */
        String NAME = "name";
        /** Item image url. */
        String IMAGE_URL = "image_url";
    }

    /** Items contract. */
    public static class Items implements ItemColumns, BaseColumns {
        /** Uri Path. */
        static final String PATH = "items";
        /** Content Uri. */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        /** Content type. */
        public static final String CONTENT_TYPE = BASE_CONTENT_TYPE + PATH;
        /** Item Content type. */
        public static final String CONTENT_ITEM_TYPE = BASE_CONTENT_ITEM_TYPE + PATH;

        /** Default projection. */
        public static final String[] DEFAULT_PROJECTION = new String[]{_ID, NAME, IMAGE_URL};
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = NAME + " ASC";

        /** Build {@link android.net.Uri} for requested entity. */
        public static Uri buildUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        /** Extract the id from given {@link android.net.Uri} */
        public static final String getId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
