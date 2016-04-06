package com.lyricaloriginal.speechtotextsample;

import android.content.res.TypedArray;

import com.lyricaloriginal.speechtotextsample.models.SpeechRecognitionEvent;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by LyricalMaestro on 2016/04/05.
 */
public class SpeechToTextRetro {
    static{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://stream.watsonplatform.net/speech-to-text/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API = retrofit.create(Api.class);
    }

    public static Api API;

    public interface Api{

        @POST("v1/recognize")
        Call<SpeechRecognitionEvent> recognize(
                @Header("Authorization") String basicAuth,
                @Header("Content-Type") String contentType,
                @Query("model") String model,
                @Body RequestBody audioBytes
        );
   }
}