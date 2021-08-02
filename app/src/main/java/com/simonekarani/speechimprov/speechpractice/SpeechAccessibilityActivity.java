//
//  SpeechAccessibilityActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.speechpractice;

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

import org.w3c.dom.Text;

import java.util.Locale;

public class SpeechAccessibilityActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    public final static String KEY_SPEECH_LOCALE   = "SpeechTTSLocale";
    public final static String KEY_SPEECH_RATE     = "SpeechTTSRate";
    public final static String KEY_SPEECH_PITCH    = "SpeechTTSPitch";
    public final static String KEY_SPEECH_RECCOUNT = "SpeechRecCount";

    public static final String[] SpeechLocaleNames = {
            "English (U.S.)", "English (Great Britain)",
            "English (Australia)", "English (India)"
    };
    public static final Locale[] SpeechLocale = {
            Locale.US, Locale.UK, Locale.ENGLISH, Locale.ENGLISH
    };

    private SeekBar speechRateSeekBar;
    private TextView mySpeechRate;
    private EditText myRecText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_accessibility);
        setTitle("Speech Accessibility");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String menuItem = sharedPreferences.getString(KEY_SPEECH_LOCALE, "English (U.S.)");

        Spinner mySpinner = (Spinner)findViewById(R.id.speech_locale_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.row, R.id.row_text, SpeechLocaleNames);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(this);
        mySpinner.setSelection( adapter.getPosition(menuItem) );

        // Speech Rate
        float speechRate = sharedPreferences.getFloat(KEY_SPEECH_RATE, 0.5f);
        speechRateSeekBar = (SeekBar) findViewById(R.id.speech_speed_slider);
        int progressPerc = (int)((speechRate*100)/(4.0-0.25));
        speechRateSeekBar.setMin(0);
        speechRateSeekBar.setMax(100);
        speechRateSeekBar.setProgress(progressPerc);

        TextView mySpeechRate = (TextView)findViewById(R.id.speech_speed_text);
        mySpeechRate.setText(""+speechRate);

        speechRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float srate = (float) (((float)progress/100)*(4-0.25) + 0.25);
                float srate_value = ((float)((int)srate*100))/100;
                mySpeechRate.setText(""+srate_value);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putFloat(KEY_SPEECH_RATE, srate_value);
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
        float speechPitch = sharedPreferences.getFloat(KEY_SPEECH_PITCH, 1.0f);
        speechRateSeekBar = (SeekBar) findViewById(R.id.speech_pitch_slider);
        float pitchValue = speechPitch + 20;
        int pitchPerc = (int)((pitchValue*100)/40);
        speechRateSeekBar.setMin(0);
        speechRateSeekBar.setMax(100);
        speechRateSeekBar.setProgress(pitchPerc);

        TextView myPitchRate = (TextView)findViewById(R.id.speech_pitch_text);
        myPitchRate.setText(""+speechPitch);

        speechRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float spitch = (float) (((float)progress/100)*40 - 20);
                float spitch_value = ((float)((int)spitch*100))/100;
                myPitchRate.setText(""+spitch_value);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putFloat(KEY_SPEECH_PITCH, spitch_value);
                edit.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int speechRecCount = sharedPreferences.getInt(KEY_SPEECH_RECCOUNT, 10);
        myRecText = (EditText)findViewById(R.id.editTextNumber);
        myRecText.setFilters(new InputFilter[]{ new InputFilterMinMax(1, 20)});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveVoiceRecordingCount();
                Intent intent = new Intent(SpeechAccessibilityActivity.this, SpeechPracticeActivity.class);
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
        edit.putInt(KEY_SPEECH_RECCOUNT, recCount);
        edit.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();

        String selectedItem = SpeechLocaleNames[position];
        edit.putString(KEY_SPEECH_LOCALE, selectedItem);
        edit.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
