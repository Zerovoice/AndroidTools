package com.zorro.http.request;

import java.net.URI;

public class Put extends HttpRequest {

    public Put(URI uri) {
        setMethod(METHOD_PUT);
        setUri(uri);
    }
}
