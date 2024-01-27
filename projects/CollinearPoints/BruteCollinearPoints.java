/* ***********************************************************************************************************
 *
 *  Name:               MinJoker
 *  Date:               27/01/2024
 *  Grade:              100/100
 *  Libraries:          algs4.jar
 *  Project source:     https://coursera.cs.princeton.edu/algs4/assignments/collinear/specification.php
 * 
 ********************************************************************************************************** */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * the {@code BruteCollinearPoints} class represents a brute force algorithm,
 * for finding all maximal line segments containing 4 (or more) points.
 * 
 * note that it is guaranteed that input will contain no more than 4 collinear points.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/collinear/specification.php
 */

public class BruteCollinearPoints {
    private final Point[] points;                           // copy of input points
    private List<LineSegment> segments = new ArrayList<>(); // list of line segments

    /**
     * finds all line segments containing 4 points
     * 
     * @param points the input points
     * @throws IllegalArgumentException if the input points is null, or if any point in the array is null,
     *                                  or if the input points contains a repeated point
     */
    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException("points is null");
        for (Point p : points) {
            if (p == null) throw new IllegalArgumentException("point is null");
        }
        int len = points.length;
        for (int i = 0; i < len; i++) {
            for (int j = i+1; j < len; j++) {
                if (points[i].compareTo(points[j]) == 0) throw new IllegalArgumentException("duplicate points");
            }
        }
        this.points = Arrays.copyOf(points, len);   // defensive copy
        findSegments();
    }

    // finds all line segments containing 4 points
    private void findSegments() {
        int len = points.length;
        for (int i = 0; i < len; i++) {
            Point p = points[i];
            for (int j = i+1; j < len; j++) {
                Point q = points[j];
                for (int k = j+1; k < len; k++) {
                    Point r = points[k];
                    for (int l = k+1; l < len; l++) {
                        Point s = points[l];
                        if (p.slopeTo(q) == p.slopeTo(r) && p.slopeTo(r) == p.slopeTo(s)) {
                            Point[] collinearPoints = {p, q, r, s};
                            Arrays.sort(collinearPoints);
                            LineSegment segment = new LineSegment(collinearPoints[0], collinearPoints[3]);
                            segments.add(segment);
                        }
                    }
                }
            }
        }
    }

    /**
     * the number of line segments
     * 
     * @return the number of line segments
     */
    public int numberOfSegments() {
        return segments.size();
    }

    /**
     * the line segments
     * 
     * @return the line segments
     */
    public LineSegment[] segments() {
        return segments.toArray(new LineSegment[0]);
    }

    /**
     * sample client
     * 
     * reads the input points from a file, and prints and draws the line segments.
     * 
     * @param args the command-line arguments, the name of the input file
     */
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
