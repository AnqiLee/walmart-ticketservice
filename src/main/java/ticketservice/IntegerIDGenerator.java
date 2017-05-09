/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

public class IntegerIDGenerator extends IDGenerator<Integer> {
    @Override
    Integer makeID(int i) {
        return i;
    }
}
