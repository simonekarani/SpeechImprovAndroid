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
import android.os.Bundle;
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

public class SpeechImprovReportActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<SpeechReportDataModel> data;

    public static String[] reportArray = {
            "06-10-2021", "06-11-2021", "06-12-2021"
    };
    public static String[] activityArray = {
            "Speech Practice", "Word Practice", "Story Practice"
    };
    public static int[] durationArray = {
            30, 45, 10
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());

        setContentView(R.layout.activity_speech_improv_report);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_report_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<SpeechReportDataModel>();
        for (int i = 0; i < SpeechImprovReportActivity.reportArray.length; i++) {
            data.add(new SpeechReportDataModel(
                    SpeechImprovReportActivity.reportArray[i],
                    SpeechImprovReportActivity.activityArray[i],
                    SpeechImprovReportActivity.durationArray[i]
            ));
        }

        adapter = new SpeechReportListAdapter(data);
        recyclerView.setAdapter(adapter);
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
}