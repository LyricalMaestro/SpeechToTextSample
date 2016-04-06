package com.lyricaloriginal.speechtotextsample;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by LyricalMaestro on 2016/04/05.
 */
public class Utils {

    public static String toBasicAuth(String username, String password) throws UnsupportedEncodingException {
        byte[] b64data = Base64.encode((username + ":" + password).getBytes("UTF-8"), Base64.DEFAULT);
        return "Basic" + new String(b64data, "UTF-8");
    }
}
