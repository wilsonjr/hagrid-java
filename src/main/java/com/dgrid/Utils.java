package com.dgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author wilson.marcilio
 */
public class Utils {
    
    public static Map<String, Double> getBounds(List<double[]> A) {
        double min_x = Double.POSITIVE_INFINITY;
        double max_x = Double.NEGATIVE_INFINITY;
        double min_y = Double.POSITIVE_INFINITY;
        double max_y = Double.NEGATIVE_INFINITY;

        for (double[] point : A) {
            double x = point[0];
            double y = point[1];

            min_x = Math.min(min_x, x);
            max_x = Math.max(max_x, x);
            min_y = Math.min(min_y, y);
            max_y = Math.max(max_y, y);
        }

        Map<String, Double> bounds = new HashMap<>();
        bounds.put("x", min_x);
        bounds.put("y", min_y);
        bounds.put("width", max_x - min_x);
        bounds.put("height", max_y - min_y);

        return bounds;
    }

    public static double[] remap(double[] point, double[] fromBounds, double[] toBounds) {
        double x = point[0];
        double y = point[1];

        double x1 = fromBounds[0];
        double y1 = fromBounds[1];
        double w1 = fromBounds[2];
        double h1 = fromBounds[3];

        double x2 = toBounds[0];
        double y2 = toBounds[1];
        double w2 = toBounds[2];
        double h2 = toBounds[3];

        double remappedX = (x - x1) / w1 * w2 + x2;
        double remappedY = (y - y1) / h1 * h2 + y2;

        return new double[]{remappedX, remappedY};
    }
    
    public static double[] extent(List<double[]> data, Function<double[], Double> accessor) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double[] d : data) {
            double v = accessor.apply(d);
            min = Math.min(min, v);
            max = Math.max(max, v);
        }
        return new double[] { min, max };
    }

    
    public static List<Function<double[], Double>> get_scales(List<double[]> data, double[][] bounds) {
        double X_min = bounds[0][0];
        double X_max = bounds[0][1];
        double Y_min = bounds[1][0];
        double Y_max = bounds[1][1];

        final boolean keep_aspect_ratio = false;
        final boolean round = true;
        
        final Function<double[], Double> x = d -> d[0];
        final Function<double[], Double> y = d -> d[1];

        double X_span = X_max - X_min;
        double Y_span = Y_max - Y_min;
        double[] x_extent = extent(data, x);
        double[] y_extent = extent(data, y);
        double x_min = x_extent[0];
        double x_max = x_extent[1];
        double y_min = y_extent[0];
        double y_max = y_extent[1];
        double x_span = x_max - x_min;
        double y_span = y_max - y_min;

        if (keep_aspect_ratio) {
            double o;
            if (x_span > y_span) {
                o = (x_span - y_span) / 2;
                y_min -= o;
                y_max += o;
            } else {
                o = (y_span - x_span) / 2;
                x_min -= o;
                x_max += o;
            }
        }
        
        Function<double[], Double> x_scale = d -> {
            double scale = (x.apply(d) - x_min) / x_span * X_span + X_min;
            return round ? Math.round(scale) : scale;
        };

        Function<double[], Double> y_scale = d -> {
            double scale = (y.apply(d) - y_min) / y_span * Y_span + Y_min;
            return round ? Math.round(scale) : scale;
        };

//        return new double[]{x_scale.apply(data.get(0)), y_scale.apply(data.get(0))};
        var scales = new ArrayList<Function<double[], Double>>();
        scales.add(x_scale);
        scales.add(y_scale);
        return scales;
    }

    public static double distance(double[] a, int[] b) {
        return Math.hypot(b[0] - a[0], b[1] - a[1]); 
    }
}
