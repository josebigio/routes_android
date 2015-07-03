package com.example.josebigio.mapapp.Utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.josebigio.mapapp.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by josebigio on 7/1/15.
 */
public class IconColorChanger {

    public static BitmapDescriptor getNewIconWithColor(int originalColor, int desiredColor, int resourceID, Activity activity) {

        Bitmap clonedBitmap = BitmapFactory.decodeResource(activity.getResources(), resourceID).copy(Bitmap.Config.ARGB_8888, true);
        int [] allpixels = new int [ clonedBitmap.getHeight()*clonedBitmap.getWidth()];

        clonedBitmap.getPixels(allpixels, 0, clonedBitmap.getWidth(), 0, 0, clonedBitmap.getWidth(), clonedBitmap.getHeight());

        for(int i =0; i<clonedBitmap.getHeight()*clonedBitmap.getWidth();i++){

            if( allpixels[i] == originalColor)
                allpixels[i] = desiredColor;
        }

        clonedBitmap.setPixels(allpixels, 0, clonedBitmap.getWidth(), 0, 0, clonedBitmap.getWidth(), clonedBitmap.getHeight());
        return BitmapDescriptorFactory.fromBitmap(clonedBitmap);
    }
}
