package com.quickids.playlog.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickids.playlog.R;
import com.quickids.playlog.activity.EditorActivity;
import com.quickids.playlog.activity.PreviewActivity;
import com.quickids.playlog.activity.VideoPlayerActivity;
import com.quickids.playlog.adapter.VideoListAdapter;
import com.quickids.playlog.model.HighlightVideo;
import com.quickids.playlog.model.Video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HighlightFragment extends Fragment implements VideoListAdapter.OnItemClickListener {

    private final static String PATH_HIGHLIGHT = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PlayLogVideos/Highlight/";

    ArrayList<Video> highlightVideoList = null;
    RecyclerView recyclerView = null;
    VideoListAdapter adapter = null;

    public HighlightFragment() {
        highlightVideoList = new ArrayList<Video>();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setFileList();
    }
    public void setFileList(){
        File directory = new File(PATH_HIGHLIGHT);
        File[] files = directory.listFiles();

        Log.d("HighlightFragment", files.length + "");

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
                Video video = new HighlightVideo(thumbnail,PATH_HIGHLIGHT,name,date,runningTime,extn,fileSize);
                highlightVideoList.add(video);
            }
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_highlight,container,false);
        recyclerView = v.findViewById(R.id.recycler_highlight);
        recyclerView.setHasFixedSize(true);
        adapter = new VideoListAdapter(highlightVideoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        //리스너 등록
        adapter.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onItemClick(View v, int position) {
        Video video = highlightVideoList.get(position);
        String path = video.getPath()+video.getName()+"."+video.getExtn();
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        showDialog(position);
    }

    private void showDialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("하이라이트 관리");
        builder.setItems(R.array.menu_highlight, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String[] items = getResources().getStringArray(R.array.menu_highlight);
                Video video = adapter.getItem(position);

                switch (items[pos]){
                    case "수정":
                        renameFile(video.getPath(), video.getName(), video.getExtn());
                        break;
                    case "삭제":
                        String path = video.getPath()+video.getName()+"."+video.getExtn();
                        deleteFile(path);
                        break;
                }
            }
        });
        builder.show();
    }

    private void deleteFile(String fullPath){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("동영상 삭제");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(fullPath);
                        file.delete();
                        Toast.makeText(getContext(),"삭제 완료.",Toast.LENGTH_LONG).show();
                        highlightVideoList = new ArrayList<Video>();
                        setFileList();
                        adapter.changedData(highlightVideoList);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void renameFile(String path, String name, String extn){
        EditText edittext = new EditText(getContext());
        edittext.setText(name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("동영상 이름 수정");
        builder.setMessage("수정할 이름을 입력하세요.");
        builder.setView(edittext);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(path + name + "." + extn);
                        if(file.exists()){
                            if(edittext.getText().toString() != ""){
                                String newName = edittext.getText().toString();
                                File fileNew = new File(path + newName + "." + extn);
                                file.renameTo(fileNew);
                                highlightVideoList = new ArrayList<Video>();
                                setFileList();
                                adapter.changedData(highlightVideoList);
                            }
                        }
                        Toast.makeText(getContext(), edittext.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}
