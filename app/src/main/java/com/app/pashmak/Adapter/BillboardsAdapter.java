package com.app.pashmak.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.app.pashmak.Model.Billboard;
import com.app.pashmak.Model.County;
import com.app.pashmak.Model.ProvinceCounties;
import com.app.pashmak.R;
import com.app.pashmak.Utils.NetUtils;
import com.app.pashmak.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Mojtaba Rajabi on 21/02/2018.
 */

public class BillboardsAdapter extends RecyclerView.Adapter<BillboardsAdapter.BillboardViewHolder> implements Filterable {


    private final BillboardsFilter mFilter;
    private ArrayList<ProvinceCounties> provinceCounties;
    private final RealmResults<County> counties;
    private ArrayList<Billboard> data;
    private ArrayList<Billboard> filteredList;
    private Context context;
    private NetUtils netUtils;
    private Billboard tmpData;
    private County tmpCounty;


    public BillboardsAdapter(ArrayList<Billboard> data, Context context) {
        this.data = data;
        this.context = context;
        mFilter = new BillboardsFilter(BillboardsAdapter.this);
        counties = Utils.getCounties();

    }

    @Override
    public BillboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new BillboardViewHolder(inflater.inflate(R.layout.row_billboards, parent, false));
    }

    @Override
    public void onBindViewHolder(BillboardViewHolder holder, int position) {

        tmpData = data.get(position);
        tmpCounty = counties.get(tmpData.getCounty_id() - 1);

        holder.tvId.setText(String.valueOf(tmpData.getId()));
        holder.tvProvinceCounty.setText(tmpCounty.getProvince().getName()/* + " , " + tmpCounty.getName()*/);
        holder.tvAddress.setText(tmpData.getAddress());

        Utils.setupStatus(holder.tvStatus, tmpData.getStatus());


        holder.itemView.setTranslationY(+30);
        holder.itemView.animate().translationYBy(-30).start();
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1).start();

        NetUtils.setImage(tmpData.getImageLink(), holder.ivImage);

        holder.setIsRecyclable(false);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<Billboard> getFilteredList() {
        return filteredList;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public void onViewRecycled(BillboardViewHolder holder) {
        if (holder.ivImage.getDrawable() == null)
            holder.ivImage.setImageResource(R.drawable.no_image);
//        super.onViewRecycled(holder);
    }


    class BillboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodeLabel;
        TextView tvId;
        TextView tvProvinceCounty;
        TextView tvAddress;
        TextView tvStatus;
        NetworkImageView ivImage;

        BillboardViewHolder(View itemView) {
            super(itemView);

            tvCodeLabel = itemView.findViewById(R.id.tvCodeLabel);
            tvId = itemView.findViewById(R.id.tvNumber);
            tvProvinceCounty = itemView.findViewById(R.id.tvProvinceCounty);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivImage = itemView.findViewById(R.id.ivBillboard);


        }
    }

    public class BillboardsFilter extends Filter {
        private BillboardsAdapter mAdapter;

        private BillboardsFilter(BillboardsAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(data);
            } else {
                final String filterPattern = constraint.toString().trim();
                for (final Billboard billboard : data) {
                    if (billboard.getAddress().contains(filterPattern)) {
                        filteredList.add(billboard);
                    }
                }
            }
            System.out.println("Count Number " + filteredList.size());
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((List<Billboard>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
