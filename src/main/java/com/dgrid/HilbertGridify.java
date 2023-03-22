/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dgrid;

/**
 *
 * @author wilson.marcilio
 */
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class HilbertGridify {
    
    public static int[][] gridify_hilbert(List<double[]> D, int level, boolean keep_aspect_ratio) {
        int size = (int) Math.pow(2, level);
        int curveLength = (int) Math.pow(4, level);
        int N = D.size();
        Map<Integer, Integer> P = new HashMap<>();
        int[][] Y = new int[N][2];
        
        
        List<Function<double[], Double>> scales = Utils.get_scales(D, new double[][] {{0, size - 1}, {0, size - 1}});
        for (int i = 0; i < N; i++) {
            double[] d = D.get(i);
            double x = scales.get(0).apply(d);
            double y = scales.get(1).apply(d);
            int p = hilbert_encode(new int[] {(int)x, (int)y}, size);
            if (P.containsKey(p)) {
                hilbert_collision(P, p, new double[] {x, y}, i, curveLength);
            } else {
                P.put(p, i);
            }
        }
        for (Map.Entry<Integer, Integer> entry : P.entrySet()) {
            int p = entry.getKey();
            int i = entry.getValue();
            Y[i] = hilbert_decode(p, size);
        }
        return Y;
    }
    
    public static void hilbert_collision(Map<Integer, Integer> P, int p, double[] d, int i, int size) {
        int e = (int) Math.pow(size, 2);
        Predicate<Integer> valid = (Integer p1) -> p1 >= 0 && p1 <= e;
        int pl = p;
        int pr = p;
        while (true) {
            pl++; pr--;
            boolean el = !P.containsKey(pl);
            boolean er = !P.containsKey(pr);
            boolean vl = valid.test(pl);
            boolean vr = valid.test(pr);
            if (vl && el && !er) {
                P.put(pl, i);
                return;
            } else if (!el && vr && er) {
                P.put(pr, i);
                return;
            } else if (el && er) {
                if (vl && vr) {
                    double dl = Utils.distance(d, hilbert_decode(pl, size));
                    double dr = Utils.distance(d, hilbert_decode(pr, size));
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
    
    public static int[] rotate(int size, int px, int py, int rx, int ry) {
        if (ry == 0) {
            if (rx == 1) {
                px = size - 1 - px;
                py = size - 1 - py;
            }
            return new int[]{py, px};
        }
        return new int[]{px, py};
    }

    public static int hilbert_encode(int[] point, int size) {
        int n = 0;
        int px = point[0], py = point[1];
        for (int s = size / 2; s > 0; s /= 2) {
            int rx = (px & s) > 0 ? 1 : 0;
            int ry = (py & s) > 0 ? 1 : 0;
            n += (s * s) * ((3 * rx) ^ ry);
            int[] result = rotate(size, px, py, rx, ry);
            px = result[0];
            py = result[1];
        }
        return n;
    }

    public static int[] hilbert_decode(int n, int size) {
        int t = n;
        int px = 0, py = 0;
        for (int s = 1; s < size; s *= 2) {
            int rx = 1 & (t / 2);
            int ry = 1 & (t ^ rx);
            int[] result = rotate(s, px, py, rx, ry);
            px = result[0] + s * rx;
            py = result[1] + s * ry;
            t = t >> 2;
        }
        return new int[]{px, py};
    }
}
