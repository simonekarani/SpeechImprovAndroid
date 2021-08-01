//
//  WordVoiceMemosActivity.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.wordpractice;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

public class WordVoiceMemosActivity extends AppCompatActivity {

    private SpeechActivityDBHelper mydb ;
    private MediaPlayer mediaPlayer;
    private ArrayList<SpeechReportDataModel> wordRecList;
    private WordMemosArrayAdapter adapter = null;
    private ImageView selectedImgView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_memos);
        setTitle("Speech Voice Memos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView listview = (ListView) findViewById(R.id.word_memos_listview);

        mydb = new SpeechActivityDBHelper(this);

        wordRecList = mydb.getSpeechDataActivity("Word");
        adapter = new WordMemosArrayAdapter(this, wordRecList );
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                selectedImgView = view.findViewById(R.id.word_voice_icon);
                selectedImgView.setImageResource(R.drawable.memos_pause);
                startItemPlay(position);
            }
        });
    }

    private void startItemPlay(int listpos) {
        SpeechReportDataModel speechItem = wordRecList.get(listpos);
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
                Intent intent = new Intent(WordVoiceMemosActivity.this, WordPracticeActivity.class);
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