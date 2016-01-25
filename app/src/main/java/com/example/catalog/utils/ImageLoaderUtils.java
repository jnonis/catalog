package com.example.catalog.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Utilities for Image Loader.
 */
public class ImageLoaderUtils {

    /** Default display options. */
    private static final DisplayImageOptions.Builder getDefaultDisplayOptionsBuilder() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(200, true, true, false));
    }

    /**
     * Fetch an image from given url and display it in given view.
     * This method is asynchronous, and use cache.
     *
     * @param url image url.
     * @param view image view.
     */
    public static void displayImage(String url, ImageView view) {
        ImageLoader.getInstance().displayImage(url, view,
                getDefaultDisplayOptionsBuilder().build());
    }
}