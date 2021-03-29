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
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainScreenDataModel> data;
    private View.OnClickListener myOnClickListener;

    private TextView dilemmaTextView = null;
    private ImageView dilemmaImageView = null;
    private Button dilemmaOptBtn1 = null;
    private Button dilemmaOptBtn2 = null;
    private Button dilemmaOptBtn3 = null;
    private Button dilemmaOptBtn4 = null;

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

        /*dilemmaTextView = (TextView) findViewById(R.id.dilemmaText);
        dilemmaImageView = (ImageView) findViewById(R.id.dilemmaImage);
        dilemmaOptBtn1  = (Button) findViewById(R.id.optButton1);
        dilemmaOptBtn2  = (Button) findViewById(R.id.optButton2);
        dilemmaOptBtn3  = (Button) findViewById(R.id.optButton3);
        dilemmaOptBtn4  = (Button) findViewById(R.id.optButton4);

        myOnClickListener = (View.OnClickListener) new MyOnClickListener(this);
        dilemmaOptBtn1.setOnClickListener(myOnClickListener);
        dilemmaOptBtn2.setOnClickListener(myOnClickListener);
        dilemmaOptBtn3.setOnClickListener(myOnClickListener);
        dilemmaOptBtn4.setOnClickListener(myOnClickListener);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //updateDilemmaView();
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

/*    private void updateDilemmaView() {
        do {
            currDilemmaDataIdx = (int)(MoralDilemmaData.MoralDilemmaDataList.length * Math.random());
        } while (mDilemmaDataSet.contains(currDilemmaDataIdx));
        mDilemmaDataSet.add(currDilemmaDataIdx);
        MoralDilemmaModel dilemmaData = MoralDilemmaData.MoralDilemmaDataList[currDilemmaDataIdx];

        dilemmaTextView.setTextSize(dilemmaData.getQuestionFontSize());
        dilemmaTextView.setText(dilemmaData.getQuestion());

        dilemmaImageView.setImageResource(dilemmaData.getImageResId());
        dilemmaOptBtn1.setTextSize(dilemmaData.getOptionFontSize());
        dilemmaOptBtn1.setText(dilemmaData.getOption1());
        dilemmaOptBtn2.setTextSize(dilemmaData.getOptionFontSize());
        dilemmaOptBtn2.setText(dilemmaData.getOption2());
        dilemmaOptBtn3.setTextSize(dilemmaData.getOptionFontSize());
        dilemmaOptBtn3.setText(dilemmaData.getOption3());
        dilemmaOptBtn4.setTextSize(dilemmaData.getOptionFontSize());
        dilemmaOptBtn4.setText(dilemmaData.getOption4());

        dilemmaOptBtn1.setPadding(0, 0, 0, dilemmaData.getBtnGap());
        dilemmaOptBtn2.setPadding(0, 0, 0, dilemmaData.getBtnGap());
        dilemmaOptBtn3.setPadding(0, 0, 0, dilemmaData.getBtnGap());
        dilemmaOptBtn4.setPadding(0, 0, 0, 0);
    }
*/

    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int selectedOptIdx = -1;
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