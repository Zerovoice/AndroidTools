package com.zorro.http;

public class DownloadEmptyFileException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
