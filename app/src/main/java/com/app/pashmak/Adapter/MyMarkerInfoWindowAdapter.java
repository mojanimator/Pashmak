package com.app.pashmak.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.app.pashmak.MainActivity;
import com.app.pashmak.Model.Billboard;
import com.app.pashmak.R;
import com.app.pashmak.Utils.NetUtils;
import com.app.pashmak.Utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Mojtaba Rajabi on 07/03/2018.
 */

public class MyMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;
    private ArrayList<Billboard> data;
    private Billboard tmp;

    public MyMarkerInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window, null);
        tmp = new Billboard();
    }

    public void setBillboards(ArrayList<Billboard> billboards) {
        data = billboards;
    }

    private void renderWindowText(Marker marker, View view) {
        int pos = Integer.valueOf(marker.getTitle());
        Log.d(MainActivity.TAG, "renderWindowText: " + pos);
        TextView tvCode = view.findViewById(R.id.tvNumber);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvStatus = view.findViewById(R.id.tvStatus);
        NetworkImageView ivBillboard = view.findViewById(R.id.ivBillboard);

        tmp = data.get(pos);

        tvCode.setText(String.valueOf(tmp.getId()));
        tvAddress.setText(tmp.getAddress());

        Utils.setupStatus(tvStatus, tmp.getStatus());
        NetUtils.setImage(tmp.getImageLink(), ivBillboard);
        while (!NetUtils.isLoadImageFinished()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
