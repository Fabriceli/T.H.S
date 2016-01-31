package app.fabrice.com.ths;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import service.UpdateService;
import utils.CheckInternet;
import utils.NetCallBack;
import utils.RequestUtils;

public class Temperature extends Activity {
    private int Njour=0,Nsemaine=0,N15jours=0,Nmois=0, heureJour=0;
    //private Typeface tf;
    static String description = "meteo",jour1="jour",jour7="7jour",jour15="15jour",jour30="30jour";
    static int descriptionsucces=1,jour1succes=5,jour7succes=6,jour15succes=7,jour30succes=8,icone=9;
    String iconeNumero =null;
    //Bitmap bmp;
    SmartImageView imageView1;
    Float courantOld =0.0f;
    String jour[],semaineJour[],quinzeJour[],moisJour[],prevision[];
    Float semaineT[][],quinzejoursT[][],moisT[][];
    String username ;
    String password ;
    TextView textDate,textMoisSemaine,textMoisMois,textDateCourant,textT,textquinzejours;
    ImageView imageMontee,imageDescende;
    boolean jourfailure =true,semainefailure=true,moisfailure=true,compteFailure=true;
    private JSONObject degreCelsius,descriptionJson;
    public static String url;
    private LineChart chart_courbe_Temperature;
    private BarChart chart_bar_semaine_Temperature,chart_bar_mois_Temperature,chart_bar_quinze_jours_Temperature;
    private Dialog loadingDialog;
    RadioButton btnCourant,btnJour,btn_7,btn_15,btn_30;
    //Button btn_reload_jour,btn_reload_semaine,btn_reload_mois,btn_reload_quinze_jours,btn_prevision_meteo;
    RadioGroup rgNormal;
    RelativeLayout rlchiffre;
    RelativeLayout rlfiguremois,rlfigurequinzejours,rlfiguresemaine,rlfigurejour;
    MyMarkerView mv;

    private TextView tvTmin;
    private TextView tvTmax;
    private TextView tvHumidite;
    private TextView tvVitesseVent;
    private TextView tvDiretionVent;
    private TextView tvBarometre;
    private Button btn_prevision_meteo;
    //boolean privisionclikok=false;
    AlertDialog previsionDialog;
    public String meteo =null,tmin=null,tmax=null,humidit=null,vitesseduvent=null,directionduvent=null,barometre=null;


    /**
     * 数据自动刷新
     *
     */
    private Handler handlerAddEntry = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1://previsionsMeteo
                    try {
                        descriptionJson =new JSONObject((String) msg.obj);

                        //Log.e("descriptionJson:", String.valueOf(descriptionJson));

                        for(int i =0;i<descriptionJson.length();i++){
                            if(!descriptionJson.getBoolean("error")){
                                switch (i+1){
                                    case 1:
                                        if(descriptionJson.getString("meteo1") !=null){
                                            meteo = descriptionJson.getString("meteo1");
                                        }else meteo = null;
                                        break;
                                    case 2:
                                        if(descriptionJson.getString("meteo2") !=null){
                                            tmin = descriptionJson.getString("meteo2");
                                        }else tmin = null;
                                        break;
                                    case 3:
                                        if(descriptionJson.getString("meteo3") !=null){
                                            tmax = descriptionJson.getString("meteo3");
                                        }else tmax = null;
                                        break;
                                    case 4:
                                        if(descriptionJson.getString("meteo4") !=null){
                                            humidit = descriptionJson.getString("meteo4");
                                        }else humidit = null;
                                        break;
                                    case 5:
                                        if(descriptionJson.getString("meteo5") !=null){
                                            vitesseduvent = descriptionJson.getString("meteo5");
                                        }else vitesseduvent = null;
                                        break;
                                    case 6:
                                        if(descriptionJson.getString("meteo6") !=null){
                                            directionduvent = descriptionJson.getString("meteo6");
                                        }else directionduvent = null;
                                        break;
                                    case 7:
                                        if(descriptionJson.getString("meteo7") !=null){
                                            barometre = descriptionJson.getString("meteo7");
                                        }else barometre = null;
                                        break;
                                    case 8:
                                        if(descriptionJson.getString("icon") !=null){
                                            iconeNumero = descriptionJson.getString("icon");
                                            //Log.e("iconeNumero:",iconeNumero);
                                        }else iconeNumero = null;
                                        break;
                                    default:
                                        break;
                                }
                            }else return;

                        }
                        //showDialog();
                        //tempsImage(iconeNumero,icone);
                        if ((previsionDialog != null) && previsionDialog.isShowing()) {
                            previsionDialog.dismiss();
                        }

                        popWindow();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        previsionDialog = null;
                    }
                    break;
                case 2://courant手动刷新
                    try {
                        degreCelsius =new JSONObject((String) msg.obj);
                        textDateCourant.setText(degreCelsius.getString("dateCourante"));

                        if(courantOld>Float.valueOf(degreCelsius.getString("courant"))){
                            imageDescende.setVisibility(View.VISIBLE);
                            imageMontee.setVisibility(View.INVISIBLE);
                            courantOld = Float.valueOf(degreCelsius.getString("courant"));
                        }else if(courantOld<Float.valueOf(degreCelsius.getString("courant"))){
                            imageMontee.setVisibility(View.VISIBLE);
                            imageDescende.setVisibility(View.INVISIBLE);
                            courantOld = Float.valueOf(degreCelsius.getString("courant"));
                        }else {
                            imageMontee.setVisibility(View.INVISIBLE);
                            imageDescende.setVisibility(View.INVISIBLE);
                            courantOld = Float.valueOf(degreCelsius.getString("courant"));
                        }
                        textT.setText(String.format("%s", degreCelsius.getString("courant")) + "°C");
                        if ((loadingDialog != null) && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        loadingDialog = null;
                    }
                    break;
                case 3://courant自动刷新
                    try {
                        JSONObject courantNew = new JSONObject((String) msg.obj);
                        textDateCourant.setText(courantNew.getString("dateCourante"));
                        textT.setText(String.format("%s", courantNew.getString("courant")) + "°C");
                        if(courantOld>Float.valueOf(courantNew.getString("courant"))){
                            imageDescende.setVisibility(View.VISIBLE);
                            imageMontee.setVisibility(View.INVISIBLE);
                            courantOld = Float.valueOf(courantNew.getString("courant"));
                        }else if(courantOld<Float.valueOf(courantNew.getString("courant"))){
                            imageMontee.setVisibility(View.VISIBLE);
                            imageDescende.setVisibility(View.INVISIBLE);
                            courantOld = Float.valueOf(courantNew.getString("courant"));
                        }else {
                            imageMontee.setVisibility(View.INVISIBLE);
                            imageDescende.setVisibility(View.INVISIBLE);
                            courantOld = Float.valueOf(courantNew.getString("courant"));
                        }
                        if ((loadingDialog != null) && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        loadingDialog = null;
                    }
                    break;
                case 4://ECHEC
                    textT.setText("O_o");
                    imageMontee.setVisibility(View.INVISIBLE);
                    imageDescende.setVisibility(View.INVISIBLE);
                    break;
                case 5://jour

                    String jsonDataJour = (String)msg.obj;
                    String heure =null;
                    int heureMin=0;
                    try {
                        //Log.e("jsonDataJour",jsonDataJour);
                        JSONObject jsonObject = new JSONObject(jsonDataJour);
                        heureJour = heureMin = jsonObject.getInt("heureMin");
                        textDate.setText(jsonObject.getString("dateCourante"));//le date ou on est
                        JSONArray jourJsonArray = jsonObject.getJSONArray("jour");
                        Njour = jourJsonArray.length();
                        jour = new String[Njour];
                        //Log.e("jourJsonArray:", String.valueOf(jourJsonArray));
                        for(int i=0;i<jourJsonArray.length();i++) {
                            heure = heureMin+"h";
                            //System.out.println("jourJsonArray : " + jourJsonArray.getJSONObject(i).getString(heure));
                            if(i<jourJsonArray.length()-1){
                                jour[i]=jourJsonArray.getJSONObject(i).getString(heure);
                                heureMin++;
                                //Log.e("jour[i]:", jour[i]);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 6://semaine
                    String jsonDataSemaine = (String)msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonDataSemaine);
                        JSONArray semaineJsonArray = jsonObject.getJSONArray("7jour");
                        Nsemaine = semaineJsonArray.length();
                        textMoisSemaine.setText(jsonObject.getString("moisCourant"));//le mois ou on est
                        semaineJour =new String[Nsemaine];
                        semaineT = new Float[Nsemaine][3];
                        //Log.e("semaineJsonArray:", String.valueOf(semaineJsonArray));
                        for(int i=0;i<semaineJsonArray.length();i++) {
                            if(semaineJsonArray.getJSONObject(i).getString("jour") !="courant"){
                                semaineT[i][0]=Float.parseFloat(semaineJsonArray.getJSONObject(i).getString("min"));
                                semaineT[i][1]=Float.parseFloat(semaineJsonArray.getJSONObject(i).getString("moy"));
                                semaineT[i][2]=Float.parseFloat(semaineJsonArray.getJSONObject(i).getString("max"));
                                semaineJour[i]=semaineJsonArray.getJSONObject(i).getString("jour");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 7://15 jours
                    String jsonDataQuinzeJours = (String)msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonDataQuinzeJours);
                        JSONArray quinzeJsonArray = jsonObject.getJSONArray("15jour");
                        N15jours = quinzeJsonArray.length();
                        textquinzejours.setText(jsonObject.getString("moisCourant"));
                        quinzeJour =new String[N15jours];
                        quinzejoursT = new Float[N15jours][3];
                        Log.e("moisJsonArray:", String.valueOf(quinzeJsonArray));
                        for(int i=0;i<quinzeJsonArray.length();i++) {
                            if(quinzeJsonArray.getJSONObject(i).getString("jour") !="courant"){
                                quinzejoursT[i][0]=Float.parseFloat(quinzeJsonArray.getJSONObject(i).getString("min"));
                                quinzejoursT[i][1]=Float.parseFloat(quinzeJsonArray.getJSONObject(i).getString("moy"));
                                quinzejoursT[i][2]=Float.parseFloat(quinzeJsonArray.getJSONObject(i).getString("max"));
                                quinzeJour[i]=quinzeJsonArray.getJSONObject(i).getString("jour");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 8://mois
                    String jsonDataMois = (String)msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonDataMois);
                        JSONArray moisJsonArray = jsonObject.getJSONArray("30jour");
                        Nmois = moisJsonArray.length();
                        textMoisMois.setText(jsonObject.getString("moisCourant"));
                        moisJour =new String[Nmois];
                        moisT = new Float[Nmois][3];
                        Log.e("moisJsonArray:", String.valueOf(moisJsonArray));
                        for(int i=0;i<moisJsonArray.length();i++) {
                            if(moisJsonArray.getJSONObject(i).getString("jour") !="courant"){
                                moisT[i][0]=Float.parseFloat(moisJsonArray.getJSONObject(i).getString("min"));
                                moisT[i][1]=Float.parseFloat(moisJsonArray.getJSONObject(i).getString("moy"));
                                moisT[i][2]=Float.parseFloat(moisJsonArray.getJSONObject(i).getString("max"));
                                moisJour[i]=moisJsonArray.getJSONObject(i).getString("jour");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 9://mois
                    //bmp=(Bitmap)msg.obj;
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        Intent intent =getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        url = intent.getStringExtra("url");

        //初始化数据
        chart_courbe_Temperature = (LineChart) findViewById(R.id.chart_courbe_Temperature);
        chart_bar_semaine_Temperature = (BarChart) findViewById(R.id.chart_bar_semaine_Temperature);
        chart_bar_mois_Temperature = (BarChart) findViewById(R.id.chart_bar_mois_Temperature);
        chart_bar_quinze_jours_Temperature = (BarChart) findViewById(R.id.chart_bar_quinze_jours_Temperature);
        mv = new MyMarkerView(this, R.layout.marker_view);
        btn_prevision_meteo = (Button) findViewById(R.id.btn_prevision_meteo);
        imageDescende = (ImageView) findViewById(R.id.imageDescende);
        imageMontee = (ImageView) findViewById(R.id.imageMontee);
        textDate = (TextView) findViewById(R.id.textDate);
        textMoisSemaine = (TextView) findViewById(R.id.textMoisSemaine);
        textquinzejours = (TextView) findViewById(R.id.textquinzejours);
        textMoisMois = (TextView) findViewById(R.id.textMoisMois);
        textDateCourant = (TextView) findViewById(R.id.textDateCourant);
       // btn_reload_jour = (Button) findViewById(R.id.btn_reload_jour);
        //btn_reload_semaine = (Button) findViewById(R.id.btn_reload_semaine);
        //btn_reload_mois = (Button) findViewById(R.id.btn_reload_mois);
        //btn_reload_quinze_jours = (Button) findViewById(R.id.btn_reload_quinze_jours);
        textT = (TextView) findViewById(R.id.textT);
        rgNormal = (RadioGroup) findViewById(R.id.rg_normal);
        btnCourant = (RadioButton) findViewById(R.id.btn_courant);
        btnJour = (RadioButton) findViewById(R.id.btn_jour);
        btn_7 = (RadioButton) findViewById(R.id.btn_7);
        btn_30 = (RadioButton) findViewById(R.id.btn_30);
        btn_15 = (RadioButton) findViewById(R.id.btn_15);
        rlchiffre = (RelativeLayout) findViewById(R.id.rlchiffre);
        rlfigurejour = (RelativeLayout) findViewById(R.id.rlfigurejour);
        rlfiguresemaine = (RelativeLayout) findViewById(R.id.rlfiguresemaine);
        rlfiguremois = (RelativeLayout) findViewById(R.id.rlfiguremois);
        rlfigurequinzejours = (RelativeLayout) findViewById(R.id.rlfigurequinzejours);

        //ok = (Button) findViewById(R.id.ok);

        //tf = Typeface.createFromFile(String.valueOf(R.drawable.OpenSans_Regular));
        chart_bar_semaine_Temperature.setMarkerView(mv);
        chart_bar_quinze_jours_Temperature.setMarkerView(mv);
        chart_courbe_Temperature.setMarkerView(mv);
        chart_bar_mois_Temperature.setMarkerView(mv);


        btn_prevision_meteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previsionDialog = ProgressDialog.show(Temperature.this, "Please wait", "Loading...");
                previsionDialog.setCanceledOnTouchOutside(true);
                temperature(description,descriptionsucces);
            }
        });

        //premier fois demander des donnees
        temperature(jour1,jour1succes);
        temperature(jour7,jour7succes);
        temperature(jour15,jour15succes);
        temperature(jour30,jour30succes);
        reloadData();

        //ouvre une service
        Intent startIntent = new Intent(Temperature.this, UpdateService.class);
        startIntent.putExtra("url", ""+url);
        startIntent.putExtra("username",""+username);
        startIntent.putExtra("password", "" + password);
        startService(startIntent);
        //binding la service
        bindService(startIntent, conn, Context.BIND_AUTO_CREATE);


    }

    ServiceConnection conn = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UpdateService.Binder binder = (UpdateService.Binder) service;
            binder.getService().setCallback(new UpdateService.Callback() {
                @Override
                public void DataChange(String result) {
                    //Log.e("UpdateService : ", result);
                    Message msg = new Message();
                    if(result.equals("failure")){
                        System.out.println(result);
                        msg.what = 4;//echec
                        msg.obj = result;
                        handlerAddEntry.sendMessage(msg);
                    }else {
                        msg.what = 3;//actualiste
                        msg.obj = result;
                        handlerAddEntry.sendMessage(msg);
                    }
                }
            });
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.btn_courant:
                if (checked){
                    rlchiffre.setVisibility(View.VISIBLE);
                    rlfigurejour.setVisibility(View.GONE);
                    rlfiguresemaine.setVisibility(View.GONE);
                    rlfiguremois.setVisibility(View.GONE);
                    rlfigurequinzejours.setVisibility(View.GONE);
                    reloadData();
                }
                break;
            case R.id.btn_jour:
                if (checked) {
                    if(jourfailure){
                        temperature(jour1,jour1succes);
                    }
                    //Log.e("Jour", String.valueOf(jour));
                    rlchiffre.setVisibility(View.GONE);
                    rlfiguresemaine.setVisibility(View.GONE);
                    rlfiguremois.setVisibility(View.GONE);
                    rlfigurequinzejours.setVisibility(View.GONE);
                    rlfigurejour.setVisibility(View.VISIBLE);
                    configChartAxis(chart_courbe_Temperature);
                    LineData mLineData = makeLineData(Njour);
                    setChartStyle(chart_courbe_Temperature, mLineData, Color.WHITE);


                }
                break;
            case R.id.btn_7:
                if (checked){
                    if(semainefailure){
                        temperature(jour7,jour7succes);
                    }
                    rlchiffre.setVisibility(View.GONE);
                    rlfigurejour.setVisibility(View.GONE);
                    rlfiguremois.setVisibility(View.GONE);
                    rlfigurequinzejours.setVisibility(View.GONE);
                    rlfiguresemaine.setVisibility(View.VISIBLE);
                    BarChartjours(Nsemaine, semaineT, semaineJour,chart_bar_semaine_Temperature);
                    //chart_bar_semaine_Temperature.setData(getBarData(Nsemaine,semaineT,semaineJour));
                    configChartAxis(chart_bar_semaine_Temperature);

                }
                break;
            case R.id.btn_15:
                if (checked){
                    if(moisfailure){
                        temperature(jour15,jour15succes);
                    }
                    rlchiffre.setVisibility(View.GONE);
                    rlfigurejour.setVisibility(View.GONE);
                    rlfiguresemaine.setVisibility(View.GONE);
                    rlfigurequinzejours.setVisibility(View.VISIBLE);
                    rlfiguremois.setVisibility(View.GONE);
                    BarChartjours(N15jours, quinzejoursT, quinzeJour, chart_bar_quinze_jours_Temperature);
                    //chart_bar_quinze_jours_Temperature.setData(getBarData(N15jours,quinzejoursT,quinzeJour));
                    configChartAxis(chart_bar_quinze_jours_Temperature);

                }
                break;
            case R.id.btn_30:
                if (checked){
                    if(moisfailure){
                        temperature(jour30,jour30succes);
                    }
                    rlchiffre.setVisibility(View.GONE);
                    rlfigurejour.setVisibility(View.GONE);
                    rlfiguresemaine.setVisibility(View.GONE);
                    rlfigurequinzejours.setVisibility(View.GONE);
                    rlfiguremois.setVisibility(View.VISIBLE);
                    BarChartjours(Nmois,moisT,moisJour,chart_bar_mois_Temperature);
                    //chart_bar_mois_Temperature.setData(getBarData(Nmois,moisT,moisJour));
                    configChartAxis(chart_bar_mois_Temperature);

                }
                break;
        }
    }

    /**
     *
     * Bar chart de 7 jours,15 jours,30jours
     *
     * */
    @SuppressWarnings("deprecation")
    public void BarChartjours(int N,Float[][] dataT,String dataJour[],BarChart barChart){
        //Float T1= 0.0f, T2= 0.0f, T3= 0.0f;

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < N-1; i++) {
            xVals.add(dataJour[i]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        ArrayList<BarEntry> yVals3 = new ArrayList<>();

        for (int i = 0; i < N-1; i++) {
            yVals1.add(new BarEntry(dataT[i][0], i));
        }
        for (int i = 0; i < N-1; i++) {
            yVals2.add(new BarEntry(dataT[i][1], i));
        }
        for (int i = 0; i < N-1; i++) {
            yVals3.add(new BarEntry(dataT[i][2], i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "min");
        set1.setColor(getResources().getColor(R.color.color_min));
        BarDataSet set2 = new BarDataSet(yVals2, "moy");
        set2.setColor(getResources().getColor(R.color.color_moy));
        BarDataSet set3 = new BarDataSet(yVals3, "max");
        set3.setColor(getResources().getColor(R.color.color_max));

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);

        BarData data = new BarData(xVals, dataSets);
//        data.setValueFormatter(new LargeValueFormatter());

        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(80f);
        //data.setValueTypeface(tf);

        barChart.setData(data);
        barChart.invalidate();
    }

    private void configChartAxis(BarChart barChart){


        barChart.animateY(3000); // 立即执行的动画,Y轴

        barChart.setDescription("");//不显示右下方字
        barChart.setNoDataTextDescription("0_o");

        BarData barData =barChart.getBarData();
        if(barChart == chart_bar_semaine_Temperature){
            barData.setDrawValues(true);
            barData.setValueTextSize(15);
            barData.setValueFormatter(new ValueFormatter() {
                //nouvelle format de bardata
                @Override
                public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                    DecimalFormat decimalFormat = new DecimalFormat("");
                    //String s = decimalFormat.format(v) + "°C";
                    return decimalFormat.format((int)v) + "°C";
                }

            });

        }else {
            barData.setDrawValues(false);
        }

        //background est transparant
        barChart.setGridBackgroundColor(0x70FFFFFF);
        barChart.setDragEnabled(true);
        barChart.setTouchEnabled(true);
        barChart.setScaleEnabled(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        //xAxis.setTypeface(tf);
        //xAxis.setLabelsToSkip(10);//x轴底部显示间隔设置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x坐标显示在下方

        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setValueFormatter(new MyValueFormatter("T"));//温度纵坐标加摄氏度符号
        //leftYAxis.setTypeface(tf);
        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);//不显示右侧

    }





    //Demande des donnees de la journee
    //Demande des donnees de 7 jours,15jours,30jours
    private void temperature(String tag, final int what){
        CheckInternet checkInternet = new CheckInternet();
        if (checkInternet.CheckInternet(Temperature.this)) {
            RequestParams requestParams = new RequestParams();
            url = "http://tempserver.changeip.org/tempserver/";
            requestParams.put("tag",tag);
            requestParams.put("username", username);
            requestParams.put("password", password);
            RequestUtils.ClientPost(url, requestParams, new NetCallBack() {
                @Override
                public void onMySuccss(String result) throws Exception {
                    Message msg = new Message();
                    msg.what = what;//succes
                    msg.obj = result;
                    handlerAddEntry.sendMessage(msg);
                }

                @Override
                public void onMyFailure(Throwable throwable) {
                    Message msg = new Message();
                    msg.what = 4;//echec
                    msg.obj = "failure";
                    semainefailure = true;
                    handlerAddEntry.sendMessage(msg);
                    if (compteFailure) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Temperature.this);
                        builder.setTitle("Failure!!");
                        builder.setItems(new String[]{"Server ERROR!"}, null);
                        builder.setNegativeButton("OK", null);
                        builder.show();
                        compteFailure = false;
                    }
                }
            });
        }
    }

    //Demande des donnees de courant
    private void reloadData() {
        CheckInternet checkInternet = new CheckInternet();
        if (checkInternet.CheckInternet(Temperature.this)) {
            loadingDialog = ProgressDialog.show(Temperature.this, "Please wait", "Loading...");
            loadingDialog.setCanceledOnTouchOutside(true);
            RequestParams requestParams = new RequestParams();
            url = "http://tempserver.changeip.org/tempserver/";
            requestParams.put("tag","courant");
            requestParams.put("username", username);
            requestParams.put("password", password);
            RequestUtils.ClientPost(url, requestParams, new NetCallBack() {
                @Override
                public void onMySuccss(String result) throws Exception {
                    Message msg = new Message();
                    msg.what = 2;//succes
                    msg.obj = result;
                    handlerAddEntry.sendMessage(msg);
                }

                @Override
                public void onMyFailure(Throwable throwable) {
                    Message msg = new Message();
                    msg.what = 4;//echec
                    msg.obj = "failure";
                    handlerAddEntry.sendMessage(msg);
                    if(compteFailure){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Temperature.this);
                        builder.setTitle("Failure!!");
                        builder.setItems(new String[]{"Server ERROR!"}, null);
                        builder.setNegativeButton("OK", null);
                        builder.show();
                        compteFailure =false;
                    }
                    loadingDialog.dismiss();
                }
            });
        }
    }

    private LineData makeLineData(int count) {
        int addheure=heureJour;

        ArrayList<String> x = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据
            if(addheure<24){
                x.add(addheure+"h");
                addheure++;
            }else {
                addheure =addheure-24;
                x.add(addheure+"h");
            }

        }

        // y轴的数据
        ArrayList<Entry> y = new ArrayList<>();
        for (int i = 0; i < count-1; i++) {
            Entry entry = new Entry(Float.parseFloat(jour[i]), i);
            y.add(entry);
        }

        // y轴数据集
        LineDataSet mLineDataSet = new LineDataSet(y, "Temperature");

        // 用y轴的集合来设置参数
        mLineDataSet.setLineWidth(3.0f);
        mLineDataSet.setCircleSize(4.0f);
        mLineDataSet.setColor(Color.GREEN);
        mLineDataSet.setCircleColor(Color.GREEN);

        mLineDataSet.setDrawHighlightIndicators(false);
        mLineDataSet.setHighLightColor(Color.CYAN);


        // 填充曲线下方的区域，红色，半透明。
        mLineDataSet.setDrawFilled(true);
        mLineDataSet.setFillAlpha(20);
        mLineDataSet.setFillColor(Color.GREEN);

        // 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
        mLineDataSet.setCircleColorHole(Color.WHITE);

        ArrayList<LineDataSet> mLineDataSets = new ArrayList<>();
        mLineDataSets.add(mLineDataSet);

        LineData mLineData = new LineData(x, mLineDataSets);
        //mLineDataSet.setLabel("Temperature");

        // 不要在折线上标出数据。
        mLineData.setDrawValues(false);

        return mLineData;

    }

    /**
     * 配置图片相关参数
     * lineChart
     */

    private void configChartAxis(LineChart lineChart){


        lineChart.animateY(3000); // 立即执行的动画,Y轴

        lineChart.setDescription("");//不显示右下方字

        //设置一个透明度
        lineChart.setGridBackgroundColor(0x70FFFFFF);
        lineChart.setDragEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setScaleEnabled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        //xAxis.setLabelsToSkip(10);//x轴底部显示间隔设置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x坐标显示在下方

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setValueFormatter(new MyValueFormatter("T"));//温度纵坐标加摄氏度符号

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);//不显示右侧

    }

    // 设置显示的样式
    private void setChartStyle(LineChart mLineChart, LineData lineData, int color) {
        mLineChart.setDrawBorders(false);

        mLineChart.setDescription(null);
        mLineChart.setNoDataTextDescription("0_o");

        mLineChart.setDrawGridBackground(false);
        mLineChart.setGridBackgroundColor(Color.CYAN);

        // 触摸
        mLineChart.setTouchEnabled(true);

        // 拖拽
        mLineChart.setDragEnabled(true);

        // 缩放
        mLineChart.setScaleEnabled(true);

        mLineChart.setPinchZoom(false);

        // 设置背景
        mLineChart.setBackgroundColor(color);

        // 设置x,y轴的数据
        mLineChart.setData(lineData);

        // 比例图标，y的value
        Legend mLegend = mLineChart.getLegend();

        mLegend.setEnabled(false);

        mLineChart.animateX(2000);
    }


    /*
    *
    *    Reload des button
    *
    * */
    public void ReloadJour(View view) {
        temperature(jour1,jour1succes);
        configChartAxis(chart_courbe_Temperature);
        LineData mLineData = makeLineData(Njour);
        setChartStyle(chart_courbe_Temperature, mLineData, Color.WHITE);
        compteFailure=true;
    }

    public void ReloadSemaine(View view) {
        temperature(jour7, jour7succes);
        BarChartjours(Nsemaine, semaineT, semaineJour, chart_bar_semaine_Temperature);
        //chart_bar_semaine_Temperature.setData(getBarData(Nsemaine,semaineT,semaineJour));
        configChartAxis(chart_bar_semaine_Temperature);
        compteFailure=true;
    }

    public void ReloadQuinzeJours(View view) {
        temperature(jour15,jour15succes);
        BarChartjours(N15jours,quinzejoursT,quinzeJour,chart_bar_quinze_jours_Temperature);
        //chart_bar_quinze_jours_Temperature.setData(getBarData(N15jours,quinzejoursT,quinzeJour));
        configChartAxis(chart_bar_quinze_jours_Temperature);
        compteFailure=true;
    }

    public void ReloadMois(View view) {
        temperature(jour30, jour30succes);
        BarChartjours(Nmois, moisT, moisJour,chart_bar_mois_Temperature);
        //chart_bar_mois_Temperature.setData(getBarData(Nmois, moisT, moisJour));
        configChartAxis(chart_bar_mois_Temperature);
        compteFailure=true;
    }

    /*
    *
    * dialog de privision de temps
    *
    * */
    @SuppressWarnings("deprecation")
    private void popWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);//获取一个填充器
        View view = inflater.inflate(R.layout.prevision_meteo_dialog, null);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();//创建一个Point点对象用来接收屏幕尺寸信息
        display.getSize(size);
        int width = size.x;
        //int height = size.y;//从Point点对象中获取屏幕的高度(单位像素)
        //Log.v("zxy", "width=" + width + ",height=" + height);
        PopupWindow popWindow = new PopupWindow(view, 2 * width / 3, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOutsideTouchable(true);//设置是否点击PopupWindow外退出PopupWindow
        WindowManager.LayoutParams params = getWindow().getAttributes();//创建当前界面的一个参数对象
        params.alpha = 0.8f;
        getWindow().setAttributes(params);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {//设置PopupWindow退出监听器
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });

        popWindow.showAtLocation(inflater.inflate(R.layout.activity_temperature, null), Gravity.CENTER, 0, 0);
        //privision de meteo
        TextView tvMeteo = (TextView) view.findViewById(R.id.tvMeteo);
        tvTmax = (TextView) view.findViewById(R.id.tvTmax);
        tvTmin = (TextView) view.findViewById(R.id.tvTmin);
        tvHumidite = (TextView) view.findViewById(R.id.tvHumidite);
        tvVitesseVent = (TextView) view.findViewById(R.id.tvVitesseVent);
        tvDiretionVent = (TextView) view.findViewById(R.id.tvDiretionVent);
        tvBarometre = (TextView) view.findViewById(R.id.tvBarometre);
        imageView1 = (SmartImageView ) view.findViewById(R.id.imageView1);
        tvMeteo.setText(meteo);
        tvTmax.setText(tmax);
        tvTmin.setText(tmin);
        tvHumidite.setText(humidit);
        tvVitesseVent.setText(vitesseduvent);
        tvDiretionVent.setText(directionduvent);
        tvBarometre.setText(barometre);
        //Log.e("iconeNumero:", iconeNumero);
        //imageView1.setImageUrl("http://openweathermap.org/img/w/"+iconeNumero+".png");
        int resourceId = getResources().getIdentifier("drawable/icon_"+iconeNumero,null,getPackageName());
        @SuppressWarnings("deprecation")
        Drawable tempsIconeDrawable = getResources().getDrawable(resourceId);
        imageView1.setImageDrawable(tempsIconeDrawable);
    }


    @Override
    protected void onDestroy() {
        Intent startIntent = new Intent(Temperature.this, UpdateService.class);
        unbindService(conn);
        stopService(startIntent);
        //finish();
        if(loadingDialog !=null){
            loadingDialog.dismiss();
        }
        if(previsionDialog !=null){
            previsionDialog.dismiss();
        }

        super.onDestroy();
    }

}