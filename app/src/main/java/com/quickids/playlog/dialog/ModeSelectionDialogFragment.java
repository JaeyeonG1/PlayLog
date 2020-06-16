package com.quickids.playlog.dialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.quickids.playlog.R;
import com.quickids.playlog.activity.PreferenceActivity;
import com.quickids.playlog.activity.RecordActivity;

public class ModeSelectionDialogFragment extends DialogFragment implements View.OnClickListener{

    public static final String TAG_SELECTION_DIALOG = "dialog_selection";

    Button matchBtn;
    Button trainingBtn;
    Button closeBtn;

    private ModeSelectionDialogFragment() {}
    public static ModeSelectionDialogFragment getInstance(){
        ModeSelectionDialogFragment msdf = new ModeSelectionDialogFragment();
        return msdf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mode_selection_dialog, container);

        matchBtn = v.findViewById(R.id.btn_match);
        trainingBtn = v.findViewById(R.id.btn_training);
        closeBtn = v.findViewById(R.id.btn_close);
        matchBtn.setOnClickListener(this);
        trainingBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_match:
                Intent intentMatch = new Intent(getContext(), PreferenceActivity.class);
                startActivity(intentMatch);
                dismiss();
                break;
            case R.id.btn_training:
                Intent intentTraining = new Intent(getContext(), RecordActivity.class);
                intentTraining.putExtra("prefInfo", "noPref");
                startActivity(intentTraining);
                dismiss();
                break;
            case R.id.btn_close:
                dismiss();
                break;
        }
    }
}
