/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dgrid;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wilson.marcilio
 */
public class Gridify {
    
    public static List<Point2D.Double> gridify(List<Point2D.Double> points, Map<String, Double> parameters) {
        if (!parameters.containsKey("pluslevel")) {
            parameters.put("pluslevel", 0.0);
        }
        if (!parameters.containsKey("whitespace")) {
            parameters.put("whitespace", 1.0);
        }
        
        var data = new ArrayList<double[]>(); 
        points.stream().forEach(v -> {
            data.add(new double[]{v.x, v.y});
        });
        
        System.out.println("whitespace: "+parameters.get("whitespace"));
        
        double level = Math.ceil(Math.log(data.size() * parameters.get("whitespace")) / Math.log(4)) + parameters.get("pluslevel");
        double size =  Math.pow(2, level);
        
        Map<String, Double> bounds = Utils.getBounds(data);
        
        var ux = bounds.get("x");
        var uy = bounds.get("y");
        var w = bounds.get("width");
        var h = bounds.get("height");
        
        double[] original_bounding_box = { ux, uy, w, h};
        double[] gridded_bounding_box = {0, 0, size, size};
        

        List<double[]> D = new ArrayList<>();
        data.stream().forEach(v -> {
            D.add(Utils.remap(v, original_bounding_box, gridded_bounding_box));            
        });
        
        int[][] output = HilbertGridify.gridify_hilbert(D, (int) level, true);
        
        List<Point2D.Double> output_points = new ArrayList<>();
        for( int i = 0; i < output.length; ++i ) {
            output_points.add(new Point2D.Double(output[i][0], output[i][1]));
        }
        
        return output_points;
    }

}
