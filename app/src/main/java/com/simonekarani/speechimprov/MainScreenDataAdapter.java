package com.simonekarani.speechimprov;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simonekarani.speechimprov.model.MainScreenDataModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MainScreenDataAdapter extends RecyclerView.Adapter<MainScreenDataAdapter.MyViewHolder> {

    private static final String TAG = "MainActivity";
    private ArrayList<MainScreenDataModel> dataSet;
    private OnMoralTopicListener moralTopicListener;

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView textViewName;
        ImageView imageViewIcon;
        OnMoralTopicListener topicListener;

        public MyViewHolder(View itemView, OnMoralTopicListener listener) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.topicListener = listener;

            this.textViewName.setOnClickListener(this);
            this.imageViewIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            topicListener.onTopicClick(getAdapterPosition());
        }
    }

    public interface OnMoralTopicListener {
        void onTopicClick(int position);
    }

    public MainScreenDataAdapter(ArrayList<MainScreenDataModel> data, OnMoralTopicListener listener) {
        this.dataSet = data;
        this.moralTopicListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view, moralTopicListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(dataSet.get(listPosition).getName());
        imageView.setImageResource(dataSet.get(listPosition).getImage());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
