package com.example.josebigio.mapapp.CustomClasses.DialogViews;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.josebigio.mapapp.R;
import com.example.josebigio.mapapp.model.Stop;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by josebigio on 5/9/15.
 */
public class TransitTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "TransitTableAdapter";

    private static final int TEST_ROWS = 100;
    public static final int TRANSIT_CELL_TYPE = 1;

    private Stop stop;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

        }

    }

    public static class TransitCellHolder extends ViewHolder {

        @InjectView(R.id.timeTextView)
        TextView timeTV;
        @InjectView(R.id.routeNameTextView)
        TextView routeNameTV;
        @InjectView(R.id.nameBlock)
        ViewGroup nameBlock;
        @InjectView(R.id.timeBlock)
        ViewGroup timeBlock;
        @InjectView(R.id.rightArrowTransit)
        View arrow;


        public TransitCellHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);

        }

        public void setRouteNameTV(String routeName) {
            routeNameTV.setText(routeName);
        }

        public void setTime(String time) {
            timeTV.setText(time);
        }

    }

    public void setStop(Stop stop){
        this.stop = stop;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Log.v(TAG, "OnCreateViewHolder(" + viewGroup + ", " + i);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transit_table_schedule_cell, viewGroup, false);

        return new TransitCellHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getClass() == TransitCellHolder.class) {
            TransitCellHolder tch = (TransitCellHolder)viewHolder;
           tch.setRouteNameTV(stop.getRoutes().get(i).getHeadsign());
            tch.setTime(stop.getRoutes().get(i).getArrivalTime());
        }
    }


    @Override
    public int getItemCount() {
        return stop.getRoutes().size();
    }

    @Override
    public int getItemViewType(int position) {
      return TRANSIT_CELL_TYPE;
    }




}


