//
//  WordPracticeActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/20.
//  Copyright © 2020 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.wordpractice;

import android.app.AlertDialog;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simonekarani.speechimprov.MainActivity;
import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.model.MainScreenDataModel;
import com.simonekarani.speechimprov.report.SpeechActivityDBHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class WordPracticeActivity extends AppCompatActivity
        implements TextToSpeech.OnInitListener, AdapterView.OnItemSelectedListener {

    private final static String KEY_WORD_SCROLLVIEW = "WordPracticeScroller";
    private final static String KEY_WORD_LISTVIEW = "WordPracticeList";
    private final static String KEY_MENU_WORDPLAY = "MenuWordPractice";

    private static final int MAX_WORD_PRACTICE_COUNT = 7;
    final int REQUEST_PERMISSION_CODE = 1000;
    private final static int RECOGNIZER_RESULT = 1;
    private final static int SPEECH_RECOGNIZER_RESULT = 10;

    private final String WP_PREFS_NAME = "simonekarani.speechimprov.wordpractice";
    private final static String WORDPRACTICE_INSTR =
            "Follow the steps below:\n" +
            "Step 1:\nPress \"Listen\" button\n\n" +
            "Step 2:\nPress \"Record\" button to record your word\n" +
            "Speak the word in blue out loud\n\n"+
            "Step 3:\nResult appears in \"What we Heard\" or \"Play\" button\n\n" +
            "Note: \"What we Heard\" or \"Play\" can be enabled via menu option\n";

    private final static String WORD_INSTR = "Repeat the words below the image, and check for correct pronunciation?\n";
            /*"- Press Word Play for correct pronunciation\n" +
            "- Press Mic to record the words\n" +
            "- Press Play to play the recorded word\n" +
            "- Press Next to move to next word\n";*/

    private java.util.HashMap<Integer,WordPracticeDataModel[]> Index2GameData = new HashMap<Integer,WordPracticeDataModel[]>();

    private final String[] TherapyGames = {
            "Words for 'V'", "Words for 'TH'", "Words for 'SH'", "Words for 'M'", "Words for 'N'",
            "Words for 'F'", "Words for 'CH'", "Words for 'T S'", "Words for 'NG K G'"
    };

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainScreenDataModel> data;
    private View.OnClickListener myOnClickListener;

    private TextView readWordView = null;
    private Button prevImageView = null;
    private Button nextImageView = null;
    private ImageView wordImageView = null;
    private ImageButton recordedBtnView = null;
    private ImageButton recordBtnView = null;
    private ImageButton playBtnView = null;
    private TextView recordedText = null;
    private TextView recText = null;
    private TextView playText = null;
    private TextView repeatText = null;

    private TextToSpeech textToSpeech;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private WordPracticeDataModel wordPracticeDataList[];
    private Set<Integer> wordPracticeDataSet = new HashSet<>();
    private ArrayList<String> speechResultList = new ArrayList<>();
    private int userResultCount = 0;
    private int currWordSetDataIdx = 0;
    private int currWordPracticeDataIdx = 0;
    private int userSelectedOptIdx = -1;
    private WordPracticeDataModel currPracticeData = null;
    private String recWordPath = null;
    private SpeechActivityDBHelper mydb ;
    private long activityStartTimeMs = 0;
    private long activityEndTimeMs = 0;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_practice);

        setTitle("Practice Word Pronunciation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mydb = new SpeechActivityDBHelper(this);

        currWordSetDataIdx = 0;
        currWordPracticeDataIdx = 0;
        userResultCount = 0;

        for (int i = 0; i < WordPracticeData.WordPracticeList.length; i++) {
            Index2GameData.put(i, WordPracticeData.WordPracticeList[i]);
        }
        wordPracticeDataList = Index2GameData.get(0);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String menuItem = sharedPreferences.getString(KEY_WORD_SCROLLVIEW, "Words for 'V'");

        Spinner mySpinner = (Spinner)findViewById(R.id.word_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.row, R.id.row_text, TherapyGames);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(this);
        mySpinner.setSelection( adapter.getPosition(menuItem) );

        readWordView = (TextView) findViewById(R.id.readWordText);
        prevImageView = (Button) findViewById(R.id.prevImage);
        nextImageView = (Button) findViewById(R.id.nextImage);
        wordImageView = (ImageView) findViewById(R.id.wordImage);
        recordedBtnView = (ImageButton) findViewById(R.id.recordedBtn);
        recordBtnView = (ImageButton) findViewById(R.id.recBtn);
        playBtnView   = (ImageButton) findViewById(R.id.playBtn);
        recordedText = (TextView) findViewById(R.id.recordedText);
        recText = (TextView) findViewById(R.id.recText);
        playText = (TextView) findViewById(R.id.playText);
        repeatText = (TextView) findViewById(R.id.repeatText);

        myOnClickListener = (View.OnClickListener) new MyOnClickListener(this);
        prevImageView.setOnClickListener(myOnClickListener);
        nextImageView.setOnClickListener(myOnClickListener);
        recordedBtnView.setOnClickListener(myOnClickListener);
        recordBtnView.setOnClickListener(myOnClickListener);
        playBtnView.setOnClickListener(myOnClickListener);

        textToSpeech = new TextToSpeech(getApplicationContext(), this);
        textToSpeech.setSpeechRate(0.3f);

        sharedPreferences = getSharedPreferences(WP_PREFS_NAME, 0);
        if (sharedPreferences.getBoolean("wordpractice_first_time", true)) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            new AlertDialog.Builder(this)
                    .setTitle("Word Practice Instructions")
                    .setMessage(WORDPRACTICE_INSTR)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();  /// here you save a boolean value ,
                        }
                    })
                    .setIcon(R.drawable.speechimprov_terms)
                    .setCancelable(false)
                    .show();

            sharedPreferences.edit().putBoolean("wordpractice_first_time", false).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWordImprovView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateWordImprovView();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Thanks for using application!!",Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }

    private void updateWordImprovView() {
        recordedBtnView.setImageResource(R.drawable.recorded);
        recordBtnView.setImageResource(R.drawable.rec);
        playBtnView.setImageResource(R.drawable.play);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currWordPracticeDataIdx = sharedPreferences.getInt(KEY_WORD_LISTVIEW, 0);
        currPracticeData = wordPracticeDataList[currWordPracticeDataIdx];
        wordImageView.setImageResource(currPracticeData.id_);
        readWordView.setText(currPracticeData.word);
        activityStartTimeMs = System.currentTimeMillis();

        if (currWordPracticeDataIdx == 0) {
            prevImageView.setAlpha(0.5f);
        }
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
            if (v.getId() == R.id.prevImage) {
                if (currWordPracticeDataIdx > 0) {
                    currWordPracticeDataIdx--;
                    currWordPracticeDataIdx = currWordPracticeDataIdx % wordPracticeDataList.length;
                    currPracticeData = wordPracticeDataList[currWordPracticeDataIdx];
                    wordImageView.setImageResource(currPracticeData.id_);
                    readWordView.setText(currPracticeData.word);
                    nextImageView.setAlpha(1.0f);
                }
                if (currWordPracticeDataIdx == 0) {
                    prevImageView.setAlpha(0.5f);
                }
                updatePreferenceWordListSetting(currWordPracticeDataIdx);
            }
            else if (v.getId() == R.id.nextImage) {
                nextPracticeWord();
            }
            else if (v.getId() == R.id.recordedBtn) {
                if (userSelectedOptIdx == -1) {
                    userSelectedOptIdx = 2;
                    recordedBtnView.setImageResource(R.drawable.recorded_play);
                    recordedText.setText("Reading");

                    int speech = textToSpeech.speak(currPracticeData.word, TextToSpeech.QUEUE_FLUSH, null, "");
                    textToSpeech.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);
                }
            }
            else if (v.getId() == R.id.recBtn) {
                if (userSelectedOptIdx == 4) {
                    userSelectedOptIdx = 5;
                    if (isEnableVoicePlayback()) {
                        stopWordRecording();
                    } else {
                        speechRecognizer.stopListening();
                    }
                    recordBtnView.setImageResource(R.drawable.rec);
                    recText.setText("Record");
                } else {
                    userSelectedOptIdx = 4;
                    recordBtnView.setImageResource(R.drawable.rec_progress);
                    recText.setText("Recording");
                    if (checkPermissionFromDevice()) {
                        if (isEnableVoicePlayback()) {
                            startWordRecording();
                        } else {
                            startSpeechRecognition();
                        }
                    } else {
                        requestPermission();
                    }
                }
            }
            else if (v.getId() == R.id.playBtn){
                if (!isEnableVoicePlayback()) {
                    createAlertDialog("Word Practice", "Voice Playback disabled ... \n"+
                            "Select the Menu \"Enable 'Play' Voice\"");
                }
                else if (userSelectedOptIdx == 6) {
                    playBtnView.setImageResource(R.drawable.play);
                    playText.setText("Play");
                    stopWordPlay();
                    userSelectedOptIdx = -1;
                } else {
                    if (recWordPath == null) {
                        createAlertDialog("Word Practice", "No Recorded voice exists ... \n"+
                                "Press the \"Record\" button and speak the word");
                    } else {
                        userSelectedOptIdx = 6;
                        playBtnView.setImageResource(R.drawable.pause);
                        playText.setText("In-Play");
                        startWordPlay();
                    }
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.enable_item);
        item.setTitle( getWordPreferenceMenuItem() );
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(WordPracticeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.enable_item:
                String mItem = (String) item.getTitle();
                if (mItem.equals("Enable 'Play' Voice")) {
                    updateWordPreferenceMenuItem("Enable 'What we Heard'");
                } else {
                    updateWordPreferenceMenuItem("Enable 'Play' Voice");
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startSpeechRecognition() {
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (speechIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(speechIntent, SPEECH_RECOGNIZER_RESULT);
        } else {
            Toast.makeText(this, "Device does not support speech input", Toast.LENGTH_LONG).show();
        }
    }

    private void startWordRecording() {
        if (checkPermissionFromDevice()) {
            recWordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    UUID.randomUUID().toString() + "_audio_record.3gp";
            Log.i("** Word Rec Speech", recWordPath);
            setupMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopWordRecording() {
        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        userSelectedOptIdx = -1;
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
        wordPracticeDataList =  Index2GameData.get(pos);
        currWordPracticeDataIdx = 0;

        updatePreferenceSetting(pos);
        updateWordImprovView();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNIZER_RESULT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resultWord = results.get(0);
                    boolean matched = false;
                    for (int i = 0; i < results.size(); i++) {
                        if (results.get(i).equalsIgnoreCase(currPracticeData.word)) {
                            resultWord = results.get(i);
                            matched = true;
                            Log.i("WordActivity", "" + i + " = " + results.get(i));
                            repeatText.setText(resultWord);
                            createCongratulationsAlert("Matched " + currPracticeData.word + " pronunciation");
                            nextPracticeWord();
                        }
                    }
                    if (!matched) {
                        repeatText.setText(resultWord);
                    }
                    recordBtnView.setImageResource(R.drawable.rec);
                    recText.setText("Record");
                    userSelectedOptIdx = -1;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(WordPracticeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WordPracticeActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void nextPracticeWord() {
        if (currWordPracticeDataIdx < wordPracticeDataList.length-1) {
            currWordPracticeDataIdx++;
            currWordPracticeDataIdx = currWordPracticeDataIdx % wordPracticeDataList.length;
            currPracticeData = wordPracticeDataList[currWordPracticeDataIdx];
            wordImageView.setImageResource(currPracticeData.id_);
            readWordView.setText(currPracticeData.word);
            prevImageView.setAlpha(1.0f);
        }
        if (currWordPracticeDataIdx == wordPracticeDataList.length-1) {
            nextImageView.setAlpha(0.5f);
        }
        updatePreferenceWordListSetting(currWordPracticeDataIdx);
        repeatText.setText("");
    }

    public boolean isEnableVoicePlayback() {
        return !getWordPreferenceMenuItem().equals("Enable 'Play' Voice");
    }

    private void updateWordReportLog() {
        activityEndTimeMs = System.currentTimeMillis();
        long durationMs = activityEndTimeMs - activityStartTimeMs;
        mydb.updateSpeechActivity(getCurrDate(), "Word", durationMs, recWordPath);
    }

    private void updatePreferenceSetting(int selectedIdx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();

        String selectedItem = TherapyGames[selectedIdx];
        edit.putString(KEY_WORD_SCROLLVIEW, selectedItem);
        edit.commit();
    }

    private void updatePreferenceWordListSetting(int selectedIdx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putInt(KEY_WORD_LISTVIEW, selectedIdx);
        edit.commit();
    }

    public String getWordPreferenceMenuItem() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String menuItem = sharedPreferences.getString(KEY_MENU_WORDPLAY, "Enable 'Play' Voice");

        return menuItem;
    }

    public void updateWordPreferenceMenuItem(String mItem) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(KEY_MENU_WORDPLAY, mItem);
        edit.commit();
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_results = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_results == PackageManager.PERMISSION_GRANTED;
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

        AlertDialog alertDialog = new AlertDialog.Builder(WordPracticeActivity.this).create();
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

    private void createCongratulationsAlert(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Congratulations!!").
                setMessage(msg);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //exitLauncher();
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 2000);
    }
}