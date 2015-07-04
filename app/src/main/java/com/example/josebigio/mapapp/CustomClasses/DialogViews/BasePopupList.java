package com.example.josebigio.mapapp.CustomClasses.DialogViews;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.josebigio.mapapp.R;

/**
 * Created by josebigio on 4/30/15.
 */
public abstract class BasePopupList<T> extends Dialog implements PopupViewInterface, View.OnClickListener {

    protected T data;
    private TextView titleTextView;
    private Button cancelButton;
    private Button okButton;
    private int childResourceLayoutID;
    private ViewGroup myLayout;


    public BasePopupList(Context context, int resource) {
        super(context);
        childResourceLayoutID = resource;
        init();
    }


    private void init() {

        //remove default title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set the content view
        setContentView(R.layout.popup_view_layout);
        myLayout = (ViewGroup)findViewById(R.id.basePopupLayout);

        //init the title
        titleTextView = (TextView)findViewById(R.id.popupTitle);

        //init the buttons
        cancelButton = (Button) findViewById(R.id.popupCancel);
        cancelButton.setOnClickListener(this);
        okButton = (Button) findViewById(R.id.popupOk);
        okButton.setOnClickListener(this);

        //add the layout that will be created by its children
        getLayoutInflater().inflate(childResourceLayoutID, getPlaceHolder());


    }

    @Override
    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void setClose() {

    }

    @Override
    public void setOpen() {

    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public void onClick(View v) {

        if(v == cancelButton) {
            dismiss();
        }
        else if(v == okButton) {
            dismiss();
        }
    }

    protected ViewGroup getPlaceHolder() {
        return (ViewGroup)findViewById(R.id.placeHolder);
    }


}
