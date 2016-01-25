package com.example.catalog.service;

import android.content.ContentProviderOperation;
import android.content.ContentValues;

import com.example.catalog.provider.AppContract;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Parse the json content from a input stream and create the {@link
 * android.content.ContentProviderOperation} for the results.
 */
public class ApiResponseParser {
    /** Items json attribute. */
    private static final String ITEMS = "items";
    /** Name json attribute. */
    private static final String NAME = "name";
    /** Image url json attribute. */
    private static final String IMAGE_URL = "imageUrl";

    /**
     * Parse the json content from input stream and returns the content provider
     * operations for parsed entries.
     *
     * @param in the input stream.
     * @param operations operation list.
     * @return the id of next page.
     * @throws IOException in case of connection error.
     */
    public void parse(InputStream in, ArrayList<ContentProviderOperation> operations)
            throws IOException {
        // Remove old data.
        operations.add(ContentProviderOperation.newDelete(
                AppContract.Items.CONTENT_URI).build());

        JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case ITEMS:
                    parseItems(reader, operations);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
    }

    /** Parse items. */
    private void parseItems(JsonReader reader, ArrayList<ContentProviderOperation> operations)
            throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            parseItem(reader, operations);
        }
        reader.endArray();
    }

    /** Parse a item. */
    private void parseItem(JsonReader reader, ArrayList<ContentProviderOperation> operations)
            throws IOException {
        ContentValues values = new ContentValues();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case NAME:
                    values.put(AppContract.Items.NAME, nextStringSafe(reader));
                    break;
                case IMAGE_URL:
                    values.put(AppContract.Items.IMAGE_URL, nextStringSafe(reader));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        operations.add(ContentProviderOperation.newInsert(AppContract.Items.CONTENT_URI)
                .withValues(values).build());
    }

    /**
     * Utility to read an string safely in case of null content.
     *
     * @param reader reader with the content to onParseResponse.
     * @return a string or null case of null content.
     * @throws java.io.IOException in case of error reading from stream.
     */
    private String nextStringSafe(final JsonReader reader) throws
            IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return reader.nextString();
    }
}
