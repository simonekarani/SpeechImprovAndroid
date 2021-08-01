package com.simonekarani.speechimprov.report;

public class SpeechReportDataModel {
    int id;
    String reportTitle;
    String activity;
    String durationStr;
    String pathStr;

    public SpeechReportDataModel(int id, String title, String activityList, String durationStr,
                                 String pathStr) {
        this.id = id;
        this.reportTitle = title;
        this.activity = activityList;
        this.durationStr = durationStr;
        this.pathStr = pathStr;
    }

    public SpeechReportDataModel(String title, String activityList, String durationStr,
                                 String pathStr) {
        this.reportTitle = title;
        this.activity = "Activity: " + activityList;
        this.pathStr = pathStr;
        Long durationValueSecs = Long.parseLong(durationStr) / 1000;
        if (durationValueSecs < 60) {
            this.durationStr = "Duration: " + durationValueSecs + " s";
        } else {
            if (durationValueSecs > 3600) {
                long durationH = durationValueSecs / 3600;
                durationValueSecs = durationValueSecs % 3600;
                long durationM = durationValueSecs / 60;
                long durationS = durationValueSecs % 60;
                this.durationStr = "Duration: " + durationH + " h" + durationM + " min " + durationS + " s";
            } else {
                long durationM = durationValueSecs / 60;
                long durationS = durationValueSecs % 60;
                this.durationStr = "Duration: " + durationM + " min " + durationS + " s";
            }
        }
    }

    public int getId() {
        return id;
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

    public String getSpeechPath() { return pathStr; }
}
