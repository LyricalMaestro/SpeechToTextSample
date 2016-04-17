package com.lyricaloriginal.speechtotextsample;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingFragment extends Fragment {

    private Listener mListener = null;
    private boolean mIsRecording = false;

    public RecordingFragment() {
        super.setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Listener){
            mListener = (Listener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void start(){
        if(mIsRecording){
            return;
        }

        mIsRecording = true;
        if(mListener != null){
            mListener.onStartRecord(getTag());
        }
    }

    public boolean isRecording(){
        return mIsRecording;
    }

    public void stop(boolean saveRecordData){
        if(!mIsRecording){
            return;
        }

        mIsRecording = false;
        if(mListener != null){
            mListener.onStopRecord(getTag(), null);
        }
    }

    public interface Listener{
        void onStartRecord(String tag);

        void onNotifyRecordingTime(String tag, int time);

        void onStopRecord(String tag, File output);
    }
}
