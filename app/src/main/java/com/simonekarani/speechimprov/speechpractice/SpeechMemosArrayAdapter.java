package com.simonekarani.speechimprov.speechpractice;

import android.content.Context;
import android.util.Log;
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
//public class SpeechMemosArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<SpeechReportDataModel> values;
    //private String[] values;

    public SpeechMemosArrayAdapter(Context context, ArrayList<SpeechReportDataModel> values) {
    //public SpeechMemosArrayAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.speech_memos_rowlayout, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.speech_voice_firstLine);
        TextView textView2 = (TextView) rowView.findViewById(R.id.speech_voice_secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.speech_voice_icon);
        textView1.setText(values.get(position).getReportTitle());
        textView2.setText(values.get(position).getDuration());
        //textView1.setText(values[position]);
        //textView2.setText(values[position]);
        imageView.setImageResource(R.drawable.memos_play);
        Log.i("SpeechMemosArrayAdapter", "*****" + values.get(position).getReportTitle() + ", " +
                values.get(position).getDuration() + ", " + values.get(position).getSpeechPath());

        return rowView;
    }
}