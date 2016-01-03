package utils;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by Fabrice on 8/3/2015.
 */
public abstract class NetCallBack extends AsyncHttpResponseHandler {
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(String s) {
        try {
            onMySuccss(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSuccess(s);
    }

    @Override
    public void onFailure(Throwable throwable) {
        onMyFailure(throwable);
        super.onFailure(throwable);
    }

    public abstract void onMySuccss(String result) throws Exception;
    public abstract void onMyFailure(Throwable throwable);
}
