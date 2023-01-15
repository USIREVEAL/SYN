package com.usi.ch.syn.core.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public interface ColorUtil {

    static List<Color> getColorLinearGradient(Color colorFrom, Color colorTo, int steps) {
        List<Color> hexColors = new ArrayList<>();

        int rD = computeDelta(colorFrom.getRed(), colorTo.getRed()) / steps;
        int gD = computeDelta(colorFrom.getGreen(), colorTo.getGreen()) / steps;
        int bD = computeDelta(colorFrom.getBlue(), colorTo.getBlue()) / steps;

        float[] multiplier = new float[steps];
        float firstStepCut = steps / 2f;
        if (steps > 2) {
            float increment = (steps - firstStepCut) / (steps - 1);
            for (int i = 0; i < steps; i++) {
                if (i == 0) {
                    multiplier[i] = firstStepCut;
                } else {
                    multiplier[i] = multiplier[i - 1] + increment;
                }
            }

            multiplier[steps - 1] = steps;
        } else if (steps == 2) {
            return List.of(colorFrom, colorTo);
        } else {
            return List.of(colorFrom);

        }

        hexColors.add(colorFrom);
        for (int i = 1; i < steps - 1; i++) {
            int r = colorFrom.getRed() + (int) (rD * multiplier[i - 1]);
            int g = colorFrom.getGreen() + (int) (gD * multiplier[i - 1]);
            int b = colorFrom.getBlue() + (int) (bD * multiplier[i - 1]);

            hexColors.add(new Color(r, g, b));
        }
        hexColors.add(colorTo);

        return hexColors;
    }

    private static int computeDelta(int colorIntFrom, int colorIntTo) {

        return colorIntFrom > colorIntTo ?
                -(colorIntFrom - colorIntTo) :
                colorIntTo - colorIntFrom;
    }

//    private static int[] parseHex(final String hexString) {
//        return new int[]{
//                Integer.parseInt(hexString.substring(1, 3), 16),
//                Integer.parseInt(hexString.substring(3, 5), 16),
//                Integer.parseInt(hexString.substring(5, 7), 16)
//        };
//    }
//
//    private static String toHex(final int[] colorArray) {
//        return "#" + Integer.toHexString(colorArray[0]) + Integer.toHexString(colorArray[1]) + Integer.toHexString(colorArray[2]);
//    }
}
