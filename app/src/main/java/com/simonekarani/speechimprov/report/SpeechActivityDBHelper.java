package com.simonekarani.speechimprov.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

public class SpeechActivityDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MySpeechActivity.db";
    public static final String SPEECH_LOG_TABLE_NAME = "myactivity";
    public static final String SPEECH_LOG_COLUMN_ID = "id";
    public static final String SPEECH_LOG_COLUMN_DATE = "date";
    public static final String SPEECH_LOG_COLUMN_ACTIVITY = "activity";
    public static final String SPEECH_LOG_COLUMN_DURATION = "duration";
    public static final String SPEECH_LOG_COLUMN_PATH = "path";
    private HashMap hp;

    public SpeechActivityDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table myactivity " +
                        "(id integer primary key, date text,activity text, duration text, path text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS myactivity");
        onCreate(db);
    }

    public boolean insertSpeechActivity (String dateStr, String activityStr, long durationMs,
                                         String pathStr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", dateStr);
        contentValues.put("activity", activityStr);
        contentValues.put("duration", Long.toString(durationMs));
        contentValues.put("path", pathStr);
        db.insert("myactivity", null, contentValues);
        db.close();
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SPEECH_LOG_TABLE_NAME);
        return numRows;
    }

    public boolean updateSpeechActivity (String dateStr, String activityStr, long durationMs,
                                         String pathStr) {
        SpeechReportDataModel dmodel = getSpeechActivityId(dateStr);
        insertSpeechActivity(dateStr, activityStr, durationMs, pathStr);
        return true;
    }

    public boolean updateSpeechEntry (String dateStr, String activityStr, long durationMs,
                                         String pathStr) {
        SpeechReportDataModel dmodel = getSpeechActivityId(dateStr);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", dateStr);
        String aStr = dmodel.getReportActivity();
        if (!dmodel.getReportActivity().contains(activityStr)) {
            aStr += ", " + activityStr;
        }
        contentValues.put("activity", aStr);
        long dMs = Long.parseLong((String)dmodel.getDuration()) + durationMs;
        contentValues.put("duration", Long.toString(dMs));
        db.update("myactivity", contentValues, "id = ? ", new String[]{Integer.toString(dmodel.getId())});
        db.close();

        return true;
    }

    public Integer deleteSpeechActivity (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public SpeechReportDataModel getSpeechActivityId(String dateStr) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from myactivity", null );
        res.moveToFirst();

        SpeechReportDataModel data = null;
        while(res.isAfterLast() == false) {
            String dStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DATE));
            if (dStr.equals(dateStr)) {
                int logId = res.getInt(res.getColumnIndex(SPEECH_LOG_COLUMN_ID));
                data = new SpeechReportDataModel(logId, dStr,
                        res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_ACTIVITY)),
                        res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DURATION)),
                        res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_PATH))
                );
                return data;
            }
            res.moveToNext();
        }
        return data;
    }

    public ArrayList<SpeechReportDataModel> getAllSpeechActivities() {
        ArrayList<SpeechReportDataModel> array_list = new ArrayList<SpeechReportDataModel>();
        ArrayList<SpeechReportDataModel> out_list = new ArrayList<SpeechReportDataModel>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from myactivity", null );
        String cDateStr = getCurrDate();
        while(res.moveToNext()) {
            String logDate = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DATE));
            String reportDateList[] = logDate.split(" ");
            if (reportDateList[0].equals(cDateStr)) {
                logDate += " (Today)";
            }
            String actStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_ACTIVITY));
            String dStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DURATION));
            String spathStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_PATH));
            SpeechReportDataModel data = new SpeechReportDataModel(logDate, actStr, dStr, spathStr);
            array_list.add(data);
        }
        for (int i = 0; i  < array_list.size(); i++) {
            out_list.add( array_list.get(array_list.size()-1-i) );
        }
        return out_list;
    }

    public ArrayList<SpeechReportDataModel> getSpeechDataActivity(String activityStr) {
        ArrayList<SpeechReportDataModel> array_list = new ArrayList<SpeechReportDataModel>();
        ArrayList<SpeechReportDataModel> out_list = new ArrayList<SpeechReportDataModel>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from myactivity", null );
        String cDateStr = getCurrDate();
        while(res.moveToNext()) {
            String logDate = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DATE));
            String reportDateList[] = logDate.split(" ");
            if (reportDateList[0].equals(cDateStr)) {
                logDate += " (Today)";
            }
            String actStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_ACTIVITY));
            String dStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DURATION));
            String spathStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_PATH));
            if (actStr.equalsIgnoreCase(activityStr)) {
                SpeechReportDataModel data = new SpeechReportDataModel(logDate, actStr, dStr, spathStr);
                array_list.add(data);
            }
        }
        for (int i = 0; i  < array_list.size(); i++) {
            out_list.add( array_list.get(array_list.size()-1-i) );
        }
        return out_list;
    }

    public SpeechReportDataModel getLatestSpeechData(String activityStr) {
        ArrayList<SpeechReportDataModel> array_list = new ArrayList<SpeechReportDataModel>();
        SpeechReportDataModel latest_data = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from myactivity", null );
        String cDateStr = getCurrDate();
        while(res.moveToNext()) {
            String logDate = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DATE));
            String reportDateList[] = logDate.split(" ");
            if (reportDateList[0].equals(cDateStr)) {
                logDate += " (Today)";
            }
            String actStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_ACTIVITY));
            String dStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_DURATION));
            String spathStr = res.getString(res.getColumnIndex(SPEECH_LOG_COLUMN_PATH));
            if (actStr.equalsIgnoreCase(activityStr)) {
                SpeechReportDataModel data = new SpeechReportDataModel(logDate, actStr, dStr, spathStr);
                array_list.add(data);
            }
        }
        if (array_list.size() > 0) {
            latest_data = array_list.get(array_list.size()-1);
        }
        return latest_data;
    }

    private String getCurrDate() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String date = DateFormat.format("MM-dd-yyyy", cal).toString();
        return date;
    }
}