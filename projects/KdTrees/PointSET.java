/* ******************************************************************************************************
 *
 *  Name:               MinJoker
 *  Date:               06/02/2024
 *  Grade:              100/100
 *  Libraries:          algs4.jar
 *  Project source:     https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php
 * 
 ***************************************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

/**
 * The {@code PointSET} class represents a set of points in the unit square, using a red-black BST.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php
 */

public class PointSET {
    private SET<Point2D> set;   // set of points
    private int size;           // number of points

    /**
     * construct an empty set of points
     */
    public PointSET() {
        set = new SET<Point2D>();
        size = 0;
    }

    /**
     * is the set empty?
     * 
     * @return true if the set is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * number of points in the set
     * 
     * @return the number of points in the set
     */
    public int size() {
        return size;
    }

    /**
     * add the point to the set (if it is not already in the set)
     * 
     * @param p the point to add
     * @throws IllegalArgumentException if the argument is null
     */
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to insert() is null");
        if (!set.contains(p)) {
            set.add(p);
            size++;
        }
    }

    /**
     * does the set contain point p?
     * 
     * @param p the point to check
     * @return true if the set contains p, false otherwise
     * @throws IllegalArgumentException if the argument is null
     */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to contains() is null");
        return set.contains(p);
    }

    /**
     * draw all points to standard draw
     */
    public void draw() {
        for (Point2D p : set) {
            p.draw();
        }
    }

    /**
     * all points that are inside the rectangle (or on the boundary)
     * 
     * @param rect the rectangle to check
     * @return all points that are inside the rectangle (or on the boundary)
     * @throws IllegalArgumentException if the argument is null
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("argument to range() is null");
        SET<Point2D> rangeSet = new SET<Point2D>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                rangeSet.add(p);
            }
        }
        return rangeSet;
    }

    /**
     * a nearest neighbor in the set to point p; null if the set is empty
     * 
     * @param p the point to check
     * @return a nearest neighbor in the set to point p; null if the set is empty
     * @throws IllegalArgumentException if the argument is null
     */
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to nearest() is null");
        if (isEmpty()) return null;
        Point2D nearest = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point2D point : set) {
            double distance = p.distanceSquaredTo(point);   // faster than distanceTo()
            if (distance < minDistance) {
                minDistance = distance;
                nearest = point;
            }
        }
        return nearest;
    }

    /**
     * unit testing of the methods (optional)
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        PointSET pointSet = new PointSET();
        pointSet.insert(new Point2D(0.1, 0.2));
        pointSet.insert(new Point2D(0.2, 0.1));

        StdOut.println("pointSet.isEmpty() = " + pointSet.isEmpty());
        StdOut.println("pointSet.size() = " + pointSet.size());
        StdOut.println("pointSet.contains(new Point2D(0.1, 0.2)) = " + pointSet.contains(new Point2D(0.1, 0.2)));
        StdOut.println("pointSet.contains(new Point2D(0.2, 0.1)) = " + pointSet.contains(new Point2D(0.2, 0.1)));
        StdOut.println("pointSet.contains(new Point2D(0.3, 0.3)) = " + pointSet.contains(new Point2D(0.3, 0.3)));
        StdOut.println("pointSet.range(new RectHV(0.1, 0.1, 0.2, 0.2)) = " + pointSet.range(new RectHV(0.1, 0.1, 0.2, 0.2)));
        StdOut.println("pointSet.range(new RectHV(0.15, 0.05, 0.5, 0.5)) = " + pointSet.range(new RectHV(0.15, 0.05, 0.5, 0.5)));
        StdOut.println("pointSet.nearest(new Point2D(0.5, 0.3)) = " + pointSet.nearest(new Point2D(0.3, 0.3)));

        pointSet.draw();
    }
}
