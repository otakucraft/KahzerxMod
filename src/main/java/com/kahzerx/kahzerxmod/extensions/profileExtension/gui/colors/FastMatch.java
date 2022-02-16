package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.colors;

public class FastMatch {
    public static double colorDelta(int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {

        if (a1 == a2 && r1 == r2 && g1 == g2 && b1 == b2)
            return 0;

        if (a1 < 255) {
            double a1_2 = (double)a1 / 255;
            r1 = blend(r1, a1_2);
            g1 = blend(g1, a1_2);
            b1 = blend(b1, a1_2);
        }

        if (a2 < 255) {
            double a2_2 = (double)a2 / 255;
            r2 = blend(r2, a2_2);
            g2 = blend(g2, a2_2);
            b2 = blend(b2, a2_2);
        }

        double y = rgb2y(r1, g1, b1) - rgb2y(r2, g2, b2);

        double i = rgb2i(r1, g1, b1) - rgb2i(r2, g2, b2);
        double q = rgb2q(r1, g1, b1) - rgb2q(r2, g2, b2);

        return 0.5053 * y * y + 0.299 * i * i + 0.1957 * q * q;
    }

    private static double rgb2y(int r, int g, int b) {
        return r * 0.29889531 + g * 0.58662247 + b * 0.11448223;
    }

    private static double rgb2i(int r, int g, int b) {
        return r * 0.59597799 - g * 0.27417610 - b * 0.32180189;
    }

    private static double rgb2q(int r, int g, int b) {
        return r * 0.21147017 - g * 0.52261711 + b * 0.31114694;
    }

    // blend semi-transparent color with white
    private static int blend(int c, double a) {
        return (int)(255 + (c - 255) * a);
    }
}
