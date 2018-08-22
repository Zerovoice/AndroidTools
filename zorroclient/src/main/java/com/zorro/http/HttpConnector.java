package com.zorro.http;

import android.os.SystemClock;
import android.util.Base64;


import com.zorro.http.request.HttpRequest;
import com.zorro.http.response.HttpResponse;
import com.zorro.http.security.RESTCallStringSign;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;


public class HttpConnector {
    private static final Logger LOGGER = Logger.getLogger(HttpConnector.class.getName());

    protected static ExecutorService executor = Executors.newFixedThreadPool(5);
    protected static ExecutorService executorLowPriority = Executors.newFixedThreadPool(1);

    public static OnStat onStat;


    public interface Callback {
        void onDone(HttpResponse httpResponse);

        void onException(Exception exception);
    }


    static {
        try {
            SSLContext.setDefault(SSLContext.getInstance("TLSv1.2"));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "init TLS v1.2 failed", e);
        }
    }



    protected static HttpResponse connect(HttpRequest request) throws Exception {
        return connect(request, null);
    }

    protected static HttpResponse connect(HttpRequest request, String key) throws Exception {
//        long before = SystemClock.uptimeMillis();
        HttpURLConnection conn = null;
        try {
            URI uri;
            InputStream stream;
            InputStream in;
            boolean hasData = request.hasData();
            byte[] urlData = null;
            if (hasData) {
                urlData = request.getData().array();
            }

            if (key != null) {
                uri = signContentUri(request.getUri(), urlData, key);
            } else {
                uri = signUri(request.getUri(), urlData);
            }
            conn = (HttpURLConnection) new URL(replaceQueryPhoneURLCode(uri.toString())).openConnection();

            conn.setRequestMethod(request.getMethod());
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            for (Entry<String, String> header : request.getHeaders().entrySet()) {
                conn.addRequestProperty((String) header.getKey(), (String) header.getValue());
            }

//            for(String keyv : conn.getHeaderFields().keySet()){
//                LOGGER.info("key:" + keyv + " valure:" + conn.getHeaderFields().get(keyv));
//            }

            conn.addRequestProperty("Accept-Language", Locale.getDefault().toString().replace("_", "-"));
            conn.setDoInput(true);
            conn.setDoOutput(hasData);
            if (hasData) {
                OutputStream bufferedOutputStream = new BufferedOutputStream(conn.getOutputStream());
                WritableByteChannel ch = Channels.newChannel(bufferedOutputStream);
                ch.write(request.getData());
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                ch.close();
            }
            conn.connect();
            HttpResponse response = new HttpResponse();
            response.setCode(conn.getResponseCode());
            if (response.isSuccess()) {
                stream = conn.getInputStream();
            } else {
                stream = conn.getErrorStream();
            }
            if (HttpRequest.ENCODING_GZIP.equals(conn.getContentEncoding())) {
                in = new GZIPInputStream(stream, 8192);
            } else {
                in = new BufferedInputStream(stream);
            }
            byte[] buf = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (true) {
                int count = in.read(buf);
                if (count <= -1) {
                    break;
                }
                out.write(buf, 0, count);
            }
            out.flush();
            ByteBuffer data = ByteBuffer.wrap(out.toByteArray());
            out.close();
            response.setData(data);
            return response;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static URI signUri(URI uri, byte[] urlData) {
        String query;
        boolean hasQuery = uri.getQuery() != null;
        String sign = "sign=" + Base64.encodeToString(RESTCallStringSign.sign(replaceQueryPhoneURLCode(uri.getPath() + (hasQuery ? "?" + uri.getRawQuery() :"BuildConfig.FLAVOR")), urlData), 2);
        if (hasQuery) {
            query = uri.getQuery().concat("&").concat(sign);
        } else {
            query = sign;
        }
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), query, uri.getFragment());
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "sign url error.", e);
            return uri;
        }
    }

    public static URI signContentUri(URI uri, byte[] urlData, String key) {
        String query;
        boolean hasQuery = uri.getQuery() != null;
        String sign = "sign=" + Base64.encodeToString(RESTCallStringSign.contentSign(replaceQueryPhoneURLCode(uri.getPath() + (hasQuery ? "?" + uri.getRawQuery() : "BuildConfig.FLAVOR")), urlData, key), 2);
        if (hasQuery) {
            query = uri.getQuery().concat("&").concat(sign);
        } else {
            query = sign;
        }
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), query, uri.getFragment());
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "sign url error.", e);
            return uri;
        }
    }

    public static String signUri(String serverUrl, byte[] urlData) {
        String signString = serverUrl;
        URI uri = URI.create(serverUrl);
        boolean hasQuery = uri.getQuery() != null;
        String sign = "sign=" + Base64.encodeToString(RESTCallStringSign.sign(replaceQueryPhoneURLCode(uri.getPath() + (hasQuery ? "?" + uri.getRawQuery() : "BuildConfig.FLAVOR")), urlData), 2);
        if (hasQuery) {
            return signString + "&" + sign;
        }
        return signString + "?" + sign;
    }

    public static Future<HttpResponse> postExecute(HttpRequest request, Callback callback) {
        return postExecute(request, callback, null);
    }

    public static Future<HttpResponse> postExecute(final HttpRequest request, final Callback callback, final String key) {
        return executor.submit(new Callable<HttpResponse>() {
            public HttpResponse call() throws Exception {
                long before = SystemClock.uptimeMillis();
                long delay;
                try {
                    HttpResponse response = HttpConnector.connect(request, key);
                    delay = SystemClock.uptimeMillis() - before;
                    if (response == null) {
                        return null;
                    }
                    String respData = "";//BuildConfig.FLAVOR;
                    if (response.getData() != null) {
                        respData = new String(response.getData().array());
                    }
                    HttpConnector.LOGGER.log(Level.INFO, request.getMethod() + " " + response.getCode() + " " + request.getUri().getPath() + " " + respData.length() + " " + delay);
                    if (callback == null) {
                        return response;
                    }
                    callback.onDone(response);
                    return response;
                } catch (Exception e) {
                    if (onStat != null){
                        onStat.postStateEvent("10390", e.getMessage());//打点:网络连接失败
                    }

                    delay = SystemClock.uptimeMillis() - before;
                    HttpConnector.LOGGER.log(Level.WARNING, "HttpConnector, uri: " + request.getUri().getAuthority() + request.getUri().getPath() + ", cost:" + (SystemClock.uptimeMillis() - before) + ", requestMethod:" + request.getMethod() + ", response error, error: ", e);
                    if (callback != null) {
                        callback.onException(e);
                    }
                    throw e;
                }
            }
        });
    }

    public static Future<HttpResponse> postExecuteLowPriority(final HttpRequest request, final Callback callback) {
        return executorLowPriority.submit(new Callable<HttpResponse>() {
            public HttpResponse call() throws Exception {
                try {
                    HttpResponse response = HttpConnector.connect(request);
                    if (response == null) {
                        return null;
                    }
                    if (response.getData() != null) {
                        HttpConnector.LOGGER.log(Level.FINE, "HttpConnector, uri:" + request.getUri().getAuthority() + request.getUri().getPath() + ", requestMethod:" + request.getMethod() + "responseCode:" + response.getCode() + ", response data:" + new String(response.getData().array()));
                    } else {
                        HttpConnector.LOGGER.log(Level.FINE, "HttpConnector, uri:" + request.getUri().getAuthority() + request.getUri().getPath() + ", requestMethod:" + request.getMethod() + "responseCode:" + response.getCode() + ", response data is null.");
                    }
                    if (callback == null) {
                        return response;
                    }
                    callback.onDone(response);
                    return response;
                } catch (Exception e) {
                    HttpConnector.LOGGER.log(Level.WARNING, "HttpConnector, uri:" + request.getUri().getAuthority() + request.getUri().getPath() + ", requestMethod:" + request.getMethod() + "response error, error: ", e);
                    if (callback != null) {
                        callback.onException(e);
                    }
                    throw e;
                }
            }
        });
    }

    private static String replaceQueryPhoneURLCode(String QueryString) {
        return QueryString.replace("phone=+", "phone=%2B");
    }

    public static interface OnStat {
        public void postStateEvent(String key, String message);
    }
}
