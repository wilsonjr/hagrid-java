/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dgrid;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 *
 * @author wilson.marcilio
 */
public class Hagrid {
    
    
    private static final String method = "hilbert";
    private static final boolean keep_aspect_ratio = true;
    private static final boolean round = true;
    
//    private static long[] rotate(long size, long px, long py, long rx, long ry) {
//        if (ry == 0) {
//            if (rx == 1) {
//                px = size - 1 - px;
//                py = size - 1 - py;
//            }
//            
//            return new long[]{py, px};
//        }
//        return new long[]{px, py};
//    }
    
//    function rotate(size, px, py, rx, ry) {
//        if (ry == 0) {
//            if (rx == 1) {
//                px = size - 1 - px;
//                py = size - 1 - py;
//            }
//            return [py, px];
//        }
//        return [px, py];
//    }
    
    public static long[] rotate(long size, long px, long py, boolean rx, boolean ry) {
        if (!ry) {
            if (rx) {
                px = size - 1 - px;
                py = size - 1 - py;
            }
            long temp = px;
            px = py;
            py = temp;
        }
        return new long[] {px, py};
    }

    public static long hilbert_encode(long[] point, long size) {
        long n = 0;
        long px = point[0];
        long py = point[1];
        for (long s = size / 2; s > 0; s /= 2) {
            boolean rx = (px & s) > 0;
            boolean ry = (py & s) > 0;
            n += (long) (s * s) * ((3 * (rx ? 1 : 0)) ^ (ry ? 1 : 0));
            long[] rotated = rotate(size, px, py, rx, ry);
            px = rotated[0];
            py = rotated[1];
        }
        return n;
    }

//    private static long  hilbert_encode(long[] p, long size) {
//        
//        long n = 0;
//        for (var s = size / 2; s > 0; s /= 2) {
//            long rx =  ((p[0] & s) > 0) ? 1 : 0;
//            long ry = ((p[1] & s) > 0) ? 1 : 0;
//            n += (s*s) * ((3 * p[0]) ^ p[1]);
//            p = rotate(size, p[0], p[1], rx, ry);
//        }
//        return n;
//    }
    
//    export function hilbert_encode([px, py], size) {
//        let n = 0;
//        for (let s = size / 2; s > 0; s /= 2) {
//            const rx = (px & s) > 0;
//            const ry = (py & s) > 0;
//            n += (s ** 2) * ((3 * rx) ^ ry);
//            [px, py] = rotate(size, px, py, rx, ry);
//        }
//        return n;
//    }

    private static long[] hilbert_decode(long n, long size) {
        long  t = n;
        long  px = 0, py = 0;
        
        for (var s = 1; s < size; s *= 2) {
            long rx = 1 & (t / 2);
            long ry = 1 & (t ^ rx);
            
            long[] p = rotate(s, px, py, rx != 0, ry != 0);
            px = p[0];
            py = p[1];
            
            px += (s * rx);
            py += (s * ry);
            
            t = t >> 2;
        }
        
        return new long[]{px, py};
    }
    
//    export function hilbert_decode(n, size) {
//        let t = n;
//        let [px, py] = [0, 0];
//        for (let s = 1; s < size; s *= 2) {
//            const rx = 1 & (t / 2);
//            const ry = 1 & (t ^ rx);
//            [px, py] = rotate(s, px, py, rx, ry);
//            px += (s * rx);
//            py += (s * ry);
//            t = t >> 2;
//        }
//        return [px, py];
//    }
    
    
    
    private static boolean valid(long p, long e) {
        return p >= 0 && p <= e;
    }
    
    private static double distance(long[] a, long[] b) {
        return Math.hypot(b[0] - a[0], b[1] - a[1]);//Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }

    private static void hilbert_collision(HashMap<Long, Long> P, long p, long[] d, long i, long size) {
        long e = size*size;
        long pl = p;
        long pr = p;
        
        
        while (true) {
            ++pl; --pr;
            boolean el = !P.containsKey(pl);
            boolean er = !P.containsKey(pr);
            boolean vl = valid(pl, e);
            boolean vr = valid(pr, e);
            
            if (vl && el && !er) {
                P.put(pl, i);
                return;
            } else if (!el && vr && er) {
                P.put(pr, i);
                return;
            } else if (el && er) {
                if (vl && vr) {
                    var dl = distance(d, hilbert_decode(pl, size));
                    var dr = distance(d, hilbert_decode(pr, size));
                    P.put(dl < dr ? pl : pr, i);
                    return;
                } else if (vl) {
                    P.put(pl, i);
                    return;
                } else if (vr) {
                    P.put(pr, i);
                    return;
                }
            }
        } 
    }

    
    
    private static List<Function<Point2D.Double, Long>> get_scales(List<Point2D.Double> data, long[][] X) {
       //        [[X_min, X_max], [Y_min, Y_max]]
        
       Function<Point2D.Float, Float> x = n -> n.x;
       Function<Point2D.Float, Float> y = n -> n.y;
       
       
       
       double X_max = X[0][1];
       double X_min = X[0][0];
       double Y_max = X[1][1];
       double Y_min = X[1][0];
       
       final double X_span = X_max - X_min;
       final double Y_span = Y_max - Y_min;
       
       double x_min_temp = data.stream().min((a, b) -> Double.compare(a.x, b.x)).get().x;
       double x_max_temp = data.stream().max((a, b) -> Double.compare(a.x, b.x)).get().x;
       
       double y_min_temp = data.stream().min((a, b) -> Double.compare(a.y, b.y)).get().y;
       double y_max_temp = data.stream().max((a, b) -> Double.compare(a.y, b.y)).get().y;
       
       final double x_span = x_max_temp-x_min_temp;
       final double y_span = y_max_temp-y_min_temp;
       
       if( keep_aspect_ratio ) {
           double o = 0;
           
            if (x_span > y_span) {
                o = (x_span - y_span) / 2;
                y_min_temp -= o;
                y_max_temp += o;
            } else {
                o = (y_span - x_span) / 2;
                x_min_temp -= o;
                x_max_temp += o;
            }
       }
       
       final double y_min = y_min_temp;
       final double y_max = y_max_temp;
       final double x_min = x_min_temp;
       final double x_max = x_max_temp;
     
       
       Function<Point2D.Double, Long> x_scale = d -> {
//           System.out.println("d.x: "+d.x);
//           System.out.println("x_min: "+x_min);
//            System.out.println("x_span: "+x_span);
//            System.out.println("X_span: "+X_span);
//            System.out.println("X_min: "+X_min);
           return Math.round((d.x - x_min) / x_span * X_span + X_min);
       }; 
       
       Function<Point2D.Double, Long> y_scale = d -> {
//           System.out.println("d.y: "+d.y);
//           System.out.println("y_min: "+y_min);
//            System.out.println("y_span: "+y_span);
//            System.out.println("Y_span: "+Y_span);
//            System.out.println("Y_min: "+Y_min);
           return Math.round((d.y - y_min) / y_span * Y_span + Y_min);
       };
       
//       Math.round((1.0914481-0)/32*31+0)
       
       
       var v = new ArrayList<Function<Point2D.Double, Long>>();
       v.add(x_scale);
       v.add(y_scale);
       
       return v;
    }
    
    
    
    public static List<Point2D.Double> gridify_hilbert(List<Point2D.Double> D, double level) {
        
        long size = (long)Math.pow(2, level);
        
        long curveLength = (long) Math.pow(4, level);
        long N = D.size();
        
        var P = new HashMap<Long, Long>();
        List<Function<Point2D.Double, Long>> scales = get_scales(D, new long[][]{{0, size-1}, {0, size-1}});
        Function<Point2D.Double, Long> x_scale = scales.get(0);
        Function<Point2D.Double, Long> y_scale = scales.get(1);
        
        IntStream.range(0, D.size())
                .forEach(i -> { 
                    var d = D.get(i);
                    long x = x_scale.apply(d);
                    long y = y_scale.apply(d);
                    var p = hilbert_encode(new long[]{x, y}, size);
                    if( P.containsKey(p) ) {
                        hilbert_collision(P, p, new long[]{x, y}, i, curveLength);
                    } else {
                        P.put(p, (long)i);
                    }
                });
        
        List<Point2D.Double> Y = new ArrayList<>();
        for( var p: P.entrySet() ) {
            long[] point = hilbert_decode(p.getKey(), size);
            Y.add(new Point2D.Double(point[0], point[1]));
        }
        
        return Y;
    }
}
