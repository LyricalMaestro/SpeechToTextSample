package com.lyricaloriginal.speechtotextsample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private SpeechToText mStt = null;

    private HandlerThread mHt = null;
    private Handler mhandler = null;

    private TextView mMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMsg = (TextView)findViewById(android.R.id.text1);

        findViewById(android.R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHt = new HandlerThread("ws");
        mHt.start();
        mhandler = new Handler(mHt.getLooper());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHt != null) {
            if (mStt != null) {
                mStt = null;
            }
            mHt.quit();
            mhandler = null;
            mHt = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    speechToText(data.getData());
                }
            });
        }

    }

    private void speechToText(final Uri uri){
        String username = "username";
        String password = "password";

        SpeechToText stt = new SpeechToText();
        stt.setUsernameAndPassword(username, password);
        stt.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");

        final RecognizeOptions options = new RecognizeOptions().contentType("audio/wav")
                .continuous(true).interimResults(true).model("ja-JP_BroadbandModel");
        mStt = stt;
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    File target = downloadWavFile(uri);
                    final SpeechResults speechResults = mStt.recognize(target, options);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                            mMsg.append(speechResults.toString());
                            mMsg.append("\n");
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private File downloadWavFile(Uri uri) throws IOException {
        File f = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "sample.wav");
        if(!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
        }

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = getContentResolver().openInputStream(uri);
            fos = new FileOutputStream(f);
            byte[] buf = new byte[256];
            int len = 0;
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
}
