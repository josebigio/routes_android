package com.example.josebigio.mapapp.CustomClasses.DialogViews;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.josebigio.mapapp.R;
import com.example.josebigio.mapapp.model.Stop;


/**
 * Created by josebigio on 5/8/15.
 */
public class TransitTablePopupList extends BasePopupList {


    private RecyclerView mRecyclerView;
    private TransitTableAdapter transitTableAdapter;

    public TransitTablePopupList(Context context) {
        super(context, R.layout.transit_table_popoup_view_layout);
        init();
    }

    private void init() {

        mRecyclerView = (RecyclerView) findViewById(R.id.transit_table_popup_view_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        transitTableAdapter = new TransitTableAdapter();
        mRecyclerView.setAdapter(transitTableAdapter);

    }

    public void setStop(Stop stop){
        transitTableAdapter.setStop(stop);
    }


}
