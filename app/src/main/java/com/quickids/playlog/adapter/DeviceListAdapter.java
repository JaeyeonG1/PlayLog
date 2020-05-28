package com.quickids.playlog.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickids.playlog.R;

import java.util.ArrayList;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    private ArrayList<String> deviceList = null; // 장치 명 리스트
    private OnItemClickListener listener = null;

    public DeviceListAdapter(ArrayList<String> deviceList){
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_device_container, parent, false);
        DeviceViewHolder vh = new DeviceViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.tvDevice.setText(deviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public String getItem(int position){
        return deviceList.get(position);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{
        TextView tvDevice;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDevice = itemView.findViewById(R.id.text_deviceName);
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
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setDeviceList(ArrayList<String> deviceList){
        this.deviceList = deviceList;
        Log.i("Adapter", deviceList.size()+"");
        notifyDataSetChanged();
    }
}
