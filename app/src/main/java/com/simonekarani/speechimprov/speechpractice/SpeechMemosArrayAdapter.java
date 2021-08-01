//
//  SpeechMemosArrayAdapter.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/21.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.speechpractice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simonekarani.speechimprov.R;
import com.simonekarani.speechimprov.report.SpeechReportDataModel;

import java.util.ArrayList;

public class SpeechMemosArrayAdapter extends ArrayAdapter<SpeechReportDataModel> {
    private final Context context;
    private final ArrayList<SpeechReportDataModel> values;

    public SpeechMemosArrayAdapter(Context context, ArrayList<SpeechReportDataModel> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.speech_memos_row, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.speech_voice_firstLine);
        TextView textView2 = (TextView) rowView.findViewById(R.id.speech_voice_secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.speech_voice_icon);
        textView1.setText(values.get(position).getReportTitle());
        textView2.setText(values.get(position).getDuration());
        imageView.setImageResource(R.drawable.memos_play);

        return rowView;
    }
}