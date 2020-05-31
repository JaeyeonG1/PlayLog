package com.quickids.playlog.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickids.playlog.R;
import com.quickids.playlog.activity.EditorActivity;
import com.quickids.playlog.activity.VideoPlayerActivity;
import com.quickids.playlog.adapter.VideoListAdapter;
import com.quickids.playlog.model.ProcessedVideo;
import com.quickids.playlog.model.TrainingVideo;
import com.quickids.playlog.model.Video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TrainingFragment extends Fragment implements VideoListAdapter.OnItemClickListener{

    ArrayList<Video> trainingVideoList = null;
    RecyclerView recyclerView = null;
    VideoListAdapter adapter = null;

    public TrainingFragment() {
        trainingVideoList = new ArrayList<Video>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setFileList();

    }
    public void setFileList(){
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PlayLogVideos/Training/";
        File directory = new File(absolutePath);
        File processedDirectory = new File(absolutePath+"Processed/"); //슬로우 모션 디렉토리
        File[] files = directory.listFiles(); //슬로우모션 파일
        File[] processedFiles = processedDirectory.listFiles();
        for(int i = 0; i < files.length; i++){
            if(!files[i].isDirectory()){
                String runningTime = getRunningTime(files[i].toString());
                long fileSize = files[i].length();
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(files[i].toString(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap,360,480);
                int pos = files[i].getName().lastIndexOf(".");
                String name = files[i].getName().substring(0,pos);
                String extn = files[i].getName().substring(pos+1);
                long lastModified = files[i].lastModified();
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date lastModifiedDate = new Date(lastModified);
                String date = simpleDateFormat.format(lastModifiedDate);
                Video video = new TrainingVideo(thumbnail,absolutePath,name,date,runningTime,extn,fileSize);
                trainingVideoList.add(video);
                //슬로우모션 영상 load
                if(i<processedFiles.length){
                    if(!processedFiles[i].isDirectory()){
                        String p_runningTime = getRunningTime(processedFiles[i].toString());
                        long p_fileSize = processedFiles[i].length();
                        Bitmap p_bitmap = ThumbnailUtils.createVideoThumbnail(processedFiles[i].toString(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        Bitmap p_thumbnail = ThumbnailUtils.extractThumbnail(p_bitmap,360,480);
                        int p_pos = processedFiles[i].getName().lastIndexOf(".");
                        String p_name = processedFiles[i].getName().substring(0,p_pos);
                        String p_extn = processedFiles[i].getName().substring(pos+1);
                        long p_lastModified = processedFiles[i].lastModified();
                        Date p_lastModifiedDate = new Date(p_lastModified);
                        String p_date = simpleDateFormat.format(p_lastModifiedDate);
                        Video p_video = new ProcessedVideo(p_thumbnail,absolutePath+"Processed/",
                                p_name,p_date,p_runningTime,p_extn,p_fileSize);
                        trainingVideoList.add(p_video);
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_training,container,false);

        recyclerView = v.findViewById(R.id.recycler_training);
        recyclerView.setHasFixedSize(true);
        adapter = new VideoListAdapter(trainingVideoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        //리스너 등록
        adapter.setOnItemClickListener(this);
        return v;
    }

    private String getRunningTime(String path){
        String result = null;
        MediaMetadataRetriever retriever = null;
        FileInputStream inputStream = null;
        try{
            retriever = new MediaMetadataRetriever();
            inputStream = new FileInputStream(path);
            retriever.setDataSource(inputStream.getFD());
            String time = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));

            long millisec = Long.parseLong(time);
            long duration = millisec / 1000;
            long hours = duration / 3600;
            long minutes = (duration - hours * 3600) / 60;
            long seconds = duration - (hours * 3600 + minutes * 60);
            result = hours+":"+minutes+":"+seconds;
            inputStream.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (retriever != null) {
                retriever.release();
            }
        }
        return result;
    }

    @Override
    public void onItemClick(View v, int position) {

        Video video = trainingVideoList.get(position);
        String path = video.getPath()+video.getName()+"."+video.getExtn();
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
    }
    @Override
    public void onItemLongClick(View v, int position) {
        Video selectedVideo = adapter.getItem(position);
        int videoType = selectedVideo.getVideoType();
        if(videoType == 200){

            String filePath = selectedVideo.getPath();
            String name = selectedVideo.getName();
            String extn = selectedVideo.getExtn();
            Intent intent = new Intent(v.getContext(), EditorActivity.class);
            intent.putExtra("filePath", filePath);
            intent.putExtra("videoType", videoType);
            intent.putExtra("name",name);
            intent.putExtra("extn",extn);
            startActivity(intent);
        }else{
            Toast.makeText(getContext(),"이미 훈련 영상입니다.", Toast.LENGTH_SHORT).show();
        }

    }
}
