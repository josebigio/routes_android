package com.example.josebigio.mapapp.Utilities;


import android.graphics.Color;

import java.util.Random;

/**
 * Created by josebigio on 6/24/15.
 */
public class ColorGenerator {


    private static final int[] colors = new int[]{ Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN,
            Color.BLACK, Color.GRAY}; //TODO: Add more colors

    public static int getColorForRoute(int routeId) {
        Random r = new Random(routeId);
        return colors[r.nextInt(colors.length)];
    }




}
