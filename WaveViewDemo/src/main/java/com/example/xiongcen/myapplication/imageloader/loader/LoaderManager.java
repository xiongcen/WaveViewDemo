package com.example.xiongcen.myapplication.imageloader.loader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiongcen on 16/11/6.
 */

public class LoaderManager {

    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String FILE = "file";

    private static LoaderManager mInstance;
    private Map<String, Loader> mLoaderMap = new HashMap<String, Loader>();
    private Loader mNullLoader = new NullLoader();


    private LoaderManager() {
        register(HTTP, new UrlLoader());
        register(HTTPS, new UrlLoader());
        register(FILE, new LocalLoader());
    }

    public static LoaderManager getInstance() {
        if (mInstance == null) {
            synchronized (LoaderManager.class) {
                if (mInstance == null) {
                    mInstance = new LoaderManager();
                }
            }
        }
        return mInstance;
    }

    public final synchronized void register(String schema, Loader loader) {
        mLoaderMap.put(schema, loader);
    }

    public Loader getLoader(String schema) {
        if (mLoaderMap.containsKey(schema)) {
            return mLoaderMap.get(schema);
        }
        return mNullLoader;
    }
}
