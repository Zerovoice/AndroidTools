package com.zorro.tools;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zorro.tools.BufferInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public class JsonUtil {

    private static final Gson gson = (new GsonBuilder()).create();

    public JsonUtil() {
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T toObject(String src, Class<T> clz) {
        return gson.fromJson(src, clz);
    }

    public static <T> T toObject(String src, Type clz) {
        return gson.fromJson(src, clz);
    }

    public static <T> T toObject(InputStream in, Class<T> clz) {
        InputStreamReader reader = new InputStreamReader(in);
        //        Object result = gson.fromJson(reader, clz);
        T result = gson.fromJson(reader, clz);
        try {
            reader.close();
        } catch (IOException var5) {
            ;
        }

        return result;
    }

    public static <T> T toObject(ByteBuffer buf, Class<T> clz) {
        InputStreamReader reader = new InputStreamReader(new BufferInputStream(buf));
        //        Object result = gson.fromJson(reader, clz);
        T result = gson.fromJson(reader, clz);

        try {
            reader.close();
        } catch (IOException var5) {
            ;
        }

        return result;
    }

    public static <T> T toObject(ByteBuffer buf, Type clz) {
        InputStreamReader reader = new InputStreamReader(new BufferInputStream(buf));
        //        Object result = gson.fromJson(reader, clz);
        T result = gson.fromJson(reader, clz);
        try {
            reader.close();
        } catch (IOException var5) {
            ;
        }

        return result;
    }
}

