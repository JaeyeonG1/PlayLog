package com.quickids.playlog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickids.playlog.R;

import java.util.ArrayList;

public class HighlightListAdapter extends RecyclerView.Adapter<HighlightListAdapter.HighlightListViewHolder> {

    ArrayList<String> list;
    OnItemClickListener listener = null;
    public HighlightListAdapter(ArrayList<String> list){
        this.list = list;
    }
    @NonNull
    @Override
    public HighlightListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_highlight, parent, false);
        HighlightListViewHolder vh = new HighlightListViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HighlightListViewHolder holder, int position) {
        String highlightTime = list.get(position);
        holder.highlightTime.setText(highlightTime);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getItem(int pos){
        return list.get(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public class HighlightListViewHolder extends RecyclerView.ViewHolder{
        TextView highlightTime;
        public HighlightListViewHolder(@NonNull View itemView) {
            super(itemView);
            highlightTime = itemView.findViewById(R.id.higlight_time);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(listener != null){
                            listener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(View view, int pos);
    }
}
