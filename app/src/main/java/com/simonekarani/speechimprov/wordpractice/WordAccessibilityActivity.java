//
//  WordAccessibilityActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.wordpractice;

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

public class WordAccessibilityActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    public final static String KEY_WORD_LOCALE   = "WordTTSLocale";
    public final static String KEY_WORD_RATE     = "WordTTSRate";
    public final static String KEY_WORD_PITCH    = "WordTTSPitch";
    public final static String KEY_WORD_RECCOUNT = "WordRecCount";

    public static final String[] WordLocaleNames = {
            "English (U.S.)", "English (Great Britain)",
            "English (Australia)", "English (India)"
    };
    public static final Locale[] WordLocale = {
            Locale.US, Locale.UK, Locale.ENGLISH, Locale.ENGLISH
    };

    private SeekBar wordRateSeekBar;
    private TextView mySpeechRate;
    private EditText myRecText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_accessibility);
        setTitle("Word Accessibility");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String menuItem = sharedPreferences.getString(KEY_WORD_LOCALE, "English (U.S.)");

        Spinner mySpinner = (Spinner)findViewById(R.id.word_locale_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.row, R.id.row_text, WordLocaleNames);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(this);
        mySpinner.setSelection( adapter.getPosition(menuItem) );

        // Speech Rate
        float speechRate = sharedPreferences.getFloat(KEY_WORD_RATE, 0.5f);
        wordRateSeekBar = (SeekBar) findViewById(R.id.word_speed_slider);
        int progressPerc = (int)((speechRate*100)/(4.0-0.25));
        wordRateSeekBar.setMin(0);
        wordRateSeekBar.setMax(100);
        wordRateSeekBar.setProgress(progressPerc);

        TextView mySpeechRate = (TextView)findViewById(R.id.word_speed_text);
        mySpeechRate.setText(""+speechRate);

        wordRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float srate = (float) (((float)progress/100)*(4-0.25) + 0.25);
                float srate_value = ((float)((int)srate*100))/100;
                mySpeechRate.setText(""+srate_value);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putFloat(KEY_WORD_RATE, srate_value);
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
        float speechPitch = sharedPreferences.getFloat(KEY_WORD_PITCH, 1.0f);
        wordRateSeekBar = (SeekBar) findViewById(R.id.word_pitch_slider);
        float pitchValue = speechPitch + 20;
        int pitchPerc = (int)((pitchValue*100)/40);
        wordRateSeekBar.setMin(0);
        wordRateSeekBar.setMax(100);
        wordRateSeekBar.setProgress(pitchPerc);

        TextView myPitchRate = (TextView)findViewById(R.id.word_pitch_text);
        myPitchRate.setText(""+speechPitch);

        wordRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float spitch = (float) (((float)progress/100)*40 - 20);
                float spitch_value = ((float)((int)spitch*100))/100;
                myPitchRate.setText(""+spitch_value);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putFloat(KEY_WORD_PITCH, spitch_value);
                edit.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int speechRecCount = sharedPreferences.getInt(KEY_WORD_RECCOUNT, 10);
        myRecText = (EditText)findViewById(R.id.editWordTextNumber);
        myRecText.setFilters(new InputFilter[]{ new InputFilterMinMax(1, 20)});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveVoiceRecordingCount();
                Intent intent = new Intent(WordAccessibilityActivity.this, WordPracticeActivity.class);
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
        edit.putInt(KEY_WORD_RECCOUNT, recCount);
        edit.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();

        String selectedItem = WordLocaleNames[position];
        edit.putString(KEY_WORD_LOCALE, selectedItem);
        edit.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}