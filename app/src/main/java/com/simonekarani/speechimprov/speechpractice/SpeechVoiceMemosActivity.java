//
//  SpeechVoiceMemosActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.speechpractice;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.report.SpeechActivityDBHelper;
import com.simonekarani.speechimprov.report.SpeechReportDataModel;

import java.io.IOException;
import java.util.ArrayList;

public class SpeechVoiceMemosActivity extends AppCompatActivity {

    private SpeechActivityDBHelper mydb ;
    private MediaPlayer mediaPlayer;
    private ArrayList<SpeechReportDataModel> speechRecList;
    private SpeechMemosArrayAdapter adapter = null;
    private ImageView selectedImgView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_memos);
        setTitle("Speech Voice Memos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView listview = (ListView) findViewById(R.id.speech_memos_listview);

        mydb = new SpeechActivityDBHelper(this);

        speechRecList = mydb.getSpeechDataActivity("Speech");
        adapter = new SpeechMemosArrayAdapter(this, speechRecList );
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                selectedImgView = view.findViewById(R.id.speech_voice_icon);
                selectedImgView.setImageResource(R.drawable.memos_pause);
                startItemPlay(position);
            }
        });
    }

    private void startItemPlay(int listpos) {
        SpeechReportDataModel speechItem = speechRecList.get(listpos);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(speechItem.getSpeechPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                selectedImgView.setImageResource(R.drawable.memos_play);
            }
        });
        mediaPlayer.start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SpeechVoiceMemosActivity.this, SpeechPracticeActivity.class);
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
        super.onBackPressed();
    }
}