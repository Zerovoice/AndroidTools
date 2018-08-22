package com.zorro.tools;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferInputStream extends InputStream {
    private ByteBuffer buf;

    public BufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    public void close() throws IOException {
        this.buf.clear();
    }

    public int read(byte[] dst, int offset, int count) throws IOException {
        if (this.buf.hasRemaining()) {
            int size = Math.min(this.buf.remaining(), count);
            this.buf.get(dst, offset, size);
            return size;
        } else {
            return -1;
        }
    }

    public int read() throws IOException {
        return this.buf.hasRemaining() ? this.buf.get() & 255 : -1;
    }

    public int available() throws IOException {
        return this.buf.remaining();
    }
}
