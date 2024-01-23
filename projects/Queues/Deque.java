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
import java.util.Iterator;

/**
 * the {@code Deque} class represents a double-ended queue.
 * 
 * note that the implementation uses a doubly linked list.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
 */

public class Deque<Item> implements Iterable<Item> {
    
    private Node first; // first node
    private Node last;  // last node
    private int size;   // size of deque

    // helper linked list class, doubly linked
    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }

    /**
     * construct an empty deque
     */
    public Deque() {
        first = null;
        last = null;
        size = 0;
    }

    /**
     * is the deque empty?
     * 
     * @return true if deque is empty, false otherwise
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * return the number of items on the deque
     * 
     * @return number of items on deque
     */
    public int size() {
        return size; 
    }

    /**
     * add the item to the front
     * 
     * @param item the item to add
     * @throws IllegalArgumentException if {@code item == null}
     */
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.prev = null;
        first.next = oldfirst;
        if (isEmpty()) last = first;
        else           oldfirst.prev = first;
        size++;
    }

    /**
     * add the item to the back
     * 
     * @param item the item to add
     * @throws IllegalArgumentException if {@code item == null}
     */
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.prev = oldlast;
        last.next = null;
        if (isEmpty()) first = last;
        else           oldlast.next = last;
        size++;
    }

    /**
     * remove and return the item from the front
     * 
     * @return the item from the front
     * @throws java.util.NoSuchElementException if deque is empty
     */
    public Item removeFirst() {
        if (isEmpty()) throw new java.util.NoSuchElementException("Deque underflow");
        Item item = first.item;
        first = first.next;
        size--;
        if (isEmpty()) last = null; // to avoid loitering
        else           first.prev = null;
        return item;
    }

    /**
     * remove and return the item from the back
     * 
     * @return the item from the back
     * @throws java.util.NoSuchElementException if deque is empty
     */
    public Item removeLast() {
        if (isEmpty()) throw new java.util.NoSuchElementException("Deque underflow");
        Item item = last.item;
        last = last.prev;
        size--;
        if (isEmpty()) first = null; // to avoid loitering
        else           last.next = null;
        return item;
    }

    /**
     * return an iterator over items in order from front to back
     */
    public Iterator<Item> iterator() {
        return new DequeIterator(); 
    }

    // the iterator
    private class DequeIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException("No more items to return");
            Item item = current.item;
            current = current.next;
            return item;
        }

        // not supported
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * unit testing (required)
     * 
     * creates a deque of "1 2 3 4 5 6",
     * then calls {@code removeFirst()} and {@code removeLast()} 3 times each.
     * tests {@code iterator()}, {@code size()} and {@code isEmpty()} as well.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<Integer>();

        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.addLast(4);
        deque.addLast(5);
        deque.addLast(6);

        for (int k = 0; k < 3; k++) {
            StdOut.println("Size: " + deque.size());
            for (int i : deque) { StdOut.println(i); }
            StdOut.println("Remove first: " + deque.removeFirst());
            StdOut.println("Remove last: " + deque.removeLast());
        }

        StdOut.println("Size: " + deque.size());
        StdOut.println("Is empty: " + deque.isEmpty());
    }
}
