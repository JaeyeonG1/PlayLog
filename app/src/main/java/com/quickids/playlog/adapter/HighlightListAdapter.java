package com.quickids.playlog.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HighlightListAdapter extends RecyclerView.Adapter<HighlightListAdapter.HighlightListViewHolder> {

    ArrayList<String> list;
    public HighlightListAdapter(ArrayList<String> list){
        this.list = list;
    }
    @NonNull
    @Override
    public HighlightListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HighlightListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public String getItem(int pos){
        return list.get(pos);
    }

    public class HighlightListViewHolder extends RecyclerView.ViewHolder{


        public HighlightListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
