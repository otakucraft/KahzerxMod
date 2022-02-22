package com.kahzerx.kahzerxmod.extensions.profileExtension.gui.colors;

public class CIEMatch {
    private static final double epsilon = Math.pow(6, 3) / Math.pow(29, 3);
    private static final double kappa = Math.pow(29, 3) / Math.pow(3, 3);
    private static final double constA = Math.pow(25, 7);

    // https://github.com/jonathantneal/convert-colors/blob/master/src/lab-xyz.js
    private static double[] rgb2lab(int r, int g, int b) {

        double r2 = r / 255.0 * 100.0;
        double g2 = g / 255.0 * 100.0;
        double b2 = b / 255.0 * 100.0;

        r2 = r2 > 4.045 ? Math.pow((r2 + 5.5) / 105.5, 2.4) * 100 : r2 / 12.92;
        g2 = g2 > 4.045 ? Math.pow((g2 + 5.5) / 105.5, 2.4) * 100 : g2 / 12.92;
        b2 = b2 > 4.045 ? Math.pow((b2 + 5.5) / 105.5, 2.4) * 100 : b2 / 12.92;

        double x = r2 * 0.4124564 + g2 * 0.3575761 + b2 * 0.1804375;
        double y = r2 * 0.2126729 + g2 * 0.7151522 + b2 * 0.0721750;
        double z = r2 * 0.0193339 + g2 * 0.1191920 + b2 * 0.9503041;

        double d50X = x * 1.0478112 + y * 0.0228866 + z * -0.0501270;
        double d50Y = x * 0.0295424 + y * 0.9904844 + z * -0.0170491;
        double d50Z = x * -0.0092345 + y * 0.0150436 + z * 0.7521316;

        double f1 = d50X / 96.42;
        double f2 = d50Y / 100.0;
        double f3 = d50Z / 82.49;

        f1 = f1 > epsilon ? Math.cbrt(f1) : (kappa * f1 + 16.0) / 116.0;
        f2 = f2 > epsilon ? Math.cbrt(f2) : (kappa * f2 + 16.0) / 116.0;
        f3 = f3 > epsilon ? Math.cbrt(f3) : (kappa * f3 + 16.0) / 116.0;

        return new double[] { 116 * f2 - 16, 500 * (f1 - f2), 200 * (f2 - f3) };
    }

    private static double atan2d(double y, double x) {
        return Math.atan2(y, x) * 180.0 / Math.PI;
    }

    private static double sind(double x) {
        return Math.sin(x * Math.PI / 180.0);
    }

    private static double cosd(double x) {
        return Math.cos(x * Math.PI / 180.0);
    }

    private static double ciedesqrd(double L1, double a1, double b1, double L2, double a2, double b2) {

        double c1 = Math.hypot(a1, b1);
        double c2 = Math.hypot(a2, b2);

        double deltaLPrime = L2 - L1;

        double lBar = (L1 + L2) / 2;
        double cBar = (c1 + c2) / 2;

        double cBarPow7 = Math.pow(cBar, 7);
        double cCoeff = Math.sqrt(cBarPow7 / (cBarPow7 + constA));
        double a1Prime = a1 + a1 / 2 * (1 - cCoeff);
        double a2Prime = a2 + a2 / 2 * (1 - cCoeff);

        double c1Prime = Math.hypot(a1Prime, b1);
        double c2Prime = Math.hypot(a2Prime, b2);
        double cBarPrime = (c1Prime + c2Prime) / 2;
        double deltaCPrime = c2Prime - c1Prime;

        double h1Prime = a1Prime == 0 && b1 == 0 ? 0 : atan2d(b1, a1Prime) % 360;
        double h2Prime = a2Prime == 0 && b2 == 0 ? 0 : atan2d(b2, a2Prime) % 360;

        double deltaSmallHPrime;
        double deltaBigHPrime;
        double hBarPrime;

        if (c1Prime == 0 || c2Prime == 0) {
            deltaSmallHPrime = 0;
            deltaBigHPrime = 0;
            hBarPrime = h1Prime + h2Prime;
        } else {
            deltaSmallHPrime = Math.abs(h1Prime - h2Prime) <= 180 ? h2Prime - h1Prime
                    : h2Prime <= h1Prime ? h2Prime - h1Prime + 360 : h2Prime - h1Prime - 360;

            deltaBigHPrime = 2 * Math.sqrt(c1Prime * c2Prime) * sind(deltaSmallHPrime / 2);

            hBarPrime = Math.abs(h1Prime - h2Prime) <= 180 ? (h1Prime + h2Prime) / 2
                    : h1Prime + h2Prime < 360 ? (h1Prime + h2Prime + 360) / 2 : (h1Prime + h2Prime - 360) / 2;
        }

        double T = 1 - 0.17 * cosd(hBarPrime - 30) + 0.24 * cosd(2 * hBarPrime) + 0.32 * cosd(3 * hBarPrime + 6)
                - 0.2 * cosd(4 * hBarPrime - 63);

        double slCoeff = (lBar - 50) * (lBar - 50);
        double sl = 1 + 0.015 * slCoeff / Math.sqrt(20 + slCoeff);
        double sc = 1 + 0.045 * cBarPrime;
        double sh = 1 + 0.015 * cBarPrime * T;

        double RtCoeff = 60 * Math.exp(-((hBarPrime - 275) / 25) * ((hBarPrime - 275) / 25));
        double Rt = -2 * cCoeff * sind(RtCoeff);

        double term1 = deltaLPrime / (sl);
        double term2 = deltaCPrime / (sc);
        double term3 = deltaBigHPrime / (sh);
        double term4 = Rt * term2 * term3;
        return term1 * term1 + term2 * term2 + term3 * term3 + term4;
    }

    // blend semi-transparent color with white
    private static int blend(int c, double a) {
        return (int) (255 + (c - 255) * a);
    }

    public static double colorDelta(int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
        if (a1 < 255) {
            double a1_2 = (double) a1 / 255;
            r1 = blend(r1, a1_2);
            g1 = blend(g1, a1_2);
            b1 = blend(b1, a1_2);
        }

        if (a2 < 255) {
            double a2_2 = (double) a2 / 255;
            r2 = blend(r2, a2_2);
            g2 = blend(g2, a2_2);
            b2 = blend(b2, a2_2);
        }

        double[] l1 = rgb2lab(r1, g1, b1);
        double[] l2 = rgb2lab(r2, g2, b2);
        return ciedesqrd(l1[0], l1[1], l1[2], l2[0], l2[1], l2[2]);
    }
}
