package com.simonekarani.speechimprov;

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

import com.simonekarani.speechimprov.model.MainScreenData;
import com.simonekarani.speechimprov.model.MainScreenDataModel;
import com.simonekarani.speechimprov.speechpractice.SpeechPracticeActivity;
import com.simonekarani.speechimprov.storypractice.StoryPracticeActivity;
import com.simonekarani.speechimprov.wordpractice.WordPracticeActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainScreenDataAdapter.OnMoralTopicListener {

    private static final String TAG = "MainActivity";

    private final String APP_PREFS_NAME = "simonekarani.SpeechImprov";
    private final static String SPEECHIMPROV_TERMS = "The SpeechImprov application is built for speech improvement.\n\n" +
            "The speech analysis is done for understanding and building enhancements to the existing application." +
            "The plan of the application is to look for early signs of speech related disorders.\n." +
            "- Practice Word play by repeating the words to improve pronunication\n" +
            "- Play the stories, and repeat to see how words create language and communication\n" +
            "- Practice Speech by use of built-in recorder and playback of audio\n\n" +
            "Note: For Speech related advise, please reach out to Speech Specialist for consultation";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainScreenDataModel> data;
    static View.OnClickListener myOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());

        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFS_NAME, 0);
        if (sharedPreferences.getBoolean("speechimprov_first_time", true)) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            new AlertDialog.Builder(this)
                    .setTitle("SpeechImprov Terms of Use")
                    .setMessage(SPEECHIMPROV_TERMS)
                    .setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();  /// here you save a boolean value ,
                            editor.putBoolean("agreed",true);
                            editor.apply();
                        }
                    })
                    .setNegativeButton("DISAGREE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean("agreed",false);
                            editor.apply();
                            finish();
                        }
                    })
                    .setIcon(R.drawable.speechimprov_terms)
                    .setCancelable(false)
                    .show();

            sharedPreferences.edit().putBoolean("speechimprov_first_time", false).commit();
        }

        setContentView(R.layout.activity_main);

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
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

        adapter = new MainScreenDataAdapter(data, this);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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