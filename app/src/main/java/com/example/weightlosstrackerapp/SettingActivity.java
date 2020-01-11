package com.example.weightlosstrackerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SettingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    DataBaseAssist db;
    private TextView textGoalWeightBox;
    private TextView textGoalDateBox;
    private Button btnUpdateGoal;
    private Button btnDeleteAll;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        db = new DataBaseAssist(this);
        intent = getIntent();

        btnUpdateGoal = (Button) findViewById(R.id.buttonUpdateGoal);
        btnUpdateGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalDateString = textGoalDateBox.getText().toString();
                String goalWeightString = textGoalWeightBox.getText().toString();
                if(goalDateString.length() == 0 || goalWeightString.length() == 0){
                    openSubmitAlertDialog();
                }
                else{
                    float weight = Float.parseFloat(textGoalWeightBox.getText().toString());
                    SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateObj = new Date(textGoalDateBox.getText().toString());
                    String formattedDate = simple.format(dateObj);
                    boolean checkIfInserted = db.insertLog("Goal",formattedDate,weight,0);
                    if (checkIfInserted == true){
                        Toast.makeText(SettingActivity.this,"Goal updated",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(SettingActivity.this,"Failed",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnDeleteAll = (Button) findViewById(R.id.buttonDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteAllData();

            }
        });

        textGoalDateBox = (TextView) findViewById(R.id.goalDateBox);
        textGoalDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragmentObj = new FutureDatePickerFragment();
                datePickerFragmentObj.show(getSupportFragmentManager(),"date picker frag tag2");
            }
        });

        textGoalWeightBox = (TextView) findViewById(R.id.goalWeightBox);
        textGoalWeightBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        String currentDateString = DateFormat.getDateInstance().format(cal.getTime());
        textGoalDateBox = (TextView)findViewById(R.id.goalDateBox);
        textGoalDateBox.setText(currentDateString);
    }

    public void openSubmitAlertDialog(){
        SubmitAlertDialog submitAlertDialogObj = new SubmitAlertDialog();
        submitAlertDialogObj.show(getSupportFragmentManager(),"submitalerttag2");
    }

    @Override
    public void onBackPressed(){
        ArrayList<String> data = db.getStatisticsData();
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("result",data);
        setResult(RESULT_OK,resultIntent);
        finish();

    }
}
