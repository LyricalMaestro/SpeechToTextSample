package com.lyricaloriginal.speechtotextsample;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpeechToTextFragment extends Fragment {

    private Listener mListener = null;
    private HandlerThread mSttThread = null;
    private Handler mSttHandler = null;
    private Handler mUiHandler = null;
    private SpeechToText mStt = null;

    public SpeechToTextFragment() {
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
        mSttThread = new HandlerThread("SpeechToText");
        mSttThread.start();
        mSttHandler = new Handler(mSttThread.getLooper());
        mUiHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSttThread != null){
            mSttThread.quit();
            mSttThread = null;
            mSttHandler = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void speechToText(String username, String password, final Uri uri) {
        if(mStt != null){
            return;
        }

        SpeechToText stt = new SpeechToText();
        stt.setUsernameAndPassword(username, password);
        stt.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");

        final RecognizeOptions options = new RecognizeOptions().
                contentType("audio/wav").
                continuous(true).
                interimResults(true).
                model("ja-JP_BroadbandModel");

        mStt = stt;
        mSttHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    speechToTextInBackground(uri, options);
                } catch (Exception ex) {
                    Log.e(SpeechToTextFragment.class.getName(), ex.getMessage(), ex);
                }

                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mStt = null;
                    }
                });
            }
        });
    }

    private void speechToTextInBackground(final Uri uri, RecognizeOptions options) throws IOException{
        File target = downloadWavFile(uri);

        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onStartTextToSpeech(getTag());
                }
            }
        });
        final SpeechResults speechResults = mStt.recognize(target, options);
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onReceiveResult(getTag(), speechResults);
                }
            }
        });

    }

    private File downloadWavFile(Uri uri) throws IOException {
        File f = new File(getContext().getExternalFilesDir(
                Environment.DIRECTORY_MUSIC), "sample.wav");
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = getContext().getContentResolver().openInputStream(uri);
            fos = new FileOutputStream(f);
            byte[] buf = new byte[256];
            int len;
            while ((len = is.read(buf, 0, buf.length)) != -1) {
                fos.write(buf, 0, len);
            }
        } finally {
            if (is != null)
                is.close();
            if (fos != null)
                fos.close();
        }
        return f;
    }

    public interface Listener {
        void onStartTextToSpeech(String tag);

        void onReceiveResult(String tag, final SpeechResults speechResults);
    }
}
