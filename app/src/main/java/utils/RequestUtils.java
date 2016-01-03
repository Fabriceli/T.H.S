package utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

/**
 * Created by Fabrice on 17/11/2015.
 */
public class RequestUtils {
    public static AsyncHttpClient client = new AsyncHttpClient();
    public static void ClientGet(String url, NetCallBack netCallBack){

        client.get(url,netCallBack);
    }
    public static void ClientPost(String url, RequestParams params, NetCallBack netCallBack){
        client.post(url, params, netCallBack);
    }
}
