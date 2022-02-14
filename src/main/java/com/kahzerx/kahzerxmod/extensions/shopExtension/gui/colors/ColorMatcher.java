package com.kahzerx.kahzerxmod.extensions.shopExtension.gui.colors;

public class ColorMatcher {
    private static int[][] MAP_COLORS;
    static {

        int[] colors = { 0, 8368696, 16247203, 13092807, 16711680, 10526975, 10987431, 31744, 16777215, 10791096,
                9923917, 7368816, 4210943, 9402184, 16776437, 14188339, 11685080, 6724056, 15066419, 8375321, 15892389,
                5000268, 10066329, 5013401, 8339378, 3361970, 6704179, 6717235, 10040115, 1644825, 16445005, 6085589,
                4882687, 55610, 8476209, 7340544, 13742497, 10441252, 9787244, 7367818, 12223780, 6780213, 10505550,
                3746083, 8874850, 5725276, 8014168, 4996700, 4993571, 5001770, 9321518, 2430480, 12398641, 9715553,
                6035741, 1474182, 3837580, 5647422, 1356933 };

        MAP_COLORS = new int[colors.length * 4][3];

        int index = 0;
        for (int color : colors) {
            for (int shade = 0; shade < 4; shade++) {
                int i = 220;
                if (shade == 3) {
                    i = 135;
                }

                if (shade == 2) {
                    i = 255;
                }

                if (shade == 1) {
                    i = 220;
                }

                if (shade == 0) {
                    i = 180;
                }
                int r = (color >> 16 & 255) * i / 255;
                int g = (color >> 8 & 255) * i / 255;
                int b = (color & 255) * i / 255;

                MAP_COLORS[index++] = new int[] {r,g,b};
            }

        }

    }
    public static byte getBestColor(int r, int g, int b, int a) {
        int bestColor = 0;
        double bestColorScore = 0;

        for (int i = 4; i < MAP_COLORS.length; i++) {
            int[] color = MAP_COLORS[i];
            double score = FastMatch.colorDelta(r, g, b, a, color[0], color[1], color[2], 255);

            if (i == 4 || score < bestColorScore) {
                bestColor = i;
                bestColorScore = score;
            }
        }

        return (byte)bestColor;
    }
}
