
package com.zorro.http.request;

import java.nio.ByteBuffer;

public interface Request {

    /**
     * @return the data
     */
    public abstract ByteBuffer getData();

    public abstract boolean hasData();

}
