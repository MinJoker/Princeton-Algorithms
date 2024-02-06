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
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;

/**
 * The {@code KdTree} class represents a set of points in the unit square, using a 2d-tree.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php
 */

public class KdTree {
    private Node root;  // root of the KdTree
    private int size;   // number of points

    private static class Node {
        private Point2D point;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node left;        // the left/bottom subtree
        private Node right;        // the right/top subtree
    }

    /**
     * construct an empty set of points
     */
    public KdTree() {
        root = null;
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
        root = insert(root, p, true, 0, 0, 1, 1);
    }

    // insert the point p into the KdTree rooted at r
    private Node insert(Node r, Point2D p, boolean isVertical, double xmin, double ymin, double xmax, double ymax) {
        if (r == null) {
            Node node = new Node();
            node.point = p;
            node.rect = new RectHV(xmin, ymin, xmax, ymax);
            size++;
            return node;
        }
        if (r.point.equals(p)) return r;
        if (isVertical) {
            if (p.x() < r.point.x()) r.left = insert(r.left, p, !isVertical, xmin, ymin, r.point.x(), ymax);
            else r.right = insert(r.right, p, !isVertical, r.point.x(), ymin, xmax, ymax);
        } else {
            if (p.y() < r.point.y()) r.left = insert(r.left, p, !isVertical, xmin, ymin, xmax, r.point.y());
            else r.right = insert(r.right, p, !isVertical, xmin, r.point.y(), xmax, ymax);
        }
        return r;
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
        return contains(root, p, true);
    }

    // does the KdTree rooted at r contain point p?
    private boolean contains(Node r, Point2D p, boolean isVertical) {
        if (r == null) return false;
        if (r.point.equals(p)) return true;
        if (isVertical) {
            if (p.x() < r.point.x()) return contains(r.left, p, !isVertical);
            else return contains(r.right, p, !isVertical);
        } else {
            if (p.y() < r.point.y()) return contains(r.left, p, !isVertical);
            else return contains(r.right, p, !isVertical);
        }
    }

    /**
     * draw all points to standard draw
     */
    public void draw() {
        draw(root, true);
    }

    // draw the KdTree rooted at r
    private void draw(Node r, boolean isVertical) {
        if (r == null) return;
        r.point.draw();
        draw(r.left, !isVertical);
        draw(r.right, !isVertical);

        // draw the splitting line, for debugging
        if (isVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(r.point.x(), r.rect.ymin(), r.point.x(), r.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            StdDraw.line(r.rect.xmin(), r.point.y(), r.rect.xmax(), r.point.y());
        }
    }

    /**
     * all points that are inside the rectangle (or on the boundary)
     * 
     * @param rect the rectangle
     * @return all points that are inside the rectangle (or on the boundary)
     * @throws IllegalArgumentException if the argument is null
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("argument to range() is null");
        Queue<Point2D> queue = new Queue<Point2D>();
        range(root, rect, queue);
        return queue;
    }

    // all points that are inside the rectangle (or on the boundary) rooted at r
    private void range(Node r, RectHV rect, Queue<Point2D> queue) {
        if (r == null) return;
        if (rect.contains(r.point)) queue.enqueue(r.point);
        if (r.left != null && rect.intersects(r.left.rect)) range(r.left, rect, queue);
        if (r.right != null && rect.intersects(r.right.rect)) range(r.right, rect, queue);
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
        return nearest(root, p, root.point, true);
    }

    // a nearest neighbor in the KdTree rooted at r to point p
    private Point2D nearest(Node r, Point2D p, Point2D nearest, boolean isVertical) {
        if (r == null) return nearest;
        if (r.point.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) nearest = r.point;
        // there is no need to search a node (or its subtrees) if
        // the nearest distance so far is closer than the distance between
        // the query point and the rectangle corresponding to the node
        if (maybeNearest(r, p, nearest)) {
            // determine which subtree to search first
            boolean goLeftFirst = false;
            if (isVertical && p.x() < r.point.x()) goLeftFirst = true;
            if (!isVertical && p.y() < r.point.y()) goLeftFirst = true;
            if (goLeftFirst) {
                nearest = nearest(r.left, p, nearest, !isVertical);
                nearest = nearest(r.right, p, nearest, !isVertical);
            } else {
                nearest = nearest(r.right, p, nearest, !isVertical);
                nearest = nearest(r.left, p, nearest, !isVertical);
            }
        }
        return nearest;
    }

    // check if the KdTree rooted at r may contain a point that is nearer to p than nearest
    private boolean maybeNearest(Node r, Point2D p, Point2D nearest) {
        if (r == null) return false;
        if (r.rect.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) return true;
        return false;
    }

    /**
     * unit testing of the methods (optional)
     * 
     * support three independent client programs:
     * 1. KdTreeVisualizer
     * 2. RangeSearchVisualizer
     * 3. NearestNeighborVisualizer
     * 
     * warning: exactly one of the three client programs should be uncommented at a time
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        
        /******************************************************************************************
         * KdTreeVisualizer:
         * https://coursera.cs.princeton.edu/algs4/assignments/kdtree/files/KdTreeVisualizer.java
         * computes and draws the 2d-tree that results from the sequence of
         * points clicked by the user in the standard drawing window
         ******************************************************************************************/

        // RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        // StdDraw.enableDoubleBuffering();
        // KdTree kdtree = new KdTree();
        // while (true) {
        //     if (StdDraw.isMousePressed()) {
        //         double x = StdDraw.mouseX();
        //         double y = StdDraw.mouseY();
        //         StdOut.printf("%8.6f %8.6f\n", x, y);
        //         Point2D p = new Point2D(x, y);
        //         if (rect.contains(p)) {
        //             StdOut.printf("%8.6f %8.6f\n", x, y);
        //             kdtree.insert(p);
        //             StdDraw.clear();
        //             kdtree.draw();
        //             StdDraw.show();
        //         }
        //     }
        //     StdDraw.pause(20);
        // }



        /************************************************************************************************
         * RangeSearchVisualizer:
         * https://coursera.cs.princeton.edu/algs4/assignments/kdtree/files/RangeSearchVisualizer.java
         * reads a sequence of points from a file (specified as a command-line argument)
         * and inserts those points into a 2d-tree. Then, it performs range searches
         * on the axis-aligned rectangles dragged by the user in the standard drawing window
         ************************************************************************************************/

        // // initialize the data structures from file
        // String filename = args[0];
        // In in = new In(filename);
        // PointSET brute = new PointSET();
        // KdTree kdtree = new KdTree();
        // while (!in.isEmpty()) {
        //     double x = in.readDouble();
        //     double y = in.readDouble();
        //     Point2D p = new Point2D(x, y);
        //     kdtree.insert(p);
        //     brute.insert(p);
        // }

        // double x0 = 0.0, y0 = 0.0;      // initial endpoint of rectangle
        // double x1 = 0.0, y1 = 0.0;      // current location of mouse
        // boolean isDragging = false;     // is the user dragging a rectangle

        // // draw the points
        // StdDraw.clear();
        // StdDraw.setPenColor(StdDraw.BLACK);
        // StdDraw.setPenRadius(0.01);
        // brute.draw();
        // StdDraw.show();

        // // process range search queries
        // StdDraw.enableDoubleBuffering();
        // while (true) {

        //     // user starts to drag a rectangle
        //     if (StdDraw.isMousePressed() && !isDragging) {
        //         x0 = x1 = StdDraw.mouseX();
        //         y0 = y1 = StdDraw.mouseY();
        //         isDragging = true;
        //     }

        //     // user is dragging a rectangle
        //     else if (StdDraw.isMousePressed() && isDragging) {
        //         x1 = StdDraw.mouseX();
        //         y1 = StdDraw.mouseY();
        //     }

        //     // user stops dragging rectangle
        //     else if (!StdDraw.isMousePressed() && isDragging) {
        //         isDragging = false;
        //     }

        //     // draw the points
        //     StdDraw.clear();
        //     StdDraw.setPenColor(StdDraw.BLACK);
        //     StdDraw.setPenRadius(0.01);
        //     brute.draw();

        //     // draw the rectangle
        //     RectHV rect = new RectHV(Math.min(x0, x1), Math.min(y0, y1),
        //                              Math.max(x0, x1), Math.max(y0, y1));
        //     StdDraw.setPenColor(StdDraw.BLACK);
        //     StdDraw.setPenRadius();
        //     rect.draw();

        //     // draw the range search results for brute-force data structure in red
        //     StdDraw.setPenRadius(0.03);
        //     StdDraw.setPenColor(StdDraw.RED);
        //     for (Point2D p : brute.range(rect))
        //         p.draw();

        //     // draw the range search results for kd-tree in blue
        //     StdDraw.setPenRadius(0.02);
        //     StdDraw.setPenColor(StdDraw.BLUE);
        //     for (Point2D p : kdtree.range(rect))
        //         p.draw();

        //     StdDraw.show();
        //     StdDraw.pause(20);
        // }



        /***************************************************************************************************
         * NearestNeighborVisualizer:
         * https://coursera.cs.princeton.edu/algs4/assignments/kdtree/files/NearestNeighborVisualizer.java
         * reads a sequence of points from a file (specified as a command-line argument)
         * and inserts those points into a 2d-tree. Then, it performs nearest-neighbor queries
         * on the point corresponding to the location of the mouse in the standard drawing window
         ***************************************************************************************************/

        // // initialize the two data structures with point from file
        // String filename = args[0];
        // In in = new In(filename);
        // PointSET brute = new PointSET();
        // KdTree kdtree = new KdTree();
        // while (!in.isEmpty()) {
        //     double x = in.readDouble();
        //     double y = in.readDouble();
        //     Point2D p = new Point2D(x, y);
        //     kdtree.insert(p);
        //     brute.insert(p);
        // }

        // // process nearest neighbor queries
        // StdDraw.enableDoubleBuffering();
        // while (true) {

        //     // the location (x, y) of the mouse
        //     double x = StdDraw.mouseX();
        //     double y = StdDraw.mouseY();
        //     Point2D query = new Point2D(x, y);

        //     // draw all of the points
        //     StdDraw.clear();
        //     StdDraw.setPenColor(StdDraw.BLACK);
        //     StdDraw.setPenRadius(0.01);
        //     brute.draw();

        //     // draw in red the nearest neighbor (using brute-force algorithm)
        //     StdDraw.setPenRadius(0.03);
        //     StdDraw.setPenColor(StdDraw.RED);
        //     brute.nearest(query).draw();
        //     StdDraw.setPenRadius(0.02);

        //     // draw in blue the nearest neighbor (using kd-tree algorithm)
        //     StdDraw.setPenColor(StdDraw.BLUE);
        //     kdtree.nearest(query).draw();
        //     StdDraw.show();
        //     StdDraw.pause(40);
        // }
    }
}
