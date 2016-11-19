package com.example.xiongcen.myapplication.network.httpstacks;

import com.example.xiongcen.myapplication.network.requests.Request;
import com.example.xiongcen.myapplication.network.response.Response;

/**
 * 执行网络请求的接口
 * Created by xiongcen on 16/11/18.
 */

public interface HttpStack {

    /**
     * 执行Http请求
     *
     * @param request 待执行的请求
     * @return
     */
    public Response performRequest(Request<?> request);
}
