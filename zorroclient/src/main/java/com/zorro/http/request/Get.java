package com.zorro.http.request;

import java.net.URI;

public class Get extends HttpRequest {

    public Get(URI uri) {
        setMethod(METHOD_GET);
        setUri(uri);
    }
}
