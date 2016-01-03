package app.fabrice.com.ths;

import android.app.Activity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class Temperature extends Activity {
    final int N = 12;
    LineChart chart_Temperature,chart_Humidite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        chart_Temperature = (LineChart) findViewById(R.id.chart_Temperature);
        chart_Humidite = (LineChart) findViewById(R.id.chart_Humidite);
        configChartAxis(chart_Temperature);//
        configChartAxis(chart_Humidite);
        chart_Temperature.setData(getLineData());
        chart_Humidite.setData(getLineData());
    }

    /**
     * 配置图片相关参数
     * @param lineChart
     */

    private void configChartAxis(LineChart lineChart){

        LineData lineData = getLineData();
        lineData.setDrawValues(false);//不显示上方数据

        lineChart.setDescription("");//不显示右下方字

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x坐标显示在下方

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setDrawGridLines(false);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);//不显示右侧

    }

    /**
     * 一組圖表資料為一個DataSet，
     * 一個圖表可以同時顯示多筆DataSets，
     * 我們現在只產生一組DataSet，
     * 將它整合到圖表資料(LineData)裡
     * @return
     */

    private LineData getLineData() {
        LineDataSet dataSetA = new LineDataSet(getCharData(),"LabelA");
        List<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA);

        return new LineData(getLabels(),dataSets);
    }

    /**
     * 用來代表一筆資料在X座標位置所相對應的顯示字串，
     * 以下產生出'X0', 'X1', 'X2', 'X3', 'X4'
     * @return
     */
    private List<String> getLabels() {
        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<N;i++){
            chartLabels.add("X"+i);
        }
        return chartLabels;
    }

    /**
     * 建立资料点
     * 每一個資料點就是一個Entry(y, x)，
     * 其中y的型態是float，x為int.
     * @return
     */
    private List<Entry> getCharData() {

        List<Entry> chartData = new ArrayList<>();
        for(int i = 0; i <N;i++){
            chartData.add(new Entry((float)Math.random(),i));
        }
        return chartData;
    }
}
