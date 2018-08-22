package com.zorro.http.request;

import java.net.URI;

public class Delete extends HttpRequest {

    public Delete(URI uri) {
        setMethod(METHOD_DELETE);
        setUri(uri);
    }
}
