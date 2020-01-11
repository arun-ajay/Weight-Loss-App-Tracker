package com.example.weightlosstrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DataBaseAssist db;
    private Button btnInputWeight;
    private Button btnAlltime;
    private Button btnLast30;
    private Button btnLast7;
    private Button btnSettings;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    //graphcomponent
    LineGraphSeries<DataPoint> series;
    GraphView graph;

    //statistics components
    private TextView statCurWt;
    private TextView statNetWt;
    private TextView statGoalWt;
    private TextView statLastTime;
    private TextView statAvgTime;
    private TextView statBestTime;
    private TextView statAvgWtLoss;
    private TextView statIdealWtLoss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBaseAssist(this);
        statCurWt = (TextView) findViewById(R.id.statCurWt);
        statNetWt = (TextView) findViewById(R.id.statNetWt);
        statGoalWt = (TextView) findViewById(R.id.statGoalWt);
        statLastTime = (TextView) findViewById(R.id.statLastTime);
        statAvgTime = (TextView) findViewById(R.id.statAvgTime);
        statBestTime = (TextView) findViewById(R.id.statBestTime);
        statAvgWtLoss = (TextView) findViewById(R.id.statAvgWtLoss);
        statIdealWtLoss = (TextView) findViewById(R.id.statIdealWtLoss);


        updateStatsAndChart();
        graphData(graph,series,7);

        btnAlltime = (Button) findViewById(R.id.buttonAllTime);
        btnAlltime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphData(graph,series,-1);
            }
        });

        btnLast30 = (Button) findViewById(R.id.buttonLast30);
        btnLast30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphData(graph,series,30);
            }
        });

        btnLast7 = (Button) findViewById(R.id.buttonLast7);
        btnLast7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphData(graph,series,7);
            }
        });

        btnSettings = (Button) findViewById(R.id.buttonSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity_Setting();


            }
        });

        btnInputWeight = (Button) findViewById(R.id.buttonInputWeight);
        btnInputWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statGoalWt.length() == 0){
                    openGoalWeightMissingAlertDialog();
                }
                else {
                    openActivity_InputWeightDate();
                }
            }
        });

    }

    public void graphData(GraphView graph, LineGraphSeries<DataPoint> series,int restriction){
        graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();;
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Weight(lbs)");
        graph.getGridLabelRenderer().setTextSize(30);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.WHITE);

        series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.WHITE);
        series.setDrawDataPoints(true);
        series.setDrawBackground(true);
        series.setThickness(10);
        ArrayList<Float> dataFloat = db.getWeightData(restriction);
        ArrayList<String> dataString = db.getDateData(restriction);
        int limit = dataFloat.size();
        for(int i = 0; i < limit; i++){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date xDate = null;
            try {
                xDate = format.parse(dataString.get(limit - i - 1));
            }catch(Exception e){
                //do nothing
            }
            float y = dataFloat.get(limit - i - 1);
            series.appendData(new DataPoint(xDate,y),true,limit);

        }
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    return format.format(new Date((long)value));
                }
                else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

    }

    public void openActivity_Setting(){
        Intent intent = new Intent(this,SettingActivity.class);
        startActivityForResult(intent,2);
    }


    public void openActivity_InputWeightDate(){
        Intent intent =  new Intent(this, InputWeightDateActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        updateStatsAndChart();
        graphData(graph,series,7);
    }


    public void updateStatsAndChart(){
        ArrayList<String> data = db.getStatisticsData();
        statCurWt.setText(data.get(0));
        statNetWt.setText(data.get(1));
        statGoalWt.setText(data.get(2));
        statLastTime.setText(data.get(3));
        statAvgTime.setText(data.get(4));
        statBestTime.setText(data.get(5));
        statAvgWtLoss.setText(data.get(6));
        statIdealWtLoss.setText(data.get(7));
        if(!statNetWt.getText().toString().equals("")){
            if(Float.valueOf(statNetWt.getText().toString()) < 0){
                statNetWt.setTextColor(Color.RED);
            }
            else if(Float.valueOf(statNetWt.getText().toString()) > 0){
                statNetWt.setTextColor(Color.GREEN);
            }
            else{
                statNetWt.setTextColor(Color.WHITE);
            }
        }
        if(!statLastTime.getText().toString().equals("")) {

            if (Integer.valueOf(statLastTime.getText().toString()) < Integer.valueOf(statAvgTime.getText().toString())) {
                statLastTime.setTextColor(Color.RED);
            } else if (Integer.valueOf(statLastTime.getText().toString()) > Integer.valueOf(statAvgTime.getText().toString())) {
                statLastTime.setTextColor(Color.GREEN);
            }
        }
        if(!statAvgWtLoss.getText().toString().equals("")) {

            if (Float.valueOf(statAvgWtLoss.getText().toString()) < Float.valueOf(statIdealWtLoss.getText().toString())) {
                statAvgWtLoss.setTextColor(Color.RED);
            } else if (Float.valueOf(statAvgWtLoss.getText().toString()) > Float.valueOf(statIdealWtLoss.getText().toString())) {
                statAvgWtLoss.setTextColor(Color.GREEN);
            }
        }

    }

    public void openGoalWeightMissingAlertDialog(){
        GoalWeightMissingAlertDialog goalWeightMissingAlertDialog = new GoalWeightMissingAlertDialog();
        goalWeightMissingAlertDialog.show(getSupportFragmentManager(),"goalalerttag");
    }



}
