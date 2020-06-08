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

import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder> {

    private ArrayList<Video> videoList = null; //비디오 리스트 데이터
    private OnItemClickListener listener = null;

    public VideoListAdapter(ArrayList<Video> videoList){
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

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public Video getItem(int position){return this.videoList.get(position);}

    //뷰홀더 정의
    public class VideoListViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView videoName;
        TextView videoType;
        TextView videoRunningTime;
        TextView videoDate;
        VideoListViewHolder(View itemView){
            super(itemView);
            //리스너 정의
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(listener != null){
                            listener.onItemClick(v, pos);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos !=RecyclerView.NO_POSITION){
                        if(listener != null){
                            listener.onItemLongClick(view, pos);
                        }
                    }
                    return false;
                }

            });
            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoName = itemView.findViewById(R.id.video_name);
            videoType = itemView.findViewById(R.id.video_type);
            videoRunningTime = itemView.findViewById(R.id.video_time);
            videoDate = itemView.findViewById(R.id.video_date);
        }
    }
    public void changedData(ArrayList<Video> list){
        this.videoList = list;
        notifyDataSetChanged();
    }

    //리스너 인터페이스 정의
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
        void onItemLongClick(View v, int position);
    }
}
