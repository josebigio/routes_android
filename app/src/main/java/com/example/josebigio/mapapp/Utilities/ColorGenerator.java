package com.example.josebigio.mapapp.Utilities;


import android.graphics.Color;

import java.util.Random;

/**
 * Created by josebigio on 6/24/15.
 */
public class ColorGenerator {


    private static final int[] colors = new int[]{ Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN,
            Color.BLACK, Color.GRAY, Color.CYAN, Color.DKGRAY, Color.LTGRAY, Color.MAGENTA, Color.WHITE}; //TODO: Add more colors

    public static int getColorForRoute(String routeHeadsign) {
        Random r = new Random(routeHeadsign.hashCode());
        return colors[r.nextInt(colors.length)];
    }




}
