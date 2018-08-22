package com.zorro.http.request;

import java.net.URI;

public class Post extends HttpRequest {

    public Post(URI uri) {
        setMethod(METHOD_POST);
        setUri(uri);
    }
}
