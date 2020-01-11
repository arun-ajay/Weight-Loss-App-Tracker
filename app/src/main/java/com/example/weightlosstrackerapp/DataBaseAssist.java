package com.example.weightlosstrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class DataBaseAssist extends SQLiteOpenHelper {

    public static final String DB_NAME = "main.db";
    public static final String TB_NAME = "logsTable";
    public static final String COL_1_LOG_TYPE = "Log_Type";
    public static final String COL_2_DATE = "Date";
    public static final String COL_3_WEIGHT = "Weight";
    public static final String COL_4_TIME_SPENT = "Time_Spent";


    public DataBaseAssist(Context context) {
        super(context, DB_NAME, null, 4);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TB_NAME + " (Log_Type TEXT, Date TEXT PRIMARY KEY, Weight REAL, Time_Spent INTEGER) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TB_NAME);
        onCreate(db);

    }

    public boolean insertLog(String logType, String date, float weight, int time_spent){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_LOG_TYPE,logType);
        contentValues.put(COL_2_DATE,date);
        contentValues.put(COL_3_WEIGHT,weight);
        contentValues.put(COL_4_TIME_SPENT,time_spent);
//        int output = (int) db.insertWithOnConflict("your_table", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
//        if (output == -1) {-_
//            db.update("your_table", contentValues, "_id=?", new String[] {"1"});
//        }
//        return true;
        if(logType.equals("Log")){
            long output = db.insert(TB_NAME,null,contentValues);
            if(output == -1){
                db.update(TB_NAME,contentValues,"Date=? AND Log_Type=?",new String[]{date,logType});
                return true;
            }
            else{
                return true;
            }
        }
        else{
            db.delete(TB_NAME,"Log_Type = 'Goal'",new String[]{});
            long output = db.insert(TB_NAME,null,contentValues);
            return true;
//            if(output == -1){
//                db.update(TB_NAME,contentValues,"Log_Type='Goal'",new String[]{});
//                return true;
//            }
//            else{
//                return true;


        }


    }

    public ArrayList<Float> getWeightData(int amount){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        ArrayList<Float> dataFloat = new ArrayList<Float>();

        //get current weight  -- INDEX 0
        res = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE Log_Type == 'Log' ORDER BY Date DESC",null);
        if(res.getCount() == 0){
            return dataFloat;
        }
        else{
            int count = 0;
            while(res.moveToNext()){
                count +=1;
                if (count == amount){
                    break;
                }
                String currentWeight = res.getString(2);
                float currentWeightVal = Float.valueOf(currentWeight);
                dataFloat.add(currentWeightVal);

            }
            return dataFloat;
        }

    }
    public ArrayList<String> getDateData(int amount){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        ArrayList<String> dataString = new ArrayList<String>();

        //get current weight  -- INDEX 0
        res = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE Log_Type == 'Log' ORDER BY Date DESC",null);
        if(res.getCount() == 0){
            return dataString;
        }
        else{
            int count = 0;
            while(res.moveToNext()){
                count +=1;
                if (count == amount){
                    break;
                }
                String currentDate = res.getString(1);
                dataString.add(currentDate);

            }
            return dataString;
        }

    }

    public ArrayList<String> getStatisticsData(){

        ArrayList<String> data = new ArrayList<String>();

        String currentWeight;
        String firstWeight;
        String goalWeight;
        String goalDate ="";
        String currentTime;
        String averageTime;
        String maxTime;

        float currentWeightVal = 0;
        float firstWeightVal = 0;
        float goalWeightVal = 0;

        String currentDate = "";
        String firstDate = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;

        //get current weight  -- INDEX 0
        res = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE Log_Type == 'Log' ORDER BY Date DESC",null);
        if(res.getCount() == 0){
            data.add("");
        }
        else{
            while(res.moveToNext()){
                currentDate = res.getString(1);
                currentWeight = res.getString(2);
                currentWeightVal = Float.valueOf(currentWeight);
                data.add(currentWeight);
                break;
            }
        }
        //get net weight -- INDEX 1
        res = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE Log_Type == 'Log' ORDER BY Date ASC",null);
        if(res.getCount() == 0){
            data.add("");
        }
        else{
            while(res.moveToNext()){
                firstDate = res.getString(1);
                firstWeight = res.getString(2);
                firstWeightVal = Float.valueOf(firstWeight);
                float netWeightVal = firstWeightVal - currentWeightVal;
                data.add(String.format("%.2f",netWeightVal));
                break;
            }
        }
        //get goal weight -- INDEX 2
        res = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE Log_Type == 'Goal'",null);
        if(res.getCount() == 0){
            data.add("");
        }
        else{
            while(res.moveToNext()){
                goalDate = res.getString(1);
                goalWeight = res.getString(2);
                goalWeightVal = Float.valueOf(goalWeight);
                data.add(goalWeight);
                break;
            }
        }
        //get last workout time -- INDEX 3
        res = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE Log_Type == 'Log' ORDER BY DATE DESC",null);
        if(res.getCount() == 0){
            data.add("");
        }
        else{
            while(res.moveToNext()){
                currentTime = res.getString(3);
                data.add(currentTime);
                break;
            }
        }

        //get avg workout time -- INDEX 4
        res = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE Log_Type == 'Log' ORDER BY DATE DESC",null);
        if(res.getCount() == 0){
            data.add("");
        }
        else{
            int count = 0;
            int sumtime = 0;
            while(res.moveToNext()){
                sumtime += Integer.valueOf(res.getString(3));
                count += 1;
            }
            if (count != 0){
                averageTime = String.valueOf((sumtime/count));
                data.add(String.valueOf(averageTime));

            }


        }

//        get max workout time -- INDEX 5
        res = db.rawQuery("SELECT MAX(Time_Spent) FROM " + TB_NAME + " WHERE Log_Type == 'Log'",null);
        if(res.getCount() == 0){
            data.add("");
        }
        else{
            while(res.moveToNext()){
                maxTime = res.getString(0);
                data.add(maxTime);
                break;
            }
        }

        // get average daily loss -- INDEX 6
        if(firstDate.equals("")){
            data.add("");
        }
        else {

            try {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = null;
                Date d2 = null;
                d1 = format.parse(currentDate);
                d2 = format.parse(firstDate);
                double difference =d2.getTime() - d1.getTime();
                double differenceDays = difference / (24*60*60*1000);
                double avg = (currentWeightVal-firstWeightVal)/differenceDays;
                data.add(String.format("%.2f",avg));
            }catch(Exception e){
                //do nothing
            }

        }

        //get ideal daily loss -- INDEX 7
        if(firstDate.equals("") || goalDate.equals("")){
            data.add("");
        }
        else {
            try {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = null;
                Date d2 = null;
                d1 = format.parse(goalDate);
                d2 = format.parse(firstDate);
                double difference =d2.getTime() - d1.getTime();
                double differenceDays = difference / (24*60*60*1000);
                double avg = (goalWeightVal-firstWeightVal)/differenceDays;
                data.add(String.format("%.2f",avg));
            }catch(Exception e){
                //do nothing
            }

        }
        return data;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TB_NAME +" ORDER BY Date DESC",null);
        return res;
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
       db.delete(TB_NAME,null,null);

    }
}
