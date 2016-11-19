package com.example.xiongcen.myapplication.network.requests;

import com.example.xiongcen.myapplication.network.response.Response;

/**
 * Created by xiongcen on 16/11/19.
 */

public class StringRequest extends Request<String> {

    public StringRequest(HttpMethod method, String url, RequestListener<String> listener) {
        super(method, url, listener);
    }

    @Override
    public String parseResponse(Response response) {
        return new String(response.getRawData());
    }

}
