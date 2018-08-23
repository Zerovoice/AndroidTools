package com.zorro.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.LruCache;
import android.util.SparseArray;
import android.widget.ImageView;

import com.zorro.imagecache.ImageLoader;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DatabaseImageLoader implements Callback {
    private final int MSG_IMAGE_LOADED = 0;
    private final int MSG_IMAGE_NOT_EXIST = 1;
    private final Handler handler;
    private final ExecutorService executor;
    private final LruCache<Integer, Bitmap> cache;
    private final SparseArray<ImageView> imageViews = new SparseArray<ImageView>();
    private final Context ctx;

    private int mkind = Thumbnails.MINI_KIND;

    public DatabaseImageLoader(Context ctx) {
        handler = new Handler(this);
        executor = new ThreadPoolExecutor(2, 2, 0L,
                TimeUnit.MILLISECONDS, new ImageLoader.BlockingStack());
        cache = new LruCache<Integer, Bitmap>(200);
        this.ctx = ctx;
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        if (MSG_IMAGE_LOADED == what) {
            int id = msg.arg1;
            ImageView v = imageViews.get(id);
            if (v != null) {
                imageViews.remove(id);
                int currentId = (Integer) v.getTag();
                if (id == currentId) {
                    v.setImageBitmap(cache.get(id));
                }
            }
        } else if (MSG_IMAGE_NOT_EXIST == what) {
            int id = msg.arg1;
            imageViews.remove(id);
        }
        return false;
    }

    public void close() {
        handler.removeCallbacksAndMessages(null);
        executor.shutdownNow();
        cache.evictAll();
        imageViews.clear();
    }

    public void loadImage(final int id, final ImageView v, final int orientation) {
        v.setTag(id);
        Bitmap bitmap = cache.get(id);
        if (bitmap != null) {
            v.setImageBitmap(bitmap);
        } else {
            v.setImageBitmap(null);
            imageViews.put(id, v);
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    Bitmap bitmap = readBitmap(id);
                    if (bitmap == null) {
                        Message.obtain(handler, MSG_IMAGE_NOT_EXIST, id, 0)
                                .sendToTarget();
                        return;
                    }
                    if (orientation != 0) {
                        Matrix m = new Matrix();
                        m.postRotate(orientation);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
                    }
                    cache.put(id, bitmap);
                    Message.obtain(handler, MSG_IMAGE_LOADED, id, 0)
                            .sendToTarget();
                }

            });
        }
    }

    public void setImageKind(int kind) {
        mkind = kind;
    }
    public Bitmap readBitmap(final int id) {
        try {
            Bitmap bitmap = Thumbnails.getThumbnail(
                    ctx.getContentResolver(), id, 0,
                    mkind, null);
            return bitmap;
        } catch (ConcurrentModificationException e){
            e.printStackTrace();
            return null;
        }
    }
}