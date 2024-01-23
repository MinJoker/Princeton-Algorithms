/* ***********************************************************************************************************
 *
 *  Name:               MinJoker
 *  Date:               23/01/2024
 *  Grade:              100/100
 *  Libraries:          algs4.jar
 *  Project source:     https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
 * 
 ********************************************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;

/**
 * the {@code RandomizedQueue} class represents a randomized queue.
 * 
 * note that the implementation uses a resizing array.
 * note that there will be two warnings when compiling, because
 * generic array creation is not allowed in Java, so
 * there is no way to avoid this warning, just ignore it.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
 */

public class RandomizedQueue<Item> implements Iterable<Item> {
    // initial capacity of underlying resizing array
    private static final int INIT_CAPACITY = 8;

    private Item[] queue;   // queue elements
    private int size;       // number of elements on queue
    private int first;      // index of first element of queue
    private int last;       // index of next available slot

    /**
     * construct an empty randomized queue
     */
    public RandomizedQueue() {
        queue = (Item[]) new Object[INIT_CAPACITY];
        size = 0;
        first = 0;
        last = 0;
    }

    /**
     * is the randomized queue empty?
     * 
     * @return true if randomized queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * return the number of items on the randomized queue
     * 
     * @return number of items on randomized queue
     */
    public int size() {
        return size;
    }

    // resize the underlying array
    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            copy[i] = queue[(first + i) % queue.length];
        }
        queue = copy;
        first = 0;
        last  = size;
    }

    /**
     * add the item
     * 
     * @param item the item to add
     * @throws IllegalArgumentException if {@code item == null}
     */
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();
        if (size == queue.length) resize(2*queue.length);
        queue[last++] = item;
        if (last == queue.length) last = 0; // wrap-around
        size++;
    }

    /**
     * remove and return a random item
     * 
     * @return a random item
     * @throws java.util.NoSuchElementException if randomized queue is empty
     */
    public Item dequeue() {
        if (isEmpty()) throw new java.util.NoSuchElementException("RandomizedQueue underflow");
        int index = ( StdRandom.uniformInt(size) + first ) % queue.length;
        Item item = queue[index];
        queue[index] = queue[first];
        queue[first] = null;    // to avoid loitering
        size--;
        first++;
        if (first == queue.length) first = 0; // wrap-around
        if (size > 0 && size == queue.length/4) resize(queue.length/2);
        return item;
    }

    /**
     * return a random item (but do not remove it)
     * 
     * @return a random item
     */
    public Item sample() {
        if (isEmpty()) throw new java.util.NoSuchElementException("RandomizedQueue underflow");
        int index = ( StdRandom.uniformInt(size) + first ) % queue.length;
        return queue[index];
    }

    /**
     * return an independent iterator over items in random order
     */
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    // the iterator
    private class RandomizedQueueIterator implements Iterator<Item> {
        private int current;
        private int[] indices;

        public RandomizedQueueIterator() {
            current = 0;
            indices = new int[size];
            for (int i = 0; i < size; i++) {
                indices[i] = (first + i) % queue.length;
            }
            StdRandom.shuffle(indices); // shuffle the indices
        }

        public boolean hasNext() {
            return current < size;
        }

        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            return queue[indices[current++]];
        }

        // not supported
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * unit testing (required)
     * 
     * creates a randomized queue of "1 2 3 4 5 6 7 8 9 10",
     * then calls {@code sample()} and {@code dequeue()} to print.
     * tests {@code iterator()}, {@code size()} and {@code isEmpty()} as well.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();

        for (int i = 0; i < 10; i++) {
            rq.enqueue(i);
        }

        for (int i : rq) {
            StdOut.println(i);
        }

        StdOut.println("Size: " + rq.size());
        for (int i = 0; i < 10; i++) {
            StdOut.println(rq.sample() + " " + rq.dequeue());
        }
        StdOut.println("Size: " + rq.size());
        StdOut.println("Is empty: " + rq.isEmpty());
    }
}
