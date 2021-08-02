//
//  SpeechPracticeActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.storypractice;

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
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simonekarani.speechimprov.MainActivity;
import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.model.MainScreenDataModel;
import com.simonekarani.speechimprov.report.SpeechActivityDBHelper;
import com.simonekarani.speechimprov.report.SpeechReportDataModel;
import com.simonekarani.speechimprov.speechpractice.SpeechPracticeActivity;
import com.simonekarani.speechimprov.speechpractice.SpeechVoiceMemosActivity;
import com.simonekarani.speechimprov.wordpractice.WordPracticeActivity;
import com.simonekarani.speechimprov.wordpractice.WordPracticeData;
import com.simonekarani.speechimprov.wordpractice.WordPracticeDataModel;

import java.io.File;
import java.io.IOException;
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

public class StoryPracticeActivity extends AppCompatActivity
        implements TextToSpeech.OnInitListener, AdapterView.OnItemSelectedListener {

    private static final int MAX_WORD_PRACTICE_COUNT = 7;
    final int REQUEST_PERMISSION_CODE = 1000;
    private final static int RECOGNIZER_RESULT = 1;
    private final static String KEY_STORY_SCROLLVIEW = "StoryPracticeScroller";

    private final String SP_PREFS_NAME = "simonekarani.speechimprov.storypractice";
    private final static String STORYPRACTICE_INSTR =
            "Follow the steps below:\n\n" +
            "Step 1:\nPress \"Listen\" button for hearing the words of the story\n\n" +
            "Step 2:\nPress \"Record\" button to record your voice.\n" +
                    "Speak the words in blue out loud\n" +
                    "During, recording press \"Next\" button to view next page of story\n\n" +
            "Step 3:\nPress \"Play\" button to play your voice\n\n";

    private final static String STORY_INSTR = "Practice Speech with story book reading";
            /*"- Press Word Play for correct pronunciation\n" +
            "- Press Mic to record the words\n" +
            "- Press Play to play the recorded word\n" +
            "- Press Next to move to next word\n";*/

    private java.util.HashMap<Integer, StoryPracticeDataModel[]> Index2GameData = new HashMap<Integer,StoryPracticeDataModel[]>();

    private final String[] TherapyStories = {
            "A Very Funny Fairy", "Talented Waiter", "Go, Kiki, Go!", "Kiki and Gary", "Ted and Todd - 1",
            "Ted and Todd - 2", "Pea will Play Ball", "Bumble Bee", "Smelly Zoo"
    };

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainScreenDataModel> data;
    private View.OnClickListener myOnClickListener;

    private Button prevImageView = null;
    private Button nextImageView = null;
    private ImageView storyImageView = null;
    private ImageButton recordedBtnView = null;
    private ImageButton recordBtnView = null;
    private ImageButton playBtnView = null;
    private TextView recordedText = null;
    private TextView recText = null;
    private TextView playText = null;
    private TextView readText = null;

    private TextToSpeech textToSpeech;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private long mRecStartTime = 0;
    private long mRecEndTime = 0;
    private int wordCountStory = 0;

    private StoryPracticeDataModel storyPracticeDataList[];
    private Set<Integer> wordPracticeDataSet = new HashSet<>();
    private ArrayList<String> speechResultList = new ArrayList<>();
    private ArrayList<Integer> wordPracticeDataArray = new ArrayList<>();
    private int userResultCount = 0;
    private int currStoryPageIdx = 0;
    private int currStoryPracticeDataIdx = 0;
    private int userSelectedOptIdx = -1;
    private StoryPracticeDataModel currPracticeData = null;
    private String recWordPath = null;
    private SpeechActivityDBHelper mydb ;
    private long activityStartTimeMs = 0;
    private long activityEndTimeMs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_practice);
        setTitle("Practice Speech with Stories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mydb = new SpeechActivityDBHelper(this);

        currStoryPracticeDataIdx = 0;
        userResultCount = 0;
        wordPracticeDataArray.add(currStoryPracticeDataIdx);

        wordCountStory = 0;
        for (int i = 0; i < StoryPracticeData.StoryPracticeList.length; i++) {
            Index2GameData.put(i, StoryPracticeData.StoryPracticeList[i]);
        }
        storyPracticeDataList = Index2GameData.get(0);
        currStoryPageIdx = 0;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String menuItem = sharedPreferences.getString(KEY_STORY_SCROLLVIEW, "A Very Funny Fairy");

        Spinner mySpinner = (Spinner)findViewById(R.id.story_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.story_row, R.id.story_row_text, TherapyStories);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(this);
        mySpinner.setSelection( adapter.getPosition(menuItem) );

        prevImageView = (Button) findViewById(R.id.prevImage2);
        nextImageView = (Button) findViewById(R.id.nextImage2);
        storyImageView = (ImageView) findViewById(R.id.storyImage);
        recordedBtnView = (ImageButton) findViewById(R.id.recordedBtn2);
        recordBtnView = (ImageButton) findViewById(R.id.recBtn2);
        playBtnView   = (ImageButton) findViewById(R.id.playBtn2);
        recordedText = (TextView) findViewById(R.id.recordedText2);
        recText = (TextView) findViewById(R.id.recText2);
        playText = (TextView) findViewById(R.id.playText2);
        readText = (TextView) findViewById(R.id.readtext);

        myOnClickListener = (View.OnClickListener) new MyOnClickListener(this);
        prevImageView.setOnClickListener(myOnClickListener);
        nextImageView.setOnClickListener(myOnClickListener);
        recordedBtnView.setOnClickListener(myOnClickListener);
        recordBtnView.setOnClickListener(myOnClickListener);
        playBtnView.setOnClickListener(myOnClickListener);

        prevImageView.setAlpha(0.5f);

        wordCountStory = wordCountStoryBook(storyPracticeDataList);

        textToSpeech = new TextToSpeech(getApplicationContext(), this);
        textToSpeech.setSpeechRate(0.3f);

        SpeechReportDataModel latestData = mydb.getLatestSpeechData("Story");
        if (latestData != null) {
            recWordPath = latestData.getSpeechPath();
        }

        sharedPreferences = getSharedPreferences(SP_PREFS_NAME, 0);
        if (sharedPreferences.getBoolean("storypractice_first_time", true)) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            new AlertDialog.Builder(this)
                    .setTitle("Story Practice Instructions")
                    .setMessage(STORYPRACTICE_INSTR)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();  /// here you save a boolean value ,
                        }
                    })
                    .setIcon(R.drawable.speechimprov_terms)
                    .setCancelable(false)
                    .show();

            sharedPreferences.edit().putBoolean("storypractice_first_time", false).commit();
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
        updateStoryReportLog();
        super.onBackPressed();
    }

    private void updateStoryImprovView() {
        recordedBtnView.setImageResource(R.drawable.recorded);
        recordBtnView.setImageResource(R.drawable.rec);
        playBtnView.setImageResource(R.drawable.play);
        recordedText.setText("Listen");
        recText.setText("Record");
        playText.setText("Play");
        currPracticeData = storyPracticeDataList[currStoryPageIdx];
        storyImageView.setImageResource(currPracticeData.id_);
        readText.setText(currPracticeData.word);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (userSelectedOptIdx == 100) {
                                // do nothing back key pressed
                            } else if (currStoryPageIdx < storyPracticeDataList.length-1) {
                                userSelectedOptIdx = 2;
                                currStoryPageIdx++;
                                prevImageView.setAlpha(1.0f);
                                if (currStoryPageIdx == storyPracticeDataList.length-1) {
                                    nextImageView.setAlpha(0.5f);
                                }
                                else {
                                    nextImageView.setAlpha(1.0f);
                                }

                                currPracticeData = storyPracticeDataList[currStoryPageIdx];
                                storyImageView.setImageResource(currPracticeData.id_);
                                readText.setText(currPracticeData.word);
                                int speech = textToSpeech.speak(currPracticeData.word, TextToSpeech.QUEUE_ADD, null, "");
                                textToSpeech.playSilentUtterance(1700, TextToSpeech.QUEUE_ADD, null);
                            } else {
                                recordedBtnView.setImageResource(R.drawable.recorded);
                                recordedText.setText("Listen");
                                currStoryPageIdx = 0;
                                prevImageView.setAlpha(0.5f);
                                nextImageView.setAlpha(1.0f);

                                currPracticeData = storyPracticeDataList[currStoryPageIdx];
                                storyImageView.setImageResource(currPracticeData.id_);
                                readText.setText(currPracticeData.word);
                                userSelectedOptIdx = -1;
                            }
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
            if (v.getId() == R.id.prevImage2) {
                if (userSelectedOptIdx == 2) {
                    createAlertDialog("Story Practice", "\"Prev\" button DISABLED during story listening");
                }
                else {
                    if (currStoryPageIdx > 0) {
                        currStoryPageIdx--;
                        currStoryPageIdx = currStoryPageIdx % storyPracticeDataList.length;
                        currPracticeData = storyPracticeDataList[currStoryPageIdx];
                        storyImageView.setImageResource(currPracticeData.id_);
                        readText.setText(currPracticeData.word);
                        nextImageView.setAlpha(1.0f);
                    }
                    if (currStoryPageIdx == 0) {
                        prevImageView.setAlpha(0.5f);
                    }
                }
            }
            else if (v.getId() == R.id.nextImage2) {
                if (userSelectedOptIdx == 2) {
                    createAlertDialog("Story Practice", "\"Next\" button DISABLED during story listening");
                } else {
                    if (currStoryPageIdx < storyPracticeDataList.length - 1) {
                        currStoryPageIdx++;
                        currStoryPageIdx = currStoryPageIdx % storyPracticeDataList.length;
                        currPracticeData = storyPracticeDataList[currStoryPageIdx];
                        storyImageView.setImageResource(currPracticeData.id_);
                        readText.setText(currPracticeData.word);
                        prevImageView.setAlpha(1.0f);
                    }
                    if (currStoryPageIdx == storyPracticeDataList.length - 1) {
                        nextImageView.setAlpha(0.5f);
                    }
                }
            }
            else if (v.getId() == R.id.recordedBtn2) {
                if (userSelectedOptIdx == -1) {
                    userSelectedOptIdx = 2;
                    recordedBtnView.setImageResource(R.drawable.recorded_play);
                    recordedText.setText("Reading");

                    int speech = textToSpeech.speak(currPracticeData.word, TextToSpeech.QUEUE_FLUSH, null, "");
                    textToSpeech.playSilentUtterance(1700, TextToSpeech.QUEUE_ADD, null);
                }
            }
            else if (v.getId() == R.id.recBtn2) {
                if (userSelectedOptIdx == 2) {
                    createAlertDialog("Story Practice", "\"Record\" button DISABLED during story listening");
                } else if (userSelectedOptIdx == 4) {
                    userSelectedOptIdx = 5;
                    stopStoryRecording();
                    recordBtnView.setImageResource(R.drawable.rec);
                    recText.setText("Record");
                } else if (userSelectedOptIdx == -1) {
                    userSelectedOptIdx = 4;
                    if (!checkPermissionFromDevice()) {
                        requestPermission();
                        try { Thread.sleep(1000); } catch (Exception e) {}
                    }
                    recordBtnView.setImageResource(R.drawable.rec_progress);
                    recText.setText("Recording");
                    startWordRecording();
                }
            }
            else if (v.getId() == R.id.playBtn2){
                if (userSelectedOptIdx == 2) {
                    createAlertDialog("Story Practice", "\"Play\" button DISABLED during story listening");
                } else if (userSelectedOptIdx == 6) {
                    playBtnView.setImageResource(R.drawable.play);
                    playText.setText("Play");
                    stopWordPlay();
                    userSelectedOptIdx = -1;
                } else if (userSelectedOptIdx == -1) {
                    if (recWordPath == null) {
                        createAlertDialog("Story Practice", "No Recorded voice exists ... \n"+
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_story, menu);
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
        updateStoryReportLog();
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(StoryPracticeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.story_memos_item:
                Intent memos_intent = new Intent(StoryPracticeActivity.this, StoryVoiceMemosActivity.class);
                memos_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(memos_intent);
                return true;
            case R.id.story_access_item:
                Intent access_intent = new Intent(StoryPracticeActivity.this, StoryAccessibilityActivity.class);
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
            Log.i("Rec Speech", recWordPath);
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
        mydb.updateSpeechActivity(getCurrDate(), "Story", deltaTime, recWordPath);
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
        storyPracticeDataList = Index2GameData.get(pos);
        currStoryPageIdx = 0;
        wordCountStory = wordCountStoryBook(storyPracticeDataList);

        updatePreferenceSetting(pos);
        updateStoryImprovView();
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
                    Toast.makeText(StoryPracticeActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StoryPracticeActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void updateStoryReportLog() {
        if (userSelectedOptIdx == 2) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        userSelectedOptIdx = 100;
        activityEndTimeMs = System.currentTimeMillis();
        long durationMs = activityEndTimeMs - activityStartTimeMs;
        //mydb.updateSpeechActivity(getCurrDate(), "Story", durationMs, recWordPath);
    }

    private void updatePreferenceSetting(int selectedIdx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();

        String selectedItem = TherapyStories[selectedIdx];
        edit.putString(KEY_STORY_SCROLLVIEW, selectedItem);
        edit.commit();
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_results = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_results == PackageManager.PERMISSION_GRANTED;
    }

    private int wordCountStoryBook(StoryPracticeDataModel storyDataList[]) {
        int wordCount = 0;
        for (int i = 0; i < storyDataList.length; i++) {
            wordCount += countWordString(storyDataList[i].word);
        }
        return wordCount;
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

        AlertDialog alertDialog = new AlertDialog.Builder(StoryPracticeActivity.this).create();
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