package com.example.catalog.service;

import android.content.Context;

import com.example.catalog.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Helper to handle http connections.
 */
public class HttpHelper {
    /** Production environment. */
    private static final int PRODUCTION = 1;
    /** Mock environment. */
    private static final int MOCK = 2;

    public static Response get(Context context, String urlString) throws IOException {
        int response = 0;
        InputStream inputStream = null;
        switch (BuildConfig.ENVIRONMENT) {
            case PRODUCTION: {
                // Setup connection.
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                // Do request.
                connection.connect();
                response = connection.getResponseCode();
                inputStream = connection.getInputStream();
                break;
            }
            case MOCK: {
                response = HttpURLConnection.HTTP_OK;
                inputStream = context.getClassLoader().getResourceAsStream("catalog_service.json");
                break;
            }
        }
        return new Response(response, inputStream);
    }

    public static class Response {
        private int mStatus;
        private InputStream mBody;

        public Response(int status, InputStream body) {
            mStatus = status;
            mBody = body;
        }

        public int getStatus() {
            return mStatus;
        }

        public InputStream getBody() {
            return mBody;
        }
    }
}
