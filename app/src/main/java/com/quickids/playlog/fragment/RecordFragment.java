package com.quickids.playlog.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.quickids.playlog.R;
import com.quickids.playlog.activity.PreferenceActivity;
import com.quickids.playlog.activity.RecordActivity;

public class RecordFragment extends Fragment implements View.OnClickListener{

    Button btnMatch;
    Button btnTraining;

    public RecordFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record, container, false);

        btnMatch = v.findViewById(R.id.btn_match);
        btnTraining = v.findViewById(R.id.btn_training);

        btnMatch.setOnClickListener(this);
        btnTraining.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_match:
                Intent intentMatch = new Intent(getContext(), PreferenceActivity.class);
                startActivity(intentMatch);
                break;
            case R.id.btn_training:
                Intent intentTraining = new Intent(getContext(), RecordActivity.class);
                intentTraining.putExtra("prefInfo", "noPref");
                startActivity(intentTraining);
                break;
        }
    }
}
