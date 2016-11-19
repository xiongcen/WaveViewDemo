package com.example.xiongcen.myapplication.network.requests;

import com.example.xiongcen.myapplication.network.response.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiongcen on 16/11/18.
 */

public class JsonRequest extends Request<JSONObject> {

    public JsonRequest(HttpMethod method, String url, RequestListener<JSONObject> listener) {
        super(method, url, listener);
    }

    @Override
    public JSONObject parseResponse(Response response) {
        String jsonString = new String(response.getRawData());
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
