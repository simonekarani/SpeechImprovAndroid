package com.simonekarani.speechimprov.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.simonekarani.speechimprov.MainActivity;
import com.simonekarani.speechimprov.MainScreenDataAdapter;
import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.model.MainScreenData;
import com.simonekarani.speechimprov.model.MainScreenDataModel;
import com.simonekarani.speechimprov.speechpractice.SpeechPracticeActivity;
import com.simonekarani.speechimprov.storypractice.StoryPracticeActivity;
import com.simonekarani.speechimprov.wordpractice.WordPracticeActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SpeechImprovReportActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<SpeechReportDataModel> data;
    private SpeechActivityDBHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());

        setContentView(R.layout.activity_speech_improv_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mydb = new SpeechActivityDBHelper(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_report_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SpeechReportListAdapter(mydb.getAllSpeechActivities());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SpeechImprovReportActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adjustFontScale(Configuration configuration)
    {
        configuration.fontScale = (float) 0.9;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    private String getCurrDate() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String date = DateFormat.format("MM-dd-yyyy", cal).toString();
        return date;
    }
}