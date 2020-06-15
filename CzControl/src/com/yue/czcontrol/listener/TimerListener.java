package com.yue.czcontrol.listener;

import com.yue.czcontrol.utils.TimeProperty;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerListener{
    /**
     * TimerListner Consructor
     * @param label The JLabel
     */
    public TimerListener(JLabel label){
        Timer timer = new Timer();
        timer.schedule(new TimeTask(label), 1000, 1000);
    }
}

class TimeTask extends TimerTask implements TimeProperty{

    /**
     * JLabel
     */
    private final JLabel label;

    /**
     * DateFormat
     */
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    /**
     * TimeTask
     * @param label JLabel
     */
    public TimeTask(JLabel label){
        this.label = label;
    }

    /**
     * Set the format time to label
     */
    @Override
    public void run() {
        label.setText(dateFormatter.format(new Date()));
    }
}
