package com.zorro.http;


import com.zorro.http.request.Get;
import com.zorro.http.request.HttpRequest;
import com.zorro.http.response.HttpResponse;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class HttpFileUploader extends HttpConnector {
    
    private static final int SIGN_CHECK_COUNT = 100;
    private static final Logger LOGGER = Logger.getLogger(HttpFileUploader.class.getName());
    private static ExecutorService uploadExecutor = Executors.newFixedThreadPool(1);
    
	protected static HttpResponse connect(HttpRequest request, List<String> files) throws Exception {
        HttpURLConnection conn = null;
        try {
            String BOUNDARY = Long.toHexString(System.currentTimeMillis()).toUpperCase();

            ByteArrayOutputStream baos = new ByteArrayOutputStream(SIGN_CHECK_COUNT);
            for(int i = 0; i < files.size(); i++){

                String fname = files.get(i);
                File file = new File(fname);
                StringBuilder sb = new StringBuilder();
                sb.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                sb.append("Content-Disposition: form-data;name=\"file"+i+"\";filename=\""+ file.getName() + "\"\r\n");
                sb.append("Content-Type:application/octet-stream\r\n\r\n");
                byte[] data = sb.toString().getBytes();

                int remainSize = SIGN_CHECK_COUNT - baos.size();
                int writeSize = Math.min(data.length, remainSize);
                if(remainSize == 0)
                    break;
                baos.write(data, 0, writeSize);

                DataInputStream in = new DataInputStream(new FileInputStream(file));
                int bytes = 0;
                byte[] bufferOut = new byte[1024*8];
                while ((bytes = in.read(bufferOut)) != -1) {
                    remainSize = SIGN_CHECK_COUNT - baos.size();
                    writeSize = Math.min(bytes, remainSize);
                    if(remainSize == 0)
                        break;
                    baos.write(data, 0, writeSize);
                }

                String postStr = "\r\n";
                byte[] postBytes = postStr.getBytes();
                remainSize = SIGN_CHECK_COUNT - baos.size();
                writeSize = Math.min(postBytes.length, remainSize);
                baos.write(data, 0, writeSize);
                if(remainSize == 0)
                    break;

                in.close();
            }
            LOGGER.info("--Uploader---url--->"+request.getUri());
            URI uri = signUri(request.getUri(), baos.toByteArray());//
            URL url = uri.toURL();
            baos.close();

            conn = (HttpURLConnection) url.openConnection();
            //   conn.setFixedLengthStreamingMode(0);//用于避免内存溢出
            conn.setChunkedStreamingMode(1024*1024);////指定流的大小，当内容达到这个值的时候就把流输出
            conn.setRequestMethod(request.getMethod());
            conn.setConnectTimeout(10 * 1000);  //let time out be 10s
            conn.setReadTimeout(10 * 1000);

            Map<String, String> headers = request.getHeaders();
            headers.put("Content-Type","multipart/form-data;boundary=" + BOUNDARY);
            for (Entry<String, String> header : headers.entrySet()) {

//                LOGGER.info("==header========>"+header.getKey() + "==" + header.getValue());
                conn.addRequestProperty(header.getKey(), header.getValue());
            }

            conn.setDoInput(true);

            conn.setDoOutput(request.hasData() || files.size() > 0);
            conn.setUseCaches(false);

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            if (request.hasData()) {
                // 将data转成str，添加到表单到json项中
                String str = HttpRequest.getString(request.getData());
                StringBuilder sb = new StringBuilder();
                sb.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                sb.append("Content-Disposition: form-data;name=\"json\"\r\n\r\n" + str);
                byte[] data = sb.toString().getBytes();
                out.write(data);
            }

            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            for(int i = 0; i < files.size() ; i++){
                String fname = files.get(i);
                File file = new File(fname);
                StringBuilder sb = new StringBuilder();
                sb.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                sb.append("Content-Disposition: form-data;name=\"file"  + i + "\";filename=\""+ file.getName() + "\"\r\n");
                sb.append("Content-Length:"+file.length()+"\r\n");
                sb.append("Content-Type:application/octet-stream\r\n\r\n");
//                LOGGER.info("==sb========>"+sb);
                byte[] data = sb.toString().getBytes();
                out.write(data);

                DataInputStream in = null;
                try {
                    in = new DataInputStream(new FileInputStream(file));
//                    long fileSize = file.length();
//                    long fileUpProgress = 0;
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024 * 8];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
//                        fileUpProgress += bytes;
//                        int progress = (int) ((fileUpProgress * 100) / fileSize);
                    }
                    out.write("\r\n".getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            out.write(end_data);

            out.flush();
            out.close();

            conn.connect();
            HttpResponse response = new HttpResponse();
            int code = conn.getResponseCode();
            response.setCode(code);
            InputStream stream;
            if (response.isSuccess()) {
                stream = conn.getInputStream();
            } else {
                stream = conn.getErrorStream();
            }
            InputStream in = new BufferedInputStream(stream);
            byte[] buf = new byte[1024];
            int count = -1;
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            while ((count = in.read(buf)) > -1) {
                outstream.write(buf, 0, count);
            }
            outstream.flush();
            ByteBuffer data = ByteBuffer.wrap(outstream.toByteArray());
            outstream.close();
            response.setData(data);

            return response;
        }finally {
            if (conn != null) conn.disconnect();
        }
    }
    
    public static Future<HttpResponse> postExecute(final HttpRequest request, final List<String> files, final Callback callback) {

        //this method will not work for large file!!!!!!!!!!!
        return executor.submit(new Callable<HttpResponse>(){

            @Override
            public HttpResponse call() throws Exception {
                try {
                    HttpResponse response = connect(request, files);
                    if (callback != null) callback.onDone(response);
                    return response;
                } catch (Exception e) {
                    if (callback != null) callback.onException(e);
                    throw e;
                }
            }});
    }
    
    public static void postFiles(final URI uri, final List<String> files, final Callback callback)
    {
        uploadExecutor.submit(new Callable<HttpResponse>(){

            @Override
            public HttpResponse call() throws Exception {
                try {
                    int retCode =  syncUploadFiles(uri, files);
                    if (callback != null && retCode == 200)
                    {
                        HttpResponse resp = new HttpResponse();
                        resp.setCode(retCode);
                        callback.onDone(resp);
                    }
                    else
                    {
                        throw new Exception("return error!" + retCode);
                    }
                    return null;
                } catch (Exception e) {
                    if (callback != null) callback.onException(e);
                    throw e;
                }
            }});
    }
    
    public static int syncUploadFiles(URI uri, final List<String> files) throws Exception {
        
        HttpResponse httpResponse = doUploadFiles(uri, files);
        
        //get result code
        int statusCode = httpResponse.getCode();
        LOGGER.info("upload vod http: result code:" + statusCode + "," + files.get(0));

        return statusCode;
    }

    public static HttpResponse doUploadFiles(URI uri, final List<String> files) throws Exception {
        return connect(new Get(uri),files);

    }


    /**
     * 限定最大长度的byte array缓冲区
     */
    private static class SelfByteArrayOutputStream extends ByteArrayOutputStream {
        private int maxCount;

        public SelfByteArrayOutputStream(int maxCount) {
            super();
            this.maxCount = maxCount;
        }

        @Override
        public synchronized void write(byte[] buffer, int offset, int len) {
            if (count == maxCount) {
                throw new RuntimeException("Exceeding the maximum limit");
            }
            int poor = maxCount - count;
            if (len > poor) {
                len = poor;
            }
            super.write(buffer, offset, len);
        }

        @Override
        public synchronized void write(int oneByte) {
            if (count == maxCount) {
                throw new RuntimeException("Exceeding the maximum limit");
            }
            super.write(oneByte);
        }
    }
}
