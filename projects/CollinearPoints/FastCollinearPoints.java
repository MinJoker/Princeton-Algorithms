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
 * the {@code FastCollinearPoints} class represents a fast algorithm,
 * for finding all maximal line segments containing 4 (or more) points.
 * 
 * some key points as follows:
 * 
 * 1. use linked-list to store the {@code segments} to satisfy the requirement of space complexity.
 * 2. {@code Array.sort()} applies merge sort in Java, which satisfies the requirement of time complexity.
 * 3. how to avoid duplicate segments?
 *    
 *    the first try is to use {@code segmentExists()},
 *    but it is bad style to write code that depends on the particular format of the output
 *    from the {@code toString()} method.
 *    
 *    a clever solution is to add a new segment only when
 *    the current point is the smallest point in the segment, which
 *    guarantees the same segment will not be added twice.
 *    note that only an extra condition in the {@code if} statement is needed.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/collinear/specification.php
 */

public class FastCollinearPoints {
    private final Point[] points;                           // copy of input points
    private List<LineSegment> segments = new ArrayList<>(); // list of line segments

    /**
     * finds all line segments containing 4 or more points
     * 
     * @param points the input points
     * @throws IllegalArgumentException if the input points is null, or if any point in the array is null,
     *                                  or if the input points contains a repeated point
     */
    public FastCollinearPoints(Point[] points) {
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

    // finds all line segments containing 4 or more points
    private void findSegments() {
        int len = points.length;
        Point[] sortedPoints = Arrays.copyOf(points, len);
        for (int i = 0; i < len; i++) {
            Point p = points[i];
            Arrays.sort(sortedPoints, p.slopeOrder());
            int j = 1;
            while (j < len) {
                int k = j + 1;
                while (k < len && p.slopeTo(sortedPoints[j]) == p.slopeTo(sortedPoints[k])) {
                    k++;
                }
                // the second condition is to avoid duplicate segments
                if (k - j >= 3 && p.compareTo(min(sortedPoints, j, k-1)) < 0) {
                    segments.add(new LineSegment(p, max(sortedPoints, j, k-1)));
                }
                j = k;
            }
        }
    }

    // finds the minimum point in the array
    private Point min(Point[] src, int start, int end) {
        Point min = src[start];
        for (int i = start+1; i <= end; i++) {
            if (src[i].compareTo(min) < 0) {
                min = src[i];
            }
        }
        return min;
    }

    // finds the maximum point in the array
    private Point max(Point[] src, int start, int end) {
        Point max = src[start];
        for (int i = start+1; i <= end; i++) {
            if (src[i].compareTo(max) > 0) {
                max = src[i];
            }
        }
        return max;
    }

    // // check if the segment exists
    // private boolean segmentExists(LineSegment segment) {
    //     for (int i = 0; i < numberOfSegments(); i++) {
    //         if (segment.toString().equals(segments.get(i).toString())) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }

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
        return segments.toArray(new LineSegment[numberOfSegments()]);
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
