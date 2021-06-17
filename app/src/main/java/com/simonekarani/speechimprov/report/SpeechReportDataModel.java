package com.simonekarani.speechimprov.report;

public class SpeechReportDataModel {
    String reportTitle;
    String activity;
    String durationStr;

    public SpeechReportDataModel(String title, String activityList, int duration) {
        this.reportTitle = title;
        this.activity = "Activity: " + activityList;
        this.durationStr = "Duration (secs): " + duration;
    }


    public String getReportTitle() {
        return reportTitle;
    }

    public String getReportActivity() {
        return activity;
    }

    public String getDuration() {
        return durationStr;
    }
}
