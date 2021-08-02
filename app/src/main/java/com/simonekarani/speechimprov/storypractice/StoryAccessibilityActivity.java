//
//  StoryAccessibilityActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.storypractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.model.InputFilterMinMax;

import java.util.Locale;

public class StoryAccessibilityActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    public final static String KEY_STORY_LOCALE   = "StoryTTSLocale";
    public final static String KEY_STORY_RATE     = "StoryTTSRate";
    public final static String KEY_STORY_PITCH    = "StoryTTSPitch";
    public final static String KEY_STORY_RECCOUNT = "StoryRecCount";

    public static final String[] StoryLocaleNames = {
            "English (U.S.)", "English (Great Britain)",
            "English (Australia)", "English (India)"
    };
    public static final Locale[] StoryLocale = {
            Locale.US, Locale.UK, Locale.ENGLISH, Locale.ENGLISH
    };

    private SeekBar storyRateSeekBar;
    private TextView mySpeechRate;
    private EditText myRecText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_accessibility);
        setTitle("Story Accessibility");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String menuItem = sharedPreferences.getString(KEY_STORY_LOCALE, "English (U.S.)");

        Spinner mySpinner = (Spinner)findViewById(R.id.story_locale_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.row, R.id.row_text, StoryLocaleNames);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(this);
        mySpinner.setSelection( adapter.getPosition(menuItem) );

        // Speech Rate
        float speechRate = sharedPreferences.getFloat(KEY_STORY_RATE, 0.5f);
        storyRateSeekBar = (SeekBar) findViewById(R.id.story_speed_slider);
        int progressPerc = (int)((speechRate*100)/(4.0-0.25));
        storyRateSeekBar.setMin(0);
        storyRateSeekBar.setMax(100);
        storyRateSeekBar.setProgress(progressPerc);

        TextView mySpeechRate = (TextView)findViewById(R.id.story_speed_text);
        mySpeechRate.setText(""+speechRate);

        storyRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float srate = (float) (((float)progress/100)*(4-0.25) + 0.25);
                float srate_value = ((float)((int)srate*100))/100;
                mySpeechRate.setText(""+srate_value);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putFloat(KEY_STORY_RATE, srate_value);
                edit.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Speech Pitch
        float speechPitch = sharedPreferences.getFloat(KEY_STORY_PITCH, 1.0f);
        storyRateSeekBar = (SeekBar) findViewById(R.id.story_pitch_slider);
        float pitchValue = speechPitch + 20;
        int pitchPerc = (int)((pitchValue*100)/40);
        storyRateSeekBar.setMin(0);
        storyRateSeekBar.setMax(100);
        storyRateSeekBar.setProgress(pitchPerc);

        TextView myPitchRate = (TextView)findViewById(R.id.story_pitch_text);
        myPitchRate.setText(""+speechPitch);

        storyRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float spitch = (float) (((float)progress/100)*40 - 20);
                float spitch_value = ((float)((int)spitch*100))/100;
                myPitchRate.setText(""+spitch_value);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putFloat(KEY_STORY_PITCH, spitch_value);
                edit.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int speechRecCount = sharedPreferences.getInt(KEY_STORY_RECCOUNT, 10);
        myRecText = (EditText)findViewById(R.id.editStoryTextNumber);
        myRecText.setFilters(new InputFilter[]{ new InputFilterMinMax(1, 20)});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveVoiceRecordingCount();
                Intent intent = new Intent(StoryAccessibilityActivity.this, StoryPracticeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveVoiceRecordingCount();
        super.onBackPressed();
    }

    public void saveVoiceRecordingCount() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();
        int recCount = Integer.parseInt(myRecText.getText().toString());
        edit.putInt(KEY_STORY_RECCOUNT, recCount);
        edit.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();

        String selectedItem = StoryLocaleNames[position];
        edit.putString(KEY_STORY_LOCALE, selectedItem);
        edit.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}