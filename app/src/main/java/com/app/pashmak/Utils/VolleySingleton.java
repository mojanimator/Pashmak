package com.app.pashmak.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by mmr on 25/02/2018.
 */

public class VolleySingleton {

    private static VolleySingleton mInstance;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private Context mCtx;
    final int Mb = 1024 * 1024;

    public VolleySingleton(final Context context) {
        mCtx = context;
        requestQueue = getRequestQueue();


    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
//            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * Mb);
//            Network network = new BasicNetwork(new HurlStack());
//            requestQueue = new RequestQueue(cache, network);

            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        if (imageLoader != null) {

            return imageLoader;
        } else {
            imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> cache = new LruCache<>(100 * Mb);

                @Override
                public Bitmap getBitmap(String url) {
//                    BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.no_image)
                    return cache.get(url);
                }


                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }


            });

            return imageLoader;
        }
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }

    public void cancelPendingRequests(String tag) {
        requestQueue.cancelAll(tag);
    }
}
