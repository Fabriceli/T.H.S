package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Fabrice on 17/11/2015.
 */
public class CheckInternet {
    Context context;

    public boolean CheckInternet(Context context){
        this.context = context;
        AlertDialog.Builder nointernet;
        ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manger.getActiveNetworkInfo();
        if(info!=null&&info.isConnected()){
            return true;
        }else{
            nointernet = new AlertDialog.Builder(context);
            nointernet.setTitle("O_o");
            nointernet.setMessage("Please check your internet connection.");
            nointernet.setNegativeButton("Configuration",onclik);
            nointernet.setNeutralButton("OK",null);
            nointernet.show();
            return false;
        }
    }

    DialogInterface.OnClickListener onclik = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(android.os.Build.VERSION.SDK_INT > 10 ){
                //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            } else {
                context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        }
    };
}
