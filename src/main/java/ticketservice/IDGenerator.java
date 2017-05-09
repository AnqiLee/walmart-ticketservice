/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

abstract class IDGenerator<T> {
    private int nextID = Integer.MIN_VALUE;

    public synchronized T getNextID() {
        return makeID(nextID++);
    }

    abstract T makeID(int i);
}
