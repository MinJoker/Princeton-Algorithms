/* ***********************************************************************************************************
 *
 *  Name:               MinJoker
 *  Date:               22/01/2024
 *  Grade:              100/100
 *  Libraries:          algs4.jar
 *  Project source:     https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php
 * 
 ********************************************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * the {@code PercolationStats} class performs a series of computational experiments.
 * 
 * note that Monte Carlo simulation is performed here.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php
 */

public class PercolationStats {
    
    private static final double CONFIDENCE_95 = 1.96;   // 95% confidence interval
    private int trials;                                 // number of trials
    private double[] thresholds;                        // thresholds of each trial

    /**
     * perform trials independent experiments on an n-by-n grid
     * 
     * Monte Carlo simulation is performed
     * to estimate the percolation threshold here.
     * 
     * @param n size of grid
     * @param trials number of trials
     * @throws IllegalArgumentException unless
     *         both {@code n > 0} and {@code trials > 0}
     */
    public PercolationStats(int n, int trials) {
        if (n <= 0)
            throw new IllegalArgumentException("size of grid must be greater than 0");
        if (trials <= 0)
            throw new IllegalArgumentException("number of trials must be greater than 0");
        this.trials = trials;
        thresholds = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int row = StdRandom.uniformInt(1, n + 1);
                int col = StdRandom.uniformInt(1, n + 1);
                if (!perc.isOpen(row, col))
                    perc.open(row, col);
            }
            thresholds[i] = (double) perc.numberOfOpenSites() / (n * n);
        }
    }

    /**
     * sample mean of percolation threshold
     * 
     * @return sample mean of percolation threshold
     */
    public double mean() {
        return StdStats.mean(thresholds);
    }

    /**
     * sample standard deviation of percolation threshold
     * 
     * @return sample standard deviation of percolation threshold
     */
    public double stddev() {
        return StdStats.stddev(thresholds);
    }

    /**
     * low endpoint of 95% confidence interval
     * 
     * @return low endpoint of 95% confidence interval
     */
    public double confidenceLo() {
        return mean() - CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    /**
     * high endpoint of 95% confidence interval
     * 
     * @return high endpoint of 95% confidence interval
     */
    public double confidenceHi() {
        return mean() + CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    /**
     * test client
     * 
     * takes two command-line arguments {@code size} and {@code trials},
     * {@code size} is the size of grid, and {@code trials} is the number of trials.
     * prints the sample mean, sample standard deviation,
     * and the 95% confidence interval.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);   // size of grid
        int trials = Integer.parseInt(args[1]); // number of trials
        PercolationStats stats = new PercolationStats(size, trials);
        StdOut.println("mean                    = " + stats.mean());
        StdOut.println("stddev                  = " + stats.stddev());
        StdOut.println("95% confidence interval = ["
                       + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");
    }
}
