/**
 * 
 */
package com.zorro.http.response;

import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private Map<String, String> headers = new HashMap<String, String>();
    private ByteBuffer data;
    private int code;

    /**
     * @return the data
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ByteBuffer data) {
        this.data = data;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }


    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    public boolean isSuccess() {
        return code == HttpURLConnection.HTTP_OK
                || code == HttpURLConnection.HTTP_CREATED
                || code == HttpURLConnection.HTTP_NO_CONTENT;
    }

    public boolean hasData() {
        return data != null && data.hasRemaining();
    }


    /**
     * @param key
     * @return
     * @see Map#get(Object)
     */
    public String getHeader(String key) {
        return headers.get(key);
    }


    /**
     * @param key
     * @param value
     * @return
     * @see Map#put(Object, Object)
     */
    public String putHeader(String key, String value) {
        return headers.put(key, value);
    }

    @Override
    public String toString() {
        return "HttpResponse [headers=" + headers + ", data=" + data + ", code=" + code + "]";
    }
    
    
}
