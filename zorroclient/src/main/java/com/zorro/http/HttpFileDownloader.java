package com.zorro.http;


import com.zorro.http.response.HttpResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class HttpFileDownloader extends HttpConnector {
    private static final Logger LOGGER = Logger.getLogger(HttpFileDownloader.class.getName());
    
    protected static HttpResponse download(URL url, String destFile) throws Exception {
        HttpURLConnection conn = null;
        OutputStream fos = null;
        try {            
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10 * 1000);  //let time out be 10s
            conn.setReadTimeout(10 * 1000);
            
            conn.setDoInput(true);            
            conn.setDoOutput(false);
           
            conn.connect();
            HttpResponse response = new HttpResponse();
            int code = conn.getResponseCode();
            response.setCode(code);
            
            if (response.isSuccess()) {
                File file = new File(destFile);
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    fos = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] buf = new byte[1024];
                    int count = -1;
                    int length = 0;
                    while ((count = in.read(buf)) > -1) {
                        fos.write(buf, 0, count);
                        length += count;
                    }
                    if (length == 0) {
                        throw new DownloadEmptyFileException();
                    }
                    fos.flush();
                } else {
                    LOGGER.warning("cancel duplicate download file: " + destFile);
                    throw new DuplicatedDownloadException();
                }
            }           
            
            return response;
        } finally {
            if (conn != null) conn.disconnect();
            if (fos != null) fos.close();
        }
    }    
    
    public static Future<HttpResponse> downloadFile(final URL url, final String destFile, final Callback callback) {
        return executor.submit(new Callable<HttpResponse>(){

            @Override
            public HttpResponse call() throws Exception {
                try {
                    HttpResponse response = download(url, destFile);
                    if (callback != null) callback.onDone(response);
                    return response;
                } catch (DuplicatedDownloadException e) {
                    LOGGER.warning("DuplicatedDownloadException, url: " + url + ", destFile:" + destFile);
                    throw e;
                } catch (Exception e) {
                    if (callback != null) callback.onException(e);
                    throw e;
                }
            }});
    }
}
