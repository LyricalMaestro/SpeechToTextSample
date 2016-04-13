package com.lyricaloriginal.speechtotextsample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements SpeechToTextFragment.Listener{

    private static final String TAG = MainActivity.class.getName();

    private SpeechToTextFragment mSttFragment = null;
    private TextView mMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            mSttFragment = new SpeechToTextFragment();
            getSupportFragmentManager().
                    beginTransaction().
                    add(mSttFragment, SpeechToTextFragment.class.getName()).
                    commit();
        }else{
            mSttFragment = (SpeechToTextFragment) getSupportFragmentManager().
                    findFragmentByTag(SpeechToTextFragment.class.getName());
        }

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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        speechToText(data.getData());
                    }catch(IOException ex) {
                        Log.d(TAG, ex.getMessage(), ex);
                    }
                }
            });
        }
    }

    private void speechToText(final Uri uri) throws IOException{
        Properties properties = new Properties();
        properties.load(getAssets().open("account.txt", MODE_PRIVATE));
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        mSttFragment.speechToText(username, password, uri);

    }

    @Override
    public void onStartTextToSpeech(String tag) {
        mMsg.setText("指定ファイル解析中・・・\n");
    }

    @Override
    public void onReceiveResult(String tag, SpeechResults speechResults) {
        if(speechResults == null){
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(Transcript transcript : speechResults.getResults()){
            String word = transcript.getAlternatives().get(0).getTranscript();
            sb.append(word);
        }
        mMsg.append(sb.toString());
        Log.d(TAG, speechResults.toString());
    }
}
