package com.zorro.http.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RESTCallStringSign {
    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static String alg = "SHA-256";

    public static byte[] sign(String urlPath, byte[] dataBytes) {
        byte[] urlPathBytes = urlPath.getBytes(DEFAULT_CHARSET);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (dataBytes != null) {
            try {
                if (dataBytes.length >= 100) {
                    outputStream.write(dataBytes, 0, 100);
                } else {
                    outputStream.write(dataBytes);
                }
            } catch (IOException e) {
            }
        }
        return Signature.signatrue(urlPathBytes, outputStream.toByteArray());
    }

    public static byte[] contentSign(String urlPath, byte[] dataBytes, String key) {
        byte[] urlPathBytes = urlPath.getBytes(DEFAULT_CHARSET);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(urlPathBytes);
            outputStream.write(key.getBytes(DEFAULT_CHARSET));
            if (dataBytes != null) {
                if (dataBytes.length >= 100) {
                    outputStream.write(dataBytes, 0, 100);
                } else {
                    outputStream.write(dataBytes);
                }
            }
        } catch (IOException e) {
        }
        byte[] urlBytes = outputStream.toByteArray();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(alg);
            messageDigest.update(urlBytes);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e2) {
            return null;
        }
    }
}
