package service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.loopj.android.http.RequestParams;

import utils.NetCallBack;
import utils.RequestUtils;

public class UpdateService extends Service {
    private boolean running = false;
    private Callback callback = null;
    private String password,username;
    private String url;
    public UpdateService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        url = intent.getStringExtra("url");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        running = true;
        super.onCreate();
        new Thread(){
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams();
                super.run();
                while (running){
                    try {
                        sleep(5 * 1000);
                        if((username != null)&&(password != null)){
                            requestParams.put("tag","courant");
                            requestParams.put("username", username);
                            requestParams.put("password", password);
                            RequestUtils.ClientPost(url, requestParams, new NetCallBack() {
                                @Override
                                public void onMySuccss(String result) throws Exception {
                                    callback.DataChange(result);
                                }

                                @Override
                                public void onMyFailure(Throwable throwable) {
                                    callback.DataChange("failure");
                                }
                            });
                        }else{
                            return;
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }


    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public class Binder extends android.os.Binder{
        public UpdateService getService(){
            return UpdateService.this;
        }
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public static interface Callback{
        void DataChange(String result);
    }

}
