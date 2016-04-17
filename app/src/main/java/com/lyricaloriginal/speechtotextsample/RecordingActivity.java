package com.lyricaloriginal.speechtotextsample;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class RecordingActivity extends AppCompatActivity implements RecordingFragment.Listener{

    private Button mRecordingBtn;
    private TextView mRecordingTimeTextView;
    private RecordingFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        mRecordingBtn = (Button)findViewById(R.id.recording_btn);
        mRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFragment.isRecording()){
                    mFragment.stop(true);
                }else{
                    mFragment.start();
                }
            }
        });
        mRecordingTimeTextView = (TextView)findViewById(R.id.recording_time_textview);

        if(savedInstanceState == null){
            mFragment = new RecordingFragment();
            getSupportFragmentManager().
                    beginTransaction().
                    add(mFragment, RecordingFragment.class.getName()).
                    commit();
        }else{
            mFragment = (RecordingFragment)getSupportFragmentManager().
                    findFragmentByTag(RecordingFragment.class.getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragment.stop(false);
    }

    @Override
    public void onStartRecord(String tag) {
        mRecordingTimeTextView.setText("0:00");
        mRecordingBtn.setText("録音をやめる");
    }

    @Override
    public void onNotifyRecordingTime(String tag, int time) {
        mRecordingTimeTextView.setText(String.valueOf(time));
    }

    @Override
    public void onStopRecord(String tag, File output) {
        mRecordingBtn.setText("録音開始");
        if(output != null){
            Intent intent = new Intent();
            intent.setData(Uri.fromFile(output));
            setResult(RESULT_OK);
            finish();
        }
    }
}
