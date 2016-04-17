package com.lyricaloriginal.speechtotextsample;


import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingFragment extends Fragment {

    private Listener mListener = null;
    private Handler mHandler = null;

    private boolean mIsRecording = false;
    private MediaRecorder mMediaRecorder = null;
    private File mOutputFile = null;

    public RecordingFragment() {
        super.setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void start() throws IOException {
        if (mIsRecording) {
            return;
        }

        File mediafile = new File(getContext().getFilesDir(), "recording.wav");
        if(!getContext().getFilesDir().exists()){
            getContext().getFilesDir().mkdirs();
        }else if (mediafile.exists()) {
            mediafile.delete();
        }

        mMediaRecorder = new MediaRecorder();
        //マイクからの音声を録音する
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //ファイルへの出力フォーマット DEFAULTにするとwavが扱えるはず
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //音声のエンコーダーも合わせてdefaultにする
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //ファイルの保存先を指定
        mMediaRecorder.setOutputFile(mediafile.getAbsolutePath());
        mMediaRecorder.setMaxDuration(3 * 60 * 1000);   //  最大3分まで
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED == what) {
                    stop(true);
                }
            }
        });
        //録音の準備をする
        mMediaRecorder.prepare();
        //録音開始
        mMediaRecorder.start();
        mHandler.postDelayed(new RecordTimeNotifier(), 1000);
        mIsRecording = true;
        mOutputFile = mediafile;
        if (mListener != null) {
            mListener.onStartRecord(getTag());
        }
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void stop(boolean saveRecordData) {
        if (!mIsRecording) {
            return;
        }

        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;

        mIsRecording = false;
        if(!saveRecordData){
            mOutputFile.delete();
            mOutputFile = null;
        }

        if (mListener != null) {
            mListener.onStopRecord(getTag(), mOutputFile);
        }
    }

    public interface Listener {
        void onStartRecord(String tag);

        void onNotifyRecordingTime(String tag, int recordedSecond);

        void onStopRecord(String tag, File output);
    }

    private class RecordTimeNotifier implements Runnable {

        private int mSecond = 0;

        @Override
        public void run() {
            if (!mIsRecording) {
                return;
            }
            mSecond++;
            final int second = mSecond;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onNotifyRecordingTime(getTag(), second);
                    }
                }
            });
            mHandler.postDelayed(this, 1000);
        }
    }
}
