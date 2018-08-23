package com.zorro.imagecache;

import android.graphics.Bitmap;
import android.view.View;

public class SimpleImageLoaderCallback<T extends View> implements IImageLoader.Callback<T> {
    public void onLoaded(String path, T t, Bitmap bitmap) {
    }

    public void onLoadFailed(String path, T t) {
    }

    public boolean handleDownloaded(String uri, String path) {
        return false;
    }
}
