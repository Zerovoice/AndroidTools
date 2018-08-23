package com.zorro.imagecache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;


import com.zorro.tools.StreamUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ImageLoader implements IImageLoader {
	private static final byte[] IMG_BUF = new byte[16 * 1024];
	private static final String TAG = "ImageLoader";

	public static final class BlockingStack extends LinkedBlockingDeque<Runnable> {
		private static final long serialVersionUID = 3077829709719998005L;

		public BlockingStack() {
			super();
		}
		
		public BlockingStack(int capacity) {
			super(capacity);
		}
		
		@Override
		public Runnable poll(long timeout, TimeUnit unit)
				throws InterruptedException {
			return super.pollLast(timeout, unit);
		}
		
		@Override
		public Runnable take() throws InterruptedException {
			return super.takeLast();
		}
	}
	final class LoadFailedCallback<T extends View> implements Runnable {
		private final Callback<T> cb;
		private final String uri;
		private final T v;
		
		private LoadFailedCallback(Callback<T> cb, T v, String uri) {
			this.cb = cb;
			this.v = v;
			this.uri = uri;
		}

		@Override
		public void run() {
			imageViews.remove(v);
			if (cb != null) cb.onLoadFailed(uri, v); 
		}
	}
	final class FetchHttpTask<T extends View> implements Runnable {

		private final Callback<T> cb;
		private final T v;
		private final String uri;

		private FetchHttpTask(Callback<T> cb, T v, String uri) {
			this.cb = cb;
			this.v = v;
			this.uri = uri;
		}

		@Override
		public void run() {
			final Bitmap bitmap = getMemoryCached(uri);
			if (bitmap != null) {
				uiHandler.post(new DisplayCallback<T>(cb, uri, v, bitmap,
						imageViews));
			} else {
				String cachePath = fileCache.get(uri);
				if (cachePath != null) {
					decExecutor.execute(new DecodeTask<T>(uri, v, cb, cachePath));
				} else {
					String filename = encodeUrl(uri);
					File destFile = new File(cacheDir, filename);
					String destPath = destFile.getAbsolutePath();
					if (destFile.exists()) {
						onCachedFileAvailable(uri, v, cb, destPath);
					} else {
						try {
							downloadImage(destFile, destPath);
						} catch (IOException e) {
							Log.w(TAG, "download image file faild: " + uri, e);
							uiHandler.post(new LoadFailedCallback<T>(cb, v, uri));
							if (destFile.exists()) destFile.delete();
						}
					}

				}

			}
		}

		final HashMap<String, AtomicInteger> downloadingMap	= new HashMap<>();

		void downloadImage(File destFile, String destPath) throws IOException {
			OutputStream tmpOutput = null;
			HttpURLConnection conn = null;
			InputStream tmpInput = null;
			OutputStream destOutput = null;
			File tmpFile = null;
			long before = System.currentTimeMillis();
			long respContentLength = 0;
			try {
				AtomicInteger lock		= null;
                synchronized (downloadingMap) {
                    lock	= downloadingMap.get(uri);
					if (null == lock) {
						lock	= new AtomicInteger();
						lock.set(1);
						downloadingMap.put(uri,lock);
					} else {
						lock.addAndGet(1);
					}
				}
				synchronized (lock) { // JAVA上string，相同的String 对象是同一个，所以这里只要锁住这个String就可以了
                    if (!destFile.exists()) {
						tmpFile = File.createTempFile(UUID.randomUUID()
								.toString(), null);

						conn = (HttpURLConnection) new URL(uri)
								.openConnection();

						conn.setConnectTimeout(10000);
						conn.setReadTimeout(30000);
						conn.setRequestMethod("GET");
						conn.setDoOutput(false);
						conn.setDoInput(true);
						conn.setInstanceFollowRedirects(true);
						Log.w(TAG, "create connection " + HttpURLConnection.getFollowRedirects());
						respContentLength = conn.getContentLength();
						if (respContentLength == 0) {
							String redirectUri = conn.getHeaderField("Location");
							if (null != redirectUri && redirectUri.length() > 0) {
								conn.disconnect();
								conn = (HttpURLConnection) new URL(redirectUri).openConnection();
								conn.setConnectTimeout(10000);
								conn.setReadTimeout(30000);
								conn.setRequestMethod("GET");
								conn.setDoOutput(false);
								conn.setDoInput(true);
								conn.setInstanceFollowRedirects(true);
								respContentLength = conn.getContentLength();
								Log.w(TAG, "create connection redirect " + conn.getResponseCode() + " " + respContentLength);
							}
						}

						InputStream httpInput = new BufferedInputStream(conn.getInputStream());
						tmpOutput = new BufferedOutputStream(
								new FileOutputStream(tmpFile));
						StreamUtil.copyStream(httpInput, tmpOutput);

						tmpInput = new BufferedInputStream(
								new FileInputStream(tmpFile));
						destOutput = new BufferedOutputStream(
								new FileOutputStream(destFile));
						StreamUtil.copyStream(tmpInput, destOutput);

						Log.w(TAG, "create connection result " + conn.getResponseCode() + " " + conn.getContentLength() + " filePath = " + destFile.getAbsolutePath());

						if (respContentLength > 0 && destFile.exists()) {
							if (destFile.length() != respContentLength) {
								destFile.delete();
								String w = "Downloaded file length is not equals response content-length." +
										" file lenth:" + destFile.length() + ", content-length:" + respContentLength + ",uri:" + uri;
								Log.w(TAG, w);
								throw new IOException(w);
							}
						}
					}
				}
				synchronized (downloadingMap) {
					lock.getAndDecrement();
					if (lock.intValue() == 0) {
						downloadingMap.remove(uri);
					}
				}

				onCachedFileAvailable(uri, v, cb, destPath);

			} finally {
				
				if (conn != null) {
					conn.disconnect();
				}
				StreamUtil.closeQuietly(tmpOutput, tmpInput,
						destOutput);
				if (tmpFile != null && tmpFile.exists()) {
					if (!tmpFile.delete()) {
						Log.w(TAG, "delete tmp file failed " + tmpFile.getAbsolutePath());
					}
				}
				long cost = System.currentTimeMillis() - before;
				if (cost > 200) {
					Log.w(TAG, String.format("download image cost %dms on %s", cost, uri));
				}
			}
		}

		void onCachedFileAvailable(String uri, T v, Callback<T> cb,
                                   String destPath) {
			if (cb != null && cb.handleDownloaded(uri, destPath)) {
				return;
			}
			decExecutor.execute(new DecodeTask<T>(uri, v, cb, destPath));
		}
	}

	final class DecodeTask<T extends View> implements Runnable {
		private final String uri;
		private final T v;
		private final Callback<T> cb;
		private final String cachePath;

		private DecodeTask(String uri, T v, Callback<T> cb,
                           String cachePath) {
			this.uri = uri;
			this.v = v;
			this.cb = cb;
			this.cachePath = cachePath;
		}

		@Override
		public void run() {
			final Bitmap bitmap = getMemoryCached(uri);
			if (bitmap != null) {
				uiHandler.post(new DisplayCallback<T>(cb, uri, v, bitmap,
						imageViews));
			} else {
				Options opts = new Options();
				opts.inTempStorage = IMG_BUF;
				Bitmap bm = BitmapFactory.decodeFile(cachePath, opts);
				if (bm != null) {
					memCache.put(uri, bm);
					uiHandler.post(new DisplayCallback<T>(cb, uri, v, bm,
							imageViews));
				} else {
					Log.e(TAG, String.format("decode image file failed, uri=%s, cachefile=%s", uri, cachePath));
					uiHandler.post(new LoadFailedCallback<T>(cb, v, uri));
				}
			}
		}
	}

	static class DisplayCallback<T extends View> implements Runnable {
		private final Callback<T> cb;
		private final String path;
		private final T v;
		private final Bitmap bitmap;
		private final Map<View, String> imageViews;

		private DisplayCallback(Callback<T> cb, String path, T v,
                                Bitmap bitmap, Map<View, String> imageViews) {
			this.cb = cb;
			this.path = path;
			this.v = v;
			this.bitmap = bitmap;
			this.imageViews = imageViews;
		}

		@Override
		public void run() {
			if (validViewPath(path, imageViews, v)) {
				if (v instanceof ImageView) {
					((ImageView) v).setImageBitmap(bitmap);
				}
				if (cb != null) {
					cb.onLoaded(path, v, bitmap);
				}
			} else {
				if (cb != null) {
					cb.onLoaded(path, null, bitmap);
				}
			}
		}
	}

	private static ImageLoader instance = new ImageLoader();
	private Handler uiHandler;
	private ExecutorService httpExecutor;
	private ExecutorService decExecutor;
	private LruCache<String, Bitmap> memCache;
	private Map<View, String> imageViews;
	private LruCache<String, String> fileCache;
	private File cacheDir;

	public static ImageLoader getInstance() {
		return instance;
	}

	private ImageLoader() {
		uiHandler = new Handler(Looper.getMainLooper());
		Runtime runtime = Runtime.getRuntime();
		int maxThreads = runtime.availableProcessors() / 2;
		httpExecutor = new ThreadPoolExecutor(maxThreads, maxThreads, 0L,
				TimeUnit.MILLISECONDS, new BlockingStack());

		decExecutor = Executors.newSingleThreadExecutor();
		long maxMemory = runtime.maxMemory() / 4;
		int maxSize = 52428800;  // 50m
	
		//maxMemory > Integer.MAX_VALUE ? Integer.MAX_VALUE
		//		: (int) maxMemory;
		Logger.getLogger("ImageLoader").info("maxSize = " + maxSize);
		memCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
		imageViews = new WeakHashMap<View, String>();
		
		fileCache = new LruCache<String, String>(200);
	}

	public void init(Context ctx) {
		File dir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			dir = ctx.getExternalCacheDir();
		} else {
			dir = ctx.getCacheDir();
		}
		cacheDir = new File(dir, "imagecache");
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
	}

	@Override
	public <T extends View> void loadImage(String uri, T v, int defResId, Callback<T> cb) {
		if (uri != null && !uri.isEmpty()) {
			Bitmap bitmap = getMemoryCached(uri);
			if (bitmap != null) {
				if (v instanceof ImageView) {
					((ImageView) v).setImageBitmap(bitmap);
				}
				if (cb != null) cb.onLoaded(uri, v, bitmap);
			} else {
				if (v != null) {
					if (v instanceof ImageView && defResId != 0) {
						((ImageView) v).setImageResource(defResId);
					}
					imageViews.put(v, uri);
				}
				httpExecutor.execute(new FetchHttpTask<T>(cb, v, uri));
			}
		} else {
			if (v != null) {
				if (v instanceof ImageView && defResId != 0) {
					((ImageView) v).setImageResource(defResId);
				}
			}
			if (cb != null) cb.onLoadFailed(uri, v);		
        }
	}

	private Bitmap getMemoryCached(String uri) {
		synchronized (memCache) {
			Bitmap bitmap = memCache.get(uri);
			if (bitmap != null) {
				if (bitmap.isRecycled()) {
					memCache.remove(uri);
					bitmap = null;
				}
			}
			return bitmap;
		}
	}

	@Override
	public void loadImage(String path, View v, int defaultResId) {
		loadImage(path, v, defaultResId, null);
	}

	@Override
	public void loadImage(String path, Callback<View> cb) {
		loadImage(path, null, 0, cb);
	}

	@Override
	public Bitmap getCachedBitmap(String path) {
		return getMemoryCached(path);
	}
	
	public String getFilePathByUri(String uri){
	    String filename = encodeUrl(uri);
        File destFile = new File(cacheDir, filename);

        if (destFile != null) {
			return destFile.getAbsolutePath();
		}

		return null;
	}

	static boolean validViewPath(String path,
                                 Map<View, String> imageViews, View v) {
		return path.equals(imageViews.get(v));
	}

	static String encodeUrl(String url) {
		return String.valueOf(url.hashCode());
	}

	@Override
	public void invalidImage(String uri) {
		memCache.remove(uri);
		fileCache.remove(uri);
	}

	@Override
	public <T extends View> void invalidView(T t) {
		this.imageViews.remove(t);
	}

	@Override
	public void close() {
		uiHandler.removeCallbacksAndMessages(null);
		httpExecutor.shutdownNow();
		decExecutor.shutdownNow();
		memCache.evictAll();
		fileCache.evictAll();
	}
}
