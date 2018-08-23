package com.zorro.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class StreamUtil {
    private static final String TAG = StreamUtil.class.getSimpleName();

    public static void copyStream(InputStream is, OutputStream os) {
        byte[] buffer = new byte[1024];
        boolean length = true;

        try {
            int length1;
            while((length1 = is.read(buffer)) > -1) {
                os.write(buffer, 0, length1);
            }

            os.flush();
        } catch (IOException var5) {
            Log.e(TAG, "copy strem failed", var5);
        }

    }

    public static String readFile2String(File file) {
        if(file != null && file.isFile() && file.canRead() && file.length() > 0L) {
            BufferedReader reader = null;
            StringBuilder sb = new StringBuilder();
            String s = null;

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                while((s = reader.readLine()) != null) {
                    sb.append(s);
                }
            } catch (IOException var8) {
                Log.e(TAG, "read file to String failed", var8);
            } finally {
                closeQuietly(new Closeable[]{reader});
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static boolean copyFileStream(FileInputStream is, FileOutputStream os) {
        FileChannel fin = is.getChannel();
        FileChannel fout = os.getChannel();
        return copyFileChannel(fin, fout);
    }

    public static boolean copyFileChannel(FileChannel fin, FileChannel fout) {
        boolean var3;
        try {
            long e = fin.size();
            long position = 0L;

            while((position += fin.transferTo(position, e - position, fout)) < e) {
                ;
            }

            boolean var6 = true;
            return var6;
        } catch (IOException var10) {
            Log.e(TAG, "copy file strem failed", var10);
            var3 = false;
        } finally {
            closeQuietly(new Closeable[]{fin, fout});
        }

        return var3;
    }

    public static void closeQuietly(Closeable... closeables) {
        Closeable[] var1 = closeables;
        int var2 = closeables.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Closeable closeable = var1[var3];
            if(closeable != null) {
                try {
                    closeable.close();
                } catch (IOException var6) {
                    ;
                }
            }
        }

    }
}
