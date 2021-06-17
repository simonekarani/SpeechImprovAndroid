//
//  SpeechReportListAdapter.java
//  SpeechImprov
//
//  Created by Simone Karani on 2/9/20.
//  Copyright © 2020 SpeechImprov. All rights reserved.
//

package com.simonekarani.speechimprov.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.simonekarani.speechimprov.R;

import java.util.ArrayList;


public class SpeechReportListAdapter extends RecyclerView.Adapter<SpeechReportListAdapter.MyViewHolder> {

    private static final String TAG = "SpeechReportListAdapter";
    private ArrayList<SpeechReportDataModel> dataSet;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView activityTextView;
        TextView durationTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = (TextView) itemView.findViewById(R.id.reportTitle);
            this.activityTextView = (TextView) itemView.findViewById(R.id.textActivity);
            this.durationTextView = (TextView) itemView.findViewById(R.id.textDuration);
        }
    }

    public SpeechReportListAdapter(ArrayList<SpeechReportDataModel> data) {
        this.dataSet = data;
    }

    @Override
    public SpeechReportListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_row, parent, false);

        SpeechReportListAdapter.MyViewHolder myViewHolder = new SpeechReportListAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final SpeechReportListAdapter.MyViewHolder holder, final int listPosition) {

        TextView reportTextViewName = holder.titleTextView;
        TextView activityTextViewName = holder.activityTextView;
        TextView durationTextViewName = holder.durationTextView;

        reportTextViewName.setText(dataSet.get(listPosition).getReportTitle());
        activityTextViewName.setText(dataSet.get(listPosition).getReportActivity());
        durationTextViewName.setText(dataSet.get(listPosition).getDuration());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}