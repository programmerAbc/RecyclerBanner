package com.practice.recyclerbanner.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.recyclerbanner.BannerData;
import com.practice.recyclerbanner.R;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    List<BannerData> bannerDataList = new LinkedList<>();

    public void setNewData(List<BannerData> data) {
        bannerDataList.clear();
        if (data != null && !data.isEmpty()) {
            bannerDataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bindData(bannerDataList.get(i % bannerDataList.size()));
    }

    @Override
    public int getItemCount() {
        return bannerDataList.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bannerTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerTv = itemView.findViewById(R.id.bannerTv);
        }

        public void bindData(BannerData bannerData) {
            bannerTv.setText(bannerData.getTitle());
        }
    }
}
