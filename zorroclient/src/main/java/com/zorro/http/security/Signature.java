package com.zorro.http.security;

public class Signature {
    public static native String getFingerprint();

    private static native int getSignature(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3);

    public static native int init(String str);

    static {
        System.loadLibrary("zorro_utils");
    }

    public static byte[] signatrue(byte[] url, byte[] tdata) {
        byte[] data = new byte[32];
        getSignature(url, url.length, tdata, tdata.length, data);
        return data;
    }
}
