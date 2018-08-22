package com.zorro.http;

public class DuplicatedDownloadException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
