package com.simonekarani.speechimprov.storypractice;

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

import com.simonekarani.speechimprov.MainScreenDataAdapter;
import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.model.MainScreenData;
import com.simonekarani.speechimprov.model.MainScreenDataModel;
import com.simonekarani.speechimprov.speechpractice.SpeechPracticeActivity;
import com.simonekarani.speechimprov.wordpractice.WordPracticeActivity;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StoryPracticeListActivity extends AppCompatActivity implements StoryListScreenDataAdapter.OnMoralTopicListener {

    private static final String TAG = "StoryPracticeListActivity";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainScreenDataModel> data;
    static View.OnClickListener myOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());

        setContentView(R.layout.activity_story_practice_list);

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_1);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<MainScreenDataModel>();
        for (int i = 0; i < MainScreenData.nameArray.length; i++) {
            data.add(new MainScreenDataModel(
                    MainScreenData.nameArray[i],
                    MainScreenData.id_[i],
                    MainScreenData.drawableArray[i]
            ));
        }

        adapter = new StoryListScreenDataAdapter(data, this);
        recyclerView.setAdapter(adapter);
    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "iconImageViewOnClick at position ");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onTopicClick(int position) {
        Intent intent = new Intent(this, SpeechPracticeActivity.class);
        switch (position) {
            case MainScreenData.PRACTICE_WORD_ID:
                intent = new Intent(this, WordPracticeActivity.class);
                break;
            case MainScreenData.MORAL_STORIES_ID:
                intent = new Intent(this, StoryPracticeActivity.class);
                break;
            case MainScreenData.PRACTICE_SPEECH_ID:
                intent = new Intent(this, SpeechPracticeActivity.class);
                break;
            case MainScreenData.SPEECH_ANALYSIS_ID:
                intent = new Intent(this, SpeechPracticeActivity.class);
                break;
            default:
                //intent = new Intent(this, MoralMachineActivity.class);
                break;
        }
        startActivity(intent);
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