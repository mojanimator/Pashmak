package com.app.pashmak.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.app.pashmak.Model.ProvinceCounties;
import com.app.pashmak.R;
import com.app.pashmak.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by Mojtaba Rajabi on 10/04/2018.
 */


public class ProvinceCountyListAdapter extends BaseExpandableListAdapter {

    private final Context ctx;
    private final ArrayList<ProvinceCounties> provinceCountiesList;


    private LayoutInflater inflater;

    public ProvinceCountyListAdapter(Context ctx) {
        this.ctx = ctx;
        this.provinceCountiesList = Utils.getProvinceCounties();

        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return provinceCountiesList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return provinceCountiesList.get(groupPosition).getCounties().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return provinceCountiesList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return provinceCountiesList.get(groupPosition).getCounty(childPosition).getName();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.parent_province, null);

        }

        TextView tvProvince = convertView.findViewById(R.id.tvProvinceParent);
        ProvinceCounties pc = (ProvinceCounties) getGroup(groupPosition);
        tvProvince.setText(pc.getProvinceName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_county, null);
        }

        TextView tvCounty = convertView.findViewById(R.id.tvCountyChild);
        tvCounty.setText((String) getChild(groupPosition, childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

}
