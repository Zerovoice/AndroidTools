package com.zorro.testcode;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public class JsonUtil {

    private final static Gson gson = new Gson();
    
    public static String toJson(Object src){
        return gson.toJson(src);
    }
    
    public static <T> T toObject(String src, Class<T> clz){
        try {
            return gson.fromJson(src, clz);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T toObject(ByteBuffer buf, Class<T> clz){
        InputStreamReader reader = null;
        T result = null;
        try {
            reader = new InputStreamReader(new BufferInputStream(buf));
            result  = gson.fromJson(reader, clz);
        } catch (Exception e) {
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {}
        }
        return result;
    }
    
    public static <T> T toObject(String src, Type clz){
        try {
            return gson.fromJson(src, clz);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T toObject(ByteBuffer buf, Type clz){
        InputStreamReader reader = null;
        T result = null;
        try {
            reader = new InputStreamReader(new BufferInputStream(buf));
            result = gson.fromJson(reader, clz);
        } catch (Exception e) {
        } finally {
            if (null != reader) {
                try {reader.close();} catch (IOException e) {}
            }
        }
        return result;
    }
    
    public static <T> T toObject(java.io.InputStream in, Class<T> clz){
        InputStreamReader reader = null;
        T result = null;
        try {
            reader = new InputStreamReader(in);
            result = gson.fromJson(reader, clz);
        } catch (Exception e) {
        } finally {
            if (null != reader) {
                try {reader.close();} catch (IOException e) {}
            }
        }
        return result;
    }
}
