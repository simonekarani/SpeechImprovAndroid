//
//  SpeechPracticeActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.speechpractice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.simonekarani.speechimprov.MainActivity;
import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.model.MainScreenDataModel;
import com.simonekarani.speechimprov.report.SpeechActivityDBHelper;
import com.simonekarani.speechimprov.report.SpeechReportDataModel;
import com.simonekarani.speechimprov.storypractice.StoryPracticeActivity;
import com.simonekarani.speechimprov.wordpractice.WordPracticeActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SpeechPracticeActivity extends AppCompatActivity
        implements TextToSpeech.OnInitListener, AdapterView.OnItemSelectedListener {

    final int REQUEST_PERMISSION_CODE = 1000;
    private final static int RECOGNIZER_RESULT = 1;

    private final String SPEECH_PREFS_NAME = "simonekarani.speechimprov.speechpractice";
    private final static String SPEECHPRACTICE_INSTR =
            "Follow the steps below:\n\n" +
                    "Step 1:\nType your speech in the blue box\n\n" +
                    "Step 2:\nPress \"Listen\" button for hearing your typed speech\n\n" +
                    "Step 3:\nPress \"Record\" button to record your speech.\n\n" +
                    "Step 4:\nPress \"Play\" button to play your recorded speech\n\n";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainScreenDataModel> data;
    private View.OnClickListener myOnClickListener;

    private TextView instrTextView = null;
    private TextView speechTextView = null;
    private Button clearButtonView = null;
    private ImageButton recordedBtnView = null;
    private ImageButton recordBtnView = null;
    private ImageButton playBtnView = null;
    private TextView recordedText = null;
    private TextView recText = null;
    private TextView playText = null;
    private TextView wordCountText = null;

    private TextToSpeech textToSpeech;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private long mRecStartTime = 0;
    private long mRecEndTime = 0;
    private int wordCountSpeech = 0;

    private ArrayList<String> speechResultList = new ArrayList<>();
    private int userResultCount = 0;
    private int userSelectedOptIdx = -1;
    private String recWordPath = null;
    private SpeechActivityDBHelper mydb ;
    private long activityStartTimeMs = 0;
    private long activityEndTimeMs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_practice);
        setTitle("Speech Practice");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mydb = new SpeechActivityDBHelper(this);

        instrTextView = (TextView) findViewById(R.id.speech_instr);
        recordedBtnView = (ImageButton) findViewById(R.id.recordedBtn3);
        clearButtonView = (Button) findViewById(R.id.clearButton);
        recordBtnView = (ImageButton) findViewById(R.id.recBtn3);
        playBtnView   = (ImageButton) findViewById(R.id.playBtn3);
        recordedText = (TextView) findViewById(R.id.recordedText3);
        recText = (TextView) findViewById(R.id.recText3);
        playText = (TextView) findViewById(R.id.playText3);
        wordCountText = (TextView) findViewById(R.id.wordCountText);

        myOnClickListener = (View.OnClickListener) new MyOnClickListener(this);
        recordedBtnView.setOnClickListener(myOnClickListener);
        recordBtnView.setOnClickListener(myOnClickListener);
        playBtnView.setOnClickListener(myOnClickListener);
        clearButtonView.setOnClickListener(myOnClickListener);

        String speechText = readFromFile(getApplicationContext());
        speechTextView = (TextView) findViewById(R.id.speechText);
        speechTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        speechTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String currentText = editable.toString();
                wordCountSpeech = countWordString(currentText);
                String wordCountStr = "# of Words: " + wordCountSpeech;
                wordCountText.setText(wordCountStr);
            }
        });
        if (!speechText.equals("")) {
            speechTextView.setText(speechText);
        }
        textToSpeech = new TextToSpeech(getApplicationContext(), this);
        textToSpeech.setSpeechRate(0.3f);

        SpeechReportDataModel latestData = mydb.getLatestSpeechData("Speech");
        if (latestData != null) {
            recWordPath = latestData.getSpeechPath();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SPEECH_PREFS_NAME, 0);
        if (sharedPreferences.getBoolean("speechpractice_first_time", true)) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            new AlertDialog.Builder(this)
                    .setTitle("Story Practice Instructions")
                    .setMessage(SPEECHPRACTICE_INSTR)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();  /// here you save a boolean value ,
                        }
                    })
                    .setIcon(R.drawable.speechimprov_terms)
                    .setCancelable(false)
                    .show();

            sharedPreferences.edit().putBoolean("speechpractice_first_time", false).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStoryImprovView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        updateStoryImprovView();
    }

    @Override
    public void onBackPressed() {
        writeToFile(speechTextView.getText().toString(), getApplicationContext());
        updateSpeechReportLog();
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else {
            Log.i("SpeechPracticeActivity", "Clicked away from edit text");
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateStoryImprovView() {
        recordedBtnView.setImageResource(R.drawable.recorded);
        recordBtnView.setImageResource(R.drawable.rec);
        playBtnView.setImageResource(R.drawable.play);
        recordedText.setText("Listen");
        recText.setText("Record");
        playText.setText("Play");
        activityStartTimeMs = System.currentTimeMillis();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int lang = textToSpeech.setLanguage(Locale.ENGLISH);
            if (status==TextToSpeech.LANG_MISSING_DATA||status==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.i("TextToSpeech","Language Not Supported");
            }

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.i("TextToSpeech","On Start");
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.i("TextToSpeech","On Done");
                    userSelectedOptIdx = -1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recordedBtnView.setImageResource(R.drawable.recorded);
                            recordedText.setText("Listen");
                        }
                    });
                }

                @Override
                public void onError(String utteranceId) {
                    Log.i("TextToSpeech","On Error");
                }
            });
        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.recordedBtn3) {
                userSelectedOptIdx = 2;
                recordedBtnView.setImageResource(R.drawable.recorded_play);
                recordedText.setText("Reading");
                int speech = textToSpeech.speak(speechTextView.getText(), TextToSpeech.QUEUE_FLUSH, null, "");
            }
            else if (v.getId() == R.id.recBtn3) {
                if (userSelectedOptIdx == 2) {
                    createAlertDialog("Speech Practice", "\"Record\" button DISABLED during story listening");
                } else if (userSelectedOptIdx == 4) {
                    userSelectedOptIdx = 5;
                    stopStoryRecording();
                    recordBtnView.setImageResource(R.drawable.rec);
                    recText.setText("Record");
                } else {
                    userSelectedOptIdx = 4;
                    recordBtnView.setImageResource(R.drawable.rec_progress);
                    recText.setText("Recording");
                    if (checkPermissionFromDevice()) {
                        startWordRecording();
                    } else {
                        requestPermission();
                    }
                }
            }
            else if (v.getId() == R.id.playBtn3){
                if (userSelectedOptIdx == 2) {
                    createAlertDialog("Speech Practice", "\"Play\" button DISABLED during story listening");
                } else if (userSelectedOptIdx == 6) {
                    playBtnView.setImageResource(R.drawable.play);
                    playText.setText("Play");
                    stopWordPlay();
                    userSelectedOptIdx = -1;
                } else {
                    if (recWordPath == null) {
                        createAlertDialog("Speech Practice", "No Recorded voice exists ... \n"+
                                "Press the \"Record\" button and speak the word");
                    } else {
                        userSelectedOptIdx = 6;
                        playBtnView.setImageResource(R.drawable.pause);
                        playText.setText("In-Play");
                        startWordPlay();
                    }
                }
            }
            else if (v.getId() == R.id.clearButton) {
                speechTextView.setText("");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_speech, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem item = menu.findItem(R.id.enable_item);
        //item.setTitle( getWordPreferenceMenuItem() );
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        writeToFile(speechTextView.getText().toString(), getApplicationContext());
        updateSpeechReportLog();
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SpeechPracticeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.speech_memos_item:
                Intent memos_intent = new Intent(SpeechPracticeActivity.this, SpeechVoiceMemosActivity.class);
                memos_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(memos_intent);
                return true;
            case R.id.speech_access_item:
                Intent access_intent = new Intent(SpeechPracticeActivity.this, SpeechAccessibilityActivity.class);
                access_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(access_intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startWordRecording() {
        if (checkPermissionFromDevice()) {
            recWordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    UUID.randomUUID().toString() + "_audio_record.3gp";
            setupMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRecStartTime = System.currentTimeMillis();
        }
    }

    private void stopStoryRecording() {
        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        userSelectedOptIdx = -1;

        mRecEndTime = System.currentTimeMillis();
        long deltaTime = mRecEndTime - mRecStartTime;
        //MediaPlayer mp = MediaPlayer.create(SpeechPracticeActivity.this, Uri.parse(recWordPath));
        //int duration = mp.getDuration();
        int durationSecs = (int) (deltaTime/1000); // convert milliseconds to seconds

        int wpmValue = (wordCountSpeech * 60) / durationSecs;
        String wordCountStr = "Words Per Min (WPM): " + wpmValue;
        mydb.updateSpeechActivity(getCurrDate(), "Speech", deltaTime, recWordPath);
    }

    private void startWordPlay() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(recWordPath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playBtnView.setImageResource(R.drawable.play);
                playText.setText("Play");
                userSelectedOptIdx = -1;
            }
        });
        mediaPlayer.start();
    }

    private void stopWordPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        userSelectedOptIdx = -1;
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(recWordPath);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            speechResultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.i("StoryPracticeActivity", speechResultList.get(0).toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SpeechPracticeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SpeechPracticeActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void updateSpeechReportLog() {
        if (userSelectedOptIdx == 2) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        activityEndTimeMs = System.currentTimeMillis();
        long durationMs = activityEndTimeMs - activityStartTimeMs;
        //mydb.updateSpeechActivity(getCurrDate(), "Speech", durationMs, recWordPath);
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_results = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_results == PackageManager.PERMISSION_GRANTED;
    }

    public void hideKeyboard(View view) {
        writeToFile(speechTextView.getText().toString(), getApplicationContext());
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(SpeechPracticeActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void writeToFile(String data, Context context) {
        wordCountSpeech = countWordString(data);
        String wordCountStr = "# of Words: " + wordCountSpeech;
        wordCountText.setText(wordCountStr);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("speechFile.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("speechFile.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        wordCountSpeech = countWordString(ret);
        String wordCountStr = "# of Words: " + wordCountSpeech;
        wordCountText.setText(wordCountStr);

        return ret;
    }

    private int countWordString(String inputStr) {
        String words = inputStr.trim();
        if (words.isEmpty())
            return 0;
        return words.split("\\s+").length;
    }

    private String getCurrDate() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String date = DateFormat.format("MM-dd-yyyy HH:MM", cal).toString();
        return date;
    }

    private void createAlertDialog(String title, String alertMsg) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setPadding(0, 25, 0, 0);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setTextSize(18);
        titleView.setTextColor(Color.BLACK);

        AlertDialog alertDialog = new AlertDialog.Builder(SpeechPracticeActivity.this).create();
        alertDialog.setCustomTitle(titleView);
        alertDialog.setMessage(alertMsg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}