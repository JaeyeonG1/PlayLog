package com.quickids.playlog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickids.playlog.R;
import com.quickids.playlog.model.Video;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder> {

    private ArrayList<Video> videoList = null;

    VideoListAdapter(ArrayList<Video> videoList){
        this.videoList = videoList;
    }
    @NonNull
    @Override

    public VideoListAdapter.VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_video_container, parent, false);
        VideoListAdapter.VideoListViewHolder vh = new VideoListAdapter.VideoListViewHolder(view);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull VideoListAdapter.VideoListViewHolder holder, int position) {
        Video selectedItem = videoList.get(position);
        holder.thumbnail.setImageBitmap(selectedItem.getThumbnail());
        holder.videoName.setText(selectedItem.getName());
        holder.videoDate.setText(selectedItem.getDate());
        holder.videoRunningTime.setText(selectedItem.getRunningTime());
        int type = selectedItem.getVideoType();
        switch (type){
            case 100:
                holder.videoType.setText("전체 경기 영상");
                break;
            case 101:
                holder.videoType.setText("하이라이트 영상");
                break;
            case 200:
                holder.videoType.setText("훈련 영상");
                break;
            case 201:
                holder.videoType.setText("Slow Motion");
                break;
            default:
                break;
        }
    }
    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoListViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView videoName;
        TextView videoType;
        TextView videoRunningTime;
        TextView videoDate;
        VideoListViewHolder(View itemView){
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoName = itemView.findViewById(R.id.video_name);
            videoType = itemView.findViewById(R.id.video_type);
            videoRunningTime = itemView.findViewById(R.id.video_time);
            videoDate = itemView.findViewById(R.id.video_date);
        }

    }
}
