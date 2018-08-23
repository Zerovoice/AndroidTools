package com.zorro.imagecache;

import android.graphics.Bitmap;
import android.view.View;

public interface IImageLoader {

    public interface Callback<T extends View> {
        boolean handleDownloaded(String uri, String path);
        void onLoaded(String uri, T v, Bitmap bitmap);
        void onLoadFailed(String uri, T v);
    }

    <T extends View> void loadImage(String uri, T v, int defaultResId, Callback<T> cb);

    void loadImage(String uri, View v, int defaultResId);

    void loadImage(String uri, Callback<View> cb);

    Bitmap getCachedBitmap(String path);

    void invalidImage(String uri);

    <T extends View> void invalidView(T t);

    void close();
}
