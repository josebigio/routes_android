package com.example.josebigio.mapapp.CustomClasses.DialogViews;

import android.content.Context;
import android.widget.TimePicker;

import com.example.josebigio.mapapp.R;

import java.lang.Override;

/**
 * Created by josebigio on 4/30/15.
 */
public class ArrivalPopupList extends BasePopupList<ArrivalPopupList.SimpleTime> implements TimePicker.OnTimeChangedListener{

    private TimePicker timePicker;

    public ArrivalPopupList(Context context) {
        super(context, R.layout.arrival_popup_view_layout);
        init();
    }

    private void init() {

        timePicker = (TimePicker)findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(this);

        setTitle("Set arrival time");

        this.data = new SimpleTime(timePicker.getCurrentHour(),timePicker.getCurrentMinute());


    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.data.setHour(hourOfDay);
        this.data.setMinutes(minute);

    }


    public class SimpleTime {


        private int hour;
        private int minutes;



        public SimpleTime(int hour, int minutes) {
            this.hour = hour;
            this.minutes = minutes;

        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }



    }


}
