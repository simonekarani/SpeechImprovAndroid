//
//  MainScreenData.java
//  SpeechImprov
//
//  Created by Simone Karani on 4/10/2021.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//
package com.simonekarani.speechimprov.model;

import com.simonekarani.speechimprov.R;

public class MainScreenData {
    public static final int PRACTICE_WORD_ID      = 0;
    public static final int MORAL_STORIES_ID      = 1;
    public static final int PRACTICE_SPEECH_ID    = 2;
    public static final int SPEECH_ANALYSIS_ID    = 3;

    public static String[] nameArray = {
            "Practice Spoken Words", "Play Stories", "Practice Speech", "Activity Report"
    };

    public static Integer[] drawableArray = {
            R.drawable.practice_word, R.drawable.storytime, R.drawable.srecord, R.drawable.speech_analysis
    };

    public static Integer[] id_ = {0, 1, 2, 3};
}
