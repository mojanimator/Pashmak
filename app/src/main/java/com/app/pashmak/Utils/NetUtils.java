package com.app.pashmak.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.app.pashmak.Adapter.BillboardsAdapter;
import com.app.pashmak.FragmentBillboardsList;
import com.app.pashmak.MainActivity;
import com.app.pashmak.Model.Billboard;
import com.app.pashmak.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mmr on 22/02/2018.
 */

public class NetUtils {
    private static final String TAG = "tagg";
    private static final String DOMAIN = "https://moj-raj.ir/tazhan/";
    private static final String IMAGE_BASE_URL = DOMAIN + "billboards_image/";
    private static final String DATABASE_URL = DOMAIN + "show.php";

    public static boolean isLoadImageFinished() {
        return loadImageFinished;
    }

    private static boolean loadImageFinished = false;
    private BillboardsAdapter billboardsAdapter;
    private static ImageLoader imageLoader;
    private Context mCtx;
    private Map<String, String> params = new HashMap<String, String>();
    private ProgressBar progressBarBillboards;
    private Communicator communicator;

    public NetUtils(View view, BillboardsAdapter billboardsAdapter) {
        this.mCtx = view.getContext();
        this.billboardsAdapter = billboardsAdapter;
        imageLoader = VolleySingleton.getInstance(mCtx).getImageLoader();
        progressBarBillboards = view.findViewById(R.id.progressBillboards);


    }

//    public NetUtils(Context context, BillboardsAdapter billboardsAdapter) {
//        this.mCtx = context;
//        this.billboardsAdapter = billboardsAdapter;
//        imageLoader = VolleySingleton.getInstance(mCtx).getImageLoader();
//        progressBarBillboards = ((AppCompatActivity) context).findViewById(R.id.progressBillboards);
//    }


    public void getBillboards(final String page, final String where) {

        progressBarBillboards.setVisibility(View.VISIBLE);
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST, DATABASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            Log.d(TAG, "onResponse  : " + response);

                            JSONArray rsp;
                            JSONObject jsonObject;
                            long len;
                            if (response != null && !response.equals("")) {

                                rsp = new JSONArray(response);
                                len = rsp.length();

                                for (int i = 0; i < len; i++) {

                                    jsonObject = rsp.getJSONObject(i);
                                    Billboard billboard = new Billboard();
                                    billboard.setId(jsonObject.getLong("id"));
                                    billboard.setLat(jsonObject.getDouble("lat"));
                                    billboard.setLng(jsonObject.getDouble("lng"));
                                    billboard.setCounty_id(jsonObject.getInt("county-id"));
                                    billboard.setAddress(jsonObject.getString("address"));
                                    billboard.setContractor_id(jsonObject.getLong("contractor-id"));
                                    billboard.setStatus(jsonObject.getString("status"));
                                    billboard.setImageLink(jsonObject.getString("image"));

                                    FragmentBillboardsList.billboards.add(billboard);

                                }

                            } else {
                                FragmentBillboardsList.billboards.clear();
                                FragmentBillboardsList.endOfBillboards = true;
                                Utils.mToast(mCtx, "نتیجه ای یافت نشد", Toast.LENGTH_SHORT);
                            }

                            FragmentBillboardsList.onRefresh = false;
                            billboardsAdapter.notifyDataSetChanged();
                            communicator.refreshMapFragment(FragmentBillboardsList.billboards);
                            //pages are every 10 item

                        } catch (JSONException e) {

                            //Toast.makeText(mCtx, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "JSONException " + e.getMessage());
                            FragmentBillboardsList.endOfBillboards = true;
                            Utils.mToast(mCtx, "نتیجه ای یافت نشد", Toast.LENGTH_SHORT);
                            FragmentBillboardsList.onRefresh = false;
                        }

                        progressBarBillboards.setVisibility(View.GONE);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error response " + error.getMessage());
                        progressBarBillboards.setVisibility(View.GONE);
                        FragmentBillboardsList.onRefresh = false;
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (!where.equals("")) {
                    params.put("where", where);
                } else
                    params.put("where", "");
                params.put("page", page);
                return params;
            }
        };
        jsonArrayRequest.setTag("billboard");
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 4, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mCtx).addToRequestQueue(jsonArrayRequest);

    }

    public static void setImage(final String imageURL, final NetworkImageView imageView) {
        loadImageFinished = false;
        imageLoader.get(IMAGE_BASE_URL + imageURL, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    imageView.setImageBitmap(response.getBitmap());
                } else {
                    imageView.setImageResource(R.drawable.loading);
                }
                loadImageFinished = true;
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.no_image);
                loadImageFinished = true;
            }
        });

//        imageLoader.get(IMAGE_BASE_URL + imageURL, ImageLoader.getImageListener(imageView,
//                R.drawable.loading, R.drawable.no_image));
//
        imageView.setImageUrl(IMAGE_BASE_URL + imageURL, imageLoader);


    }


    public interface Communicator

    {
        public void refreshMapFragment(ArrayList<Billboard> billboards);
    }

    public void setCommunicator(Communicator c) {
        this.communicator = c;
    }

    public static boolean isNetworkAvailable(AppCompatActivity c) {
        boolean state;
        ConnectivityManager cmg = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cmg != null ? cmg.getActiveNetworkInfo() : null;
        state = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (state) {
            return true;
        } else {
            //NO interntet
            return false;
        }
    }

    public static void httpsConnect() {
        try {

            URL url = new URL("https://moj-raj.ir/tazhan/show.php");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            String urlParameters = "page=0";
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

            dataOutputStream.writeBytes(urlParameters);
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = connection.getResponseCode();
            Log.d(MainActivity.TAG, connection.getResponseMessage());
            String output = "";

            BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            StringBuilder responseOutput = new StringBuilder();
            while ((line = bReader.readLine()) != null) {
                responseOutput.append(line);
                Log.d(MainActivity.TAG, line);
            }
            bReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
