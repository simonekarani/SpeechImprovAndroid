//
//  SpeechPracticeActivity.java
//  MoralIQ
//
//  Created by Simone Karani on 2/9/20.
//  Copyright © 2020 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.wordpractice;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.simonekarani.speechimprov.MainActivity;
import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.model.MainScreenDataModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class WordPracticeActivity extends AppCompatActivity {

    private static final int MAX_DILEMMA_COUNT = 7;

    private final static String WORD_INSTR = "Repeat the words below the image, and check for correct pronunciation?\n";
            /*"- Press Word Play for correct pronunciation\n" +
            "- Press Mic to record the words\n" +
            "- Press Play to play the recorded word\n" +
            "- Press Next to move to next word\n";*/

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainScreenDataModel> data;
    private View.OnClickListener myOnClickListener;

    private TextView instrTextView = null;
    private ImageButton prevImageView = null;
    private ImageButton nextImageView = null;
    private ImageView wordImageView = null;
    private ImageButton recordedBtnView = null;
    private ImageButton recordBtnView = null;
    private ImageButton playBtnView = null;

    private Set<Integer> mDilemmaDataSet = new HashSet<>();
    private int userResultCount = 0;
    private int currDilemmaDataIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_practice);
        setTitle("Speech Practice");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currDilemmaDataIdx = 0;
        userResultCount = 0;

        instrTextView = (TextView) findViewById(R.id.instrText);
        prevImageView = (ImageButton) findViewById(R.id.prevImage);
        nextImageView = (ImageButton) findViewById(R.id.nextImage);
        wordImageView = (ImageView) findViewById(R.id.wordImage);
        recordedBtnView = (ImageButton) findViewById(R.id.recordedBtn);
        recordBtnView = (ImageButton) findViewById(R.id.recBtn);
        playBtnView   = (ImageButton) findViewById(R.id.playBtn);

        myOnClickListener = (View.OnClickListener) new MyOnClickListener(this);
        prevImageView.setOnClickListener(myOnClickListener);
        nextImageView.setOnClickListener(myOnClickListener);
        recordedBtnView.setOnClickListener(myOnClickListener);
        recordBtnView.setOnClickListener(myOnClickListener);
        playBtnView.setOnClickListener(myOnClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWordImprovView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        /*
        if (userResultCount < MAX_DILEMMA_COUNT) {
            //updateDilemmaView();
        }
        else {
            Intent intent = new Intent(this, MDilemmaResultActivity.class);
            Bundle resultBundle = new Bundle();
            resultBundle.putParcelableArrayList("dilemmaResult", mDilemmaResultList);
            intent.putExtras(resultBundle);
            startActivity(intent);
        }*/
    }

    private void updateWordImprovView() {
        instrTextView.setText(WORD_INSTR);
        recordedBtnView.setImageResource(R.drawable.recorded);
        recordBtnView.setImageResource(R.drawable.rec);
        playBtnView.setImageResource(R.drawable.play);
         /*do {
            currDilemmaDataIdx = (int)(MoralDilemmaData.MoralDilemmaDataList.length * Math.random());
        } while (mDilemmaDataSet.contains(currDilemmaDataIdx));
        mDilemmaDataSet.add(currDilemmaDataIdx);
        MoralDilemmaModel dilemmaData = MoralDilemmaData.MoralDilemmaDataList[currDilemmaDataIdx];

        dilemmaTextView.setTextSize(dilemmaData.getQuestionFontSize());
        */
        wordImageView.setImageResource(R.drawable.lava);
    }

    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int selectedOptIdx = -1;
            if (prevImageView.isPressed()) {
                selectedOptIdx = 0;
            }
            else if (nextImageView.isPressed()) {
                selectedOptIdx = 1;
            }
            else if (recordedBtnView.isPressed()) {
                selectedOptIdx = 2;
                recordedBtnView.setImageResource(R.drawable.recorded_play);
            }
            else if (recordBtnView.isPressed()) {
                selectedOptIdx = 3;
                recordBtnView.setImageResource(R.drawable.rec_progress);
            }
            else {
                selectedOptIdx = 4;
                playBtnView.setImageResource(R.drawable.pause);
            }
            onRestart();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}