package com.zorro.http.request;



import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRequest implements Request {
    private static String VERSION_CODE = "";
    private static String DEV_REVISION = "";
    public static final String ENCODING_GZIP = "gzip";
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";
    public static final String CHARSET = "UTF-8";
    private static final Logger LOGGER = Logger.getLogger(HttpRequest.class.getName());
    private static final Map<String, String> DEFAULT_HEADERS = new HashMap();
    private String method;
    private URI uri;
    private Map<String, String> headers;
    private ByteBuffer data;

    public HttpRequest() {
        this.headers = new HashMap(DEFAULT_HEADERS);
    }

    private static void setZorroUserAgent() {
//        DeviceInfoUtils.DeviceInfo di = DeviceInfoUtils.getDeviceInfo();
        String ua = "";//"PL=ANDROID" + "&AV=" + VERSION_CODE + "&DR=" + DEV_REVISION + "&RL=" + di.getScreenWidth() + "*" + di.getScreenHeight() + "&MF=" + di.getManufacturer() + "&MO=" + di.getModel() + "&OS=" + di.getOs() + "&API=" + di.getApi();
        DEFAULT_HEADERS.put("n-ua", ua);
    }

    public static void setHeader(String name, String value) {
        DEFAULT_HEADERS.put(name, value);
    }

    public URI getUri() {
        return this.uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public void setData(byte[] data) {
        this.data = ByteBuffer.wrap(data);
    }

    public void setData(String data) {
        try {
            this.data = ByteBuffer.wrap(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var3) {
            LOGGER.log(Level.SEVERE, "httprequest setData error", var3);
        }

    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean hasData() {
        return this.data != null && this.data.hasRemaining();
    }

    public static void setVersionInfo(String versionCode, String revision) {
        VERSION_CODE = versionCode;
        DEV_REVISION = revision;
        setZorroUserAgent();
    }

    static {
        DEFAULT_HEADERS.put("Accept", "application/json");
        DEFAULT_HEADERS.put("Accept-Charset", "UTF-8");
        DEFAULT_HEADERS.put("Accept-Encoding", "gzip");
        DEFAULT_HEADERS.put("Accept-Language", Locale.getDefault().toString().replace("_", "-"));
        DEFAULT_HEADERS.put("Content-Type", "application/json;charset=UTF-8");

    }

    /**
     * ByteBuffer 转换 String
     * @param buffer
     * @return
     */
    public static String getString(ByteBuffer buffer) {
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

}
