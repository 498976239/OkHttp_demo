package com.okhttp_demo.www.okhttp_demo.Model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


/**
 * Created by Administrator on 16-12-1.
 */
public class OKManager {
    private OkHttpClient mClient;
    private static volatile OKManager manager;//防止多线程访问
    private  final String TAG = OKManager.class.getSimpleName();//获得当前类名
    private Handler handler;
    //向服务器提交JSON数据
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串给服务器
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");
    private OKManager(){
        mClient = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }
    //采用单例模式获取OKManager
    public static OKManager getInstance(){
        OKManager instance = null;
        if(manager==null){
               synchronized (OKManager.class){//同步代码块
                   if(instance==null){
                       instance = new OKManager();
                       manager = instance;
                   }
               }
        }
        return instance;
    }
    //同步请求在android开发中不常用，会阻塞UI线程
    public String syncGetByURL(String url){
        //构建一个request请求
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();//同步数据请求
            if(response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //请求指定的URL，返回的是一个JSON字符串(异步请求)
    public void asyncJSONStringByURL(String url,final Func1 callBack){
            final Request request = new Request.Builder().url(url).build();
            mClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(response.isSuccessful()&&response!=null){
                        onSuccessJsonStringMethod(response.body().string(),callBack);
                    }
                }
            });
    }
    /*请求返回的结果是JSON字符串*/
    private void onSuccessJsonStringMethod(final String jsonValue, final Func1 callBack){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(callBack!=null){
                    try {
                        callBack.onResponse(jsonValue);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    //返回的是字节数组
    private void onSuccessByteMethod(final byte[] date,final Func2 callBack){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(callBack!=null){
                    callBack.onResponse(date);
                }
            }
        });
    }
    //返回结果是JSON对象
    private void onSuccessJSONObjectMethod(final String jsonValue, final Func4 callBack){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(callBack!=null){
                        try {
                            callBack.onResponse(new JSONObject(jsonValue));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }
        });
    }
    public interface Func1{
        void onResponse(String result);
        }
    interface Func2{
        void onResponse(byte[] result);
        }
    interface Func3{
        void onResponse(Bitmap bitmap);
    }
    interface Func4{
        void onResponse(JSONObject jsonObject);
    }

}
