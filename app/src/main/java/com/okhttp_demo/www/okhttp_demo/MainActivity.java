package com.okhttp_demo.www.okhttp_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.okhttp_demo.www.okhttp_demo.Model.OKManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private Button button2;
    private ImageView iv;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SUCCESS_STATUS = 1;
    private static final int FAIL_STATUS = 0;
    private OkHttpClient mClient;
    private OKManager manager;
    private String image_path ="https://www.baidu.com/img/bd_logo1.png";
    private String image_path2 = "http://pic2.ooopic.com/13/49/18/03bOOOPIC53_1024.jpg";
    private String JSON_PATH = "http://apistore.baidu.com/apiworks/servicedetail/688.html";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case SUCCESS_STATUS:
                    byte[] result = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result,0,result.length);
                    iv.setImageBitmap(bitmap);
                break;
                case FAIL_STATUS:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.pictrue_show);
        mClient = new OkHttpClient();
        //使用的是GET请求
       final Request request = new Request.Builder().get().url(image_path).build();
        button = (Button) findViewById(R.id.down);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        Message message = handler.obtainMessage();
                        if(response.isSuccessful()){
                            message.what = SUCCESS_STATUS;
                            message.obj = response.body().bytes();
                            handler.sendMessage(message);
                        }else{
                            handler.sendEmptyMessage(FAIL_STATUS);
                        }
                    }
                });
            }
        });
        manager = OKManager.getInstance();
        button2 = (Button) findViewById(R.id.show);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               manager.asyncJSONStringByURL(JSON_PATH, new OKManager.Func1() {
                   @Override
                   public void onResponse(String result) {
                        Log.i(TAG,result);
                   }
               });
            }
        });

    }
}
