package com.example.catalog.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.catalog.provider.AppContract;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * This service fetch catalog data from web service.
 */
public class ApiIntentService extends IntentService {
    /** Log tag. */
    private static final String TAG = ApiIntentService.class.getSimpleName();

    /** Action for result intent. */
    public static final String ACTION_SERVICE_FINISHED = "ACTION_SERVICE_FINISHED";
    /** Result extra. */
    public static final String EXTRA_RESULT = "EXTRA_RESULT";
    /** Indicates that service finished successfully. */
    public static final int RESULT_OK = 1;
    /** Indicates that a connection error has happened. */
    public static final int RESULT_NETWORK_FAIL = 2;
    /** Indicates that a application error has happened. */
    public static final int RESULT_APP_FAIL = 3;
    /** Indicates that a server error has happened. */
    public static final int RESULT_SERVICE_FAIL = 4;

    /** Catalog service url. */
    private static final String CATALOG_URL = "https://api.something.com/catalog";

    /** Response parser. */
    private ApiResponseParser mParser;

    /** Constructor. */
    public ApiIntentService() {
        super(ApiIntentService.class.getSimpleName());
        mParser = new ApiResponseParser();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        StringBuilder urlString = new StringBuilder(CATALOG_URL);
        Bundle result = new Bundle();
        InputStream in = null;
        try {
            // Execute request.
            HttpHelper.Response response = HttpHelper.get(getApplicationContext(), "");

            // Check response.
            Log.d(TAG, "The response in: " + response);
            if (response.getStatus() == HttpsURLConnection.HTTP_OK) {
                in = response.getBody();
                ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                // Parse response.
                mParser.parse(in, operations);
                // Execute operations.
                getContentResolver().applyBatch(AppContract.CONTENT_AUTHORITY, operations);
                result.putInt(EXTRA_RESULT, RESULT_OK);
            } else {
                result.putInt(EXTRA_RESULT, RESULT_SERVICE_FAIL);
            }
        } catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            result.putInt(EXTRA_RESULT, RESULT_NETWORK_FAIL);
        } catch (Exception e) {
            Log.e(TAG, "App error", e);
            result.putInt(EXTRA_RESULT, RESULT_APP_FAIL);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        // Notify results.
        Intent resultIntent = new Intent(ACTION_SERVICE_FINISHED);
        resultIntent.putExtras(result);
        sendBroadcast(resultIntent);
    }
}
