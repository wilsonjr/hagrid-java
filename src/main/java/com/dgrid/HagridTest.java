/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.dgrid;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 *
 * @author wilson.marcilio
 */
public class HagridTest {

    public static void main(String[] args) {
        // 
        String handle = "original_large";
        int N = 120;
        double[] whitespaces = new double[]{0.5, 1.0};
        
        var runtimes = new ArrayList<>();
        var descriptions = new ArrayList<>();
        var whitespaceRuntime = new ArrayList<>();
        
        for( Double whitespace: whitespaces ) {
            for( int i = 1; i <= N; ++i ) {
                String id = String.format("%1$4s", i).replace(' ', '0');
                String path = handle+"/scatterplot["+id+"].csv";
                String output_path = handle+"_hagrid_java/scatterplot["+id+"]_Hagrid_"+whitespace+".csv";
                
                System.out.println(path +" -> "+ output_path);
            
            
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(path));
                    var line = "";
                    var data = new ArrayList<Point2D.Double>();
                    var ids = new ArrayList<String>();
                    var labels = new ArrayList<String>();
                    
                    reader.readLine();
                    while ((line = reader.readLine()) != null) {
                        String[] values = line.split(",");
                        var p = new Point2D.Double(Double.parseDouble(values[1]), Double.parseDouble(values[2]));
                        data.add(p);
                        
                        ids.add(values[0]);
                        labels.add(values[5]);
                    }
                    reader.close(); 

                    System.out.println(data.size());

                    var parameters = new HashMap<String, Double>();

                    parameters.put("whitespace", whitespace);

                    long begin = System.currentTimeMillis();
                    var gridified = Gridify.gridify(data, parameters);
                    long end = System.currentTimeMillis();

                    long time = end-begin;

                    System.out.println("time: "+time);
                    
                    runtimes.add(time);
                    descriptions.add(path);
                    whitespaceRuntime.add(whitespace);


                    FileWriter writer = new FileWriter(output_path);
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);

                    bufferedWriter.write("id,ux,uy,width,height,label");
                    bufferedWriter.newLine();
                    
                    for( int j = 0; j < gridified.size(); ++j ) {
                        bufferedWriter.write(ids.get(j)+","+(gridified.get(j).x*10)+","+(gridified.get(j).y*10)+",10.0,10.0,"+labels.get(j));
                        bufferedWriter.newLine();
                    }

                    bufferedWriter.close();

//                    plot(gridified);


                } catch( IOException | NumberFormatException e ) {
                    System.out.println(e);
                }

            }
        }
        
        
        FileWriter writer;
        try {
            writer = new FileWriter(handle+"_hagrid_java_runtime.csv");
            
            try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                bufferedWriter.write("dataset,whitespace,milliseconds");
                bufferedWriter.newLine();

                for( int j = 0; j < runtimes.size(); ++j ) {
                    bufferedWriter.write(descriptions.get(j)+","+whitespaceRuntime.get(j)+","+runtimes.get(j));
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HagridTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
        
        
        
    }
    
    
    public static void plot(List<Point2D.Double> points ) {
        // Generate some random data        
        int numPoints = points.size();
        double[] xValues = new double[numPoints];
        double[] yValues = new double[numPoints];
        for( var i = 0; i < numPoints; ++i ) {            
            xValues[i] = points.get(i).x * 10;
            yValues[i] = points.get(i).y * 10;
        }

        // Create a dataset
        XYDataset dataset = createDataset(xValues, yValues);

        // Create a chart
        JFreeChart chart = ChartFactory.createScatterPlot(
            "Scatter Plot Example",
            "X Axis Label",
            "Y Axis Label",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        // Set colors
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);

        // Display the chart
        ChartFrame frame = new ChartFrame("Scatter Plot Example", chart);
        frame.pack();
        frame.setVisible(true);
    

    
    }
    
    private static XYDataset createDataset(double[] xValues, double[] yValues) {
        XYSeries series = new XYSeries("Data");
        for (int i = 0; i < xValues.length; i++) {
            series.add(xValues[i], yValues[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
}
