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
    
    public static List<Point2D.Double> gridify(List<Point2D.Double> data, Map<String, Double> parameters) {
        if (!parameters.containsKey("pluslevel")) {
            parameters.put("pluslevel", 0.0);
        }
        if (!parameters.containsKey("whitespace")) {
            parameters.put("whitespace", 1.0);
        }
        
        System.out.println("whitespace: "+parameters.get("whitespace"));
        
        double level = Math.ceil(Math.log(data.size() * parameters.get("whitespace")) / Math.log(4)) + parameters.get("pluslevel");
        double size =  Math.pow(2, level);
        
        Map<String, Integer> bounds = get_bounds(data);
        
        var ux = bounds.get("min_x");
        var uy = bounds.get("min_y");
        var w = bounds.get("width");
        var h = bounds.get("height");
        
        double[] original_bounding_box = { ux, uy, w, h};
        double[] gridded_bounding_box = {0, 0, size, size};
        
        List<Point2D.Double> D = new ArrayList<>();
        for (Point2D.Double d : data) {
            D.add(remap(d, original_bounding_box, gridded_bounding_box));
        }
        
        List<Point2D.Double> points =  Hagrid.gridify_hilbert(D, level);
        
        return points;
    }

    public static HashMap<String, Integer> get_bounds(List<Point2D.Double> data) {
        
        HashMap<String, Integer> bounds = new HashMap<>();
        
        bounds.put("min_x", (int)data.stream().min((a, b) -> Double.compare(a.x, b.x)).get().x);
        bounds.put("max_x", (int) data.stream().max((a, b) -> Double.compare(a.x, b.x)).get().x);
        
        bounds.put("min_y",  (int)data.stream().min((a, b) -> Double.compare(a.y, b.y)).get().y);
        bounds.put("max_y",  (int)data.stream().max((a, b) -> Double.compare(a.y, b.y)).get().y);       
        
        bounds.put("width", bounds.get("max_x")-bounds.get("min_x"));
        bounds.put("height", bounds.get("max_y")-bounds.get("min_y"));
        
        return bounds;
    }

    private static Point2D.Double remap(Point2D.Double d, double[] fromRect, double[] toRect) {
        Point2D.Double p = new Point2D.Double();
        p.setLocation((d.getX() - fromRect[0]) / fromRect[2] * toRect[2] + toRect[0], 
                      (d.getY() - fromRect[1]) / fromRect[3] * toRect[3] + toRect[1]);
        return p;
    }

}
