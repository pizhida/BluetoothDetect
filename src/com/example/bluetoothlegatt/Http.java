package com.example.bluetoothlegatt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Http 
{
    public static final int HTTP_POST_OK = 1;
    public static final int HTTP_POST_ERROR =2;

    private HttpClient client;
    private HttpPost post;
    private HttpResponse response;
    private Thread thread;


    public Http(String link){
        client = new DefaultHttpClient();
        post = new HttpPost(link);
    }

    public void doPost(final ArrayList<NameValuePair> data ,final Handler handler) {
//        thread = new Thread(){
//            @Override
//            public void run() {
//                Message message = new Message();
//
//                try {
//                    // data preparation
//                    post.setEntity(new UrlEncodedFormEntity(data));
//
//                    // execute data
//                    response = client.execute(post);
////                    message.what = HTTP_POST_OK;
////                    message.obj = (String)EntityUtils.toString(response.getEntity(),HTTP.UTF_8) ;
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    Log.d("FUCK", "e1");
//                    message.what = HTTP_POST_ERROR;
//                } catch (ClientProtocolException e) {
//                    e.printStackTrace();
//                    Log.d("FUCK", "e2");
//                    message.what = HTTP_POST_ERROR;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.d("FUCK", "e3");
//                    message.what = HTTP_POST_ERROR;
//                }
//                finally{
//                    handler.sendMessage(message);
//
//                }
//
//            }
//
//        };
//
//        thread.start();

        Message message = new Message();

        try {
            // data preparation
            post.setEntity(new UrlEncodedFormEntity(data));

            // execute data
            response = client.execute(post);
//                    message.what = HTTP_POST_OK;
//                    message.obj = (String)EntityUtils.toString(response.getEntity(),HTTP.UTF_8) ;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("FUCK", "e1");
            message.what = HTTP_POST_ERROR;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.d("FUCK", "e2");
            message.what = HTTP_POST_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("FUCK", "e3");
            message.what = HTTP_POST_ERROR;
        }
        finally{
            handler.sendMessage(message);

        }

    }

}
